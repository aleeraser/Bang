package com.bang.gameui;

import java.rmi.RemoteException;
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
import com.bang.actors.Character;
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
	protected Group charImage;
	
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
	protected CardHighlight border;	
	protected boolean isPlayableCardSelected;
	
	// Character
	protected Character character;
	protected boolean isLastClickedChar;
	
	public PlayerBoardGroup(float width, float height, SceneManager sceneManager) {
		this.width = width;
		this.height = height;
		this.sceneManager = sceneManager;
		this.setSize(width, height);
		try {
			character = sceneManager.getPlayer().getCharacter();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		setupLayout();
	}
	
	protected void setupLayout() {
		
		// Board image handling
		boardImage = new Image(sceneManager.getSkin().getDrawable("textfield"));
		boardImage.setSize((float)width, (float)height);
		boardImage.setPosition(0, 0);
		this.addActor(boardImage);
		
		// Char image handling
		//charImage = new Image( new Texture(Gdx.files.internal("cards_characters/bartcassidy.png")));
		charWidth = width * CHAR_WIDTH_PERCENTAGE;
		charHeight = (float) (charWidth / CardsUtils.CARD_HEIGHT_WIDTH_RATIO);
		charPosX = width * CHAR_POS_WIDTH_PERCENTAGE;
		charPosY = height/2 - charHeight/2;
		
		try {
			charImage = sceneManager.getPlayer().getCharacter().getCharacterCard(charHeight);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		charImage.setSize(charWidth, charHeight);
		charImage.setPosition(charPosX, charPosY);
		
		charImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	isLastClickedChar = true;
            	// System.out.println("Inside handler");
            }
        });
		
		this.addActor(charImage);
		
		cardHeight = (float) (height * 0.42);
		cardListWidth = width - charWidth - (float)3.5*charPosX;
		cardListPosX = charWidth + 2*charPosX;
	}
	
	public void updateBoardCards(ArrayList<Card> boardCards) {
		this.boardCards = boardCards;
		
		/* Remove if already present */
		if (this.boardCardImages != null) {
			for (Group g : this.boardCardImages) {
				g.remove();
			}
		}
		
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
	            	isLastClickedChar = false;
	            	
	            	// Card border
	            	if (border != null) {
	            		border.remove();
	            	}
	        		border = new CardHighlight(img, 8);
	        		img.addActorAt(0, border);
	        		
	        		isPlayableCardSelected = false;
	            }
			});
			boardCardImages.add(img);
			img.setPosition(cardListPosX + index * spacing, cardHeight + 2 * (LIST_POS_HEIGHT_PERCENTAGE * height));
			this.addActor(img);
		}
	}
	
	public void updateHandCards(ArrayList<Card> handCards) {
		this.handCards = handCards;
		
		/* Remove if already present */
		if (this.handCardImages != null) {
			for (Group g : this.handCardImages) {
				g.remove();
			}
		}
		
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
	            	isLastClickedChar = false;
	                	        		
	        		// Card border
	            	if (border != null) {
	            		border.remove();
	            	}
	        		border = new CardHighlight(img, 8);
	        		img.addActorAt(0, border);
	        		
	        		isPlayableCardSelected = true;
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
	
	public void dismissHighlight() {
		if (border != null) border.remove();
	}
	
	public boolean isSelectedCardPlayable() {
		return isPlayableCardSelected;
	}
	
	public boolean isLastClickedCharacter() {
		return isLastClickedChar;
	}
	
	public com.bang.actors.Character getCharacter() {
		return character;
	}
	
	
	/* Card Highlight class */
	class CardHighlight extends Actor {
		
		ShapeRenderer shapeRenderer;
		Group card;
		int boardWidth;
		
	    public CardHighlight(Group card, int boardWidth) {
	    	shapeRenderer = new ShapeRenderer();
	    	this.card = card;
	    	this.boardWidth = boardWidth;
	    	this.setSize((float)card.getWidth() + boardWidth, (float)card.getHeight() + boardWidth);
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
	        		g.getParent().getX() + g.getX() - boardWidth/2, 
	        		g.getParent().getY() + g.getY() - boardWidth/2, 
	        		this.getWidth(), 
	        		this.getHeight()
	        		);
	        //shapeRenderer.rectLine(new Vector2(10, 10), new Vector2(10, 10),  50);
	        shapeRenderer.end();
	        batch.begin();
	    }
		
	}
	
}
