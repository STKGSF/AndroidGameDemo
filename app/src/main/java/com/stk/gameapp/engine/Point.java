package com.stk.gameapp.engine;

/**
 * Created by hufeiyan on 16/04/03.
 */
public class Point {
    public float x;
    public float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Point[%f,%f]",x,y);
    }
}
