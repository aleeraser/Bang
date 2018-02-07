package com.bang.scenemanager;

import org.json.JSONArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bang.utils.NetworkUtils;

public class RoomListScene extends GameScene {

	Skin skin;
	TextureAtlas textureAtlas;
	List<String> list;
    ScrollPane scrollPane;
    
	
	public RoomListScene() {
		this.setup();
	}
	
	@Override
	public void setup() {
		
		stage = new Stage();
		
		// skinName = "default" o "visui" (o altre, se verranno aggiunte)
        String skinPath, skinBtn, skinName = "visui";

        if (skinName == "visui") {
            skinPath = "skins/visui/uiskin";
            skinBtn = "button";
        } else {
            skinPath = "skins/default/uiskin";
            skinBtn = "default-rect";
        }
		
		textureAtlas = new TextureAtlas(Gdx.files.internal(skinPath + ".atlas"));
		skin = new Skin(Gdx.files.internal(skinPath + ".json"), textureAtlas);
		
		// Lobby list
        list = new List<String>(skin);
        scrollPane = new ScrollPane(list);
        scrollPane.setBounds(0, 0, stage.getWidth() / 2, 100);
        scrollPane.setSmoothScrolling(false);
        scrollPane.setPosition(stage.getWidth() / 4, stage.getHeight() / 2);
        scrollPane.setTransform(true);
		
		String[] server_url = new String[3];    // only for debugging
        server_url[0] = "http://emilia.cs.unibo.it:5002/list";
        server_url[1] = "http://marullo.cs.unibo.it:5002/list";
        server_url[2] = "http://localhost:5002/list";

        if (backgroundImage != null) {
            try {
                JSONArray lob = new JSONArray(NetworkUtils.getHTTP(server_url[1]));
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
            }
        } else {
            backgroundImage = new Texture(Gdx.files.internal("images/bang_logo_edit.png"));
            //currentlyDrawnStage.addActor(g);
            scrollPane.remove();
        }
    }
	

	private void print(String s) {
	    System.out.println(s);
	}
}
