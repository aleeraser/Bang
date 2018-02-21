package com.bang.scenemanager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

public class InLobbyScene extends Scene {

    List<String> list;
    ScrollPane scrollPane;
    TextButton btnBack, btnJoin, btnNewLobby;
    Label text, title;
    boolean isCreator;
    String lobbyName;
    String[] playerNames;
    final ArrayList<Actor> removeOnError = new ArrayList<Actor>();

    public InLobbyScene(SceneManager sceneManager, String lobbyName, boolean isCreator) {
        this.sceneManager = sceneManager;
        sceneManager.setCurrentLobby(lobbyName);

        this.isCreator = isCreator;
        this.lobbyName = lobbyName;

        this.setup();
    }

    @Override
    public void setup() {
        stage = new Stage();

        // Players names list
        list = new List<String>(sceneManager.getSkin());

        scrollPane = new ScrollPane(list);
        scrollPane.setBounds(0, 200, stage.getWidth(), 150);
        scrollPane.setTransform(true);
        scrollPane.layout();

        UIUtils.createBtn(btnBack, "Indietro", 10, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    String[] params = new String[2];
                    params[0] = "ip";
                    params[1] = "lobby";

                    String[] vals = new String[2];
                    vals[0] = sceneManager.getPlayer().getIp();
                    vals[1] = lobbyName;

                    JSONObject res = NetworkUtils.postHTTP(NetworkUtils.getBaseURL() + "/remove_player", params, vals);
                    if (res.getInt("code") == 1) { // player not in lobby
                        list.remove();
                        scrollPane.remove();
                        UIUtils.showError(res.getString("msg"), null, stage, sceneManager, text, removeOnError);
                    } else {
                        sceneManager.setScene(new RoomListScene(sceneManager));
                    }
                    
                } catch (Exception e) {
                    list.remove();
                    scrollPane.remove();
                    UIUtils.showError("Errore di connessione al server", e, stage, sceneManager, text, removeOnError);
                }
            }
        });

        UIUtils.createBtn(btnJoin, "Aggiorna", 210, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updatePlayerList();
            }
        });

        if (isCreator) {
            UIUtils.createBtn(btnJoin, "Inizia Partita", Gdx.graphics.getWidth() - 230, 10, stage,
                    sceneManager.getTextButtonStyle(), new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            updatePlayerList();
                        }
                    });
        }

        updatePlayerList();
    }

    protected void updatePlayerList() {
        playerNames = new String[0];

        try {
            String[] params = new String[1],
                     vals = new String[1];
            
            params[0] = "lobby";
            vals[0] = lobbyName;

            JSONArray ret = new JSONArray(NetworkUtils.getHTTP(NetworkUtils.getBaseURL() + "/get_players", params, vals));
            int player_num = ret.length();
            playerNames = new String[player_num];
            for (int i = 0; i < ret.length(); i++) {
                playerNames[i] = ret.getString(i);
            }

            list.setItems(playerNames);

            stage.addActor(scrollPane);
            removeOnError.add(scrollPane);

        } catch (Exception e) {
            UIUtils.showError("Errore di connessione al server", e, stage, sceneManager, text, removeOnError);
        }
    }

}
