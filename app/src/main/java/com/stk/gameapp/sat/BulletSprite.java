package com.stk.gameapp.sat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.stk.gameapp.engine.Behavior;
import com.stk.gameapp.engine.Circle;
import com.stk.gameapp.engine.Move;
import com.stk.gameapp.engine.Point;
import com.stk.gameapp.engine.Polygon;
import com.stk.gameapp.engine.Shape;
import com.stk.gameapp.engine.Sprite;

/**
 * Created by hufeiyan on 16/04/03.
 */
public class BulletSprite extends Sprite {

    public BulletSprite(String name, float x, float y, float w, float h) {
        super(name, x, y, w, h);

        behaviors.add(new Move());
        behaviors.add(new Behavior() {
            @Override
            public void execute(Sprite sprite, Canvas canvas, long time) {
                Shape shape = sprite.getShape();
                if (shape == null) return;

                if (shape instanceof Circle) {
                    Circle circle = (Circle) shape;
                    if (circle.y < -circle.radius) {
                        sprite.setActive(false);
                    }
                } else if (shape instanceof Polygon) {
                    Point[] points = ((Polygon) shape).points;
                    if (points != null) {
                        for (Point p : points) {
                            if (p.y>0) {
                                return;
                            }
                        }
                        sprite.setActive(false);
                    }
                }
            }
        });

        Shape shape = new Circle(x,y, w/2);
        setShape(shape);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        setPaint(paint);

        setVelocity(0, -400);
    }
}
