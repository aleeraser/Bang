package com.bang.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.graphics.GL20;

public class Bang extends ApplicationAdapter {
  Stage stage;
  TextButton button, button2;
  TextButtonStyle textButtonStyle;
  BitmapFont font;
  Skin skin;
  TextureAtlas buttonAtlas;

	@Override
	public void create () {
		stage = new Stage();
    Gdx.input.setInputProcessor(stage);
    font = new BitmapFont();
    skin = new Skin();
    buttonAtlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
    skin.addRegions(buttonAtlas);
    textButtonStyle = new TextButtonStyle();
    textButtonStyle.font = font;
    textButtonStyle.up = skin.getDrawable("default-rect");
    textButtonStyle.down = skin.getDrawable("default-rect-down");
    //textButtonStyle.checked = skin.getDrawable("checked-button");
    button = new TextButton("Button1", textButtonStyle);
    button2 = new TextButton("Button2", textButtonStyle);
    stage.addActor(button);
    stage.addActor(button2);
    button.setSize(200, 80);
    button2.setSize(200, 80);
    button2.setPosition(stage.getWidth()-200, 0);

    button.addListener(new ChangeListener() {
			
	     @Override
       public void changed(ChangeEvent event, Actor actor) {
		       System.out.println("Click");				
		   }
    });
    
    button2.addListener(new ChangeListener() {

      @Override
      public void changed(ChangeEvent event, Actor actor) {
        System.out.println("Click2");
      }
    });
	}

	@Override
	public void render () {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    super.render();
    stage.draw();
	}
	
	@Override
	public void dispose () {
	}
}
