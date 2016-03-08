package com.stk.gameapp.engine;

/**
 * Created by hufeiyan on 16/24/02.
 */
public class Projection {
    private float min;
    private float max;

    public Projection(float a, float b) {
        min = a;
        max = b;
    }

    public boolean overlaps (Projection p) {
        return max>p.min && p.max >min;
    }

    public float getOverlap(Projection projection) {
        float overlap = -1;

        if (overlaps(projection)) {
            overlap = max > projection.max ? (projection.max-min) : (max-projection.min);
        }
        return overlap;
    }

    @Override
    public String toString() {
        return String.format("Projection[%f,%f]",min,max);
    }
}
