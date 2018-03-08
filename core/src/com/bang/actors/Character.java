package com.bang.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.bang.utils.CardsUtils;

public class Character {

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
	
	public Group getCharacterCard(double cardImageHeight) {
		return CardsUtils.createCharacterCardImageGroup(name + ".png", cardImageHeight);
	}
	
}
