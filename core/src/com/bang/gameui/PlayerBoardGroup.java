package com.bang.gameui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
	
	// Action
	protected Card lastClickedCard;
	
	public PlayerBoardGroup(float width, float height, SceneManager sceneManager) {
		this.width = width;
		this.height = height;
		this.sceneManager = sceneManager;
		this.setSize(width, height);
		setupLayout();
	}
	
	protected void setupLayout() {
		
		// Board image handling
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
		
		charImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	System.out.println("Inside handler");
            }
        });
		
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
			final Group img = boardCards.get(index).generateImage(cardHeight);
			final Card c = boardCards.get(index);
			img.addListener(new ClickListener() {
				@Override
	            public void clicked(InputEvent event, float x, float y) {
	            	lastClickedCard = c;
	            }
			});
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
			final Group img = handCards.get(index).generateImage(cardHeight);
			final Card c = handCards.get(index);
			handCardImages.add(img);
			img.setPosition(cardListPosX + index * spacing, LIST_POS_HEIGHT_PERCENTAGE * height);
			this.addActor(img);
			img.addListener(new ClickListener() {
				@Override
	            public void clicked(InputEvent event, float x, float y) {
	            	lastClickedCard = c;
	            	
	            	CardHighlight border;	        		
	        		// Card border
	        		border = new CardHighlight(img);
	        		//border.setPosition(img.getX(), img.getY());
	        		border.setSize((float)img.getWidth() + 10, (float)img.getHeight() + 10);
	        		
	        		img.addActorAt(0, border);
	            }
			});			
		}
	}
	
	public Card getLastClickedCard() {
		return lastClickedCard;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	
	/* Card Highlight class */
	class CardHighlight extends Actor {
		
		ShapeRenderer shapeRenderer;
		Group card;
		
	    public CardHighlight(Group card) {
	    	shapeRenderer = new ShapeRenderer();
	    	this.card = card;
	    }
	    
	    @Override
	    public void draw(Batch batch, float parentAlpha) {
	        batch.end();
	        Group g = this.getParent();
	        shapeRenderer.begin(ShapeType.Filled);
	        //shapeRenderer.begin(ShapeType.Line);
	        shapeRenderer.setColor(Color.BLUE);
	        
	        //System.out.println(g.getHeight() + "   " + this.getHeight());
	        
	        shapeRenderer.rect(
	        		g.getParent().getX() + g.getX()/* - ((this.getWidth() - g.getWidth())/2)*/, 
	        		g.getParent().getY() + g.getY()/* - ((this.getHeight() - g.getHeight())/2)*/, 
	        		this.getWidth(), 
	        		this.getHeight()
	        		);
	        //shapeRenderer.rectLine(new Vector2(10, 10), new Vector2(10, 10),  50);
	        shapeRenderer.end();
	        batch.begin();
	    }
		
	}
	
}
