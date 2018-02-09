package com.bang.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.bang.utils.CardsUtils;

public class Card {
	
	protected String name;
	protected String value;
	protected int suit;
	protected Boolean hasTarget;

	public Card(String name, String value, int suit, Boolean type) {
		this.name = name;
		this.value = value;
		this.suit = suit;
		this.hasTarget = type;
	}
	
	public String getName() {
		return name;
	}

	public String getNameShort() {
		return name.substring(0, 1).toUpperCase() + name.substring(1, name.length() - 4);
	}
	
	public Group generateImage(double height) {
		return CardsUtils.createCardImageGroup(name, value, suit, height);
	}

	public Boolean hasTarget(){
		return this.hasTarget;
	}
}
