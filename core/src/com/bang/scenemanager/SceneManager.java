package com.bang.scenemanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class SceneManager {

	protected GameScene gameScene;
	
	public SceneManager() {
		gameScene = null;
	}
	
	public void setScene(GameScene scene) {
		if (this.gameScene != null) this.gameScene.getStage().dispose();
		this.gameScene = scene;		
		scene.setGameManager(this);
		Gdx.input.setInputProcessor(scene.getStage());
	}

	public Stage getCurrentStage() {
		return gameScene.getStage();
	}
	
	public GameScene getCurrentScene() {
		return gameScene;
	}
	
	public Texture getCurrentBackgroundImage() {
		return gameScene.getBackgroundImage();
	}
}