package com.stk.gameapp.engine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

/**
 * Created by hufeiyan on 16/24/02.
 */
public class Polygon extends Shape {

    public Point[] points;

    public Polygon(Point[] ps) {
        points = ps;
    }

    @Override
    public Vector[] getAxes() {
        Vector[] axes = new Vector[points.length];
        for (int i=0; i<points.length-1; i++) {
            Vector v1 = new Vector(points[i]);
            Vector v2 = new Vector(points[i+1]);

            axes[i] = v1.edge(v2).normal();
        }
        axes[axes.length-1] = new Vector(points[points.length-1]).edge(new Vector(points[0])).normal();
        return axes;
    }

    @Override
    public Projection project(Vector axis) {
        float max = 0-Float.MAX_VALUE;
        float min = Float.MAX_VALUE;
        Vector vector = new Vector(0,0);
        for (Point point : points) {
            vector.x = point.x;
            vector.y = point.y;
            float scalar = vector.dotProduct(axis);
//            Log.e(Polygon.class.getSimpleName(), "vector="+vector+" axis="+axis+" scalar="+scalar);
            min = min>scalar?scalar:min;
            max = max<scalar?scalar:max;
        }
//Log.e(Polygon.class.getSimpleName(),axis+ "len="+(max-min)+" max="+max+" min="+min);
        return new Projection(min, max);
    }

    @Override
    public void paint(Canvas canvas, Paint paint) {
        if (points == null || points.length<1) return;
        Path path = new Path();
        path.moveTo(points[0].x, points[0].y);
        for (int i=1; i<points.length; i++) {
            path.lineTo(points[i].x, points[i].y);
        }
        path.close();
        canvas.drawPath(path, paint);
    }

    @Override
    public void move(float dx, float dy) {
        for (Point point : points) {
            point.x += dx;
            point.y += dy;
        }
    }

    public MinimumTranslationVector collidesWith(Shape otherShape) {
        if (otherShape instanceof Circle) {
            return Util.polygonCollidesWithCircle(this, (Circle) otherShape);
        } else {
            return Util.polygonCollidesWithPolygon(this, (Polygon) otherShape);
        }
    }
}
