package com.bang.actors;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> orderedCardList, deck;

    private ArrayList<Integer> randomArrayList(int n) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        return list;
    }

    public Deck(int cardsNumber) {
        // init deck object
        ArrayList<Integer> deckIndices = randomArrayList(cardsNumber);

        for (int i = 0; i < cardsNumber; i++) {
            deck.add(orderedCardList.get(deckIndices.get(i)));
        }
    }

    public Deck getDeck() {
        return this;
    }

    public Card getCard(int cardIndex) {
        return deck.get(cardIndex);
    }
}
