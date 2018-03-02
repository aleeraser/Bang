package com.bang.gameui;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LogBox {
	
	protected List<String> paneList;
	protected ArrayList<String> eventList;
	protected String[] strList;
	protected ScrollPane scrollPane;
	
	
	public LogBox(Skin skin) {
		paneList = new List<String>(skin);
		eventList = new ArrayList<String>();
		scrollPane = new ScrollPane(paneList);
		
		strList = new String[0];
		paneList.setItems(eventList.toArray(new String[0]));
		scrollPane.setBounds(200, 200, 200, 200);
        scrollPane.setTransform(true);
        scrollPane.layout();
	}
	
	public void addEvent(String eventStr) {
		 eventList.add(eventStr);
		 paneList.setItems(eventList.toArray(new String[0]));
		 paneList.layout();
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
