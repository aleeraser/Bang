package com.bang.scenemanager;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bang.actors.Card;
import com.bang.gameui.OtherBoardGroup;
import com.bang.gameui.PlayerBoardGroup;
import com.bang.gameui.SelectedCardGroup;
import com.bang.utils.CardsUtils;
import com.bang.utils.UIUtils;

public class GameScene extends Scene {
	
	PlayerBoardGroup playerBoard;
	OtherBoardGroup otherBoard;
	SelectedCardGroup selectedCard;
	TextButton playCardButton;
	
	/* Other Boards size */
	float obHeight;
	float obWidth;
	int otherPlayerNumber;

	public GameScene (SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		this.setup();
	}
	
	@Override
	public void setup() {
		stage = new Stage();
        batch = stage.getBatch();
        backgroundImage = null;
        
        selectedCard = new SelectedCardGroup((float)(stage.getWidth() * 0.22), sceneManager);
        selectedCard.setPosition(10, 10);
        stage.addActor(selectedCard);
        
        UIUtils.createBtn(
        		playCardButton, 
        		"Gioca Carta", 
        		(float)(selectedCard.getX() + selectedCard.getWidth() + 150), 
        		(float)4, 
        		stage, 
        		sceneManager.getTextButtonStyle(), 
        		new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	
            }
        });
        
        playerBoard = new PlayerBoardGroup((float)(stage.getWidth() * 0.4), (float)(stage.getHeight() * 0.3), sceneManager);
        playerBoard.setPosition((float)(selectedCard.getX() + selectedCard.getWidth() + 20), (float)(stage.getHeight() * 0.15));	
        
        playerBoard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	//System.out.println("Ouside handler");
            	Card clickedCard = playerBoard.getLastClickedCard();
            	if (clickedCard != null) {
            		System.out.println(clickedCard.getName());
            		selectedCard.showCard(clickedCard);
            	}
            }
        });
        
        stage.addActor(playerBoard);
        
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card(CardsUtils.CARD_BANG, CardsUtils.CARD_ACE, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_MISSED, CardsUtils.CARD_THREE, CardsUtils.SUIT_CLUBS));
        cards.add(new Card(CardsUtils.CARD_INDIANS, CardsUtils.CARD_QUEEN, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_INDIANS, CardsUtils.CARD_QUEEN, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_INDIANS, CardsUtils.CARD_QUEEN, CardsUtils.SUIT_DIAMONDS));
        
        playerBoard.updateBoardCards(cards);
        playerBoard.updateHandCards(cards);
        
        otherPlayerNumber = 7;
        
        obHeight = (float)(stage.getHeight() * 0.24);
        obWidth = (float)(stage.getWidth() / (otherPlayerNumber + 1));
        
        for (int i = 0; i < otherPlayerNumber; i++) {
        	OtherBoardGroup otherBoard = new OtherBoardGroup(obWidth, obHeight, sceneManager);
	        otherBoard.setPosition(50 + 10 *i + obWidth * i, 500);
	        otherBoard.updateBoardCards(cards);
	        otherBoard.updateHandCards(cards);
	        stage.addActor(otherBoard);
        }
	}
	
}
