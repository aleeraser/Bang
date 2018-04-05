package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bang.actors.Card;
import com.bang.actors.IPlayer;
import com.bang.actors.Player;

public class BangDialog extends Dialog {

	protected Card card;
	protected SceneManager sceneManager;
	protected IPlayer me;
	protected ArrayList<IPlayer> players;
	protected String opponentName;
	protected String bangRole;
	
	public BangDialog(SceneManager sceneManager, String bangRole, String opponentName) {
		super("Bang con " + opponentName, sceneManager.getSkin(), "dialog");
		this.sceneManager = sceneManager;
		this.opponentName = opponentName;
		this.bangRole = bangRole;
		this.me = sceneManager.getPlayer();
		
		setup();
	}
	
	protected void setup() {
		if (this.bangRole.matches("killer")) {
			this.text("Chissa' se riusciro' a colpire " + opponentName + "...");
		}
		
		else {
			this.text("Vuoi giocare il mancato?");
			
			try {
				if (me.findCard(me.getHandCards(), "mancato") != -1)
					this.button("si", true);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			this.button("no", false);
		}

	}
	
}
