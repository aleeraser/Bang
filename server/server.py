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

def storeLobbies():
    with open('lobbies.json', 'w') as outfile:
        json.dump(lobbies, outfile, encoding='utf-8')


@app.route("/reset", methods=["GET"])
def reset():
    for ip_list in lobbies:
        lobbies[ip_list] = ["111.222.333.444"]
    storeLobbies()
    return "OK"

@app.route("/list", methods=["GET"])
def list_lobbies():
    name_list = []
    for name in list(lobbies.keys()):
        name_list.append(name)
    res = json.dumps(name_list, sort_keys=True, separators=(',', ':'))
    return res


@app.route("/new&ip=<string:ip>&lobby=<string:_lobby>", methods=["POST"])
def new_lobby(ip, _lobby):
    try:
        lobby = unquote_plus(_lobby)
        if lobby not in list(lobbies.keys()):
            lobbies[lobby] = [ip]

            res = res_builder("Ok", 0)

            storeLobbies()
        else:
            res = res_builder("Lobby name already present", 1)

    except Exception as e:
        res = res_builder("Error: " + str(e.message), -1)

    return json.dumps(res, sort_keys=True, separators=(',', ':'))


@app.route("/get_players&lobby=<string:_lobby>", methods=["GET"])
def get_players(_lobby):
    lobby = unquote_plus(_lobby)
    players = []
    for ip in lobbies[lobby]:
        players.append(ip)
    res = json.dumps(players, sort_keys=True, separators=(',', ':'))
    return res


@app.route("/remove_player&ip=<string:ip>&lobby=<string:_lobby>", methods=["POST"])
def remove_player(ip, _lobby):
    try:
        lobby = unquote_plus(_lobby)

        if ip in lobbies[lobby]:
            lobbies[lobby].remove(ip)

            if not lobbies[lobby]:
                del lobbies[lobby]

            res = res_builder("Ok", 0)

            storeLobbies()
        else:
            res = res_builder("Player wasn't in lobby", 1)

    except Exception as e:
        res = res_builder("Error: " + str(e.message), -1)

    return json.dumps(res, sort_keys=True, separators=(',', ':'))


@app.route("/add_player&ip=<string:ip>&lobby=<string:_lobby>", methods=["POST"])
def add_player(ip, _lobby):
    try:
        lobby = unquote_plus(_lobby)

        if ip not in lobbies[lobby]:
            lobbies[lobby].append(ip)

            res = res_builder("Ok", 0)

            storeLobbies()
        else:
            res = res_builder("Already joined", 1)

    except Exception as e:
        res = res_builder("Error: " + str(e.message), -1)

    return json.dumps(res, sort_keys=True, separators=(',', ':'))


@app.route("/", methods=["GET"])
def main():
    return "<h2>We</h2>"
