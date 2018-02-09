package com.bang.scenemanager;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bang.actors.Card;
import com.bang.gameui.PlayerBoardGroup;
import com.bang.utils.CardsUtils;

public class TestBangGameScene extends GameScene {
	
	PlayerBoardGroup playerBoard;

	public TestBangGameScene (SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		this.setup();
	}
	
	@Override
	public void setup() {
		stage = new Stage();
        batch = stage.getBatch();
        backgroundImage = null;
        
        playerBoard = new PlayerBoardGroup((float)(stage.getWidth() * 0.9), (float)(stage.getHeight() * 0.9), sceneManager);
        //playerBoard.setPosition(stage.getWidth()/4,( stage.getHeight()/4);
        playerBoard.setPosition(0, 0);
        stage.addActor(playerBoard);
        
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card(CardsUtils.CARD_BANG, CardsUtils.CARD_ACE, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_MISSED, CardsUtils.CARD_THREE, CardsUtils.SUIT_CLUBS));
        cards.add(new Card(CardsUtils.CARD_INDIANS, CardsUtils.CARD_QUEEN, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_BANG, CardsUtils.CARD_ACE, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_MISSED, CardsUtils.CARD_THREE, CardsUtils.SUIT_CLUBS));
        cards.add(new Card(CardsUtils.CARD_INDIANS, CardsUtils.CARD_QUEEN, CardsUtils.SUIT_DIAMONDS));
     
        playerBoard.updateBoardCards(cards);
        playerBoard.updateHandCards(cards);
	}
}
