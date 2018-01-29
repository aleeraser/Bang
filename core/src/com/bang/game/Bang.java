package com.bang.game;

import java.io.*;
import java.net.*;
import org.json.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.graphics.GL20;

public class Bang extends ApplicationAdapter {
    Stage stage;
    TextButton button, button2;
    TextButtonStyle textButtonStyle;
    BitmapFont font;
    Skin skin;
    TextureAtlas buttonAtlas;
    JSONArray lobbies;

    // TODO: test dovrebbe essere la lista delle lobby attualmente sul server
    String test = "";

    Label text;
    LabelStyle textStyle;

    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    @Override
    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont();
        skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin.addRegions(buttonAtlas);
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("default-rect");
        textButtonStyle.down = skin.getDrawable("default-rect-down");
        //textButtonStyle.checked = skin.getDrawable("checked-button");
        button = new TextButton("Get Lobbies", textButtonStyle);
        button2 = new TextButton("Button2", textButtonStyle);
        stage.addActor(button);
        stage.addActor(button2);
        button.setSize(200, 80);
        button2.setSize(200, 80);
        button2.setPosition(stage.getWidth() - 200, 0);

        button.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    // TODO: aspetta/controlla che il server abbia risposto completamente
                    lobbies = new JSONArray(getHTML("http://emilia.cs.unibo.it:5002/list"));
                    for (int i = 0; i < lobbies.length(); i++) {
                        test += lobbies.getJSONObject(i).getString("name") + "\n";
                        System.out.println(lobbies.getJSONObject(i).getString("name"));
                    }

                    // TODO: questo codice aggiorna la lista delle lobby
                    textStyle = new LabelStyle();
                    textStyle.font = font;
                    textStyle.background = skin.getDrawable("default-rect");
                    text = new Label(test, textStyle);
                    stage.addActor(text);
                    text.setBounds(-100.0f, 30, 100, 100);
                    text.setFontScale(1f, 1f);
                    text.setPosition(stage.getWidth() / 2 - 100 / 2, stage.getHeight() / 2);
                    test = "";

                } catch (Exception e) {
                    System.out.println("Error getting lobby list");
                }
            }
        });

        button2.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Click2");
            }
        });
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
        stage.draw();
    }

    @Override
    public void dispose() {
    }
}
