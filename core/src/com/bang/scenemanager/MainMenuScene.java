package com.bang.scenemanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bang.utils.UIUtils;

public class MainMenuScene extends Scene {

    TextButton btnStart;

    Label text;

    public MainMenuScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.setup();
    }

    @Override
    public void setup() {

        stage = new Stage();
        batch = stage.getBatch();

        backgroundImage = new Texture(Gdx.files.internal("images/bang_logo_edit.png"));

        btnStart = UIUtils.createBtn("Inizia!", Gdx.graphics.getWidth() / 2 - UIUtils.btnWidth / 2, 10,
                stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        /* Goto RoomListScene */
                        sceneManager.setScene(new RoomListScene(sceneManager));
                    }
                });
    }
}
