package com.bang.scenemanager;

import org.apache.http.util.NetUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

public class NewLobbyScene extends GameScene {

    TextButton btnBack, btnAdd;
    Label text;
    String[] lob_names;
    String server_url;
    Label sceneTitle;
    TextField lobbyName;

    public NewLobbyScene(SceneManager sceneManager, String[] lobs, String url) {
        this.sceneManager = sceneManager;
        lob_names = lobs;
        server_url = url;
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

        lobbyName.remove();
        sceneTitle.remove();

        stage.addActor(text);
    }

    @Override
    public void setup() {

        stage = new Stage();

        backgroundImage = null;

        sceneTitle = new Label("Inserisci il nome della stanza", sceneManager.getLabelStyle());
        sceneTitle.setBounds(stage.getWidth() / 2 - 150, stage.getHeight() / 2 + 50, 300, 100);
        sceneTitle.setFontScale(1.1f, 1.1f);
        sceneTitle.setAlignment(Align.center);
        stage.addActor(sceneTitle);

        lobbyName = new TextField("", sceneManager.getSkin());
        lobbyName.setStyle(sceneManager.getTextfieldStyle());
        lobbyName.setBounds(stage.getWidth() / 6, stage.getHeight() / 2 - 50, stage.getWidth() / 3 * 2, 80);
        lobbyName.setAlignment(Align.center);
        stage.addActor(lobbyName);

        UIUtils.createBtn(btnBack, "Indietro", 10, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                /* Go back */
                sceneManager.setScene(new RoomListScene(sceneManager));
            }
        });

        UIUtils.createBtn(btnAdd, "Crea", Gdx.graphics.getWidth() - 230, 10, stage, sceneManager.getTextButtonStyle(),
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        try {
                            JSONObject res = NetworkUtils.postHTTP(server_url + "/new", "name", lobbyName.getText());
                            if (res.getInt("code") == 1) { // nome già presente

                                showError(res.getString("msg"), null);
                            } else {
                                sceneManager.setScene(new RoomListScene(sceneManager));
                            }
                        } catch (Exception e) {
                            showError("Errore di connessione al server", null);
                        }
                    }
                });
    }
}
