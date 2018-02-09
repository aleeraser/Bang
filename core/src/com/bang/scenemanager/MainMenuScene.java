package com.bang.scenemanager;

import org.json.JSONArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

public class MainMenuScene extends GameScene {

	float windowWidth;
    float windowHeight;

    TextButton btnJoinLobby, btnNewLobby;
    
	
	public MainMenuScene(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		this.setup();
	}
	
	@Override
	public void setup() {

        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();

        stage = new Stage();
        batch = stage.getBatch();

        backgroundImage = new Texture(Gdx.files.internal("images/bang_logo_edit.png"));       

        UIUtils.createBtn(btnJoinLobby, "Mostra/nascondi\nstanze", 10, 10, stage, sceneManager.getTextButtonStyle(), 
        		new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	/* Goto RoomListScene */
            	sceneManager.setScene(new RoomListScene(sceneManager));
            }
        });

        UIUtils.createBtn(btnNewLobby, "Nuova stanza", windowWidth - 210, 10, stage, sceneManager.getTextButtonStyle(),
        		new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: implementare creazione nuova stanza
            }
        });
	}
	
    private void print(String s) {
        System.out.println(s);
    }
}
