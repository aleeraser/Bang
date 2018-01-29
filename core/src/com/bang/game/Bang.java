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
    createBtn(button2, "btn2", stage.getWidth()-210, 10, new ChangeListener() {

      @Override
      public void changed(ChangeEvent event, Actor actor) {
        System.out.println("Click2");
      }
    }); 
    
    createBtn(button, "btn1", 10, 10, new ChangeListener() {

      @Override
      public void changed(ChangeEvent event, Actor actor) {
        System.out.println("Click1");
      }
    });

  }
	@Override
	public void render () {
    Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    super.render();
    stage.draw();
	}
	
	@Override
	public void dispose () {
  }
  
  public void createBtn(TextButton btn, String text, float x, float y, ChangeListener listener){
    btn = new TextButton(text, textButtonStyle);
    
    stage.addActor(btn);
    btn.setSize(200, 80);
    btn.setPosition(x, y);

    btn.addListener(listener);
    
  }
}
