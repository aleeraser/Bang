package com.bang.scenemanager;

import org.json.JSONArray;

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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

public class RoomListScene extends Scene {

    List<String> list;
    ScrollPane scrollPane;
    TextButton btnBack, btnJoin, btnNewLobby;
    Label text, title;

    public RoomListScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.setup();
    }

    @Override
    public void setup() {

        stage = new Stage();

        backgroundImage = null;

        // Lobby list
        list = new List<String>(sceneManager.getSkin());

        LabelStyle l = new LabelStyle();

        // TODO: aggiungere titolo delle lobby usando il font 'font-title-export.fnt'
        // title = new Label("Lobbies", sceneManager.getSkin());
        // stage.addActor(title);

        scrollPane = new ScrollPane(list);
        scrollPane.setBounds(0, 200, stage.getWidth(), 150);
        scrollPane.setTransform(true);
        scrollPane.layout();

        UIUtils.createBtn(btnBack, "Indietro", 10, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                /* Go back */
                sceneManager.setScene(new MainMenuScene(sceneManager));
            }
        });
        
        UIUtils.createBtn(btnJoin, "Entra", 210, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: implementa il join
            }
        });

        final String[] server_url = new String[3]; // only for debugging
        server_url[0] = "http://emilia.cs.unibo.it:5002";
        server_url[1] = "http://marullo.cs.unibo.it:5002";
        server_url[2] = "http://localhost:5002";
        final int server_index = 0;

        String[] lob_names = new String[0];
        try {
            JSONArray lob = new JSONArray(NetworkUtils.getHTTP(server_url[server_index] + "/list"));
            int lob_num = lob.length();
            lob_names = new String[lob_num];
            for (int i = 0; i < lob.length(); i++) {
                lob_names[i] = lob.getString(i);
            }

            list.setItems(lob_names);

            stage.addActor(scrollPane);

        } catch (Exception e) {
            UIUtils.print("Error getting lobby list\nERROR: ");
            e.printStackTrace();
            text = new Label("Errore di connessione al server", sceneManager.getLabelStyle());
            text.setBounds(stage.getWidth() / 2 - 150, stage.getHeight() / 2, 300, 100);
            text.setFontScale(1f, 1f);
            text.setAlignment(Align.center);

            title.remove();
            scrollPane.remove();
            stage.addActor(text);
        }

        final String[] lobs = lob_names;

        UIUtils.createBtn(btnNewLobby, "Nuova stanza", Gdx.graphics.getWidth() - 230, 10, stage,
                sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        sceneManager.setScene(new NewLobbyScene(sceneManager, lobs, server_url[server_index]));
                    }
                });
    }
}
