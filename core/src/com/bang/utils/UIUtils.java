package com.bang.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.bang.scenemanager.SceneManager;

public class UIUtils {

    public static int btnWidth = 200;

    public static TextButton createBtn(String t, float x, float y, Stage stage, TextButtonStyle textButtonStyle,
            ChangeListener cl) {

        TextButton b = new TextButton(t, textButtonStyle);
        b.setTransform(true);

        stage.addActor(b);

        int lines = StringUtils.countMatches(t, "\n") > 1 ? StringUtils.countMatches(t, "\n") : 2;

        b.setSize(200, 80 + 15 * lines);
        b.setPosition(x, y);

        b.addListener(cl);

        return b;
    }

    public static void enable(TextButton b) {
        b.setTouchable(Touchable.enabled);

        for (Actor c : b.getChildren()) {
            if (c instanceof Image) {
                c.remove();
            }
        }
    }

    public static void disable(TextButton b) {
        b.setTouchable(Touchable.disabled);

        Image btnBlock = new Image(new Texture(Gdx.files.internal("images/divieto2.png")));
        btnBlock.setPosition(b.getWidth() / 2 - btnBlock.getWidth() / 4, b.getHeight() / 2 - btnBlock.getWidth() / 2);
        b.addActor(btnBlock);
    }

    public static void print(String s) {
        System.out.println(s);
    }

    public static void showError(String err, Exception e, Stage s, SceneManager sm, Label t, ArrayList<Actor> actors) {
        for (Actor a : actors) {
            a.remove();
        }

        if (e != null) {
            print("Error getting lobby list\nERROR: " + e);
            //e.printStackTrace();
        } else {
            print(err);
        }
        t = new Label(err, sm.getLabelStyle());
        t.setBounds(s.getWidth() / 2 - 150, s.getHeight() / 2, 300, 100);
        t.setFontScale(1f, 1f);
        t.setAlignment(Align.center);

        s.addActor(t);
    }

    public static void setCursor(String cursorImagePath) {
        Pixmap pm = new Pixmap(Gdx.files.internal(cursorImagePath));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 1, 1));
        pm.dispose();
    }

    public static void restoreDefaultCursor() {
        SystemCursor sc = SystemCursor.Arrow;
        Gdx.graphics.setSystemCursor(sc);
    }
}
