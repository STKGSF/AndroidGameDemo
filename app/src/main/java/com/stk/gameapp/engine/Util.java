package com.stk.gameapp.engine;

import android.util.Log;

/**
 * Created by hufeiyan on 16/01/03.
 */
public class Util {

    /**
     * Check to see if a polygon collides with another polygon
     * @param p1
     * @param p2
     * @return
     */
    public static MinimumTranslationVector polygonCollidesWithPolygon (Polygon p1, Polygon p2) {
        MinimumTranslationVector mtv1 = p1.minimumTranslationVector(p1.getAxes(), p2 ),
                mtv2 = p1.minimumTranslationVector(p2.getAxes(), p2 );

        if (mtv1 == null || mtv1.overlap < 0 || mtv2 == null || mtv2.overlap < 0)
            return null;
        else {
//            Log.e("PCWP"," mtv1=" + mtv1 + " mtv2=" + mtv2);
            return mtv1.overlap < mtv2.overlap ? mtv1 : mtv2;
        }
    };

    /**
     * Check to see if a circle collides with another circle
     * @return
     */
    public static MinimumTranslationVector circleCollidesWithCircle (Circle c1, Circle c2) {
        float distance = (float) Math.sqrt( Math.pow(c2.x - c1.x, 2) +
                Math.pow(c2.y - c1.y, 2));
        float overlap = Math.abs(c1.radius + c2.radius) - distance;

        return overlap < 0 ?
                new MinimumTranslationVector(null, -1) :
                new MinimumTranslationVector(null, overlap);
    };

// ..............................................................
// Get the polygon's point that's closest to the circle
// ..............................................................

    public static Point getPolygonPointClosestToCircle(Polygon polygon,Circle circle) {
        double min = Float.MAX_VALUE;
        double length;
        Point testPoint, closestPoint = null;
        for (int i=0; i < polygon.points.length; ++i) {
            testPoint = polygon.points[i];
            length = Math.sqrt(Math.pow(testPoint.x - circle.x,2)
                    + Math.pow(testPoint.y - circle.y, 2));
            if (length < min) {
                min = length;
                closestPoint = testPoint;
            }
        }

        return closestPoint;
    }

    /**
     * Get the circle's axis (circle's don't have an axis, so this method manufactures one)
     * @return
     */
    public static Vector getCircleAxis(Circle circle, Polygon polygon, Point closestPoint) {
        Vector v1 = new Vector(circle.x, circle.y),
                v2 = new Vector(closestPoint.x, closestPoint.y),
                surfaceVector = v1.subtract(v2);

        return surfaceVector.normalize();
    }

    /**
     * Tests to see if a polygon collides with a circle
     * @param polygon
     * @param circle
     * @return
     */
    public static MinimumTranslationVector polygonCollidesWithCircle (Polygon polygon,
                                                               Circle circle ) {
        Vector[] axes = polygon.getAxes();
        Point closestPoint = getPolygonPointClosestToCircle(polygon, circle);

        Vector axes2 = getCircleAxis(circle, polygon, closestPoint);
        Vector[] allAxes = new Vector[axes.length+1];
        for (int i=0; i<allAxes.length; i++) {
            allAxes[i] = i<axes.length?axes[i]:axes2;
        }

        return polygon.minimumTranslationVector(allAxes, circle);
    };

    /***
     * Given two shapes, and a set of axes, returns the minimum translation vector.
     * @param shape1
     * @param shape2
     * @return
     */
    public static MinimumTranslationVector getMTV(Shape shape1, Shape shape2, Vector[] axes) {
        float minOverlap = Float.MAX_VALUE;
        float overlap;
        Vector axisWithSmallestOverlap = null;
        for (Vector axis : axes) {
            Projection p1 = shape1.project(axis);
            Projection p2 = shape2.project(axis);
            overlap = p1.getOverlap(p2);
            if (overlap<0) {
                return null;
            } else {
                if (minOverlap>overlap) {
                    minOverlap = overlap;
                    axisWithSmallestOverlap = axis;
//                    Log.e("MTV"," axis="+axis+" p1="+p1+" p2="+p2+" op="+overlap);
                }
            }
        }
        return new MinimumTranslationVector(axisWithSmallestOverlap, minOverlap);
    }

    /**
     *
     * @param shape
     * @param mtv
     * @param vx X axis velocity
     * @param vy Y axis velocity
     */
    public static void separate(Shape shape, MinimumTranslationVector mtv, float vx, float vy) {
        float dx,dy, velocityMagnitude;
        float x, y;
        if (mtv.axis == null) {
            // circle
            velocityMagnitude = (float) Math.sqrt(vx*vx+vy*vy);
            x = vx / velocityMagnitude;
            y = vy / velocityMagnitude;
            mtv.axis = new Vector(x, y);
        }

        dx = mtv.axis.x * mtv.overlap;
        dy = mtv.axis.y * mtv.overlap;

        if ((dx<0 && vx<0) || (dx>0 && vx>0)) {
            // Don't move in same direction
            dx = -dx;
        }

        if ((dy<0 && vy<0) || (dy>0 && vy>0)) {
            // Don't move in same direction
            dy = -dy;
        }

        shape.move(dx, dy);
    }
}
