package com.bang.gameui;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.bang.actors.Card;
import com.bang.scenemanager.SceneManager;
import com.bang.utils.CardsUtils;

public class PlayerBoardGroup extends Group {
	
	protected float CHAR_WIDTH_PERCENTAGE = (float) 0.2;
	protected float CHAR_HEIGHT_PERCENTAGE = (float) 0.4;
	protected float CHAR_POS_WIDTH_PERCENTAGE = (float) 0.05;
	protected float LIST_POS_HEIGHT_PERCENTAGE = (float) 0.04;
	
	protected SceneManager sceneManager;
	
	// Board
	protected float width, height;
	protected Image boardImage;
	
	// Character card
	protected float charWidth, charHeight;
	protected float charPosX, charPosY;
	protected Image charImage;
	
	// For both kind of cards
	protected float cardHeight, cardWidth;
	protected float cardListWidth;
	protected float cardListPosX;
	
	// Cards on the board
	protected float boardCardWidth, boardCardHeight;
	protected ArrayList<Card> boardCards;
	protected ArrayList<Group> boardCardImages;
	
	// Cards in hand
	protected float handCardWidth, handCardHeight;
	protected ArrayList<Card> handCards;
	protected ArrayList<Group> handCardImages;
	
	public PlayerBoardGroup(float width, float height, SceneManager sceneManager) {
		this.width = width;
		this.height = height;
		this.sceneManager = sceneManager;
		this.setSize(width, height);
		setupLayout();
	}
	
	protected void setupLayout() {
		
		// Card image handling
		boardImage = new Image(sceneManager.getSkin().getDrawable("textfield"));
		boardImage.setSize((float)width, (float)height);
		boardImage.setPosition(0, 0);
		this.addActor(boardImage);
		
		// Char image handling
		charImage = new Image( new Texture(Gdx.files.internal("cards_characters/bartcassidy.png")));
		charWidth = width * CHAR_WIDTH_PERCENTAGE;
		charHeight = (float) (charWidth / CardsUtils.CARD_HEIGHT_WIDTH_RATIO);
		charPosX = width * CHAR_POS_WIDTH_PERCENTAGE;
		charPosY = height/2 - charHeight/2;
		charImage.setSize(charWidth, charHeight);
		charImage.setPosition(charPosX, charPosY);
		this.addActor(charImage);
		
		cardHeight = (float) (height * 0.42);
		cardListWidth = width - charWidth - (float)3.5*charPosX;
		cardListPosX = charWidth + 2*charPosX;
	}
	
	public void updateBoardCards(ArrayList<Card> boardCards) {
		this.boardCards = boardCards;
		this.boardCardImages = new ArrayList<Group>();
		
		float spacing = (cardListWidth - cardWidth - charPosX) / (boardCards.size());
		
		int index = 0;
		for (index = 0; index < boardCards.size(); index++) {
			Group img = boardCards.get(index).generateImage(cardHeight);
			boardCardImages.add(img);
			img.setPosition(cardListPosX + index * spacing, cardHeight + 2 * (LIST_POS_HEIGHT_PERCENTAGE * height));
			this.addActor(img);
		}
	}
	
	public void updateHandCards(ArrayList<Card> handCards) {
		this.handCards = handCards;
		this.handCardImages = new ArrayList<Group>();
		
		float spacing = (cardListWidth - cardWidth - charPosX) / (handCards.size());
		
		int index = 0;
		for (index = 0; index < handCards.size(); index++) {
			Group img = handCards.get(index).generateImage(cardHeight);
			handCardImages.add(img);
			img.setPosition(cardListPosX + index * spacing, LIST_POS_HEIGHT_PERCENTAGE * height);
			this.addActor(img);
		}
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
}