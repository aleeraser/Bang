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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bang.scenemanager.MainMenuScene;
import com.bang.scenemanager.SceneManager;
import com.bang.scenemanager.TestBangGameScene;
// Utils
import com.bang.utils.CardsUtils;

import java.util.Map;
import java.util.HashMap;

public class Bang extends ApplicationAdapter {

	SceneManager sceneManager;
	
    @Override
    public void create() {
        sceneManager = new SceneManager();
        sceneManager.setScene(new TestBangGameScene(sceneManager));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        Stage s = sceneManager.getCurrentStage();
        Texture bg = sceneManager.getCurrentBackgroundImage();
        Batch batch = s.getBatch();
        
        if (bg != null) {
            batch.begin();
            batch.draw(bg, 0, 0, s.getWidth(), s.getHeight());
            batch.end();
        }

        super.render();
        s.draw();
    }

    @Override
    public void dispose() {
        /*if (sceneManager.getCurrentStage() != null) {
			sceneManager.getCurrentStage().dispose();
		}*/
    }
}
