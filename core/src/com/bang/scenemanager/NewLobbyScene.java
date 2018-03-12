package com.bang.scenemanager;

import org.json.JSONObject;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

public class NewLobbyScene extends Scene {
    
    TextButton btnBack, btnNewLobby;
    Label text;
    String[] lob_names;
    String server_url;
    Label sceneTitle;
    TextField lobbyName;
    final ArrayList<Actor> removeOnError = new ArrayList<Actor>();
    
    public NewLobbyScene(SceneManager sceneManager, String[] lobs) {
        this.sceneManager = sceneManager;
        lob_names = lobs;
        server_url = NetworkUtils.getBaseURL();
        
        this.setup();
    }

    @Override
    public void setup() {
        stage = new Stage();

        
        backgroundImage = null;
        
        sceneTitle = new Label("Inserisci il nome della stanza", sceneManager.getLabelStyle());
        sceneTitle.setBounds(stage.getWidth() / 2 - 150, stage.getHeight() / 2 + 50, 300, 100);
        sceneTitle.setFontScale(1.1f, 1.1f);
        sceneTitle.setAlignment(Align.center);
        stage.addActor(sceneTitle);
        removeOnError.add(sceneTitle);
        
        lobbyName = new TextField("", sceneManager.getSkin());
        lobbyName.setStyle(sceneManager.getTextfieldStyle());
        lobbyName.setBounds(stage.getWidth() / 6, stage.getHeight() / 2 - 50, stage.getWidth() / 3 * 2, 80);
        lobbyName.setAlignment(Align.center);
        stage.addActor(lobbyName);
        removeOnError.add(lobbyName);
        
        // Back
        btnBack = UIUtils.createBtn("Indietro", 10, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sceneManager.setScene(new RoomListScene(sceneManager));
            }
        });
        
        // New lobby
        btnNewLobby = UIUtils.createBtn("Crea", Gdx.graphics.getWidth() - 230, 10, stage, sceneManager.getTextButtonStyle(),
        new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    String[] params = new String[2];
                    params[0] = "ip";
                    params[1] = "lobby";
                    
                    String[] vals = new String[2];
                    vals[0] = sceneManager.getPlayer().getIp();
                    vals[1] = lobbyName.getText();
                    
                    JSONObject res = NetworkUtils.postHTTP(server_url + "/new", params, vals);
                    if (res.getInt("code") == 1) { // nome già presente
                        UIUtils.showError(res.getString("msg"), null, stage, sceneManager, text, removeOnError);
                    } else {
                        sceneManager.setScene(new InLobbyScene(sceneManager, lobbyName.getText(), true));
                    }
                    
                } catch (Exception e) {
                    UIUtils.showError("Errore di connessione al server", e, stage, sceneManager, text,
                    removeOnError);
                }
            }
        });

    }
}
