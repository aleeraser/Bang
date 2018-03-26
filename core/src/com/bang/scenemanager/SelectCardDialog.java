package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bang.actors.Card;
import com.bang.actors.IPlayer;
import com.bang.utils.CardsUtils;

public class SelectCardDialog extends Dialog {

	protected SceneManager sceneManager;
	protected IPlayer me;
	protected ArrayList<IPlayer> players;
	protected IPlayer remotePlayer;
	protected ArrayList<Card> handCards;
	protected ArrayList<Card> tableCards;
	
	public SelectCardDialog(Card clickedCard, SceneManager sceneManager, int playerIndex) {
		super("Carta bersaglio", sceneManager.getSkin(), "dialog");
	
		this.sceneManager = sceneManager;
		try {
			this.me = sceneManager.getPlayer();
			this.players = sceneManager.getPlayer().getPlayers();
			this.remotePlayer = players.get(playerIndex);
			this.handCards = remotePlayer.getHandCards();
			this.tableCards = remotePlayer.getCards(new int [players.size()]);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		setup();
	}
	
	protected void setup() {
		this.text("Scegli la carta bersaglio");
		
		for(int i = 0; i < handCards.size(); i++) {
			/* Carte in mano hanno indice <i + numero di carte in tavolo> */
			this.button("Mano " + (i + 1), tableCards.size() + i);
		}
		
		for(int i = 0; i < tableCards.size(); i++) {
			//this.button(tableCards.get(i).getName(), i);
			Drawable drawable = new TextureRegionDrawable(new TextureRegion(CardsUtils.getCardTexture(tableCards.get(i))));
			ImageButton b = new ImageButton(drawable);
			this.button(b, i);
		}
	}
	
}
