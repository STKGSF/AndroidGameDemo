package com.stk.gameapp.engine;


import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by hufeiyan on 16/24/02.
 */
public abstract class Shape {

    public abstract MinimumTranslationVector collidesWith(Shape otherShape);

    public abstract Vector[] getAxes();

    public MinimumTranslationVector minimumTranslationVector(Vector[] axes, Shape otherShape) {
        return Util.getMTV(this, otherShape, axes);
    }

    /**
     * 计算图形在axis坐标轴上的投影
     * @param axis
     * @return 返回投影对象
     */
    public abstract Projection project(Vector axis);

    public abstract void paint(Canvas canvas, Paint paint);

    //=====================Behavior=========================
    public abstract void move(float dx, float dy);
}
