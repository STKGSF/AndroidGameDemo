package com.stk.gameapp.engine;

import android.app.LoaderManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by hufeiyan on 16/24/02.
 */
public class Sprite {
    private String name;
    public float x;
    public float y;
    private float width;
    private float height;

    private boolean isActive;

    private float velocityX;
    private float velocityY;

    private long lastUpdateTime;

    private Paint paint;
    private Shape shape;
    protected ArrayList<Behavior> behaviors;

    public Sprite(String name) {
        this(name, 0,0,0,0, null);
    }

    public Sprite(String name, float x, float y, float w, float h) {
        this(name,x,y,w,h, null);
    }

    public Sprite(String name, float x, float y, float w, float h, ArrayList<Behavior> bs) {
        this.name = name;
        this.x = x;
        this.y = y;
        width = w;
        height = h;

        isActive = true;

        behaviors = new ArrayList<>();
        if (bs!=null) {
            behaviors.addAll(bs);
        }
    }

    public void paint(Canvas canvas) {
        if (shape != null) {
            shape.paint(canvas, paint);
        }
    }

    public void update(Canvas canvas, long time) {
        for (int i = behaviors.size() - 1; i >= 0; i--) {
            behaviors.get(i).execute(this, canvas, time);
        }

        lastUpdateTime = time;
    }

    public void setShape (Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocity(float vx, float vy) {
        velocityX = vx;
        velocityY = vy;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getName() {
        return name;
    }

    public MinimumTranslationVector collidesWith(Sprite otherSprite) {
        return shape.collidesWith(otherSprite.getShape());
    }

    public void setPosition(float x, float y) {
        this.x = x; this.y = y;
    }

    public Bundle saveStateToBundle(Bundle map) {
        map.putString("name",name);
        return map;
    }

    public void loadStateFromBundle(Bundle map) {
        Log.e("Sprite","loadStateFromBundleString : "+map.get("name"));
    }

    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }
}
