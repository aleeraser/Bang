package com.bang.actors;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bang.utils.CardsUtils;
import com.bang.utils.UIUtils;

import org.json.*;

public class Deck {

    protected ArrayList<Card> orderedDeck;
    protected ArrayList<Integer> deckIndices;
    protected int nextCardIndex;
    private ArrayList<Integer> discardPile;
    private Integer currentDeckSize;
    private ArrayList<Integer> playersCards;

    public Deck() {
        this.orderedDeck = this.buildOrderedDeck();
        this.nextCardIndex = 0;
        this.discardPile = new ArrayList<Integer>();
        this.currentDeckSize = orderedDeck.size();
        // this.playersCards = new ArrayList<Integer>();
    }

    protected ArrayList<Integer> randomArrayList(int n) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list;
    }

    public ArrayList<Card> getOrderedDeck() {
        return orderedDeck;
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
        UIUtils.print("\n########### START DECK FINISEHD DEBUG ###########");
        UIUtils.print("Discard pile size: " + this.discardPile.size());
        UIUtils.print("nextCardIndex: " + this.nextCardIndex);
        UIUtils.print("Current discard pile:");
        printDiscardPile();

        this.currentDeckSize = this.discardPile.size() + this.playersCards.size();
        this.deckIndices = new ArrayList<Integer>();
        this.deckIndices.addAll(playersCards);
        this.nextCardIndex = this.deckIndices.size();
        Collections.shuffle(this.discardPile);
        this.deckIndices.addAll(this.discardPile);
        this.discardPile = new ArrayList<Integer>();

        UIUtils.print("------------------------");
        UIUtils.print("New deck:");
        printDeck();
        UIUtils.print("\n############ END DECK FINISEHD DEBUG #############");
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
        // Card nextCard = this.getCard(this.nextCardIndex);
        UIUtils.print("Pescata carta " + (1 + this.nextCardIndex) + "/" + this.currentDeckSize);

        return this.getCard(nextCardIndex++);
    }

    public void discard(int cardIndex) {
        if (discardPile.indexOf(cardIndex) == -1) {
            this.discardPile.add(cardIndex);
        } else {
            System.out.println("#### Scartando carta gia scartata, indice :" + cardIndex);
            CardsUtils.printCard(this.orderedDeck.get(this.deckIndices.get(cardIndex)));
        }

        UIUtils.print("#### CURRENT DISCARD PILE ####");
        printDiscardPile();
    }

    public void setDiscardPile(ArrayList<Integer> pile) {
        this.discardPile = pile;
    }

    public ArrayList<Integer> getDiscardPile() {
        return this.discardPile;
    }

    public void setCurrentSize(Integer size) {
        this.currentDeckSize = size;
    }

    private void printDiscardPile() {
        for (Integer i : discardPile) {
            UIUtils.print("\t" + i + "/" + this.discardPile.size() + ": " + this.getCard(i).getName());
        }
    }

    public void printDeck() {
        for (int i = 0; i < this.currentDeckSize; i++) {
            UIUtils.print("\t" + i + "/" + this.currentDeckSize + ": " + this.getCard(i).getName());
        }
    }

    public void setPlayerCards(ArrayList<Integer> pc) {
        this.playersCards = pc;
    }
}
