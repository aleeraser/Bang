package com.bang.scenemanager;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.bang.actors.Card;
import com.bang.gameui.PlayerBoardGroup;
import com.bang.gameui.SelectedCardGroup;
import com.bang.utils.CardsUtils;

public class GameScene extends Scene {
	
	PlayerBoardGroup playerBoard;
	SelectedCardGroup selectedCard;

	public GameScene (SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		this.setup();
	}
	
	@Override
	public void setup() {
		stage = new Stage();
        batch = stage.getBatch();
        backgroundImage = null;
        
        playerBoard = new PlayerBoardGroup((float)(stage.getWidth() * 0.6), (float)(stage.getHeight() * 0.5), sceneManager);
        playerBoard.setPosition((float)(stage.getWidth()/3 + 20), (float)(stage.getHeight()/2 - playerBoard.getHeight()/2));
        //playerBoard.setPosition(0, 0);
        System.out.println("Ouside handler");	
        
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
        
        selectedCard = new SelectedCardGroup(200, sceneManager);
        selectedCard.setPosition(20, (stage.getHeight()/2 - selectedCard.getHeight()/2));
        stage.addActor(selectedCard);
	}
}
