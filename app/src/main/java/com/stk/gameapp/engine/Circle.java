package com.stk.gameapp.engine;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by hufeiyan on 16/24/02.
 */
public class Circle extends Shape {
    public float x;
    public float y;
    public float radius;

    public Circle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
    @Override
    public Vector[] getAxes() {
        return null;
    }

    @Override
    public Projection project(Vector axis) {
        Vector vector = new Vector(x, y);
        float dotProduct = vector.dotProduct(axis);

        float a = dotProduct - radius;
        float b = dotProduct + radius;

        return new Projection(Math.min(a,b), Math.max(a,b));
    }

    @Override
    public void paint(Canvas canvas, Paint paint) {
        canvas.drawCircle(x,y, radius, paint);
    }

    @Override
    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    @Override
    public MinimumTranslationVector collidesWith(Shape otherShape) {
        if (otherShape instanceof Circle) {
            return Util.circleCollidesWithCircle(this,(Circle) otherShape);
        } else {
            return Util.polygonCollidesWithCircle((Polygon)otherShape, this);
        }
    }

    public Point getPolygonPointClosestToCircle(Polygon polygon){
        Point closestPoint = null;
        float min = Float.MAX_VALUE;
        for (Point point : polygon.points) {
            float distance = (float) Math.sqrt((point.x-x)*(point.x-x)+(point.y-y)*(point.y-y));
            if (min>distance) {
                min = distance;
                closestPoint = point;
            }
        }

        return closestPoint;
    }
}
