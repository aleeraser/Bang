package com.bang.actors;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.json.*;

public class CharacterDeck {
    
    private ArrayList<Character> orderedCharDeck;
    private ArrayList<Integer> charDeckIndices;
    private int nextCardIndex;
    
    public CharacterDeck() {
        this.orderedCharDeck = buildOrderedDeck();
        this.nextCardIndex = 0;
    }

    private ArrayList<Integer> randomArrayList(int n) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list;
    }

    private ArrayList<Character> buildOrderedDeck() {
        ArrayList<Character> orderedDeck = new ArrayList<Character>();

        FileHandle jsonSource = Gdx.files.internal("characterlist.json");
        JSONArray jsonList = new JSONArray(jsonSource.readString());

        Character character;

        for (int i = 0; i < jsonList.length(); i++) {
            JSONObject entry = jsonList.getJSONObject(i);
            
            character = new Character(entry.getString("name"), entry.getInt("lives"));
           
            orderedDeck.add(character);
        }

        return orderedDeck;
    }

    public ArrayList<Integer> shuffleDeck() {
        this.charDeckIndices = randomArrayList(orderedCharDeck.size());
        return this.charDeckIndices;
    }

    public Character getCard(int cardIndex) {
        return this.orderedCharDeck.get(cardIndex);
    }

    public void setIndices(ArrayList<Integer> indices) {
        this.charDeckIndices = indices;
    }

    public ArrayList<Integer> getIndices() {
        return this.charDeckIndices;
    }

    public void setNextCardIndex(int i) {
        nextCardIndex = i;
    }

    public int getNextCardIndex() {
        return nextCardIndex;
    }
}
