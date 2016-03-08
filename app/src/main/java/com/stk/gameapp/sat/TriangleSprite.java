package com.stk.gameapp.sat;

import com.stk.gameapp.engine.Move;
import com.stk.gameapp.engine.Point;
import com.stk.gameapp.engine.Polygon;
import com.stk.gameapp.engine.Shape;
import com.stk.gameapp.engine.Sprite;

/**
 * Created by hufeiyan on 16/04/03.
 */
public class TriangleSprite extends Sprite {

    public TriangleSprite(String name, float x, float y, float w, float h) {
        super(name, x, y, w, h);

        Point[] points = new Point[3];
        points[0] = new Point(x, y+h);
        points[1] = new Point(x+w/2, y);
        points[2] = new Point(x+w,y+h);

//        points[0] = new Point(x, y);
//        points[1] = new Point(x+w, y);
//        points[2] = new Point(x,y+h);

        Shape shape = new Polygon(points);
        setShape(shape);
    }
}
