package com.apptoeat.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.apptoeat.R;
import com.apptoeat.env.Entity;
import com.apptoeat.env.Flower;
import com.apptoeat.env.LivingEntity;
import com.apptoeat.env.World;
import com.apptoeat.env.proprties.CreationProperty;
import com.apptoeat.env.types.EntityType;
import com.apptoeat.threads.SimulationThread;
import com.apptoeat.menus.WorldCreator; // This is your world config object

import java.util.Random;

public class SimulationView extends SurfaceView implements SurfaceHolder.Callback {
    private SimulationThread simulationThread;
    private World world;
    private final int angleStep = 10;
    private long last = System.currentTimeMillis();

    public SimulationView(Context context, int wid, int height) {
        super(context);
        world = new World(wid, height, context);
        System.out.println("NEW WORLD!!!");
        getHolder().addCallback(this);
    }

    // This method replaces (or supplements) your default initEntities() method.
    // It initializes the simulation based on the given world configuration.
    public void initEntitiesFromWorld(WorldCreator worldConfig) {
        // Clear current entities, if needed
        // world.clearEntities();  // Assuming you have a method to clear them

        for (EntityType et : worldConfig.getEntityTypes()) {
            for (int i = 0; i < et.getCreationProperty().baseAmount; i++) {
                if (et.getCreationProperty().getTypeEnum() == CreationProperty.CreationPropertyType.Planet)
                    world.addEntity(new Flower(Math.random() * world.getWidth(), Math.random()  * world.getHeight(), world, et));
                else if (et.creationProperty.getTypeEnum() == CreationProperty.CreationPropertyType.EntityLiving)
                    world.addEntity(new LivingEntity(Math.random() * world.getWidth(), Math.random()  * world.getHeight(), world, et));
            }
            if (et.getCreationProperty().randomSpawn)
                if (et.getCreationProperty().getTypeEnum() == CreationProperty.CreationPropertyType.Planet)
                    world.getSpawners().add(new Flower(Math.random(), Math.random(), world, et));
                else if (et.creationProperty.getTypeEnum() == CreationProperty.CreationPropertyType.EntityLiving)
                    world.getSpawners().add(new LivingEntity(Math.random(), Math.random(), world, et));

        }

    }

    private static Bitmap  bg;                // cached once for all views
    private static Paint   bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void lazyInitBackground(int w,int h){
        if (bg != null || w==0 || h==0) return;

        Bitmap raw = BitmapFactory.decodeResource(
                getResources(), R.drawable.back2);               // your jpg / png
        bg = Bitmap.createScaledBitmap(raw, w, h, true);
        raw.recycle();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /* 1. lazy-load & scale background once ------------------------ */
        if (bg == null) {
            Bitmap raw = BitmapFactory.decodeResource(
                    getResources(), R.drawable.back2);          // put any 1080p jpg/png in res/drawable
            bg = Bitmap.createScaledBitmap(
                    raw, world.getWindowWidth(), world.getWindowHeight(), true);
            raw.recycle();                                      // free original
        }

        simulationThread = new SimulationThread(getHolder(), this);
        simulationThread.setRunning(true);
        simulationThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        simulationThread.setRunning(false);
        while (retry) {
            try {
                simulationThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // no-op
    }

    public void update() {
        long now = System.currentTimeMillis();
        world.update((now - last) / 20.0);
        last = now;
    }

    private static final float HUD_HEIGHT_DP = 10f;     // bar thickness

    private static final Paint HUD_BG  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint HUD_TXT = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint HUD_SEG = new Paint(Paint.ANTI_ALIAS_FLAG);

    /* top of SimulationView -------------------------------------------- */



    static {
        HUD_BG.setColor(Color.argb(90, 0, 0, 0));    // subtle translucent black
        HUD_TXT.setColor(Color.WHITE);
        HUD_TXT.setTextAlign(Paint.Align.CENTER);
        HUD_TXT.setTextSize(32f);                       // will be scaled by density
    }

    /* same fields/ctor as before … */

    /* ──────────────────────────────────────────────────────────────── */
    /* ▼ util: deterministic colour for an EntityType id               */
    /* ──────────────────────────────────────────────────────────────── */
    private static int colourForId(int id) {
        Random r = new Random(id);
        return Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;

        /* NEW – make sure bg exists exactly once */
        lazyInitBackground(canvas.getWidth(), canvas.getHeight());

        /* if it’s still null (e.g., missing drawable) fall back to colour */
        if (bg != null) {
            canvas.drawBitmap(bg, 0, 0, bgPaint);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        world.draw(canvas);

        /* 1 . collect counts ----------------------------------------- */
        SparseIntArray counts = new SparseIntArray();   // id → count
        for (Entity e : world.getEntities()) {
            counts.put(e.getType().getId(),
                    counts.get(e.getType().getId()) + 1);
        }
        int total = 0;
        for (int i = 0; i < counts.size(); i++) total += counts.valueAt(i);
        if (total == 0) return;                         // nothing alive → skip HUD

        /* 2 . layout -------------------------------------------------- */
        float density = getResources().getDisplayMetrics().density;
        float barH    = HUD_HEIGHT_DP * density;
        float y0      = 0;                              // pinned to top-edge
        float xCur    = 0;
        float fullW   = canvas.getWidth();

        /* 3 . draw background strip ---------------------------------- */
        canvas.drawRect(0, y0, fullW, y0 + barH, HUD_BG);

        /* 4 . draw each segment -------------------------------------- */
        for (int i = 0; i < counts.size(); i++) {
            int id  = counts.keyAt(i);
            int cnt = counts.valueAt(i);
            float segW = (cnt / (float) total) * fullW;

            HUD_SEG.setColor(colourForId(id));
            canvas.drawRect(xCur, y0, xCur + segW, y0 + barH, HUD_SEG);

            // label “×N” in the middle of the segment (skip if too thin)
            if (segW > 40 * density) {
                canvas.drawText("×" + cnt,
                        xCur + segW / 2,
                        y0 + barH * 0.7f, HUD_TXT);
            }
            xCur += segW;
        }

            // (Optional) Visualization of rays, etc.
            double originX = world.mapX(0.5);
            double originY = world.mapY(0.5);
            Paint rayPaint = new Paint();
            rayPaint.setColor(Color.RED);
            rayPaint.setStrokeWidth(2);
            Paint hitPaint = new Paint();
            hitPaint.setColor(Color.BLUE);
            Paint originPaint = new Paint();
            originPaint.setColor(Color.BLACK);
            canvas.drawCircle((float) originX, (float) originY, 8, originPaint);
            for (int angle = 0; angle < 360; angle += angleStep) {
                double rad = Math.toRadians(angle);
                double dx = Math.cos(rad);
                double dy = Math.sin(rad);
                Pair<Double, Integer> result = world.shootRay(originX, originY, dx, dy);
                double t = result.first;
                int hitIndex = result.second;
                float endX, endY;
                if (hitIndex != -1 && t >= 0) {
                    endX = (float) (originX + dx * t);
                    endY = (float) (originY + dy * t);
                    canvas.drawLine((float) originX, (float) originY, endX, endY, rayPaint);
                    canvas.drawCircle(endX, endY, 5, hitPaint);
                } else {
                    t = Math.max(world.getWindowWidth(), world.getWindowHeight());
                    endX = (float) (originX + dx * t);
                    endY = (float) (originY + dy * t);
                    canvas.drawLine((float) originX, (float) originY, endX, endY, rayPaint);
                }
            }

    }

    public void pause() {
        simulationThread.setRunning(false);
        try {
            simulationThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resumeSimulation() {
        simulationThread = new SimulationThread(getHolder(), this);
        simulationThread.setRunning(true);
        simulationThread.start();
    }
}
