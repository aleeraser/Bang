package com.bang.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class UIUtils {
	
	public static TextButton createBtn(TextButton b, String t, float x, float y, 
			Stage stage, TextButtonStyle textButtonStyle, ChangeListener cl) {
        b = new TextButton(t, textButtonStyle);

        stage.addActor(b);
        b.setSize(200, 80);
        b.setPosition(x, y);

        b.addListener(cl);

        return b;
    }
	
}
