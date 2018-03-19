package com.bang.actors;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bang.utils.UIUtils;

import org.json.*;

public class Deck {

    protected ArrayList<Card> orderedDeck;
    protected ArrayList<Integer> deckIndices;
    protected int nextCardIndex;
    private ArrayList<Integer> discardPile;
    private int currentDeckSize;

    public Deck() {
        this.orderedDeck = this.buildOrderedDeck();
        this.nextCardIndex = 0;
        this.discardPile = new ArrayList<Integer>();
        this.currentDeckSize = orderedDeck.size();
    }

    protected ArrayList<Integer> randomArrayList(int n) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list;
    }

    private ArrayList<Card> buildOrderedDeck() {
        ArrayList<Card> orderedDeck = new ArrayList<Card>();

        FileHandle jsonSource = Gdx.files.internal("cardlist.json");
        JSONArray jsonList = new JSONArray(jsonSource.readString());

        Card card;

        for (int i = 0; i < jsonList.length(); i++) {
            JSONObject entry = jsonList.getJSONObject(i);
            String name = entry.getString("name");
            card = new Card(name, entry.getString("value"), entry.getInt("suit"), entry.getString("type"));            
            orderedDeck.add(card);
        }

        return orderedDeck;
    }

    public ArrayList<Integer> initialShuffle() {
        this.deckIndices = this.randomArrayList(this.orderedDeck.size());
        return this.deckIndices;
    }

    private void shuffleDeck() {
        this.currentDeckSize = this.discardPile.size();
        this.nextCardIndex = 0;
        Collections.shuffle(this.discardPile);
        this.deckIndices = this.discardPile;
        this.discardPile = new ArrayList<Integer>();
    }

    public Card getCard(int cardIndex) {
        return this.orderedDeck.get(this.deckIndices.get(cardIndex));
    }

    public void setIndices(ArrayList<Integer> indices) {
        this.deckIndices = indices;
    }

    public ArrayList<Integer> getIndices() {
        return this.deckIndices;
    }

    public void setNextCardIndex(int i) {
        this.nextCardIndex = i;
    }

    public int getNextCardIndex() {
        return this.nextCardIndex;
    }

    public Card draw() {
        if (this.nextCardIndex == this.currentDeckSize) {
            UIUtils.print("Reshuffling deck");
            shuffleDeck();
        }
        Card nextCard = this.getCard(this.nextCardIndex);
        UIUtils.print("Pescata carta " + (1 + this.nextCardIndex++) + "/" + this.currentDeckSize);
        return nextCard;
    }
    
    public void discard(int cardIndex) {
        this.discardPile.add(cardIndex);
    }

    public void setDiscardPile(ArrayList<Integer> pile) {
        this.discardPile = pile;
    }

    public ArrayList<Integer> getDiscardPile() {
        return this.discardPile;
    }
}
