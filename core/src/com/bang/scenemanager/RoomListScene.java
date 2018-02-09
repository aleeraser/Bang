package com.bang.scenemanager;

import org.json.JSONArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.bang.utils.NetworkUtils;
import com.bang.utils.UIUtils;

public class RoomListScene extends GameScene {
	
	List<String> list;
    ScrollPane scrollPane;    
    TextButton btnBack; 
    Label text;
	
	public RoomListScene(SceneManager sceneManager) {
		this.sceneManager = sceneManager;
		this.setup();
	}
	
	@Override
	public void setup() {
		
		stage = new Stage();
		
		backgroundImage = null;
	
		// Lobby list
        list = new List<String>(sceneManager.getSkin());
        scrollPane = new ScrollPane(list);
        scrollPane.setBounds(0, 0, stage.getWidth() / 2, 100);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setPosition(stage.getWidth() / 4, stage.getHeight() / 2);
        scrollPane.setTransform(true);
        
        UIUtils.createBtn(btnBack, "Indietro", 10, 10, stage, sceneManager.getTextButtonStyle(), 
        		new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	/* Go back */
            	sceneManager.setScene(new MainMenuScene(sceneManager));
            }
        });
		
		String[] server_url = new String[3];    // only for debugging
        server_url[0] = "http://emilia.cs.unibo.it:5002/list";
        server_url[1] = "http://marullo.cs.unibo.it:5002/list";
        server_url[2] = "http://localhost:5002/list";

       
        try {
            JSONArray lob = new JSONArray(NetworkUtils.getHTTP(server_url[0]));
            int lob_num = lob.length();
            String[] lob_names = new String[lob_num];
            for (int i = 0; i < lob.length(); i++) {
                lob_names[i] = lob.getJSONObject(i).getString("name") + "\n";
            }

            list.setItems(lob_names);
            backgroundImage = null;
            
            stage.addActor(scrollPane);

        } catch (Exception e) {
            print("Error getting lobby list\nERROR: ");
            e.printStackTrace();
            text = new Label("Errore di connessione al server", sceneManager.getTextStyle());
            text.setBounds(stage.getWidth()/2 - 150, stage.getHeight()/2, 300, 100);
            text.setFontScale(1f,1f);
            text.setAlignment(Align.center);
            stage.addActor(text);
        }
    }
	

	private void print(String s) {
	    System.out.println(s);
	}
}
