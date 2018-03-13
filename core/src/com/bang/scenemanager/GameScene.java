package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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
    IPlayer me;
    Boolean inputEnabled;

    /* Gameplay info */
    boolean isPlayableCardSelected;

    /* Other Boards size */
    float obHeight;
    float obWidth;
    int otherPlayerNumber;

    public GameScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.setup();
    }

    @Override
    public void setup() {
        stage = new Stage();
        batch = stage.getBatch();
        backgroundImage = null;
        me = sceneManager.getPlayer();
        
        this.inputEnabled = false;

        /* TEST */
        /*try {
        	sceneManager.player = new Player();
        } catch (RemoteException e) {
        	e.printStackTrace();
        };
        try {
        	me.setCharacter(new Character("slabthekiller", 4));
        } catch (RemoteException e) {
        	e.printStackTrace();
        }*/

        sceneManager.setInGame(true);

        isPlayableCardSelected = false;

        selectedCard = new SelectedCardGroup((float) (stage.getWidth() * 0.23), sceneManager);
        selectedCard.setPosition(10, 10);
        stage.addActor(selectedCard);

        playCardButton = UIUtils.createBtn("Gioca Carta", (float) (selectedCard.getX() + selectedCard.getWidth() + 20),
                (float) 4, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        System.out.println("Should play card");
                        logBox.addEvent("Carta giocata");
                    }
                });

        endTurnButton = UIUtils.createBtn("Termina turno",
                (float) (selectedCard.getX() + selectedCard.getWidth() + 250), (float) 4, stage,
                sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        //il turno può terminare solo se uno ha un numero di carte in mano <= al numero di vite
                        try {
                            int hand_cards = sceneManager.player.getHandCardsSize();
                            int lives = sceneManager.player.getLifes(new int[otherPlayerNumber + 1]);
                            if (hand_cards <= lives) {
                                System.out.println("End turn");
                                logBox.addEvent("Turno terminato");

                                me.giveTurn();

                                playCardButton.setTouchable(Touchable.disabled);
                                endTurnButton.setTouchable(Touchable.disabled);
                                inputEnabled = false;
                            } else {
                                System.out.println("Too many cards in hand");
                                logBox.addEvent("Hai troppe carte in mano, devi scartarne " + (hand_cards - lives) + "!");
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });

        playerBoard = new PlayerBoardGroup((float) (stage.getWidth() * 0.4), (float) (stage.getHeight() * 0.3),
                sceneManager);
        playerBoard.setPosition((float) (selectedCard.getX() + selectedCard.getWidth() + 20),
                (float) (stage.getHeight() * 0.15));

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

        // ArrayList<Card> cards = new ArrayList<Card>();
        // try {
        //     cards = me.getHandCards();
        // } catch (RemoteException e) {
        //     UIUtils.print("Remote Exception in GameScene.java while getting the cards in my hand.");
        //     e.printStackTrace();
        // }

        // playerBoard.updateHandCards(cards);
        // playerBoard.updateBoardCards(new ArrayList<Card>()); 

        update();

        try {
            otherPlayerNumber = sceneManager.player.getPlayers().size() - 1;
        } catch (RemoteException e) {
            UIUtils.print("Remote Exception in GameScene.java while getting the number of players.");
            e.printStackTrace();
        }

        /* Dynamic sizing of other players boards */
        obHeight = (float) (stage.getHeight() * 0.24);
        obWidth = (float) (stage.getWidth() / (otherPlayerNumber + 1));

        otherBoardList = new ArrayList<OtherBoardGroup>();

        /* Get player info */
        IPlayer me = sceneManager.player;
        int playerNum = 0;
        int myPos = 0;
        ArrayList<IPlayer> players;

        try {
            players = me.getPlayers();
            playerNum = players.size();
            myPos = me.getPos(new int[playerNum]);
        } catch (RemoteException e) {
            System.out.println("ERROR: couldn't get other players' info.");
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < otherPlayerNumber; i++) {
            final OtherBoardGroup otherBoard = new OtherBoardGroup(obWidth, obHeight, sceneManager);
            otherBoardList.add(otherBoard);
            otherBoard.setPosition(50 + 10 * i + obWidth * i, 500);

            int index = (myPos + 1 + i) % (playerNum);

            try {
                otherBoard.setCharacter(players.get(index).getCharacter());
                otherBoard.updateBoardCards(players.get(index).getCards(new int[playerNum]));
                otherBoard.updateHandCards(players.get(index).getHandCards());
            } catch (RemoteException e) {
                System.out.println("ERROR: couldn't get other players' info.");
                e.printStackTrace();
                return;
            }

            otherBoard.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (otherBoard.isLastClickedCharacter() == false) {
                        Card clickedCard = otherBoard.getLastClickedCard();
                        dismissOldHighlights(otherBoard);
                        if (clickedCard != null) {
                            System.out.println(clickedCard.getName());
                            selectedCard.showCard(clickedCard);

                            isPlayableCardSelected = false;
                            playCardButton.setTouchable(isPlayableCardSelected ? Touchable.enabled : Touchable.disabled);
                        }
                    } else {
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

    public Boolean areUserInputEnabled() {
        return this.inputEnabled;
    }

    public Boolean areUserInputEnabled(Boolean val) {
        if (val) {
            playCardButton.setTouchable(Touchable.enabled);
            endTurnButton.setTouchable(Touchable.enabled);
        } else {
            playCardButton.setTouchable(Touchable.disabled);
            endTurnButton.setTouchable(Touchable.disabled);
        }
        return this.inputEnabled = val;
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
                if (b != otherBoardGroup)
                    b.dismissHighlight();
            }
            playerBoard.dismissHighlight();
        }
    }

    public void update() {
    	/* Update my board */
        try {
            playerBoard.updateHandCards(me.getHandCards());
            playerBoard.updateBoardCards(me.getCards(new int[me.getPlayers().size()]));
            me.redraw(false);
        } catch (RemoteException e) {
            e.printStackTrace();
            UIUtils.print("ERROR");
        }
        
        /* Update other players board */
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
        
        /* TODO check if null */
        for (int i = 0; i < otherPlayerNumber; i++) {
        	int index = (myPos + 1 + i) % (playerNum);
        	OtherBoardGroup otherBoard = otherBoardList.get(i);
        	
        	try {
        		otherBoard.updateBoardCards(players.get(index).getCards(new int [playerNum]));
        		otherBoard.updateHandCards(players.get(index).getHandCards());
        	} catch (RemoteException e) {
        		e.printStackTrace();
        		System.out.println("ERROR: not able to get other playes info.");
        	}
        }

    }

}
