package com.stk.gameapp.engine;

/**
 * Created by hufeiyan on 16/01/03.
 */
public interface CollideCallBack {
    void onCollide(Sprite sprite1, Sprite sprite2, MinimumTranslationVector mtv);
}
