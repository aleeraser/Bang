package com.bang.scenemanager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Scene {

    protected Stage stage;
    protected Batch batch;
    protected SceneManager sceneManager;
    protected Texture backgroundImage;

    public Scene() {
        backgroundImage = null;
        stage = null;
        //this.setup();
    }

    public Scene(SceneManager sceneManager) {
        backgroundImage = null;
        stage = null;
        this.sceneManager = sceneManager;
        this.setup();
    }

    public void setup() {
    }

    public Stage getStage() {
        return stage;
    }

    public void setGameManager(SceneManager gm) {
        this.sceneManager = gm;
    }

    public void setBackgroundImage(Texture background) {
        this.backgroundImage = background;
    }

    public Texture getBackgroundImage() {
        return this.backgroundImage;
    }
}
