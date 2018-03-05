package com.bang.actors;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.json.*;

public class Deck {
    
    private ArrayList<Card> deck;
    
    public Deck(int cardsNumber) {
        ArrayList<Card> orderedCardList = buildOrderdList();
    
        // init deck object
        ArrayList<Integer> deckIndices = randomArrayList(cardsNumber);
    
        for (int i = 0; i < cardsNumber; i++) {
            deck.add(orderedCardList.get(deckIndices.get(i)));
        }
    }

    private ArrayList<Integer> randomArrayList(int n) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list;
    }

    private ArrayList<Card> buildOrderdList() {
        ArrayList<Card> orderedCardList = new ArrayList<Card>();

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
            orderedCardList.add(card);
        }

        return orderedCardList;
    }


    public Deck getDeck() {
        return this;
    }

    public Card getCard(int cardIndex) {
        return deck.get(cardIndex);
    }
}
