package com.stk.gameapp.engine;

/**
 * Created by hufeiyan on 16/01/03.
 */
public class MinimumTranslationVector {
    public Vector axis;
    public float overlap;

    public MinimumTranslationVector(Vector vector, float overlap) {
        axis = vector;
        this.overlap = overlap;
    }

    @Override
    public String toString() {
        return String.format("[axis=%s, overlap:%f]",axis,overlap);
    }
}
