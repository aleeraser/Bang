package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bang.actors.Card;
import com.bang.actors.IPlayer;
import com.bang.actors.Player;

public class SelectTargetPlayerDialog extends Dialog {

    protected Card card;
    protected SceneManager sceneManager;
    protected IPlayer me;
    protected ArrayList<IPlayer> players;

    public SelectTargetPlayerDialog(Card card, SceneManager sceneManager) {
        super("Bersaglio", sceneManager.getSkin(), "dialog");
        this.card = card;
        this.sceneManager = sceneManager;
        try {
            this.me = sceneManager.getPlayer();
            this.players = sceneManager.getPlayer().getPlayers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        setup();
    }

    protected void setup() {
        this.text("Scegli il giocatore bersaglio della carta " + card.getName());
        int myPos = 0;
        try {
            myPos = me.getPos(new int[players.size()]);
        } catch (RemoteException e1) {
            e1.printStackTrace();
            return;
        }

        for (int i = 0; i < players.size(); i++) {
            int index = (myPos + i) % players.size();
            if (players.get(index) != null && players.get(index) != me) {
                try {
                    this.button(
                            players.get(index).getCharacter().getName() + "\n" + "(" + players.get(index).getIp() + ")",
                            index);
                } catch (RemoteException e) {
                    try {
                        me.alertPlayerMissing(index);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }
    }

}
