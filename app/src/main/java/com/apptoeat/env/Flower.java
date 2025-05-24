package com.apptoeat.env;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.apptoeat.R;
import com.apptoeat.env.types.EntityType;

public class Flower extends Entity {
    private static Bitmap bitmap;
    public double scale;

    public Flower(double x, double y, World world, EntityType type) {
        super(x, y, world, type);

        scale = Math.random() / 10.0;

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(world.getContent().getResources(), R.drawable.my_flow2);
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 25, bitmap.getHeight() / 25, true);
        }

    }

    @Override
    public void update(double dt) {
        // Grow the flower at a dt-dependent rate
        if (scale < type.flowerProperty.MAX_SCALE) {
            scale += type.flowerProperty.growthRate * dt;
            // Optionally clamp the scale to its maximum value
            if (scale > type.flowerProperty.MAX_SCALE) {
                scale = type.flowerProperty.MAX_SCALE;
            }
        } else {
            // Attempt reproduction; adjust reproduction chance by dt
            if (Math.random() < type.multiplicationProperty.reproductionRate * dt) {
                // Number of times we try to find a non-colliding spot
                int attempts = 0;
                boolean placed = false;
                while (attempts < 10 && !placed) {
                    double newX = x + Math.random() * type.multiplicationProperty.offsetMul;
                    double newY = y + Math.random() * type.multiplicationProperty.offsetMul * ratio;

                    // Clamp coordinates so they don't exceed [0, 1]
                    newX = Math.max(0, Math.min(world.width, newX));
                    newY = Math.max(0, Math.min(world.height, newY));

                    // Use a small threshold for neighbor check; adjust if needed based on desired distance
                    double minDistance = 0.002;
                    boolean hasNeighbor = world.getNearByEntities(newX, newY, minDistance)
                            .stream()
                            .anyMatch(e -> e instanceof Flower && e.getId() != getId());

                    if (!hasNeighbor) {
                        // If a free spot is found, add a new flower
                        world.addEntity(new Flower(newX, newY, world, type));
                        placed = true;
                    }
                    attempts++;
                }
            }
        }
    }


    private static final Paint SHADOW_PAINT  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint BAR_BG_PAINT  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint BAR_FG_PAINT  = new Paint(Paint.ANTI_ALIAS_FLAG);

    static {
        SHADOW_PAINT.setColor(Color.argb(60, 0, 0, 0));        // soft black shadow
        BAR_BG_PAINT.setColor(Color.argb(120, 0, 0, 0));
        BAR_FG_PAINT.setColor(Color.rgb(120, 220, 120));       // gentle green
    }

    /* … ctor & update stay unchanged … */

    @Override
    public void draw(Canvas canvas) {
        /* --- size ----------------------------------------------------- */
        float bmpW = (float) (bitmap.getWidth()  * scale);
        float bmpH = (float) (bitmap.getHeight() * scale);

        float cx = world.mapX(x);
        float cy = world.mapY(y);

        /* --- drop shadow --------------------------------------------- */
        float shadowOffset = bmpH * 0.10f;
        RectF shadow = new RectF(
                cx - bmpW / 2,
                cy - bmpH / 2 + shadowOffset,
                cx + bmpW / 2,
                cy + bmpH / 2 + shadowOffset);
        canvas.drawOval(shadow, SHADOW_PAINT);

        /* --- flower bitmap ------------------------------------------- */
        RectF dest = new RectF(
                cx - bmpW / 2,
                cy - bmpH / 2,
                cx + bmpW / 2,
                cy + bmpH / 2);
        canvas.drawBitmap(bitmap, null, dest, null);

        /* --- health bar (same width as bitmap) ----------------------- */
        float barHeight = bmpH * 0.10f;           // 10 % of sprite height
        float barTop   = dest.top - barHeight - 4;

        canvas.drawRect(dest.left, barTop, dest.right, barTop + barHeight, BAR_BG_PAINT);

        float ratio = (float) (health / type.getHealthProperty().initHealth);
        ratio = Math.max(0, Math.min(1, ratio));

        canvas.drawRect(dest.left,
                barTop,
                dest.left + bmpW * ratio,
                barTop + barHeight,
                BAR_FG_PAINT);
    }

    @Override
    public Entity clone() {
        return new Flower(x, y, world, type);
    }

    @Override
    public RectF getBoundingBox() {
        float baseSize = Math.min(world.windowWidth, world.windowHeight) / 25f;
        float aspect = bitmap.getWidth() / (float) bitmap.getHeight();
        float scaledHeight = baseSize * (float) scale;
        float scaledWidth = scaledHeight * aspect;
        float centerX = world.mapX(x);
        float centerY = world.mapY(y);
        return new RectF(centerX - scaledWidth / 2, centerY - scaledHeight / 2,
                centerX + scaledWidth / 2, centerY + scaledHeight / 2);
    }
}
