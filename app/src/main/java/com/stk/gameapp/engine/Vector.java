package com.stk.gameapp.engine;

/**
 * Created by hufeiyan on 16/24/02.
 */
public class Vector {
    public float x;
    public float y;

    public Vector(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector(Point point) {
        if (point == null) {
            x = 0;
            y = 0;
        } else {
            x = point.x;
            y = point.y;
        }
    }

    /**
     *
     * @return 返回向量大小
     */
    public float getMagnitude() {
        return (float) Math.sqrt(x*x+y*y);
    }

    public Vector add(Vector v) {
        return new Vector(x+v.x, y+v.y);
    }

    public Vector subtract(Vector v) {
        return new Vector(x-v.x, y-v.y);
    }

    public float dotProduct(Vector v) {
        return x*v.x+y*v.y;
    }

    public Vector edge(Vector v) {
        return this.subtract(v);
    }

    /**
     *
     * @return 返回垂直向量
     */
    public Vector perpendicular() {
        return new Vector(y, -x);
    }

    /**
     *
     * @return 返回单位向量
     */
    public Vector normalize() {
        float m = getMagnitude();
        Vector v = new Vector(0,0);
        if (m>0.000001) {
            v.x = x/m;
            v.y = y/m;
        }

        return v;
    }

    /**
     *
     * @return 返回法向量
     */
    public Vector normal() {
        return perpendicular().normalize();
    }

    /**
     *
     * @param axis
     * @return 返回反射向量
     */
    public Vector reflect(Vector axis) {
        float dotProductRatio, vdotl, ldotl;
        Vector v = new Vector(0,0);
        vdotl = this.dotProduct(axis);
        ldotl = axis.dotProduct(axis);
        dotProductRatio = vdotl / ldotl;

        v.x = 2 * dotProductRatio * axis.x - this.x;
        v.y = 2 * dotProductRatio * axis.y - this.y;

        return v;
    }

    @Override
    public String toString() {
        return String.format("Vector[%f,%f]",x,y);
    }
}
