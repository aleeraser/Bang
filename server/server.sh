#!/bin/bash

VENV="/home/alessandro/git/Bang/.venv"

function usage {
	echo
	echo "Usage:"
	echo "  - server [-h, --help]             : print this help"
	echo "  - server start [server_name]      : start server"
	echo "  - server restart [server_name]    : restart server"
	echo "  - server stop [server_name]       : stop server"
    echo "  - server debug [server_name]      : start server in debug mode (foreground)"
    echo
	echo "Note: the server starts on 0.0.0.0:5002."
}

function err {
	echo $1
    usage
	exit 1
}

if [ $# -gt "2" ]; then
    err "Wrong number of parameters."
elif [ $# -eq "1" ] && [ "$1" != "debug" ]; then
    err "Missing server name."
fi

SERVER_NAME="${2:-bang}"

if [ "$1" == "start" ]; then
    if [ -f "./server.$SERVER_NAME.pid" ]; then
        echo "A process associated with '$SERVER_NAME' is already running."
        exit 0
    fi
    "$VENV/bin/gunicorn" server:app -b 0.0.0.0:5002 -p "server.$SERVER_NAME.pid" -D && echo "Server started"
elif [ "$1" == "restart" ]; then
    kill -HUP "$(cat server.$SERVER_NAME.pid)" && echo "Server restarted"
elif [ "$1" == "stop" ]; then
    if [ ! -f "./server.$SERVER_NAME.pid" ]; then
        echo "No pid found associated with '$SERVER_NAME'."
        exit 0
    fi
    kill "$(cat ./server.$SERVER_NAME.pid)" && rm -f "./server.$SERVER_NAME.pid" && echo "Server stopped"
elif [ "$1" == "debug" ]; then
    "$VENV/bin/gunicorn" server:app -b 0.0.0.0:5002
else
    err "Wrong parameter."
fi
