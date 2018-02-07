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

    BitmapFont font;
    Skin skin;
    TextureAtlas textureAtlas;
    Batch batch;

    TextButton btnJoinLobby, btnNewLobby;
    TextButtonStyle textButtonStyle;

    Label text;
    LabelStyle textStyle;
    
	
	public MainMenuScene() {
		this.setup();
	}
	
	@Override
	public void setup() {
		
		// skinName = "default" o "visui" (o altre, se verranno aggiunte)
        String skinPath, skinBtn, skinName = "visui";

        if (skinName == "visui") {
            skinPath = "skins/visui/uiskin";
            skinBtn = "button";
        } else {
            skinPath = "skins/default/uiskin";
            skinBtn = "default-rect";
        }

        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();

        stage = new Stage();
        batch = stage.getBatch();
        font = new BitmapFont();
        textureAtlas = new TextureAtlas(Gdx.files.internal(skinPath + ".atlas"));
        skin = new Skin(Gdx.files.internal(skinPath + ".json"), textureAtlas);

        backgroundImage = new Texture(Gdx.files.internal("images/bang_logo_edit.png"));

        // Button textures
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable(skinBtn);
        textButtonStyle.down = skin.getDrawable(skinBtn + "-down");

        // Label textures
        textStyle = new LabelStyle();
        textStyle.font = font;
        textStyle.background = skin.getDrawable("textfield");

        

        UIUtils.createBtn(btnJoinLobby, "Mostra/nascondi\nstanze", 10, 10, stage, textButtonStyle, 
        		new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	/* Goto RoomListScene */
            	sceneManager.setScene(new RoomListScene());
            }
        });

        UIUtils.createBtn(btnNewLobby, "Nuova stanza", windowWidth - 210, 10, stage, textButtonStyle,
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
