package com.bang.gameui;

import java.rmi.RemoteException;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bang.actors.Card;
import com.bang.actors.Character;
import com.bang.actors.IPlayer;
import com.bang.scenemanager.SceneManager;
import com.bang.utils.CardsUtils;

public class SelectedCardGroup extends Group {
	
	protected static float BOARD_RATIO = (float)0.87;
	
	protected float width, height;
	protected SceneManager sceneManager;
	protected Image boardImage;
	protected Group cardImage;
	
	public SelectedCardGroup(float width, SceneManager sceneManager) {
		this.width = width;
		this.height = (float)(width / (CardsUtils.CARD_HEIGHT_WIDTH_RATIO));
		this.sceneManager = sceneManager;
		setupLayout();
	}
	
	protected void setupLayout() {
		boardImage = new Image(sceneManager.getSkin().getDrawable("textfield"));
		boardImage.setSize((float)width, (float)height);
		boardImage.setPosition(0, 0);
		this.addActor(boardImage);
	}
	
	public void showCard(Card card) {
		cardImage = CardsUtils.createCardImageGroup(card.getName(), card.getValue(), card.getSuit(), height * BOARD_RATIO);
		cardImage.setPosition((float)(width * (1-BOARD_RATIO) * 0.5), (float)(height * (1-BOARD_RATIO) * 0.5));
		this.addActor(cardImage);
	}
	
	public void  showCharacterCard(Character character, IPlayer player, int playerNum) {
		//removeShownCard();
		try {
			cardImage = character.getCharacterCard(height * BOARD_RATIO, player.getLives(new int[playerNum]));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		cardImage.setPosition((float)(width * (1-BOARD_RATIO) * 0.5), (float)(height * (1-BOARD_RATIO) * 0.5));
		this.addActor(cardImage);		
	}
	
	protected void removeShownCard() {
		if (cardImage != null) cardImage.remove();
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
}
