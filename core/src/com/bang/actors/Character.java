package com.bang.actors;

import java.io.Serializable;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.bang.utils.CardsUtils;

public class Character implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5568870557982759991L;
	
	protected String name;
	protected int lives;
	
	public Character(String name, int lives) {
		this.name = name;
		this.lives = lives;
	}
	
	public int getLives() {
		return lives;
	}
	
	public String getName() {
		return name;
	}
	
	public Group getCharacterCard(double cardImageHeight, int remainingLives) {
		return CardsUtils.createCharacterCardImageGroup(name + ".png", cardImageHeight, lives, remainingLives);
	}
	
}
