package com.bang.scenemanager;

import org.json.JSONArray;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
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

    public InLobbyScene(SceneManager sceneManager, String lobbyName, boolean isCreator) {
        this.sceneManager = sceneManager;
        this.isCreator = isCreator;
        this.lobbyName = lobbyName;

        System.out.println(lobbyName);

        this.setup();
    }

    private void showError(String err, Exception e) {
        if (e != null) {
            UIUtils.print("Error getting lobby list\nERROR: " + e);
            e.printStackTrace();
        } else {
            UIUtils.print(err);
        }
        text = new Label(err, sceneManager.getLabelStyle());
        text.setBounds(stage.getWidth() / 2 - 150, stage.getHeight() / 2, 300, 100);
        text.setFontScale(1f, 1f);
        text.setAlignment(Align.center);

        scrollPane.remove();
        list.remove();

        stage.addActor(text);
    }

    @Override
    public void setup() {
        stage = new Stage();

        // Players names list
        list = new List<String>(sceneManager.getSkin());

        LabelStyle l = new LabelStyle();

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

                    JSONObject res = NetworkUtils.postHTTP(sceneManager.getBaseURL() + "/removeplayer", params, vals);
                    if (res.getInt("code") == 1) { // player not in lobby

                        showError(res.getString("msg"), null);
                    } else {
                        sceneManager.setScene(new RoomListScene(sceneManager));
                    }

                } catch (Exception e) {
                    showError("Errore di connessione al server", null);
                }
            }
        });

        UIUtils.createBtn(btnJoin, "Aggiorna", 210, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

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
        stage.addActor(scrollPane);
    }

    protected void updatePlayerList() {
        playerNames = new String[0];

        try {
            String[] params = new String[1];
            params[0] = "lobby";

            String[] vals = new String[1];
            vals[0] = lobbyName;

            JSONArray ret = new JSONArray(NetworkUtils.getHTTP(sceneManager.getBaseURL() + "/getplayers", params, vals));
            int player_num = ret.length();
            playerNames = new String[player_num];
            for (int i = 0; i < ret.length(); i++) {
                playerNames[i] = ret.getString(i);
            }

            list.setItems(playerNames);

            stage.addActor(scrollPane);

        } catch (Exception e) {
            showError("Errore di connessione al server", null);
        }
    }

}
