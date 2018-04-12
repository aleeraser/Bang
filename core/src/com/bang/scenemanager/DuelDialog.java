package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bang.actors.Card;
import com.bang.actors.IPlayer;
import com.bang.actors.Player;

public class DuelDialog extends Dialog {

    protected Card card;
    protected SceneManager sceneManager;
    protected IPlayer me;
    protected ArrayList<IPlayer> players;
    protected String opponentName;
    protected boolean isMyDuelTurn;

    public DuelDialog(SceneManager sceneManager, boolean isMyDuelTurn, String opponentName) {
        super("Duello con " + opponentName, sceneManager.getSkin(), "dialog");
        this.sceneManager = sceneManager;
        this.opponentName = opponentName;
        this.isMyDuelTurn = isMyDuelTurn;
        this.me = sceneManager.getPlayer();

        setup();
    }

    protected void setup() {
        if (!isMyDuelTurn) {
            this.text("In attesa della mossa di " + opponentName);
        }

        else {
            this.text("Vuoi giocare il Bang?");

            try {
                if (me.findCard(me.getHandCards(), "bang") != -1)
                    this.button("Spara", true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            this.button("Arrenditi", false);
        }

    }

}
