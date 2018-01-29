#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
# import os
# import base64
import json
from flask import Flask

reload(sys)
sys.setdefaultencoding('utf8')

app = Flask(__name__)
app.secret_key = '\xeb9\xb9}_\x83\xcb\xafp\xf1P\xcb@\x83\x0b\xb4Z"\xc9\x91\xbd\xf0\xaa\xac'

lobbies = []

def result_builder(msg, code):
    return {
        "msg": msg,
        "code": code
    }

@app.route("/list_lobbies", methods=["GET"])
def list_lobbies():
    return json.dumps(lobbies, sort_keys=True, separators=(',', ':'))

@app.route("/new_lobby&name=<string:name>", methods=["POST"])
def new_lobby(name):
    try:
        lobby = {
            "id" : len(lobbies),
            "name" : name
        }

        lobbies.append(lobby)

        res = result_builder("Ok", 0)
    except Exception as e:
        res = result_builder("Error: " + str(e.message), -1)

    return json.dumps(res, sort_keys=True, separators=(',', ':'))

@app.route("/", methods=["GET"])
def main():
    return "<h2>We</h2>"
