package com.bang.actors;

import java.io.Serializable;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.bang.utils.CardsUtils;

public class Card implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8118838334949635992L;
	
	
	protected String name;
	protected String value;
	protected int suit;
	protected String type;
	
	public Card(String name, String value, int suit) {
		this.name = name;
		this.value = value;
		this.suit = suit;
		this.type = null;
	}

	public Card(String name, String value, int suit, String type) {
		this.name = name;
		this.value = value;
		this.suit = suit;
		this.type = type;
	}

	public Card copyCard(){
		return new Card(this.name, this.value, this.suit, this.type);
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
	
	public Group generateBackImage(double height) {
		return CardsUtils.createBackCardImageGroup(height);
	}

	public String getType(){
		return this.type;
	}
}
