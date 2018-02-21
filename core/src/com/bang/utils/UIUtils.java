package com.bang.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.bang.scenemanager.SceneManager;

public class UIUtils {

    public static int btnWidth = 200;

    public static TextButton createBtn(TextButton b, String t, float x, float y, Stage stage,
            TextButtonStyle textButtonStyle, ChangeListener cl) {

        b = new TextButton(t, textButtonStyle);
        b.setTransform(true);

        stage.addActor(b);

        int lines = StringUtils.countMatches(t, "\n") > 1 ? StringUtils.countMatches(t, "\n") : 2;

        b.setSize(200, 80 + 15 * lines);
        b.setPosition(x, y);

        b.addListener(cl);

        return b;
    }

    public static void print(String s) {
        System.out.println(s);
    }

    public static void showError(String err, Exception e, Stage s, SceneManager sm, Label t, ArrayList<Actor> actors) {
        for (Actor a: actors) {
            a.remove();
        }

        if (e != null) {
            print("Error getting lobby list\nERROR: " + e);
            e.printStackTrace();
        } else {
            print(err);
        }
        t = new Label(err, sm.getLabelStyle());
        t.setBounds(s.getWidth() / 2 - 150, s.getHeight() / 2, 300, 100);
        t.setFontScale(1f, 1f);
        t.setAlignment(Align.center);

        s.addActor(t);
    }
}
