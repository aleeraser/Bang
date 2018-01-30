package com.bang.game;

// For HTTP requests and stream reading
import org.apache.commons.io.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.net.*;

// JSON parsing
import org.json.*;

// libgdx libs
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
    BitmapFont font;
    Skin skin;
    TextureAtlas textureAtlas;
    
    TextButton btnJoinLobby, btnNewLobby;
    TextButtonStyle textButtonStyle;

    Label text;
    LabelStyle textStyle;

    public void createBtn(TextButton btn, String text, float x, float y, ChangeListener listener) {
        btn = new TextButton(text, textButtonStyle);

        stage.addActor(btn);
        btn.setSize(200, 80);
        btn.setPosition(x, y);

        btn.addListener(listener);
    }

    private String getHTTP(String _url) {
        String result;
        try {
            URL url = new URL(_url);
            InputStream is = url.openStream();
            result = IOUtils.toString(is, StandardCharsets.UTF_8);
            is.close();
        } catch (Exception e) {
            result = "Error while fetching lobbies";
            System.out.println(result);
        }
        return result;
    }

    int i = 10;

    @Override
    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont();
        skin = new Skin();
        textureAtlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin.addRegions(textureAtlas);

        // Button textures
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("default-rect");
        textButtonStyle.down = skin.getDrawable("default-rect-down");

        // Label textures
        textStyle = new LabelStyle();
        textStyle.font = font;
        textStyle.background = skin.getDrawable("textfield");
        
        
        createBtn(btnJoinLobby, "Mostra stanze", 10, 10, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    JSONArray lob = new JSONArray(getHTTP("http://emilia.cs.unibo.it:5002/list"));
                    String lob_names = "";
                    for (int i = 0; i < lob.length(); i++) {
                        lob_names += lob.getJSONObject(i).getString("name") + "\n";
                    }
                    
                    text = new Label(lob_names, textStyle);
                    stage.addActor(text);
                    text.setBounds(0, 0, 120, 100);
                    // text.setFontScale(1f, 1f);
                    text.setPosition(stage.getWidth() / 2 - 120 / 2, stage.getHeight() / 2);
                    lob_names = "";
                    
                } catch (Exception e) {
                    System.out.println("Error getting lobby list");
                }
            }
        });
        
        createBtn(btnNewLobby, "Nuova stanza", stage.getWidth() - 210, 10, new ChangeListener() {
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
    }
}
