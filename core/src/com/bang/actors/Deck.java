package com.bang.actors;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.json.*;

public class Deck {

    protected ArrayList<Card> orderedDeck;
    protected ArrayList<Integer> deckIndices;
    protected int nextCardIndex;

    public Deck() {
        this.orderedDeck = buildOrderedDeck();
        this.nextCardIndex = 0;
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

    public ArrayList<Integer> shuffleDeck() {
        this.deckIndices = randomArrayList(this.orderedDeck.size());
        return this.deckIndices;
    }

    public Card getCard(int cardIndex) {
        return this.orderedDeck.get(deckIndices.get(cardIndex));
    }

    public void setIndices(ArrayList<Integer> indices) {
        this.deckIndices = indices;
    }

    public ArrayList<Integer> getIndices() {
        return this.deckIndices;
    }

    public void setNextCardIndex(int i) {
        nextCardIndex = i;
    }

    public int getNextCardIndex() {
        return nextCardIndex;
    }

    public Card draw() {
        return getCard(nextCardIndex++);
    }
}
