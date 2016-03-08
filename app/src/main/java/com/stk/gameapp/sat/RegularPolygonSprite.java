package com.stk.gameapp.sat;

import com.stk.gameapp.engine.Move;
import com.stk.gameapp.engine.Point;
import com.stk.gameapp.engine.Polygon;
import com.stk.gameapp.engine.Shape;
import com.stk.gameapp.engine.Sprite;

/**
 * Created by hufeiyan on 16/04/03.
 */
public class RegularPolygonSprite extends Sprite {
    public RegularPolygonSprite(String name, float x, float y, float diameter, int edgeNum) {
        super(name, x, y, diameter, diameter);

        Point[] points = new Point[edgeNum];
        float angular = (float) (Math.PI*2/edgeNum);
        for (int i=0; i<points.length; i++) {
            points[i] = new Point((float) (x+diameter/2-diameter/2*Math.cos(angular*i)),
                    (float) (y-diameter/2*Math.sin(angular*i)));
        }
        Shape shape = new Polygon(points);
        setShape(shape);
    }
}
