package com.apptoeat.env;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Pair;

import com.apptoeat.utils.FastMath;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class World {

    private List<Entity> entities, bufferEntities;
    private List<Entity> spawners;
    protected int windowWidth, windowHeight;
    protected double width, height;
    private Context content;

    public Context getContent() {
        return content;
    }

    public World(int width, int height, Context content) {
        entities = new ArrayList<>();
        bufferEntities = new ArrayList<>();
        this.spawners = new ArrayList<>();
        this.windowWidth = width;
        this.windowHeight = height;
        this.content = content;
        this.width = 2;
        this.height = 2;
    }

    public void update(double deltaTime) {
        entities.addAll(bufferEntities);
        bufferEntities.clear();
        List<Entity> removed = new ArrayList<>();

        for (var entity : entities) {
            entity.update(deltaTime);
            if (!entity.isAlive()) removed.add(entity);
        }

        for (var remove : removed) {
            entities.remove(remove);
        }

        for (var spawner : spawners) {
            if (spawner.type.multiplicationProperty.spawnRate * deltaTime >= Math.random())
                addEntity(spawner.clone().teleport(Math.random() * width, Math.random() * height));
        }
    }

    public int mapX(double x) {
        return (int) ((x * windowWidth) / width);
    }
    public int mapY(double y) {
        return (int) ((y * windowHeight) / height);
    }

    public void addEntity(Entity entity) {
        bufferEntities.add(entity);
    }

    public void draw(Canvas canvas) {
        for (var x : entities) {
            x.draw(canvas);
        }
    }

    public Pair<Double, Integer> shootRay(double ox, double oy, double dx, double dy) {
        return shootRay(ox, oy, dx, dy, -1);
    }
    public Pair<Double, Entity> shootRayEntity(double ox, double oy, double dx, double dy) {
        return shootRayEntity(ox, oy, dx, dy, -1);
    }

    public Pair<Double, Integer> shootRay(double ox, double oy, double dx, double dy, int avoidId) {
        double minDistance = Double.POSITIVE_INFINITY;
        int hitIndex = -1;
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            RectF box = entity.getBoundingBox();
            double distance = rayIntersectAABB(ox, oy, dx, dy, box);
            if (distance >= 0 && distance < minDistance && entity.getType().getId() != avoidId) {
                minDistance = distance;
                hitIndex = entity.getType().id;
            }
        }
        return new Pair<>(minDistance, hitIndex);
    }

    public Pair<Double, Entity> shootRayEntity(double ox, double oy, double dx, double dy, int avoidId) {
        double minDistance = Double.POSITIVE_INFINITY;
        Entity hit = null;
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            RectF box = entity.getBoundingBox();
            double distance = rayIntersectAABB(ox, oy, dx, dy, box);
            if (distance >= 0 && distance < minDistance && entity.getType().getId() != avoidId) {
                minDistance = distance;
                hit = entity;
            }
        }
        return new Pair<>(minDistance, hit);
    }

    private double rayIntersectAABB(double ox, double oy, double dx, double dy, RectF box) {
        // If the ray origin is inside the box, we can return 0 immediately.
        if (box.contains((float) ox, (float) oy)) {
            return 0.0;
        }

        double tmin = Double.NEGATIVE_INFINITY;
        double tmax = Double.POSITIVE_INFINITY;

        // Check X axis
        if (dx != 0) {
            double tx1 = (box.left - ox) / dx;
            double tx2 = (box.right - ox) / dx;
            double tminX = Math.min(tx1, tx2);
            double tmaxX = Math.max(tx1, tx2);
            tmin = Math.max(tmin, tminX);
            tmax = Math.min(tmax, tmaxX);
        } else {
            if (ox < box.left || ox > box.right) {
                return -1;
            }
        }

        if (dy != 0) {
            double ty1 = (box.top - oy) / dy;
            double ty2 = (box.bottom - oy) / dy;
            double tminY = Math.min(ty1, ty2);
            double tmaxY = Math.max(ty1, ty2);
            tmin = Math.max(tmin, tminY);
            tmax = Math.min(tmax, tmaxY);
        } else {
            if (oy < box.top || oy > box.bottom) {
                return -1;
            }
        }
        if (tmax < 0 || tmin > tmax) {
            return -1;
        }
        return tmin >= 0 ? tmin : tmax;
    }

    public List<Entity> getNearByEntities(double x, double y, double dist) {
        List<Entity> nearByEntities = new ArrayList<>();

        for (var entity : entities) {
            if (FastMath.dist(x, y, entity.getX(), entity.getY()) < dist)
                nearByEntities.add(entity);
        }
        return nearByEntities;
    }
}
