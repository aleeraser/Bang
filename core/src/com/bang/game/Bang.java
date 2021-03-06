package com.bang.game;

import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;

// libgdx libs
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bang.actors.IPlayer;
import com.bang.scenemanager.GameScene;
import com.bang.scenemanager.InLobbyScene;
import com.bang.scenemanager.MainMenuScene;
import com.bang.scenemanager.SceneManager;
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

import org.json.JSONObject;

public class Bang extends ApplicationAdapter {

    SceneManager sceneManager;

    @Override
    public void create() {
        sceneManager = new SceneManager();
        sceneManager.setScene(new MainMenuScene(sceneManager));
        //sceneManager.setScene(new GameScene(sceneManager));
        //sceneManager.setScene(new InLobbyScene(sceneManager, "TestLobby", true));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Stage s = sceneManager.getCurrentStage();
        Texture bg = sceneManager.getCurrentBackgroundImage();
        Batch batch = s.getBatch();
        IPlayer me = sceneManager.getPlayer();
        GameScene gs = null;
        InLobbyScene inLobbyScene = null;

        try {
            if (me.shouldExit()) {
                s.dispose();
                return;
            }
        } catch (RemoteException e) {
            s.dispose();
            return;
        }

        if (bg != null) {
            batch.begin();
            batch.draw(bg, 0, 0, s.getWidth(), s.getHeight());
            batch.end();
        }

        try {
            sceneManager.acquireInGame();
            if (sceneManager.isInGame()) {
                // Starts the timeout to check if the current turnHolder is still alive.
                me.checkTimeout(System.currentTimeMillis());

                if (gs == null)
                    gs = (GameScene) sceneManager.getCurrentScene();

                if (me.isIndiansTurn() && me.shouldUpdateGUI()) {
                    gs.update();
                    gs.showIndiansDialog(me.isMyTurn());
                }

                if (!me.isIndiansTurn()) {
                    gs.dismissIndiansDialog();
                }

                if (me.isInDuel() && me.shouldUpdateDuel()) {
                    System.out.println("Showing duel dialog");
                    System.out.println("IS MY TURN: " + me.isDuelTurn());
                    gs.showDuelDialog(me.isDuelTurn(), me.getPlayers().get(me.getDuelEnemy()).getCharacter().getName());
                    me.redrawDuel(false);
                }

                if (!me.isInDuel()) {
                    gs.dismissDuelDialog();
                }

                if (!me.isInBangTurn().matches("") && me.shouldUpdateBang()) {
                    System.out.println("Showing bang dialog");
                    System.out.println("IS MY TURN: " + me.isInBangTurn());
                    gs.showBangDialog(me.isInBangTurn(),
                            me.getPlayers().get(me.getBangEnemy()).getCharacter().getName());
                    me.redrawBang(false);
                }

                if (me.isInBangTurn().matches("")) {
                    gs.dismissBangDialog();
                }

                /*if (me.isMarketTurn() && me.shouldUpdateGUI()) {
                    gs.update();
                    gs.showMarketDialog();
                }*/

                if (!me.isMarketTurn()) {
                    gs.dismissMarketDialog();
                }

                if (me.shouldUpdateGUI()) {
                    sceneManager.clearGlyphCache();
                    gs.update();
                    if (me.isMarketTurn()) {
                    		gs.showMarketDialog();
                    }
                }

                if (me.isMyTurn() && !gs.areUserInputEnabled()) {
                    // UIUtils.print("It's my turn and user input were NOT enabled. Enabling user inputs");
                    gs.areUserInputEnabled(true);
                } else if (!me.isMyTurn() && gs.areUserInputEnabled()) {
                    // UIUtils.print("It's NOT my turn and user input were enabled. Disabling user inputs");
                    gs.areUserInputEnabled(false);
                }

                if (me.getGameStatus().matches("dead")) {
                    gs.showEndingDialog(false);
                }

                if (me.getGameStatus().matches("winner")) {
                    gs.showEndingDialog(true);
                }
            } else if (me.getGameStatus().matches("")) {
                if (me.isMyTurn()) {
                    if (me.getTurn() == 1) {
                        // During the first turn cards are drawn and the game is set up. The GUI can't
                        // start yet, since not every player has drawn the cards, the character, etc...
                        me.giveTurn();
                    } else if (me.getTurn() == 2) {
                        // During the second turn the GUI starts. After all the GUI started, the game starts.
                        sceneManager.setScene(new GameScene(sceneManager));
                        me.giveTurn();
                    }
                }
            }

            sceneManager.releaseInGame();

        } catch (RemoteException e) {
            UIUtils.print("Remote Exeception in Bang.java while doing the main render");
            //e.printStackTrace();
            Gdx.app.exit();

        }

        sceneManager.clearGlyphCache();
        try {
            super.render();
            s.draw();

            // Needed to allow scrolling
            s.act();
        } catch (Exception e) {
            System.out.println("-----------------ECCEZIONE RENDER------------------");
            //e.printStackTrace();
            Gdx.app.exit();
            //super.render();
            return;
        }

    }

    @Override
    public void dispose() {
        // If the user is currently in a lobby and the windows is closed
        // attempt to remove it from the lobby's players list.
        // This will not work if the game crashes.
        try {
            if (sceneManager.getCurrentLobby() != null) {
                String[] params = new String[2];
                params[0] = "ip";
                params[1] = "lobby";

                String[] vals = new String[2];
                vals[0] = sceneManager.getPlayer().getIp();
                vals[1] = sceneManager.getCurrentLobby();

                JSONObject res = NetworkUtils.postHTTP(NetworkUtils.getBaseURL() + "/remove_player", params, vals);
                if (res.getInt("code") != 0) {
                    UIUtils.print("WARNING: Failed to remove player from lobby!");
                }
            }
        } catch (RemoteException e) {
            UIUtils.print("WARNING: Failed to remove player from lobby!");
            //e.printStackTrace();
            Gdx.app.exit();

        } catch (Exception e) {
            UIUtils.print("WARNING: Failed to remove player from lobby!");
        }
    }
}
