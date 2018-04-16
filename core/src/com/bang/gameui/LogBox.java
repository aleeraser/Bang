package com.bang.gameui;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LogBox {

    protected List<String> paneList;
    protected ArrayList<String> eventList;
    protected String[] strList;
    protected ScrollPane scrollPane;
    protected Semaphore semaphore;

    public LogBox(Skin skin) {
        paneList = new List<String>(skin);
        eventList = new ArrayList<String>();

        ScrollPaneStyle sps = new ScrollPaneStyle();
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
        
        semaphore = new Semaphore(1);
    }

    public void addEvent(String eventStr) {
        eventList.add(eventStr);
        eventList.add(" ");
        try {
			semaphore.acquire(1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        if (paneList != null) {
            try {
                paneList.setItems(eventList.toArray(new String[0]));
                paneList.layout();
            } catch (Exception e) {
                System.out.println("----------------------> catched");
                semaphore.release(1);
            }
        }
        if (scrollPane != null) {
            scrollPane.setScrollPercentY(200);
        }
        semaphore.release(1);
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
