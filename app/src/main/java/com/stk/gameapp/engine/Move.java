package com.stk.gameapp.engine;

import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by hufeiyan on 16/26/02.
 */
public class Move implements Behavior {
    @Override
    public void execute(Sprite sprite, Canvas canvas, long time) {
        long lastTime = sprite.getLastUpdateTime();
        if (lastTime==0) return;
        float escapeTime = (time - sprite.getLastUpdateTime())/1000f;
        Shape shape = sprite.getShape();
        if (shape != null) {
            shape.move(escapeTime * sprite.getVelocityX(),escapeTime * sprite.getVelocityY());
        }
    }
}
