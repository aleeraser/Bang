package com.bang.gameui;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class txtArea {
	
	protected List<String> paneList;
	protected ArrayList<String> eventList;
	protected String[] strList;
	protected TextArea textArea;
	
	
	public txtArea(Skin skin) {
		paneList = new List<String>(skin);
		eventList = new ArrayList<String>();
		
		textArea = new TextArea("...",skin);
		
		

		/*Texture scrollTexture = new Texture(Gdx.files.internal("images/bang_logo_edit.png"));
		NinePatch scrollNine = new NinePatch(new TextureRegion(scrollTexture,6,6),2,2,2,2);
		scrollStyle = new ScrollPane.ScrollPaneStyle();
		scrollStyle.vScrollKnob = new NinePatchDrawable(box);
		scrollPane = new ScrollPane(paneList, scrollStyle);*/
		
		strList = new String[0];
		paneList.setItems(eventList.toArray(new String[0]));
		textArea.setBounds(200, 200, 200, 200);
        textArea.layout();
	}
	
	public void addEvent(String eventStr) {
		 eventList.add(eventStr);
		 eventList.add(" ");
		 if (paneList != null) {
			 try{
			 paneList.setItems(eventList.toArray(new String[0]));
			 paneList.layout();
			 }
			 catch (Exception e ){
				 System.out.println("----------------------> catched");
			 }
		 }
	}
	
	public void setSize(float width, float height) {
		textArea.setSize(width, height);
	}
	
	public void setPosition(float x, float y) {
		textArea.setPosition(x, y);
	}
	
	public TextArea getPane() {
		return textArea;
	}

}
