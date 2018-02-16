package com.bang.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.bang.utils.CardsUtils;

public class Card {
	
	protected String name;
	protected String value;
	protected int suit;
	protected Boolean hasTarget;
	
	public Card(String name, String value, int suit) {
		this.name = name;
		this.value = value;
		this.suit = suit;
		this.hasTarget = null;
	}

	public Card(String name, String value, int suit, Boolean type) {
		this.name = name;
		this.value = value;
		this.suit = suit;
		this.hasTarget = type;
	}
	
	public String getName() {
		return name;
	}

	public String getShortName() {
		return name.substring(0, 1).toUpperCase() + name.substring(1, name.length() - 4);
	}
	
	public String getValue() {
		return value;
	}
	
	public int getSuit() {
		return suit;
	}
	
	public Group generateImage(double height) {
		return CardsUtils.createCardImageGroup(name, value, suit, height);
	}

	public Boolean hasTarget(){
		return this.hasTarget;
	}
}
