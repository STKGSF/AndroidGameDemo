package com.stk.gameapp.engine;

import android.graphics.Canvas;

/**
 * Created by hufeiyan on 16/25/02.
 */
public interface Behavior {
    void execute(Sprite sprite, Canvas canvas, long time);
}
