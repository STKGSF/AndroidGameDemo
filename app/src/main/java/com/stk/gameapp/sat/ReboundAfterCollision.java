package com.stk.gameapp.sat;

import android.util.Log;

import com.stk.gameapp.R;
import com.stk.gameapp.engine.Circle;
import com.stk.gameapp.engine.CollideCallBack;
import com.stk.gameapp.engine.GameEngine;
import com.stk.gameapp.engine.MinimumTranslationVector;
import com.stk.gameapp.engine.Point;
import com.stk.gameapp.engine.Polygon;
import com.stk.gameapp.engine.Shape;
import com.stk.gameapp.engine.Sprite;
import com.stk.gameapp.engine.Util;

/**
 * Created by hufeiyan on 16/01/03.
 */
public class ReboundAfterCollision implements CollideCallBack {
    private GameEngine game;

    public ReboundAfterCollision(GameEngine gameEngine) {
        game = gameEngine;
    }

    @Override
    public void onCollide(Sprite sprite1, Sprite sprite2, MinimumTranslationVector mtv) {
Log.e(ReboundAfterCollision.class.getSimpleName(),sprite1.getName()+" collision with "+sprite2.getName()+" mtv="+mtv);
        if (sprite1 instanceof BulletSprite || sprite2 instanceof BulletSprite) {
            sprite1.setActive(false);
            sprite2.setActive(false);
            game.playSound(R.raw.wreck);
            return;
        }

        Shape shape1 = sprite1.getShape();
        Shape shape2 = sprite2.getShape();

        // Separate shape first
        Util.separate(shape1, mtv, sprite1.getVelocityX(), sprite1.getVelocityY());

        float m1 = getMass(shape1);
        float m2 = getMass(shape2);

        float vx1 = ((m1-m2)*sprite1.getVelocityX()+2*m2*sprite2.getVelocityX())/(m1+m2);
        float vx2 = ((m2-m1)*sprite2.getVelocityX()+2*m1*sprite1.getVelocityX())/(m1+m2);

        float vy1 = ((m1-m2)*sprite1.getVelocityY()+2*m2*sprite2.getVelocityY())/(m1+m2);
        float vy2 = ((m2-m1)*sprite2.getVelocityY()+2*m1*sprite1.getVelocityY())/(m1+m2);

        sprite1.setVelocity(vx1, vy1);
        sprite2.setVelocity(vx2, vy2);
        game.playSound(R.raw.collision);
    }

    public float getMass(Shape shape) {
        return perimeterToMass(shape);
    }

    private float areaToMass(Shape shape) {
        // 小球的质量按照面积计算，与面积成正比，公式计算中使用面积代替质量
        float area = 0;
        if (shape instanceof Circle) {
            float radius = ((Circle) shape).radius;
            area = (float) (Math.PI * radius*radius);
        } else {
            Point[] points = ((Polygon)shape).points;
            for (int i=0; i<points.length-1; i++) {
                area += (points[i].x*points[i+1].y +points[i+1].x*points[i].x)/2;
            }
        }

        return area;
    }

    /**
     * 将周长作为精灵的质量
     * @param shape
     * @return
     */
    public float perimeterToMass(Shape shape) {
        float mass = 0;
        if (shape instanceof Circle) {
            float radius = ((Circle) shape).radius;
            mass = (float) (2*Math.PI*radius);
        } else {
            Point[] points = ((Polygon)shape).points;
            for (int i=1; i<points.length; i++) {
                mass += Math.sqrt((points[i].x-points[i-1].x)*(points[i].x-points[i-1].x)
                +(points[i].y-points[i-1].y)*(points[i].y-points[i-1].y));
            }
        }

        return mass;
    }
}
