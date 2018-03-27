package com.bang.scenemanager;

import java.rmi.RemoteException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

public class OptionsScene extends Scene {

    TextButton btnBack;
    TextButton btnSave;
    TextField serverName;
    Label serverLabel;
    Label ipLabel;
    List<String> ipList;
    ScrollPane scrollPane;

    Label text;

    public OptionsScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.setup();
    }

    @Override
    public void setup() {

        stage = new Stage();
        batch = stage.getBatch();

        backgroundImage = null;
        
        // Back
        btnBack = UIUtils.createBtn("Indietro", 10, 10, stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sceneManager.setScene(new MainMenuScene(sceneManager));
                try {
					sceneManager.getPlayer().setIp(ipList.getSelected());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        });
        
        btnSave = UIUtils.createBtn("Salva", Gdx.graphics.getWidth() / 2 - UIUtils.btnWidth / 2, 10,
                stage, sceneManager.getTextButtonStyle(), new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        NetworkUtils.setBaseURL(serverName.getText());
                        sceneManager.setScene(new MainMenuScene(sceneManager));
                    }
                });
        
        // Server label
        serverLabel = new Label("Nome del server", sceneManager.getLabelStyle());
        serverLabel.setBounds(stage.getWidth() / 2 - 150, stage.getHeight() / 2 + 220, 300, 60);
        serverLabel.setFontScale(1.1f, 1.1f);
        serverLabel.setAlignment(Align.center);
        stage.addActor(serverLabel);
        
        // Server name textfield
        serverName = new TextField(NetworkUtils.getBaseURL(), sceneManager.getSkin());
        serverName.setStyle(sceneManager.getTextfieldStyle());
        serverName.setBounds(stage.getWidth() / 6, stage.getHeight() / 2 + 140, stage.getWidth() / 3 * 2, 80);
        serverName.setAlignment(Align.center);
        stage.addActor(serverName);
        
        // IP label
        ipLabel = new Label("Indirizzo IP", sceneManager.getLabelStyle());
        ipLabel.setBounds(stage.getWidth() / 2 - 150, stage.getHeight() / 2 + 20, 300, 60);
        ipLabel.setFontScale(1.1f, 1.1f);
        ipLabel.setAlignment(Align.center);
        stage.addActor(ipLabel);
        
        ipList = new List<String>(sceneManager.getSkin());

        scrollPane = new ScrollPane(ipList);
        scrollPane.setBounds(0, 200, stage.getWidth(), 150);
        scrollPane.setTransform(true);
        scrollPane.layout();
        
        String[] ips = NetworkUtils.findAllIps().toArray(new String[0]);
        
        ipList.setItems(ips);

        stage.addActor(scrollPane);
    }
}
