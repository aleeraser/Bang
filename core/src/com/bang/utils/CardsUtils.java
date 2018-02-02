package com.bang.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class CardsUtils {
	
	public static final String CARD_ACE = "A";
	public static final String CARD_TWO = "2";
	public static final String CARD_THREE = "3";
	public static final String CARD_FOUR = "4";
	public static final String CARD_FIVE = "5";
	public static final String CARD_SIX = "6";
	public static final String CARD_SEVEN = "7";
	public static final String CARD_EIGHT = "8";
	public static final String CARD_NINE = "9";
	public static final String CARD_TEN = "10";
	public static final String CARD_JACK = "J";
	public static final String CARD_QUEEN = "Q";
	public static final String CARD_KING = "K";
	
	public static final int SUIT_SPADES = 1;
	public static final int SUIT_HEARTS = 2;
	public static final int SUIT_DIAMONDS = 3;
	public static final int SUIT_CLUBS = 4;
	
	private static final String CARD_PATH = "cards"; 
	
	public static final String CARD_BANG = "bang.png";
	public static final String CARD_BARREL = "barrel.png";
	public static final String CARD_BEER = "birra.png";
	public static final String CARD_CATBALOU = "catbalou.png";
	public static final String CARD_STAGECOACH = "diligenza.png";
	public static final String CARD_DYNAMITE = "dinamite.png";
	public static final String CARD_GENERALSTORE = "emporio.png";
	public static final String CARD_GATLING = "gatling.png";
	public static final String CARD_INDIANS = "indiani.png";
	public static final String CARD_MISSED = "mancato.png";
	public static final String CARD_SCOPE = "mirino.png";
	public static final String CARD_MUSTANG = "mustang.png";
	public static final String CARD_PANIC = "panico.png";
	public static final String CARD_PRISON = "prigione.png";
	public static final String CARD_SALOON = "saloon.png";
	public static final String CARD_WELLSFARGO = "wellsfargo.png";
	
	public static final double CARD_HEIGHT_WIDTH_RATIO = 0.6426;
	public static final double FONT_SCALE_CARD_HEIGHT_RATIO = 0.005;

	/* Creates the image group of card, given the image, card number and suit */
	public static Group createCardImageGroup(
			String cardName,
			String cardNumber,
			int cardSuit,
			double cardImageHeight) {
		
		Group g;
		Image card, suit;
		Label number;
		double width, height;
		
		g = new Group();
		height = cardImageHeight;
		width = cardImageHeight * CARD_HEIGHT_WIDTH_RATIO;
		
		// Card image handling
		card = new Image(new Texture(Gdx.files.internal(CARD_PATH + "/" + cardName)));
		card.setSize((float)width, (float)height);
		card.setPosition(0, 0);
		
		
		// Card number handling
		LabelStyle textStyle;
        BitmapFont font = new BitmapFont();
        textStyle = new LabelStyle();
        textStyle.font = font;
        number = new Label(cardNumber, textStyle);
        number.setSize((float) width/5, (float) height/8);
        number.setPosition((float) width/10, (float) height/35);
        number.setFontScale((float)(FONT_SCALE_CARD_HEIGHT_RATIO * height));
        if (cardSuit == SUIT_HEARTS || cardSuit == SUIT_DIAMONDS)
        	number.setColor(Color.RED);
        else 
        	number.setColor(Color.BLACK);
        
        
        // Card suit handling 
        switch (cardSuit) {
        	case SUIT_CLUBS:
        		suit = new Image(new Texture(Gdx.files.internal("cards_suit/clubs.png")));
        		break;
        		
        	case SUIT_SPADES:
        		suit = new Image(new Texture(Gdx.files.internal("cards_suit/spades.png")));
        		break;
        		
        	case SUIT_HEARTS:
        		suit = new Image(new Texture(Gdx.files.internal("cards_suit/hearts.png")));
        		break;
        		
        	default:
        		suit = new Image(new Texture(Gdx.files.internal("cards_suit/diamonds.png")));
        		break;	
        }
        
		suit.setSize((float) width/10, (float) height/12);
		
		if (cardNumber.matches(CARD_TEN))
			suit.setPosition((float) width/4, (float) height/20);
		else
			suit.setPosition((float) width/5, (float) height/20);
		
		g.addActor(card);
		g.addActor(number);
		g.addActor(suit);
		return g;
		
	}
	
}
