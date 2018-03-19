package com.bang.scenemanager;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.bang.actors.IPlayer;
import com.bang.actors.Player;

public class SceneManager {

    protected Scene gameScene;
    protected BitmapFont font;
    protected Skin skin;
    protected TextureAtlas textureAtlas;
    protected Batch batch;
    protected TextButtonStyle textButtonStyle;
    protected LabelStyle labelStyle;
    protected TextFieldStyle textFieldStyle;
    protected IPlayer player;
    protected String currentLobby;
    protected boolean inGame;
    protected boolean inLobbyScene;

    public SceneManager() {
        gameScene = null;

        try {
            LocateRegistry.createRegistry(1099);
            player = new Player();
            Naming.rebind("//" + player.getIp() + "/Player", player);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String skinPath = "skins/rusty-robot/rusty-robot-ui";

        font = new BitmapFont();
        textureAtlas = new TextureAtlas(Gdx.files.internal(skinPath + ".atlas"));
        skin = new Skin(Gdx.files.internal(skinPath + ".json"), textureAtlas);

        // Button textures
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("button");
        textButtonStyle.down = skin.getDrawable("button-pressed");

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

    public void setScene(Scene scene) {
        if (this.gameScene != null) {
            this.gameScene.getStage().dispose();
        }
        this.gameScene = scene;
        //scene.setGameManager(this);
        Gdx.input.setInputProcessor(scene.getStage());
    }

    public Stage getCurrentStage() {
        return gameScene.getStage();
    }

    public Scene getCurrentScene() {
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

    public IPlayer getPlayer() {
        return player;
    }

    public void setCurrentLobby(String l) {
        this.currentLobby = l;
    }

    public String getCurrentLobby() {
        return currentLobby;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean value) {
        this.inGame = value;
    }

    public boolean isInLobbyScene() {
        return inLobbyScene;
    }

    public void setInLobbyScene(boolean value) {
        this.inLobbyScene = value;
    }
}