package com.bang.actors;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.json.*;

public class CharacterDeck extends Deck{

    protected ArrayList<Character> orderedDeck;
    
    public CharacterDeck() {
        orderedDeck = buildOrderedDeck();
        nextCardIndex = 0;
    }
    
    public ArrayList<Integer> shuffleDeck() {
        this.deckIndices = randomArrayList(this.orderedDeck.size());
        return this.deckIndices;
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

    public Character drawCharacter() {
        return getCharacter(nextCardIndex++);
    }

    public Character getCharacter(int cardIndex) {
        return this.orderedDeck.get(this.deckIndices.get(cardIndex));
    }
}
