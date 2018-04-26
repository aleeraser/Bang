package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bang.actors.Card;
import com.bang.actors.Character;
import com.bang.actors.IPlayer;
import com.bang.actors.Player;
import com.bang.gameui.LogBox;
import com.bang.gameui.OtherBoardGroup;
import com.bang.gameui.PlayerBoardGroup;
import com.bang.gameui.SelectedCardGroup;
import com.bang.utils.UIUtils;
//TODO: errore quando si clicca un punto vuoto nel tabellone e poi scarta.

public class GameScene extends Scene {

    PlayerBoardGroup playerBoard;
    ArrayList<OtherBoardGroup> otherBoardList;
    SelectedCardGroup selectedCard;
    Character selectedCharacter;
    TextButton playCardButton, endTurnButton, discardButton;
    LogBox logBox;
    IPlayer me;
    Boolean inputEnabled;
    boolean isShowingMarketDialog = false;

    /* Gameplay info */
    boolean isPlayableCardSelected;
    Card clickedCard;
    boolean isDiscarding = false;
    protected GeneralStoreCardDialog generalStoreDialog;
    protected DuelDialog duelDialog;
    protected IndiansDialog indiansDialog;
    protected BangDialog bangDialog;
    protected EndingDialog endingDialog;

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
        	//e.printStackTrace();
        };
        try {
        	me.setCharacter(new Character("slabthekiller", 4));
        } catch (RemoteException e) {
        	//e.printStackTrace();
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
                        if (clickedCard == null) {
                            logBox.addEvent("Nessuna carta selezionata.");
                            return;
                        }

                        // System.out.println("Should play card");
                        String type = clickedCard.getType();
                        if (type.matches("target")) {
                            SelectTargetPlayerDialog d = new SelectTargetPlayerDialog(clickedCard, sceneManager) {
                                public void result(Object obj) {
                                    final int playerIndex = (Integer) obj;
                                    //System.out.println("result " + obj);
                                    try {
                                        players.get(playerIndex).getPos( new int[players.size()]);
                                        if (!(clickedCard.getName().matches("catbalou")
                                                || clickedCard.getName().matches("panico"))) {
                                            logBox.addEvent("Carta giocata: " + clickedCard.getName() + " contro "
                                                    + players.get(playerIndex).getCharacter().getName());
                                            sceneManager.player.playCard(clickedCard, playerIndex);
                                            clickedCard = null;
                                            selectedCard.removeShownCard();
                                        } else {
                                            SelectCardDialog d1 = new SelectCardDialog(clickedCard, sceneManager,
                                                    (Integer) obj) {
                                                public void result(Object cardIndex) {
                                                    try {
                                                        int len = players.get(playerIndex)
                                                                .getCards(new int[players.size()]).size();
                                                        logBox.addEvent("Carta giocata: " + clickedCard.getName()
                                                                + " contro "
                                                                + players.get(playerIndex).getCharacter().getName());

                                                        if ((Integer) cardIndex >= len) //card index is the right card index if the card is a tableCard, elseway it is the hand card index + the number of table cards.
                                                            sceneManager.player.playCard(clickedCard, playerIndex,
                                                                    (Integer) cardIndex - len, false);
                                                        else
                                                            sceneManager.player.playCard(clickedCard, playerIndex,
                                                                    (Integer) cardIndex, true);
                                                        clickedCard = null;
                                                        selectedCard.removeShownCard();
                                                    } catch (RemoteException e) {
                                                        ////e.printStackTrace();
                                                        try {
                                                            me.alertPlayerMissing(playerIndex);
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                        }
                                                    }
                                                }

                                            };
                                            d1.show(stage);
                                        }
                                    } catch (RemoteException e) {
                                        //e.printStackTrace();
                                        try {
                                            me.alertPlayerMissing(playerIndex);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            };

                            d.show(stage);
                        } else {
                            try {
                                sceneManager.player.playCard(clickedCard);
                                if(!clickedCard.getName().matches("mancato"))
                                    logBox.addEvent("Carta giocata: " + clickedCard.getName());
                                clickedCard = null;
                                selectedCard.removeShownCard();
                            } catch (RemoteException e) {
                                //e.printStackTrace();
                            }
                        }

                        //UIUtils.disable(playCardButton);
                    }
                });

        UIUtils.disable(playCardButton);

        endTurnButton = UIUtils.createBtn("Termina turno",
                (float) (selectedCard.getX() + selectedCard.getWidth() + 250), (float) 4, stage,
                sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        //il turno puo' terminare solo se uno ha un numero di carte in mano <= al numero di vite
                        try {
                            int hand_cards = sceneManager.player.getHandCardsSize();
                            int lives = sceneManager.player.getLives(new int[otherPlayerNumber + 1]);
                            // UIUtils.print("Currently the player has:\n\t- " + hand_cards + " cards in hand\n\t- "
                            //         + lives + " lives");
                            if (hand_cards <= lives) {
                                // System.out.println("End turn");
                                logBox.addEvent("Turno terminato");

                                me.giveTurn();

                                UIUtils.disable(playCardButton);
                                UIUtils.disable(endTurnButton);
                                inputEnabled = false;
                            } else {
                                // System.out.println("Too many cards in hand.");
                                // System.out.println("  you must discard " + (hand_cards - lives) + "!");
                                logBox.addEvent("Hai troppe carte in mano,");
                                logBox.addEvent("  devi scartarne " + (hand_cards - lives) + "!");
                                isDiscarding = true;
                                playCardButton.setVisible(false);
                                discardButton.setVisible(true);
                            }
                        } catch (RemoteException e) {
                            //e.printStackTrace();
                        }
                    }
                });
        UIUtils.disable(endTurnButton);

        discardButton = UIUtils.createBtn("Scarta", (float) (selectedCard.getX() + selectedCard.getWidth() + 20),
                (float) 4, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (clickedCard == null) {
                            logBox.addEvent("Nessuna carta selezionata.");
                            return;
                        }

                        // Discarding
                        try {
                            sceneManager.player.removeHandCard(sceneManager.player.getHandCards().indexOf(clickedCard),
                                    new int[otherPlayerNumber + 1]);

                            logBox.addEvent("Carta scartata " + clickedCard.getName());

                            clickedCard = null;
                            selectedCard.removeShownCard();

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
                            } else {
                                //UIUtils.disable(discardButton);
                            }
                        } catch (RemoteException e) {
                            //e.printStackTrace();
                        }

                        clickedCard = null;
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
                        // System.out.println(clickedCard.getName());
                        selectedCard.showCard(clickedCard);

                        isPlayableCardSelected = playerBoard.isSelectedCardPlayable();

                        if (isPlayableCardSelected && areUserInputEnabled()) {
                            UIUtils.enable(playCardButton);
                            UIUtils.enable(discardButton);
                        } else {
                            UIUtils.disable(playCardButton);
                            UIUtils.disable(discardButton);
                        }
                    }
                }

                else {
                    dismissAllHighlights();
                    selectedCard.showCharacterCard(playerBoard.getCharacter(), sceneManager.player,
                            otherPlayerNumber + 1);
                }
            }
        });

        stage.addActor(playerBoard);

        update();

        try {
            otherPlayerNumber = sceneManager.player.getPlayers().size() - 1;
        } catch (RemoteException e) {
            UIUtils.print("Remote Exception in GameScene.java while getting the number of players.");
            //e.printStackTrace();
        }

        /* Dynamic sizing of other players boards */
        obHeight = (float) (stage.getHeight() * 0.24);
        obWidth = (float) (stage.getWidth() / (otherPlayerNumber + 1));

        otherBoardList = new ArrayList<OtherBoardGroup>();

        /* Get player info */
        IPlayer me = sceneManager.player;
        int playerNum = 0;
        int myPos = 0;
        final ArrayList<IPlayer> players;

        try {
            players = me.getPlayers();
            playerNum = players.size();
            myPos = me.getPos(new int[playerNum]);
        } catch (RemoteException e) {
            System.out.println("ERROR: couldn't get other players' info.");
            //e.printStackTrace();
            return;
        }

        for (int i = 0; i < otherPlayerNumber; i++) {
            final int index = (myPos + 1 + i) % (playerNum);

            final OtherBoardGroup otherBoard = new OtherBoardGroup(obWidth, obHeight, sceneManager, players.get(index));
            otherBoardList.add(otherBoard);
            otherBoard.setPosition(50 + 10 * i + obWidth * i, 500);

            try {
                otherBoard.setCharacter(players.get(index).getCharacter());
                otherBoard.updateBoardCards(players.get(index).getCards(new int[playerNum]));
                otherBoard.updateHandCards(players.get(index).getHandCards());
            } catch (RemoteException e) {
                System.out.println("ERROR: couldn't get other players' info.");
                //e.printStackTrace();
                return;
            }

            otherBoard.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (otherBoard.isLastClickedCharacter() == false) {
                        Card otherClickedCard = otherBoard.getLastClickedCard();
                        dismissOldHighlights(otherBoard);
                        if (otherClickedCard != null) {
                            // System.out.println(otherClickedCard.getName());
                            selectedCard.showCard(otherClickedCard);

                            isPlayableCardSelected = false;

                            //if (isPlayableCardSelected && areUserInputEnabled()) {
                            //    UIUtils.enable(playCardButton);
                            //    UIUtils.enable(discardButton);
                            //} else {
                            UIUtils.disable(playCardButton);
                            UIUtils.disable(discardButton);
                            //}
                        }
                    } else {
                        dismissAllHighlights();
                        selectedCard.showCharacterCard(otherBoard.getCharacter(), players.get(index),
                                otherPlayerNumber + 1);
                    }
                }
            });

            stage.addActor(otherBoard);

            /* Log Box */
            logBox = new LogBox(sceneManager.getSkin());
            logBox.setPosition(playerBoard.getWidth() + playerBoard.getX(), 50);
            logBox.setSize(450, 300);
            stage.addActor(logBox.getPane());

            try {
                sceneManager.player.setLogBox(logBox);
            } catch (RemoteException e) {
                //e.printStackTrace();
            }
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
            try {
                me.getCardsSemaphore().acquire(1);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            playerBoard.updateHandCards(me.getHandCards());
            playerBoard.updateBoardCards(me.getCards(new int[me.getPlayers().size()]));
            playerBoard.setCharacter();
            me.redrawSingle(false);
            me.getCardsSemaphore().release(1);
        } catch (RemoteException e) {
            //e.printStackTrace();
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
                    otherBoard.setCharacter();
                } catch (RemoteException e) {
                    // //e.printStackTrace();
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

    public void showMarketDialog() {
        //if (isShowingMarketDialog == true)
        //	return;

        if (generalStoreDialog != null) {
            generalStoreDialog.remove();
        }

        System.out.println("Showing dialog");

        isShowingMarketDialog = true;
        try {
            generalStoreDialog = new GeneralStoreCardDialog(sceneManager,
                    me.getMarketCards(new int[me.getPlayers().size()]), me.getTurn()) {
                public void result(Object obj) {
                    try {
                        if (me.isMyTurn()) {
                            me.addMarketCardToHand((Integer) obj);
                            isShowingMarketDialog = false;
                            //me.syncMarketCards();
                        } else {
                            isShowingMarketDialog = false;
                        }
                    } catch (RemoteException e) {
                        //e.printStackTrace();
                    }
                }
            };
            if (!me.isMyTurn())
                generalStoreDialog.setTouchable(Touchable.disabled);
            generalStoreDialog.show(stage);
        } catch (RemoteException e) {
            //e.printStackTrace();
        }
    }

    public void dismissMarketDialog() {
        if (generalStoreDialog != null) {
            generalStoreDialog.remove();
            isShowingMarketDialog = false;
        }
    }

    public void showDuelDialog(boolean isMyDuelTurn, String opponentName) {
        if (duelDialog != null) {
            duelDialog.remove();
        }

        duelDialog = new DuelDialog(sceneManager, isMyDuelTurn, opponentName) {
            public void result(Object obj) {
                Boolean res = (Boolean) obj;
                int enemyIndex;
                try {
                    enemyIndex = me.getDuelEnemy();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                    return;
                }

                try {
                    IPlayer me = sceneManager.getPlayer();
                    ArrayList<IPlayer> players = me.getPlayers();
                    IPlayer enemy = players.get(enemyIndex);

                    if (res) {
                        int bangIndex = me.findCard(me.getHandCards(), "bang");
                        me.removeHandCard(bangIndex, new int[players.size()]);
                    }

                    enemy.duello(res, res, me.getPos(new int[players.size()]), new int[players.size()]);

                    if (!res) {
                        me.duello(false, false, -1, new int[players.size()]);
                        me.decreaselives(new int[players.size()]);
                        me.logOthers(enemy.getCharacter().getName() + " ha vinto il duello");
                        me.redraw();
                    }
                    
                    else {
                        me.duello(true, false, enemyIndex, new int[players.size()]);
                    }

                } catch (RemoteException e) {
                    //e.printStackTrace();
                    try {
                        me.alertPlayerMissing(enemyIndex);
                        me.duello(false, false, -1, new int[players.size()]);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        };
        duelDialog.show(stage);
    }

    public void dismissDuelDialog() {
        if (duelDialog != null) {
            duelDialog.remove();
        }
    }

    public void showBangDialog(String bangRole, String opponentName) {
        System.out.println("in show Bang dialog");
        if (bangDialog != null) {
            bangDialog.remove();
        }

        bangDialog = new BangDialog(sceneManager, bangRole, opponentName) {
            public void result(Object obj) {
                Boolean res = (Boolean) obj;
                int enemyIndex;
                try {
                    enemyIndex = me.getBangEnemy();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                    return;
                }

                try {
                    IPlayer me = sceneManager.getPlayer();
                    ArrayList<IPlayer> players = me.getPlayers();
                    IPlayer enemy = players.get(enemyIndex);

                    if (res) {
                        int missIndex = me.findCard(me.getHandCards(), "mancato");
                        me.removeHandCard(missIndex, new int[players.size()]);
                        me.setBangTurn(null);
                        me.logOthers(me.getCharacter().getName() + " ha giocato un mancato");
                    } else {
                        me.setBangTurn(null);
                        ;
                        me.decreaselives(new int[players.size()]);
                        me.redraw();
                    }

                    enemy.setBangTurn(null);

                } catch (RemoteException e) {
                    //e.printStackTrace();
                    try {
                        me.alertPlayerMissing(enemyIndex);
                        me.setBangTurn(null);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        };
        try {
            bangDialog.show(stage);
        } catch (Exception e) {
            //e.printStackTrace();
            //TODO: handle exception
        }
    }

    public void dismissBangDialog() {
        if (bangDialog != null) {
            bangDialog.remove();
        }
    }

    public void showIndiansDialog(boolean isMyIndiansTurn) {
        if (indiansDialog != null) {
            indiansDialog.remove();
        }

        indiansDialog = new IndiansDialog(sceneManager, isMyIndiansTurn) {
            public void result(Object obj) {
                Boolean res = (Boolean) obj;

                try {
                    IPlayer me = sceneManager.getPlayer();
                    ArrayList<IPlayer> players = me.getPlayers();

                    me.indiani(res);

                } catch (RemoteException e) {
                    //e.printStackTrace();
                }

            }
        };
        indiansDialog.show(stage);
    }

    public void dismissIndiansDialog() {
        if (indiansDialog != null) {
            indiansDialog.remove();
        }
    }
    
    public void showEndingDialog(boolean winner) {
    	if (endingDialog == null) {
	        endingDialog = new EndingDialog(sceneManager, winner) {
	            public void result(Object obj) {
	            	sceneManager.acquireInGame();
	            	sceneManager.setInGame(false);
	            	sceneManager.releaseInGame();
	            	try {
						sceneManager.player = new Player();
					} catch (RemoteException e) {
						//e.printStackTrace();
					}
	            	sceneManager.setScene(new MainMenuScene(sceneManager));
	            }
	        };
	        endingDialog.show(stage);
    	}
        
    }
    
    public void dismissEndingDialog() {
        if (endingDialog != null) {
            endingDialog.remove();
        }
    }
}
