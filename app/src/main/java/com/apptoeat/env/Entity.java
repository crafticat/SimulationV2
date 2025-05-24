package com.apptoeat.env;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.apptoeat.env.types.EntityType;

import lombok.Getter;

@Getter
public abstract class Entity {
    protected double x, y, health;
    protected double velX = 0;            // NEW  current velocity (world units / s)
    protected double velY = 0;            // NEW
    protected World world;
    protected EntityType type;
    protected float width = 0.001f, height = 0.001f;
    protected double ratio;
    protected int id;
    protected static int nextId = 0;

    public Entity(double x, double y, World world, EntityType type) {
        this.x = x;
        this.y = y;
        this.world = world;
        this.type = type;
        this.health = type.getHealthProperty().initHealth;

        ratio = world.windowWidth / (double) world.windowHeight;
        id = nextId++;
    }

    public Entity teleport(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public void damage(double dmg) {
        health -= dmg;
    }

    public void remove() {
        world.getEntities().remove(this);
    }

    public RectF getBoundingBox() {
        return new RectF((float)x - width / 2, (float)y - height / 2,
                (float)x + width / 2, (float)y + height / 2);
    }

    public abstract void update(double dt);
    public abstract void draw(Canvas canvas);

    public boolean isAlive() {
        return health > 0;
    }

    public abstract Entity clone();
}
