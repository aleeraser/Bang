package com.bang.game;

// libgdx libs
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bang.scenemanager.GameScene;
import com.bang.scenemanager.MainMenuScene;
import com.bang.scenemanager.SceneManager;

public class Bang extends ApplicationAdapter {

    SceneManager sceneManager;

    @Override
    public void create() {
        sceneManager = new SceneManager();
        //sceneManager.setScene(new MainMenuScene(sceneManager));
        sceneManager.setScene(new GameScene(sceneManager));
        //sceneManager.setScene(new InLobbyScene(sceneManager, "TestLobby", true));
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
