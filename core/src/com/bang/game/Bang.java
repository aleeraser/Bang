package com.bang.game;

// For HTTP requests and stream reading
import org.apache.commons.io.*;

import java.nio.charset.StandardCharsets;

import java.io.*;
import java.net.*;

// JSON parsing
import org.json.*;

// libgdx libs
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

// Utils
import com.bang.utils.CardsUtils;
import java.util.Map;
import java.util.HashMap;

public class Bang extends ApplicationAdapter {
    float gameWidth;
    float gameHeight;

    Stage stage;
    BitmapFont font;
    Skin skin;
    TextureAtlas textureAtlas;

    TextButton btnJoinLobby, btnNewLobby;
    TextButtonStyle textButtonStyle;

    Label text;
    LabelStyle textStyle;

    List<String> list;

    ScrollPane scrollPane;
    
    public void createBtn(TextButton b, String t, float x, float y, ChangeListener cl) {
        b = new TextButton(t, textButtonStyle);

        stage.addActor(b);
        b.setSize(200, 80);
        b.setPosition(x, y);

        b.addListener(cl);
    }

    private String getHTTP(String _url) {
        String result;
        try {
            URL url = new URL(_url);
            InputStream is = url.openStream();
            result = IOUtils.toString(is, StandardCharsets.UTF_8);
            is.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data from HTTP request (GET).", e);
        }
        return result;
    }

    private void print(String s) {
        System.out.println(s);
    }
    @Override
    public void create() {
        // skinName = "default" o "visui" (o altre, se verranno aggiunte)
        String skinPath,
               skinBtn,
               skinName = "visui";

        if (skinName == "visui") {
            skinPath = "skins/visui/uiskin";
            skinBtn = "button";
        } else {
            skinPath = "skins/default/uiskin";
            skinBtn = "default-rect";
        }

        gameWidth = Gdx.graphics.getWidth();
        gameHeight = Gdx.graphics.getHeight();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont();
        textureAtlas = new TextureAtlas(Gdx.files.internal(skinPath + ".atlas"));
        skin = new Skin(Gdx.files.internal(skinPath + ".json"), textureAtlas);

        // Example of Bang card creation
        Group g = CardsUtils.createCardImageGroup(CardsUtils.CARD_BANG, CardsUtils.CARD_ACE, CardsUtils.SUIT_CLUBS,
                200);
        g.setPosition(20, stage.getHeight() - 250);
        stage.addActor(g);

        // Button textures
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable(skinBtn);
        textButtonStyle.down = skin.getDrawable(skinBtn + "-down");

        // Label textures
        textStyle = new LabelStyle();
        textStyle.font = font;
        textStyle.background = skin.getDrawable("textfield");

        // Lobby list
        list = new List<String>(skin);
        scrollPane = new ScrollPane(list);
        scrollPane.setBounds(0, 0, gameWidth / 2, 100);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setPosition(gameWidth / 4, stage.getHeight() / 2);
        scrollPane.setTransform(true);

        stage.addActor(scrollPane);
        
        createBtn(btnJoinLobby, "Mostra stanze", 10, 10, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    // JSONArray lob = new JSONArray(getHTTP("http://emilia.cs.unibo.it:5002/list"));
                    JSONArray lob = new JSONArray(getHTTP("http://localhost:5002/list"));
                    int lob_num = lob.length();
                    String[] lob_names = new String[lob_num];
                    for (int i = 0; i < lob.length(); i++) {
                        lob_names[i] = lob.getJSONObject(i).getString("name") + "\n";
                    }

                    list.setItems(lob_names);

                } catch (Exception e) {
                    print("Error getting lobby list\nERROR: ");
                    e.printStackTrace();
                }
            }
        });

        createBtn(btnNewLobby, "Nuova stanza", gameWidth - 210, 10, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: implementare creazione nuova stanza
            }
        });
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        textureAtlas.dispose();
    }
}
