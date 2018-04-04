package com.bang.gameui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class LogBox {
	
	protected List<String> paneList;
	protected ArrayList<String> eventList;
	protected String[] strList;
	protected ScrollPane scrollPane;
	
	
	public LogBox(Skin skin) {
		paneList = new List<String>(skin);
		eventList = new ArrayList<String>();
		scrollPane = new ScrollPane(paneList);
		
		ScrollPane.ScrollPaneStyle scrollStyle;

		/*Texture scrollTexture = new Texture(Gdx.files.internal("images/bang_logo_edit.png"));
		NinePatch scrollNine = new NinePatch(new TextureRegion(scrollTexture,6,6),2,2,2,2);
		scrollStyle = new ScrollPane.ScrollPaneStyle();
		scrollStyle.vScrollKnob = new NinePatchDrawable(box);
		scrollPane = new ScrollPane(paneList, scrollStyle);*/
		
		strList = new String[0];
		paneList.setItems(eventList.toArray(new String[0]));
		scrollPane.setBounds(200, 200, 200, 200);
        scrollPane.setTransform(true);
        scrollPane.layout();
	}
	
	public void addEvent(String eventStr) {
		 eventList.add(eventStr);
		 if (paneList != null) {
			 try{
			 paneList.setItems(eventList.toArray(new String[0]));
			 paneList.layout();
			 }
			 catch (Exception e ){
				 System.out.println("----------------------> catched");
			 }
		 }
		 if (scrollPane != null) {
			 scrollPane.setScrollPercentY(100);
		 }
	}
	
	public void setSize(float width, float height) {
		scrollPane.setSize(width, height);
	}
	
	public void setPosition(float x, float y) {
		scrollPane.setPosition(x, y);
	}
	
	public ScrollPane getPane() {
		return scrollPane;
	}

}
