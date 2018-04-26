package com.bang.scenemanager;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bang.actors.Card;
import com.bang.actors.IPlayer;
import com.bang.utils.CardsUtils;

public class GeneralStoreCardDialog extends Dialog {

    protected SceneManager sceneManager;
    protected ArrayList<Card> storeCards;

    public GeneralStoreCardDialog(SceneManager sceneManager, ArrayList<Card> storeCards, int turn) {
        super("Emporio", sceneManager.getSkin(), "dialog");

        this.sceneManager = sceneManager;
        this.storeCards = storeCards;

        setup();
    }

    protected void setup() {
        IPlayer me = sceneManager.getPlayer();

        try {
            if (me.isMyTurn()) {
                this.text("E' il mio turno per scegliere");
            } else {
                this.text("Deve scegliere " + me.getPlayers().get(me.getTurnOwner()).getCharacter().getName());
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        int cardsNum = storeCards.size();
        int widthTot = 600;
        float cardWidth;
        if (cardsNum > 0) {
            cardWidth = (float) ((widthTot / cardsNum) * 0.9);
        } else
            cardWidth = 180;
        if (cardWidth > 180)
            cardWidth = 180;

        float cardHeight = (float) (cardWidth / CardsUtils.CARD_HEIGHT_WIDTH_RATIO);

        System.out.println(cardWidth + " - " + cardHeight);

        for (int i = 0; i < storeCards.size(); i++) {
            Drawable drawable = new TextureRegionDrawable(
                    new TextureRegion(CardsUtils.getCardTexture(storeCards.get(i))));
            drawable.setMinHeight(cardHeight);
            drawable.setMinWidth(cardWidth);
            ImageButton b = new ImageButton(drawable);
            b.setTransform(true);
            b.setSize(cardWidth, cardHeight);
            this.button(b, i);
        }
    }

}
