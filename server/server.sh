#!/bin/bash

function usage {
	echo
	echo "Usage:"
	echo "  - server [-h, --help]             : print this help"
	echo "  - server setup                    : setup virtualenv and pip packages"
	echo "  - server (re)start [server_name]  : (re)start server"
	echo "  - server stop [server_name]       : stop server"
    echo "  - server debug [server_name]      : start server in debug mode"
    echo
	echo "Note: the server starts on localhost:5002."
}

function err {
	echo $1
    usage
	exit 1
}

function setup {
    echo
    echo "WARNING: pre-requisites are a working version of Python (2.7 or"
    echo "3.x *SHOULD* make no difference) and having virtualenv installed."
    echo
    echo "If you meet the requirements, press any key to continue."
    echo "Otherwise, CTRL-C now."

    # Press any key to continue
    read -n 1 -s -r

    virtualenv serverenv && source ./serverenv/bin/activate && pip install gunicorn Flask pylint
}

if [ "$1" == "setup" ]; then
    setup
else
    if [ $# -gt "2" ]; then
        err "Wrong number of parameters."
    elif [ $# -eq "1" ]; then
        err "Missing server name."
    fi

    [ -d "./serverenv" ] || setup
    SERVER_NAME="$2"


    if [ "$1" == "start" ]; then
        if [ -f "./server.$SERVER_NAME.pid" ]; then
            echo "A process associated with '$SERVER_NAME' is already running."
            exit 0
        fi
        source ./serverenv/bin/activate && gunicorn server:app -b 0.0.0.0:5002 -p "server.$SERVER_NAME.pid" -D -w 4 && echo "Server started"
    elif [ "$1" == "restart" ]; then
        source ./serverenv/bin/activate && kill -HUP `cat server.$SERVER_NAME.pid` && echo "Server restarted"
    elif [ "$1" == "stop" ]; then
        if [ ! -f "./server.$SERVER_NAME.pid" ]; then
            echo "No pid found associated with '$SERVER_NAME'."
            exit 0
        fi
        source ./serverenv/bin/activate && kill `cat ./server.$SERVER_NAME.pid` && echo "Server stopped"
    elif [ "$1" == "debug" ]; then
        source ./serverenv/bin/activate && gunicorn server:app -b 0.0.0.0:5002
    elif [ $# != 0 ]; then
        err "Wrong parameter."
    fi
fi