package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bang.actors.Card;
import com.bang.actors.IPlayer;
import com.bang.actors.Player;

public class EndingDialog extends Dialog {

    protected SceneManager sceneManager;
    protected IPlayer me;
    protected boolean winner;
    protected ArrayList<IPlayer> players;

    public EndingDialog(SceneManager sceneManager, boolean winner) {
        super("Fine della Partita", sceneManager.getSkin(), "dialog");
        this.sceneManager = sceneManager;
        this.winner = winner;
        this.me = sceneManager.getPlayer();

        setup();
    }

    protected void setup() {
        if (winner) {
            this.text("Hai vinto!");
        }

        else {
            this.text("Sei morto!");
        }

        this.button("Torna al Menu", 0);

    }

}
