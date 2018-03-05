package com.bang.actors;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.json.*;

public class Deck {
    
    private ArrayList<Card> orderedDeck;
    
    public Deck() {
        orderedDeck = buildOrderedDeck();
    }

    private ArrayList<Integer> randomArrayList(int n) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list;
    }

    public ArrayList<Card> buildOrderedDeck() {
        ArrayList<Card> orderedDeck = new ArrayList<Card>();

        FileHandle jsonSource = Gdx.files.internal("cardlist.json");
        JSONArray jsonList = new JSONArray(jsonSource.readString());

        Card card;

        for (int i = 0; i < jsonList.length(); i++) {
            JSONObject entry = jsonList.getJSONObject(i);
            String name = entry.getString("name");
            if (name.matches("bang") || name.matches("panico") || name.matches("catbalou") || name.matches("duello") 
                    || name.matches("prigione") ){
                card = new Card(name, entry.getString("value"), entry.getInt("suit"), true);    
            }
            else{
                card = new Card(name, entry.getString("value"), entry.getInt("suit"));
            }
            orderedDeck.add(card);
        }

        return orderedDeck;
    }

    public ArrayList<Integer> shuffleDeck() {
        ArrayList<Integer> deckIndices = randomArrayList(orderedDeck.size());
        return deckIndices;
    }

    public Deck getDeck() {
        return this;
    }

    public Card getCard(int cardIndex) {
        return orderedDeck.get(cardIndex);
    }
}
