package com.stk.gameapp.sat;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.stk.gameapp.R;
import com.stk.gameapp.engine.Behavior;
import com.stk.gameapp.engine.Circle;
import com.stk.gameapp.engine.GameEngine;
import com.stk.gameapp.engine.Move;
import com.stk.gameapp.engine.Point;
import com.stk.gameapp.engine.Polygon;
import com.stk.gameapp.engine.Shape;
import com.stk.gameapp.engine.Sprite;

import java.util.ArrayList;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SATActivity extends AppCompatActivity {

    private Random random;
    ArrayList<String> spriteNames;

    private SurfaceView surfaceView;
    private Button pauseBtn;
    private GameEngine game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        random = new Random(System.currentTimeMillis());
        spriteNames = new ArrayList<>();

        setContentView(R.layout.activity_sat);

        surfaceView = (SurfaceView) findViewById(R.id.game_stage);

        game = new GameEngine(surfaceView, this);
        int count = random.nextInt(10);
        for (int i=0; i<count; i++) {
            //game.addSprite(createCircleSprite());
            game.addSprite(createTriangleSprite());
//            game.addSprite(createRegularPolygonSprite());
//            game.addSprite(randomCreateSprite());
        }
        game.setCollideCallBack(new ReboundAfterCollision(game));
        game.setSoundIDs(new int[]{R.raw.shoot, R.raw.collision, R.raw.wreck});

        pauseBtn = (Button) findViewById(R.id.pause_btn);
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (game.isPaused()) {
                    game.resume();
                    ((Button)v).setText("Pause");
                } else {
                    game.pause();
                    ((Button)v).setText("Resume");
                }
            }
        });

        Button button = (Button) findViewById(R.id.increase_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                game.addSprite(randomCreateSprite());
                game.addSprite(createTriangleSprite());
            }
        });

        button = (Button) findViewById(R.id.decrease_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spriteNames.size()==0) return;

                int index = random.nextInt(spriteNames.size());
                Log.e("SATActivity", "remove sprite "+spriteNames.get(index));
                game.removeSpriteByName(spriteNames.get(index));
                spriteNames.remove(index);
            }
        });

        button = (Button) findViewById(R.id.bullet_btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = surfaceView.getWidth()/8;
                float y = surfaceView.getHeight();

                game.addSprite(new BulletSprite("bullet"+random.nextInt(),x,y, 20,20));
                game.playSound(R.raw.shoot);
            }
        });

        button = (Button) findViewById(R.id.bullet_btn2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = surfaceView.getWidth()*3/8;
                float y = surfaceView.getHeight();
                BulletSprite sprite = new BulletSprite("bullet"+random.nextInt(),x,y, 20,20);
                sprite.setVelocity(0, sprite.getVelocityY()*2);
                game.addSprite(sprite);
                game.playSound(R.raw.shoot);
            }
        });

        button = (Button) findViewById(R.id.bullet_btn3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = surfaceView.getWidth()*5/8;
                float y = surfaceView.getHeight();
                float w = 40; float h= 40;
                BulletSprite sprite = new BulletSprite("bullet"+random.nextInt(),x,y, w,h);
                Point[] points = new Point[3];
                points[0] = new Point(x, y+h);
                points[1] = new Point(x+w/2, y);
                points[2] = new Point(x+w,y+h);
                Shape shape = new Polygon(points);
                sprite.setShape(shape);
                sprite.addBehavior(new RotateBehavior((float) Math.PI));

                game.addSprite(sprite);
                game.playSound(R.raw.shoot);
            }
        });

        button = (Button) findViewById(R.id.bullet_btn4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float x = surfaceView.getWidth()*7/8;
                float y = surfaceView.getHeight();
                float w = 40; float h= 40;
                BulletSprite sprite = new BulletSprite("bullet"+random.nextInt(),x,y, w,h);
                Point[] points = new Point[3];
                points[0] = new Point(x, y+h);
                points[1] = new Point(x+w/2, y);
                points[2] = new Point(x+w,y+h);
                Shape shape = new Polygon(points);
                sprite.setShape(shape);
                sprite.addBehavior(new RotateBehavior((float) Math.PI*2));
                sprite.setVelocity(0, sprite.getVelocityY()*2);

                game.addSprite(sprite);
                game.playSound(R.raw.shoot);
            }
        });

        if (savedInstanceState != null) {
            // we are being restored: resume a previous game
            game.restoreState(savedInstanceState);
            Log.w(this.getClass().getSimpleName(), "savedInstanceState is nonnull");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        if(game != null) {
            game.resume(); // pause game when Activity pauses
            pauseBtn.setText("Pause");
        }
        Log.w(this.getClass().getName(), "onResume()");
        super.onResume();
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        if(game != null) {
            game.pause(); // pause game when Activity pauses
            pauseBtn.setText("Resume");
        }
        Log.w(this.getClass().getName(), "onPause()");
        super.onPause();
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     *
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        game.saveState(outState);
        Log.w(this.getClass().getName(), "onSaveInstanceState called");
    }

    private Sprite createCircleSprite() {
        ArrayList<Behavior> behaviors = new ArrayList<>();
        behaviors.add(new Move());
        behaviors.add(new ReboundWhenTouchEdge(game));

        int radius = 10+random.nextInt(50);
        int x = 200+random.nextInt(500);
        int y = 200+random.nextInt(500);
        String name = "Circle"+random.nextInt();
        Sprite sprite = new Sprite(name, x, y, 2*radius, 2*radius, behaviors);
        spriteNames.add(name);

        Shape shape = new Circle(x+radius, y+radius,radius);
        sprite.setShape(shape);
        sprite.setVelocity(20+random.nextInt(400),20+random.nextInt(400));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(random.nextInt()|0xFF000000);
        sprite.setPaint(paint);

        return sprite;
    }

    private Sprite createTriangleSprite() {

        int w = 80+random.nextInt(50);
        int h = 80+random.nextInt(50);
        int x = 200+random.nextInt(500);
        int y = 200+random.nextInt(500);

        String name = "Triangle"+random.nextInt();
        spriteNames.add(name);

        TriangleSprite sprite = new TriangleSprite(name, x,y, w, h);

//        Point[] points = new Point[3];
//        points[0] = new Point(x+random.nextInt(w),y);
//        points[1] = new Point(x+w,y+random.nextInt(h));
//        points[2] = new Point(x,y+random.nextInt(h));
//        Polygon polygon = new Polygon(points);
//        sprite.setShape(polygon);

        sprite.setVelocity(20+random.nextInt(400),20+random.nextInt(400));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(random.nextInt()|0xFF000000);
        sprite.setPaint(paint);


        sprite.addBehavior(new Move());
        sprite.addBehavior(new ReboundWhenTouchEdge(game));

        return sprite;
    }

    private Sprite createRegularPolygonSprite() {

        int w = 80+random.nextInt(50);
        int edgeNums = 1+random.nextInt(10);
        int x = 200+random.nextInt(500);
        int y = 200+random.nextInt(500);

        String name = "RegularPolygon"+random.nextInt();
        spriteNames.add(name);

        RegularPolygonSprite sprite = new RegularPolygonSprite(name, x,y, w, edgeNums);
        sprite.setVelocity(20+random.nextInt(400),20+random.nextInt(400));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(random.nextInt()|0xFF000000);
        paint.setStyle(Paint.Style.FILL);
        sprite.setPaint(paint);

        sprite.addBehavior(new Move());
        sprite.addBehavior(new ReboundWhenTouchEdge(game));

        return sprite;
    }

    private Sprite randomCreateSprite() {
        int n = random.nextInt(3);
        switch (n) {
            case 0:
                return createCircleSprite();
            case 1:
                return createTriangleSprite();
            case 2:
                return createRegularPolygonSprite();
            default:
                return createCircleSprite();
        }
    }
}
