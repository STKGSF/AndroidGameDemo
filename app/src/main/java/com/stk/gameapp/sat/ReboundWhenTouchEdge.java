package com.stk.gameapp.sat;

import android.graphics.Canvas;
import android.util.Log;

import com.stk.gameapp.R;
import com.stk.gameapp.engine.Behavior;
import com.stk.gameapp.engine.Circle;
import com.stk.gameapp.engine.GameEngine;
import com.stk.gameapp.engine.Point;
import com.stk.gameapp.engine.Polygon;
import com.stk.gameapp.engine.Shape;
import com.stk.gameapp.engine.Sprite;

/**
 * Created by hufeiyan on 16/29/02.
 */
public class ReboundWhenTouchEdge implements Behavior {
    private GameEngine game;
    public ReboundWhenTouchEdge(GameEngine gameEngine) {
        game = gameEngine;
    }
    @Override
    public void execute(Sprite sprite, Canvas canvas, long time) {
        if (sprite == null || canvas == null) return;

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        Shape shape = sprite.getShape();
        if (shape instanceof Circle) {
            Circle circle = (Circle)shape;
            boolean isOut = circle.x<circle.radius;
            if(isOut) {
                game.playSound(R.raw.collision);
            }
            float v = sprite.getVelocityX();
            sprite.setVelocityX(isOut?Math.abs(v):v);

            v = sprite.getVelocityX();
            isOut = circle.x>(w-circle.radius);
            if(isOut) {
                game.playSound(R.raw.collision);
            }
            sprite.setVelocityX(isOut?-Math.abs(v):v);

            isOut = circle.y<circle.radius;
            if(isOut) {
                game.playSound(R.raw.collision);
            }
            v = sprite.getVelocityY();
            sprite.setVelocityY(isOut?Math.abs(v):v);

            v = sprite.getVelocityY();
            isOut = circle.y>(h-circle.radius);
            if(isOut) {
                game.playSound(R.raw.collision);
            }
            sprite.setVelocityY(isOut?-Math.abs(v):v);
        } else {
            float tp = Float.MAX_VALUE;
            float lp = Float.MAX_VALUE;
            float rp = 0-Float.MAX_VALUE;
            float bp = 0-Float.MAX_VALUE;
            Polygon polygon = (Polygon) shape;
            for (Point point: polygon.points) {
                tp = tp > point.y ? point.y : tp;
                bp = bp < point.y ? point.y : bp;
                lp = lp > point.x ? point.x : lp;
                rp = rp < point.x ? point.x : rp;
            }

            boolean isOut = lp<0;
            if(isOut) {
                game.playSound(R.raw.collision);
            }
            float v = sprite.getVelocityX();
            sprite.setVelocityX(isOut?Math.abs(v):v);

            isOut = rp>w;
            if(isOut) {
                game.playSound(R.raw.collision);
            }
            v = sprite.getVelocityX();
            sprite.setVelocityX(isOut?-Math.abs(v):v);

            isOut = tp<0;
            if(isOut) {
                game.playSound(R.raw.collision);
            }
            v = sprite.getVelocityY();
            sprite.setVelocityY(isOut?Math.abs(v):v);

            v = sprite.getVelocityY();
            isOut = bp>h;
            if(isOut) {
                game.playSound(R.raw.collision);
            }
            sprite.setVelocityY(isOut?-Math.abs(v):v);
        }
    }
}
