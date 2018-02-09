package com.bang.utils;

import org.apache.commons.lang3.StringUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class UIUtils {

    public static TextButton createBtn(TextButton b, String t, float x, float y, Stage stage,
            TextButtonStyle textButtonStyle, ChangeListener cl) {

        b = new TextButton(t, textButtonStyle);
        b.setTransform(true);

        stage.addActor(b);

        int lines = StringUtils.countMatches(t, "\n") > 1 ? StringUtils.countMatches(t, "\n") : 2;

        b.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        b.setPosition(x, y);

        b.addListener(cl);

        return b;
    }

    public static void print(String s) {
        System.out.println(s);
    }

}
