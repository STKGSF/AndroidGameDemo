package com.stk.gameapp.sat;

import android.graphics.Canvas;
import android.util.Log;

import com.stk.gameapp.engine.Behavior;
import com.stk.gameapp.engine.Circle;
import com.stk.gameapp.engine.Point;
import com.stk.gameapp.engine.Polygon;
import com.stk.gameapp.engine.Shape;
import com.stk.gameapp.engine.Sprite;

import java.util.Arrays;

/**
 * Created by hufeiyan on 16/04/03.
 */
public class RotateBehavior implements Behavior {
    private float rate;

    public RotateBehavior (float rate) {
        this.rate = rate;
    }
    @Override
    public void execute(Sprite sprite, Canvas canvas, long time) {
        float centerX = 0; float centerY = 0;

        Shape shape = sprite.getShape();
        if (shape instanceof Polygon) {
            Point[] points = ((Polygon) shape).points;
            if (points == null || points.length == 0) {
                return;
            }

            for (Point point : points) {
                centerX += point.x;
                centerY += point.y;
            }

            centerX /= points.length;
            centerY /= points.length;

            long lastTime = sprite.getLastUpdateTime();
            if (lastTime==0) return;
            float angle = (time - sprite.getLastUpdateTime())/1000f * rate;
            for (Point point : points) {
                float x = point.x-centerX; float y = point.y-centerY;
                point.x = (float) (Math.cos(angle)*x-Math.sin(angle)*y+centerX);
                point.y = (float) (Math.cos(angle)*y+Math.sin(angle)*x+centerY);
            }
        }
    }
}
