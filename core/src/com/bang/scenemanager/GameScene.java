package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    TextButton playCardButton, endTurnButton, discardButton;
    LogBox logBox;
    IPlayer me;
    Boolean inputEnabled;

    /* Gameplay info */
    boolean isPlayableCardSelected;
    Card clickedCard;
    boolean isDiscarding = false;

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
                        String type = clickedCard.getType();
                        if (type.matches("target")){
                            SelectTargetPlayerDialog d = new SelectTargetPlayerDialog(clickedCard, sceneManager) {
                                public void result(Object obj) {
                                    System.out.println("result " + obj);
                                }
                            };

                            d.show(stage);
                        }
                        else {
                            try{
                                sceneManager.player.playCard(sceneManager.player.getHandCards().indexOf(clickedCard));
                            }catch(RemoteException e){
                                e.printStackTrace();
                            }
                        }

                        logBox.addEvent("Carta giocata");
                    }
                });

        UIUtils.disable(playCardButton);

        endTurnButton = UIUtils.createBtn("Termina turno",
                (float) (selectedCard.getX() + selectedCard.getWidth() + 250), (float) 4, stage,
                sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        //il turno può terminare solo se uno ha un numero di carte in mano <= al numero di vite
                        try {
                            int hand_cards = sceneManager.player.getHandCardsSize();
                            int lives = sceneManager.player.getLives(new int[otherPlayerNumber + 1]);
                            UIUtils.print("Currently the player has:\n\t- " + hand_cards + " cards in hand\n\t- "
                                    + lives + " lives");
                            if (hand_cards <= lives) {
                                System.out.println("End turn");
                                logBox.addEvent("Turno terminato");

                                me.giveTurn();

                                UIUtils.disable(playCardButton);
                                UIUtils.disable(endTurnButton);
                                inputEnabled = false;
                            } else {
                                System.out.println("Too many cards in hand.");
                                System.out.println("  you must discard " + (hand_cards - lives) + "!");
                                logBox.addEvent("Hai troppe carte in mano,");
                                logBox.addEvent("  devi scartarne " + (hand_cards - lives) + "!");
                                isDiscarding = true;
                                playCardButton.setVisible(false);
                                discardButton.setVisible(true);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
        UIUtils.disable(endTurnButton);
        
        discardButton = UIUtils.createBtn("Scarta", (float) (selectedCard.getX() + selectedCard.getWidth() + 20),
                (float) 4, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        System.out.println("Should discard card " + clickedCard.getName());
                        logBox.addEvent("Carta scartata " + clickedCard.getName());
                        
                        // Discarding
                        try {
							sceneManager.player.removeHandCard(
									sceneManager.player.getHandCards().indexOf(clickedCard),
									new int[otherPlayerNumber + 1]);
							
							sceneManager.player.redrawSingle();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
                        
                        // Check if needs to discard again
                        try {
	                        int hand_cards = sceneManager.player.getHandCardsSize();
	                        int lives = sceneManager.player.getLives(new int[otherPlayerNumber + 1]);
	                        if (hand_cards <= lives) {
	                        	isDiscarding = false;
	                        	playCardButton.setVisible(true);
	                        	discardButton.setVisible(false);
	                        	logBox.addEvent("Turno terminato.");
	                        	me.giveTurn();

                                UIUtils.disable(playCardButton);
                                UIUtils.disable(endTurnButton);
                                inputEnabled = false;
	                        }
                        } catch (RemoteException e) {
                        	e.printStackTrace();
                        }
                    }
                });
        /* Initially not visible */
        discardButton.setVisible(false);

        playerBoard = new PlayerBoardGroup((float) (stage.getWidth() * 0.4), (float) (stage.getHeight() * 0.3),
                sceneManager);
        playerBoard.setPosition((float) (selectedCard.getX() + selectedCard.getWidth() + 20),
                (float) (stage.getHeight() * 0.15));

        playerBoard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (!playerBoard.isLastClickedCharacter()) {
                    clickedCard = playerBoard.getLastClickedCard();
                    dismissOldHighlights();
                    if (clickedCard != null) {
                        System.out.println(clickedCard.getName());
                        selectedCard.showCard(clickedCard);

                        isPlayableCardSelected = playerBoard.isSelectedCardPlayable();

                        if (isPlayableCardSelected && areUserInputEnabled()) {
                            UIUtils.enable(playCardButton);
                        	UIUtils.enable(discardButton);
                        }
                        else {
                            UIUtils.disable(playCardButton);
                            UIUtils.disable(discardButton);
                        }
                    }
                }

                else {
                    dismissAllHighlights();
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
                        Card otherClickedCard = otherBoard.getLastClickedCard();
                        dismissOldHighlights(otherBoard);
                        if (otherClickedCard != null) {
                            System.out.println(otherClickedCard.getName());
                            selectedCard.showCard(otherClickedCard);

                            isPlayableCardSelected = false;

                            if (isPlayableCardSelected && areUserInputEnabled()) {
                                UIUtils.enable(playCardButton);
                                UIUtils.enable(discardButton);
                            }
                            else {
                                UIUtils.disable(playCardButton);
                                UIUtils.disable(discardButton);
                            }
                        }
                    } else {
                        dismissAllHighlights();
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
            UIUtils.enable(playCardButton);
            UIUtils.enable(endTurnButton);
        } else {
            UIUtils.disable(playCardButton);
            UIUtils.disable(endTurnButton);
        }
        return this.inputEnabled = val;
    }

    /* Called in character */
    protected void dismissAllHighlights() {
        for (OtherBoardGroup b : otherBoardList) {
            b.dismissHighlight();
        }
        playerBoard.dismissHighlight();
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
            me.redrawSingle(false);
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
            myPos = me.getPos(new int[playerNum]);
        } catch (RemoteException e1) {
            e1.printStackTrace();
            System.out.println("ERROR: not able to get other playes info.");
            return;
        }

        for (int i = 0; i < otherPlayerNumber; i++) {
            int index = (myPos + 1 + i) % (playerNum);
            OtherBoardGroup otherBoard = otherBoardList.get(i);

            IPlayer p = players.get(index);
            if (p != null) {
                try {
                    otherBoard.updateBoardCards(p.getCards(new int[playerNum]));
                    otherBoard.updateHandCards(p.getHandCards());
                } catch (RemoteException e) {
                    // e.printStackTrace();
                    System.out.println("ERROR: not able to get other playes info, calling alert.");
                    try {
                        me.alertPlayerMissing(index);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }

                }
            } else {
                System.out.println("Found null player, draw the X");
                otherBoard.setDisabledPlayer();
            }
        }

    }

}
