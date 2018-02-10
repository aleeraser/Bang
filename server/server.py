#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
# import os
# import base64
import json
from flask import Flask
from urllib import unquote_plus

reload(sys)
sys.setdefaultencoding('utf8')

try:
    with open('./lobbies.json', 'r') as data_file:
        lobbies = json.load(data_file)
except Exception as IOError:
    lobbies = {}

app = Flask(__name__)
app.secret_key = '\xeb9\xb9}_\x83\xcb\xafp\xf1P\xcb@\x83\x0b\xb4Z"\xc9\x91\xbd\xf0\xaa\xac'


def res_builder(msg, code):
    return {
        "msg": msg,
        "code": code
    }


@app.route("/list", methods=["GET"])
def list_lobbies():
    name_list = []
    for name in list(lobbies.keys()):
        name_list.append(name)
    res = json.dumps(name_list, sort_keys=True, separators=(',', ':'))
    return res


@app.route("/new&name=<string:name>", methods=["POST"])
def new_lobby(name):
    try:
        if unquote_plus(name) not in list(lobbies.keys()):
            lobbies[name] = []

            res = res_builder("Ok", 0)

            with open('lobbies.json', 'w') as outfile:
                json.dump(lobbies, outfile, encoding='utf-8')
        else:
            res = res_builder("Name already present", 1)

    except Exception as e:
        res = res_builder("Error: " + str(e.message), -1)

    return json.dumps(res, sort_keys=True, separators=(',', ':'))

@app.route("/addplayer&ip=<string:ip>&name=<string:name>", methods=["POST"])
def addplayer(ip, name):
    lobbies[name].append(ip)
    res = res_builder("Ok", 0)
    return json.dumps(res, sort_keys=True, separators=(',', ':'))


@app.route("/", methods=["GET"])
def main():
    return "<h2>We</h2>"
