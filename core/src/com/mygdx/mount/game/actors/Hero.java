package com.mygdx.mount.game.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by wannabe on 24.04.15.
 */
public class Hero extends Actor {
    public final static String TEXTURE_URL = "sprites/hero.jpg";
    public final static int WIDTH = 50;
    public final static int HEIGHT = 50;
    protected Texture heroTexture;

    public Hero() {
        heroTexture = new Texture(TEXTURE_URL);
    }

    public Texture getHeroTexture() {
        return heroTexture;
    }
}