package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bang.actors.Card;
import com.bang.actors.Character;
import com.bang.actors.IPlayer;
import com.bang.gameui.LogBox;
import com.bang.gameui.OtherBoardGroup;
import com.bang.gameui.PlayerBoardGroup;
import com.bang.gameui.SelectedCardGroup;
import com.bang.utils.UIUtils;

public class GameScene extends Scene {
	
	PlayerBoardGroup playerBoard;
	ArrayList<OtherBoardGroup> otherBoardList;
	SelectedCardGroup selectedCard;
	Character selectedCharacter;
	TextButton playCardButton, endTurnButton;
	LogBox logBox;
	
	/* Gameplay info */
	boolean isPlayableCardSelected;
	
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
        
        /* TEST */
        /*try {
			sceneManager.player = new Player();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		};
        try {
			sceneManager.getPlayer().setCharacter(new Character("slabthekiller", 4));
		} catch (RemoteException e) {
			e.printStackTrace();
		}*/

        sceneManager.setInGame(true);
        
        isPlayableCardSelected = false;
        
        selectedCard = new SelectedCardGroup((float)(stage.getWidth() * 0.23), sceneManager);
        selectedCard.setPosition(10, 10);
        stage.addActor(selectedCard);
        
        playCardButton = UIUtils.createBtn(
        		"Gioca Carta", 
        		(float)(selectedCard.getX() + selectedCard.getWidth() + 20), 
        		(float)4, 
        		stage, 
        		sceneManager.getTextButtonStyle(), 
        		new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	System.out.println("Should play card");
            	logBox.addEvent("Carta giocata");
            }
        });
        
        endTurnButton = UIUtils.createBtn(
        		"Termina turno", 
        		(float)(selectedCard.getX() + selectedCard.getWidth() + 250), 
        		(float)4, 
        		stage, 
        		sceneManager.getTextButtonStyle(), 
        		new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	System.out.println("End turn");
                logBox.addEvent("Turno terminato");
                playCardButton.setTouchable(Touchable.disabled);
                endTurnButton.setTouchable(Touchable.disabled);
            }
        });

        playerBoard = new PlayerBoardGroup((float)(stage.getWidth() * 0.4), (float)(stage.getHeight() * 0.3), sceneManager);
        playerBoard.setPosition((float)(selectedCard.getX() + selectedCard.getWidth() + 20), (float)(stage.getHeight() * 0.15));	
        
        playerBoard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	
            	if (!playerBoard.isLastClickedCharacter()) {
	            	Card clickedCard = playerBoard.getLastClickedCard();
	            	dismissOldHighlights();
	            	if (clickedCard != null) {
	            		System.out.println(clickedCard.getName());
	            		selectedCard.showCard(clickedCard);
	            		
                        isPlayableCardSelected = playerBoard.isSelectedCardPlayable();
                        playCardButton.setTouchable(isPlayableCardSelected ? Touchable.enabled : Touchable.disabled);
	            	}
            	}
            	
            	else {
            		dismissOldHighlights();
            		selectedCard.showCharacterCard(playerBoard.getCharacter());
            	}
            }
        });
        
        stage.addActor(playerBoard);
        
        /*ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card(CardsUtils.CARD_BANG, CardsUtils.CARD_ACE, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_MISSED, CardsUtils.CARD_THREE, CardsUtils.SUIT_CLUBS));
        cards.add(new Card(CardsUtils.CARD_INDIANS, CardsUtils.CARD_QUEEN, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_INDIANS, CardsUtils.CARD_QUEEN, CardsUtils.SUIT_DIAMONDS));
        cards.add(new Card(CardsUtils.CARD_INDIANS, CardsUtils.CARD_QUEEN, CardsUtils.SUIT_DIAMONDS));
        */
        
        ArrayList<Card> cards = new ArrayList<Card>();
		try {
			cards = sceneManager.getPlayer().getHandCards();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        

		// playerBoard.updateHandCards(cards);
        // playerBoard.updateBoardCards(new ArrayList<Card>()); 
        
        update();
        
        try {
			otherPlayerNumber = sceneManager.player.getPlayers().size() - 1;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        /* Dynamic sizing of other players boards */
        obHeight = (float)(stage.getHeight() * 0.24);
        obWidth = (float)(stage.getWidth() / (otherPlayerNumber + 1));
        
        otherBoardList = new ArrayList<OtherBoardGroup>();
        
        /* Get player info */
        IPlayer me = sceneManager.player;
        int playerNum = 0;
        int myPos = 0;
        ArrayList<IPlayer> players;
        
        try {
        	players = me.getPlayers();
        	playerNum = players.size();
        	myPos = me.getPos(new int [playerNum]);        	
        } catch (RemoteException e1) {
			e1.printStackTrace();
			System.out.println("ERROR: not able to get other playes info.");
			return;
		}
        
        for (int i = 0; i < otherPlayerNumber; i++) {
        	final OtherBoardGroup otherBoard = new OtherBoardGroup(obWidth, obHeight, sceneManager);
	        otherBoardList.add(otherBoard);
        	otherBoard.setPosition(50 + 10 *i + obWidth * i, 500);
        	
        	int index = (myPos + 1 + i) % (playerNum);
        	
        	try {
        		otherBoard.setCharacter(players.get(index).getCharacter());
        		otherBoard.updateBoardCards(players.get(index).getCards(new int [playerNum]));
        		otherBoard.updateHandCards(players.get(index).getHandCards());
        	} catch (RemoteException e) {
        		e.printStackTrace();
        		System.out.println("ERROR: not able to get other playes info.");
        		return;
        	}
	        
	        otherBoard.addListener(new ClickListener() {
	            @Override
	            public void clicked(InputEvent event, float x, float y) {
	            	if (otherBoard.isLastClickedCharacter() == false){
		            	Card clickedCard = otherBoard.getLastClickedCard();
		            	dismissOldHighlights(otherBoard);
		            	if (clickedCard != null) {
		            		System.out.println(clickedCard.getName());
		            		selectedCard.showCard(clickedCard);
		            		
		            		isPlayableCardSelected = false;
		            		playCardButton.setVisible(isPlayableCardSelected);
		            	}
	            	}
	            	else {
	            		dismissOldHighlights();
	            		selectedCard.showCharacterCard(otherBoard.getCharacter());
	            	}
	            }
	        });
	        
	        stage.addActor(otherBoard);
	        
	        /* Log Box */
	        logBox = new LogBox(sceneManager.getSkin());
	        logBox.setPosition(playerBoard.getWidth() + playerBoard.getX(), 50);
	        logBox.setSize(450, 300);
	        stage.addActor(logBox.getPane());
        }
	}
	
	/* Called by player board */
	protected void dismissOldHighlights() {		
		for (OtherBoardGroup b : otherBoardList) {
			b.dismissHighlight();
		}
	}
	
	/* Called by otherBoardGroup */
	protected void dismissOldHighlights(OtherBoardGroup otherBoardGroup) {
		if (otherBoardGroup != null) {
			for (OtherBoardGroup b : otherBoardList) {
				if (b != otherBoardGroup) b.dismissHighlight();
			}
			playerBoard.dismissHighlight();
		}
    }
    
    public void update() {
        try {
            playerBoard.updateHandCards(sceneManager.getPlayer().getHandCards());
            playerBoard.updateBoardCards(sceneManager.getPlayer().getCards(new int[sceneManager.getPlayer().getPlayers().size()]));
        } catch (RemoteException e) {
            e.printStackTrace();
            UIUtils.print("ERROR");
        }

    }
	
}
