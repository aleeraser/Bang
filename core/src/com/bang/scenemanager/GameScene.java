package com.bang.scenemanager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScene {

	protected Stage stage;
	protected SceneManager sceneManager;
	protected Texture backgroundImage;
	
	public GameScene() {
		backgroundImage = null;
		stage = null;
		this.setup();
	}
	
	public void setup() {}
	
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
