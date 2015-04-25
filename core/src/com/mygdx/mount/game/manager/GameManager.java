package com.mygdx.mount.game.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mount.game.actors.*;
import com.mygdx.mount.game.manager.game.services.*;

import java.util.ArrayList;

/**
 * Created by wannabe on 24.04.15.
 */
public class GameManager extends Stage implements InputProcessor {
    private static final String CAVE_URL = "sprites/cave-bg.jpg";
    private static final String GROUND_URL = "sprites/wheat-field-bg.jpg";
    public static final int SCREEN_WIDTH = 2000;
    public static final int SCREEN_HEIGHT = 900;
    CollisionService.Collision collision;

    public static enum GAME_STATE {
        VALID, INVALID
    }

    public TouchService getTouchService() {
        return touchService;
    }

    public void setTouchService(TouchService touchService) {
        this.touchService = touchService;
    }

    TouchService touchService;
    DrawService drawService;
    Hero hero;
    Texture caveTexture;
    Texture groundTexture;
    Batch batch;
    MoveService moveService;
    Camera camera;

    public CollisionService getCollisionService() {
        return collisionService;
    }

    public void setCollisionService(CollisionService collisionService) {
        this.collisionService = collisionService;
    }

    CollisionService collisionService;
    GAME_STATE state;

    public TouchService.REALM getCurrentTouch() {
        return currentTouch;
    }

    public void setCurrentTouch(TouchService.REALM currentTouch) {
        this.currentTouch = currentTouch;
    }

    TouchService.REALM currentTouch;

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public void setWalls(ArrayList<Wall> walls) {
        this.walls = walls;
    }

    ArrayList<Wall> walls;
    Saw[] saws;

    public GameManager(Viewport viewport, Batch batch) {
        super(viewport, batch);
        Gdx.input.setInputProcessor(this);
        drawService = new DrawService();
        touchService = new TouchService();
        moveService = new MoveService(this);
        hero = new Hero();
        caveTexture = new Texture(CAVE_URL);
        groundTexture = new Texture(GROUND_URL);
        this.batch = batch;
        saws = BuildService.getSaws();
        state = GAME_STATE.VALID;
        walls = BuildService.createMap(BuildService.generateConfigurations("configurations/demoLevel.json"), new CaveBlock());
        walls.addAll(BuildService.createMap(BuildService.generateConfigurations("configurations/groundLevel.json"), new GroundBlock()));
        camera = getCamera();
        collisionService = new CollisionService();
    }

    @Override
    public void draw() {
        update();
        batch.begin();
        batch.draw(caveTexture, -500, -200, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.draw(groundTexture, SCREEN_WIDTH - 500, -200, SCREEN_WIDTH, SCREEN_HEIGHT);
        drawService.drawHero(hero, getBatch());
        drawService.drawWallArray(walls, getBatch());
        drawService.drawSaws(saws, batch);
        batch.end();
    }

    public void update() {
        if (state.equals(GAME_STATE.VALID)) {
            if (Gdx.input.isTouched()) {
                currentTouch = TouchService.getRealmByTouch();
            } else {
                currentTouch = null;
            }


            hero.setBoundRectangle((int) hero.getX(), (int) hero.getY(), (int) hero.getWidth(), (int) hero.getHeight());
            if (collisionService.isHeroCollide(hero, walls)) {
                collision = collisionService.getCollisionForHero();
                System.out.println(collision.block.getClass().toString());
                if (collision != null && (collision.block instanceof Wall || collision.block instanceof BaseBlock)) {
                    if (collision.direction == CollisionService.DIRECTION.DOWN) {
                        hero.setState(Hero.State.Standing);
                        hero.currentJumpSpeed = Hero.JUMP_MAX_HEIGHT;
                        hero.heroJumpHeight = 0;
                    } else if (collision.direction == CollisionService.DIRECTION.RIGHT) {
                        hero.setCurrentSprite(Hero.heroSprites[0]);
                        hero.setSpeed(0);
                    }
                }
            } else {
                collision = null;
            }
            moveService.act(hero);
            moveService.moveCameraWithHero(camera, hero, batch);

            for (int i = 0; i < saws.length; i++) {
                if (collisionService.isHeroCollide(hero, saws[i])) {
                    state = GAME_STATE.INVALID;
                }
                saws[i].rotate(90 * Gdx.graphics.getDeltaTime());
            }
        }
    }


    public boolean checkGameValid() {
        if (state.equals(GAME_STATE.INVALID)) {
            return false;
        }
        return true;
    }
}
