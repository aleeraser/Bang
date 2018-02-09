package com.bang.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.bang.utils.CardsUtils;

public class Card {
	
	protected String name;
	protected String value;
	protected int suit;
	
	public Card(String name, String value, int suit) {
		this.name = name;
		this.value = value;
		this.suit = suit;
	}
	
	public String getName() {
		return name;
	}
	
	public Group generateImage(double height) {
		return CardsUtils.createCardImageGroup(name, value, suit, height);
	}
}
