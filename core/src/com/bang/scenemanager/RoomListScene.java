package com.bang.scenemanager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.rmi.RemoteException;
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

public class RoomListScene extends Scene {

    List<String> list;
    ScrollPane scrollPane;
    TextButton btnBack, btnJoin, btnNewLobby;
    Label text, title;
    String server_url;
    final ArrayList<Actor> removeOnError = new ArrayList<Actor>();

    public RoomListScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        server_url = NetworkUtils.getBaseURL();
        this.setup();
    }

    @Override
    public void setup() {
        stage = new Stage();

        sceneManager.setCurrentLobby(null);
        backgroundImage = null;

        list = new List<String>(sceneManager.getSkin());

        // TODO: aggiungere titolo delle lobby usando il font 'font-title-export.fnt'
        // title = new Label("Lobbies", sceneManager.getSkin());
        // stage.addActor(title);

        scrollPane = new ScrollPane(list);
        scrollPane.setBounds(0, 200, stage.getWidth(), 150);
        scrollPane.setTransform(true);
        scrollPane.layout();

        // Back
        UIUtils.createBtn(btnBack, "Indietro", 10, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sceneManager.setScene(new MainMenuScene(sceneManager));
            }
        });

        // Join room
        UIUtils.createBtn(btnJoin, "Entra", 210, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                try {
                    String[] params = new String[2];
                    params[0] = "ip";
                    params[1] = "lobby";

                    String[] vals = new String[2];
                    vals[0] = sceneManager.getPlayer().getIp();
                    vals[1] = list.getSelected();

                    JSONObject res = NetworkUtils.postHTTP(server_url + "/add_player", params, vals);
                    if (res.getInt("code") == 1) { // nome già presente
                        UIUtils.showError(res.getString("msg"), null, stage, sceneManager, text, removeOnError);
                    } else {
                        sceneManager.setCurrentLobby(list.getSelected());
                        sceneManager.setScene(new InLobbyScene(sceneManager, list.getSelected(), false));
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    UIUtils.showError("Errore di connessione al server", e, stage, sceneManager, text, removeOnError);
                }
            }
        });

        // New room
        final String[] lobs = updateLobbyList();
        UIUtils.createBtn(btnNewLobby, "Nuova stanza", Gdx.graphics.getWidth() - 230, 10, stage,
                sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        sceneManager.setScene(new NewLobbyScene(sceneManager, lobs));
                    }
                });
    }

    private String[] updateLobbyList() {
        String[] lob_names = new String[0];
        try {
            JSONArray lob = new JSONArray(NetworkUtils.getHTTP(server_url + "/list"));
            int lob_num = lob.length();
            lob_names = new String[lob_num];
            for (int i = 0; i < lob.length(); i++) {
                lob_names[i] = lob.getString(i);
            }

            list.setItems(lob_names);

            stage.addActor(scrollPane);
            removeOnError.add(scrollPane);

        } catch (Exception e) {
            UIUtils.showError("Errore di connessione al server", e, stage, sceneManager, text, removeOnError);
        }

        return lob_names;
    }
}
