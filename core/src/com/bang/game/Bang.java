package com.bang.game;

import java.rmi.RemoteException;

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
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

import org.json.JSONObject;

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
        
        /* To allow scrolling */
        s.act();

        if (sceneManager.isInGame()) {
            try {
				sceneManager.getPlayer().checkTimeout(System.currentTimeMillis());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
        }
    }

    @Override
    public void dispose() {
        /*if (sceneManager.getCurrentStage() != null) {
        	sceneManager.getCurrentStage().dispose();
        }*/

        // If the user is currently in a lobby, attempt to remove it from the lobby's players list
        try {
            if (sceneManager.getCurrentLobby() != null) {
                String[] params = new String[2];
                params[0] = "ip";
                params[1] = "lobby";

                String[] vals = new String[2];
                vals[0] = sceneManager.getPlayer().getIp();
                vals[1] = sceneManager.getCurrentLobby();

                JSONObject res = NetworkUtils.postHTTP(NetworkUtils.getBaseURL() + "/remove_player", params, vals);
                if (res.getInt("code") != 0) {
                    UIUtils.print("WARNING: Failed to remove player from lobby!");
                }
            }
        } catch (RemoteException e) {
            UIUtils.print("WARNING: Failed to remove player from lobby!");
            e.printStackTrace();
        } catch (Exception e) {
            UIUtils.print("WARNING: Failed to remove player from lobby!");
        }
    }
}
