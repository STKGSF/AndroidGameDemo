package com.stk.gameapp.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.stk.gameapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by hufeiyan on 16/22/02.
 */
public class GameEngine {

    private static int DEFAULT_FRAME_SPEED = 60;
    private static int MAX_SOUND_NUM = 10;

    private SurfaceHolder holder;
    private Paint textPaint;
    private int fontHeight;
    private Paint backgroundPaint;
    private int frameSpeed;

    private ArrayList<Sprite> sprites;

    private boolean isRunning;
    private boolean isPaused;

    private long startTime;
    private long lastFrameTime;
    private long pauseStartTime;
    private long pauseDuration;

    private CollideCallBack collideCallBack;
    private GameThread thread;

    private SoundPool soundPool;
    private HashMap<Integer,Integer> soundIDs;
    private boolean isAllowSoundPlay;
    private MediaPlayer backgroundSoundPlayer;
    private boolean isAllowBackgroundMusic;

    private Context context;

    public GameEngine(SurfaceView surfaceView, Context context) {

        this.context = context.getApplicationContext();

        holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceViewCallBack());

        isAllowBackgroundMusic = true;
        backgroundSoundPlayer = MediaPlayer.create(context, R.raw.background);
        backgroundSoundPlayer.setLooping(true);
        backgroundSoundPlayer.setVolume(0.5f,0.5f);

        isAllowSoundPlay = true;
        SoundPool.Builder builder = new SoundPool.Builder();
        soundIDs = new HashMap<>(MAX_SOUND_NUM);
        builder.setMaxStreams(MAX_SOUND_NUM);
        soundPool = builder.build();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    Log.e(GameEngine.class.getSimpleName(),"Sound loaded, sampleId="+sampleId);
                }
            }
        });

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(context.getResources().getColor(R.color.stage_background));
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.RED);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(DisplayUtil.sp2px(context,24));
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        fontHeight = fontMetricsInt.descent-fontMetricsInt.ascent;

        frameSpeed = DEFAULT_FRAME_SPEED;

        sprites = new ArrayList<>();

        collideCallBack = new CollideCallBack() {
            @Override
            public void onCollide(Sprite sprite1, Sprite sprite2, MinimumTranslationVector mtv) {
                if (mtv.overlap>0) {
                    Log.e("GameEngine", "CollideCallBack overlap=" + mtv.overlap);
                }
            }
        };
        thread = new GameThread();
    }

    public void start() {

        if (isPaused()) {
            resume();
        }

        isRunning = true;

        thread = new GameThread();
        thread.start();

        playBackgroundMusic();

        startTime = System.currentTimeMillis();
        lastFrameTime = startTime;
    }

    private void playBackgroundMusic() {
        if (backgroundSoundPlayer == null) {
            backgroundSoundPlayer = MediaPlayer.create(context, R.raw.background);
            backgroundSoundPlayer.setVolume(0.5f,0.5f);
            backgroundSoundPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    backgroundSoundPlayer.start();
                }
            });
            backgroundSoundPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("GameEngine", "backgroundSoundPlayer OnErrorListener what="+what+" extra="+extra);
                    return false;
                }
            });
        } else {
            backgroundSoundPlayer.start();
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundSoundPlayer!=null) {
            backgroundSoundPlayer.stop();
        }
    }

    public void pause() {
        isPaused = true;
        pauseStartTime = System.currentTimeMillis();

        if (backgroundSoundPlayer.isPlaying()) {
            backgroundSoundPlayer.pause();
        }
    }

    public void resume(){
        isPaused = false;
        pauseDuration += System.currentTimeMillis() - pauseStartTime;

        if (backgroundSoundPlayer!=null) {
            if (!backgroundSoundPlayer.isPlaying()) {
                backgroundSoundPlayer.start();
            }
        } else {
            playBackgroundMusic();
        }
    }

    public void stop() {
        pause();
        isRunning = false;
        boolean retry = true;

        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
        stopBackgroundMusic();
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void detectCollisions() {
        MinimumTranslationVector mtv;
       for (int i=0; i<sprites.size()-1; i++) {
           Sprite sprite = sprites.get(i);
           for (int j=i+1; j<sprites.size(); j++) {
               mtv = sprite.collidesWith(sprites.get(j));
               if (mtv != null && mtv.overlap>0 && collideCallBack!=null) {
                   collideCallBack.onCollide(sprite, sprites.get(j), mtv);
               }
           }
       }
    }

    private void update(Canvas canvas, long time) {
//        long s = System.currentTimeMillis();
        synchronized (sprites) {
            for (int i = 0; i < sprites.size(); i++) {
                Sprite sprite = sprites.get(i);
                sprite.update(canvas, time);
                for (int j = 0; j < sprites.size(); j++) {
                    if (j != i) {
                        sprite.collidesWith(sprites.get(j));
                    }
                }
            }
        }
//        Log.e("Game Thread", "function[update] time="+(System.currentTimeMillis()-s));
    }

    private void render(Canvas canvas) {
//        long s = System.currentTimeMillis();

        if (canvas == null) return;

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        canvas.drawRect(0,0,w,h,backgroundPaint);

        synchronized (sprites) {
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).paint(canvas);
            }
        }

        // draw FPS
        long currentTime = System.currentTimeMillis();
        long escapeTime = currentTime - lastFrameTime;
        escapeTime = escapeTime==0?10:escapeTime;
        int fps = (int) (1000/escapeTime);
        canvas.drawText(String.format("FPS:%d Count:%d",fps,sprites.size()), 10, fontHeight, textPaint);
//        Log.e("Game Thread", "FPS escapeTime="+escapeTime);
//        Log.e("Game Thread", "function[render] time="+(System.currentTimeMillis()-s));
    }

    public void setCollideCallBack(CollideCallBack collideCallBack) {
        this.collideCallBack = collideCallBack;
    }

    public void addSprite(Sprite sprite) {
        synchronized (sprites) {
            sprites.add(sprite);
        }
    }

    public void remove(Sprite sprite) {
        synchronized (sprites) {
            sprites.remove(sprite);
        }
    }

    public void removeSpriteByName(String name) {
        for (Sprite sprite : sprites) {
            if (sprite.getName().equals(name)) {
                sprites.remove(sprite);
                return;
            }
        }
    }

    public void setSoundIDs(int[] resources) {
        for (int res : resources) {
            int id = soundPool.load(context, res, 1);
            soundIDs.put(res,id);
        }
    }

    public void playSound(int resourceId) {
        Integer id = soundIDs.get(resourceId);
        if (id != null) {
            soundPool.play(id,1,1,0,0,1);
        }
    }

    public void onStageResize(int format, int width, int height) {
        Log.d("GameEngine", String.format("onStageResize format=%d width=%d height=%d",
                format, width, height));
    }

    public Bundle saveState(Bundle map) {
        synchronized (holder) {
            if (map != null) {
                for (Sprite sprite : sprites) {
                    Bundle bundle = new Bundle();
                    sprite.saveStateToBundle(bundle);
                    map.putBundle(sprite.getName(),bundle);
                }
            }
        }
        return map;
    }

    /**
     * Restores game state from the indicated Bundle. Typically called when
     * the Activity is being restored after having been previously
     * destroyed.
     *
     * @param savedState Bundle containing the game state
     */
    public synchronized void restoreState(Bundle savedState) {
        synchronized (holder) {
            pause();
            for (Sprite sprite : sprites) {
                Log.e("GameEngine", "restoreState : sprite->"+savedState.getBundle(sprite.getName()));
            }
        }
    }

    public void removeInactiveSprite() {
        for (int i=sprites.size()-1; i>=0; i--) {
            if (!sprites.get(i).isActive()) {
                sprites.remove(i);
            }
        }
    }


    class GameThread extends Thread {
        @Override
        public void run() {
            while (isRunning) {

                try {
                    long currentTime = System.currentTimeMillis();
                    long escapeTime = currentTime - lastFrameTime;
                    if (escapeTime < 1000/frameSpeed) {
                        Thread.sleep(1000/frameSpeed - escapeTime);
                    } else {
                        if (isPaused()) {
                            Thread.sleep(1000 / frameSpeed);
                        } else {
                            synchronized (holder) {
                                Canvas canvas = holder.lockCanvas();
                                if (canvas != null) {
                                    try {
                                        detectCollisions();
                                        update(canvas, currentTime-pauseDuration);
                                        render(canvas);
                                    } catch (Exception e) {
                                        Log.e("GameEngine", "update or render exception.", e);
                                    } finally {
                                        holder.unlockCanvasAndPost(canvas);
                                    }
                                }
                            }
                            removeInactiveSprite();
                        }
                        lastFrameTime = currentTime;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.e("GameEngine", "thread run[] end. GameEngin.isrunning="+isRunning);
        }
    }

    class SurfaceViewCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            start();
            Log.e(GameEngine.class.getSimpleName(), "surfaceCreated()");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            onStageResize(format, width, height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stop();
            Log.e(GameEngine.class.getSimpleName(), "surfaceDestroyed()");
        }
    }
}
