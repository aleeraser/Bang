package com.bang.scenemanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class SceneManager {

    protected GameScene gameScene;
    protected BitmapFont font;
    protected Skin skin;
    protected TextureAtlas textureAtlas;
    protected Batch batch;
    protected TextButtonStyle textButtonStyle;
    protected LabelStyle labelStyle;
    protected TextFieldStyle textFieldStyle;

    public SceneManager() {
        gameScene = null;

        // skinName = "default" o "visui" (o altre, se verranno aggiunte)
        String skinPath, skinBtn, skinName = "rusty-robot";

        if (skinName == "default") {
            skinPath = "skins/default/uiskin";
            skinBtn = "default-rect";
        } else if (skinName == "rusty-robot") {
            skinPath = "skins/rusty-robot/rusty-robot-ui";
            skinBtn = "button";
        } else {
            skinPath = "skins/visui/uiskin";
            skinBtn = "button";
        }

        font = new BitmapFont();
        textureAtlas = new TextureAtlas(Gdx.files.internal(skinPath + ".atlas"));
        skin = new Skin(Gdx.files.internal(skinPath + ".json"), textureAtlas);

        // Button textures
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable(skinBtn);
        textButtonStyle.down = skin.getDrawable(skinBtn + "-pressed");

        // Label textures
        labelStyle = new LabelStyle();
        labelStyle.font = font;
        labelStyle.background = skin.getDrawable("label-bg");

        // Textfield textures
        textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = new Color(0, 0, 0, 1);
        textFieldStyle.background = skin.getDrawable("textfield");
    }

    public void setScene(GameScene scene) {
        if (this.gameScene != null)
            this.gameScene.getStage().dispose();
        this.gameScene = scene;
        //scene.setGameManager(this);
        Gdx.input.setInputProcessor(scene.getStage());
    }

    public Stage getCurrentStage() {
        return gameScene.getStage();
    }

    public GameScene getCurrentScene() {
        return gameScene;
    }

    public Texture getCurrentBackgroundImage() {
        return gameScene.getBackgroundImage();
    }

    public Skin getSkin() {
        return skin;
    }

    public TextButtonStyle getTextButtonStyle() {
        return textButtonStyle;
    }

    public LabelStyle getLabelStyle() {
        return labelStyle;
    }

    public TextFieldStyle getTextfieldStyle() {
        return textFieldStyle;
    }
}