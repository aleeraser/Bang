package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bang.actors.Card;
import com.bang.actors.IPlayer;
import com.bang.actors.Player;

public class IndiansDialog extends Dialog {

    protected Card card;
    protected SceneManager sceneManager;
    protected IPlayer me;
    protected ArrayList<IPlayer> players;
    protected String opponentName;
    protected boolean isMyIndiansTurn;

    public IndiansDialog(SceneManager sceneManager, boolean isMyIndiansTurn) {
        super("Indiani", sceneManager.getSkin(), "dialog");
        this.sceneManager = sceneManager;
        this.isMyIndiansTurn = isMyIndiansTurn;
        this.me = sceneManager.getPlayer();

        setup();
    }

    protected void setup() {
        if (!isMyIndiansTurn) {
            this.text("In attesa della mossa degli avversari");
        }

        else {
            this.text("Vuoi giocare il Bang?");

            try {
                if (me.findCard(me.getHandCards(), "bang") != -1)
                    this.button("Bang!", true);
            } catch (RemoteException e) {
                //e.printStackTrace();
            }

            this.button("No", false);
        }

    }

}
