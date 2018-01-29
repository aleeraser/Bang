#!/bin/bash

function usage {
	echo
	echo "Usage:"
	echo "  - server [-h, --help] : print this help"
	echo "  - server setup        : setup virtualenv and pip packages"
	echo "  - server (re)start    : (re)start server"
	echo "  - server stop         : stop server"
    echo "  - server debug        : start server in debug mode"
    echo
	echo "Note: the server starts on localhost:5002."
}

function err {
	echo $1
    usage
	exit 1
}

if [ $# -ge "2" ]; then
    err "Wrong number of parameters"
elif [ "$1" == "setup" ]; then
    echo
    echo "WARNING: pre-requisites are a working version of Python (2.7 or"
    echo "3.x *SHOULD* make no difference) and having virtualenv installed."
    echo
    echo "If you meet the requirements, press any key to continue."
    echo "Otherwise, CTRL-C now."

    # Press any key to continue
    read -n 1 -s -r

    virtualenv serverenv && source ./serverenv/bin/activate && pip install gunicorn Flask pylint
elif [ "$1" == "start" ]; then
    source ./serverenv/bin/activate && gunicorn server:app -b 0.0.0.0:5002 -p server.pid -D -w 4
elif [ "$1" == "restart" ]; then
    source ./serverenv/bin/activate && kill -HUP `cat server.pid`
elif [ "$1" == "stop" ]; then
    source ./serverenv/bin/activate && kill `cat server.pid`
elif [ "$1" == "debug" ]; then
    source ./serverenv/bin/activate && gunicorn server:app -b 0.0.0.0:5002
elif [ $# != 0 ]; then
	err "Wrong parameter"
fi