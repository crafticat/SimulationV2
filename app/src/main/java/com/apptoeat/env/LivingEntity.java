package com.apptoeat.env;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.apptoeat.env.ml.Brain;
import com.apptoeat.env.proprties.CreationProperty;
import com.apptoeat.env.types.EntityType;

import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivingEntity extends Entity {
    protected Brain brain;
    protected double direction; // in degrees
    protected double hunger = getType().getHealthProperty().initFood;

    public LivingEntity(double x, double y, World world, EntityType type) {
        super(x, y, world, type);
        this.brain = new Brain((int) (type.getViewProperty().fov /
                type.getViewProperty().acc * 2 + 1), 5, 4);    }

    public void stepForward(double dt) {
        double drag = type.getViewProperty().drag;

        velX *= drag * dt;
        velY *= drag * dt;

        velX += Math.cos(Math.toRadians(direction))
                * type.getViewProperty().movementSpeed;
        velY += Math.sin(Math.toRadians(direction))
                * type.getViewProperty().movementSpeed * ratio;

        x += velX * dt;
        y += velY * dt;

        if (x < 0) { x = 0;           velX *= -1; }
        if (x > world.width)  { x = world.width;  velX *= -1; }
        if (y < 0) { y = 0;           velY *= -1; }
        if (y > world.height) { y = world.height; velY *= -1; }

    }

    public void setVelocity(double dx, double dy) {
        this.velX = dx;
        this.velY = dy;
    }
    public void addVelocity(double dx, double dy) {
        this.velX += dx;
        this.velY += dy;
    }

    public double[] getProperties() {
        double maxHealth  = type.getHealthProperty().initHealth;
        double maxFood    = type.getHealthProperty().initFood;
        double maxSpeed   = type.getViewProperty().movementSpeed;

        return new double[]{
                health  / maxHealth,                 // 0-1
                hunger  / maxFood,                   // 0-1
                (velX / maxSpeed + 1) * 0.5,         // 0-1   (map –max→0 , +max→1)
                (velY / maxSpeed + 1) * 0.5          // 0-1
        };
    }


    @Override
    public void update(double dt) {
        brain.handleMovement(this, dt);

        double dx, dy;

        if (Math.abs(velX) > 1e-4 || Math.abs(velY) > 1e-4) {        // moving → face travel dir
            double len = Math.hypot(velX, velY);
            dx = velX / len;
            dy = velY / len;
            direction = Math.toDegrees(Math.atan2(dy, dx));          // keep `direction` in sync
        } else {                                                     // idle → fall back to heading
            double rad = Math.toRadians(direction);
            dx = Math.cos(rad);
            dy = Math.sin(rad);
        }


        var hit = world.shootRayEntity(
                world.mapX(x),        // origin in pixels
                world.mapY(y),
                dx, dy,
                type.getId());        // don’t hit ourselves

        double reach = type.getHealthProperty().reach;
        Entity target = hit.second;

        if (target != null && hit.first <= reach) {
            boolean cantEat = target.getType().getCreationProperty().getTypeEnum() == CreationProperty.CreationPropertyType.Planet && type.getHealthProperty().carnivore;
            if (!cantEat) {
                // apply damage
                target.damage(type.getHealthProperty().attackDamage * dt);

                if (target instanceof LivingEntity) {
                    double kb = type.getHealthProperty().knockback;
                    ((LivingEntity) (target)).addVelocity(dx * kb * dt, dy * kb * dt);
                }

                // feed on kill
                if (!target.isAlive()) {
                    hunger += target.getType().getHealthProperty().food;
                    hunger = Math.min(hunger, type.getHealthProperty().initFood);
                }
            }
        }

        hunger -= type.getHealthProperty().hungerDec * dt;
        if (hunger < 0) {
            damage(type.getHealthProperty().hungerDamage * dt);
            hunger = 0;
        }

        boolean healthy = health  > 0.5 * type.getHealthProperty().initHealth;
        boolean fed     = hunger  > 0.5 * type.getHealthProperty().initFood;

        if (healthy && fed &&
                Math.random() < type.getMultiplicationProperty().reproductionRate * dt) {
            reproduce();
        }
    }


        private static final Paint BODY_PAINT      = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static final Paint OUTLINE_PAINT   = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static final Paint HEALTH_BG_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static final Paint HEALTH_FG_PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static final Paint DIR_PAINT       = new Paint(Paint.ANTI_ALIAS_FLAG);

        static {                     // one-time static init
            OUTLINE_PAINT.setStyle(Paint.Style.STROKE);
            OUTLINE_PAINT.setStrokeWidth(2);

            HEALTH_BG_PAINT.setColor(Color.argb(120, 0, 0, 0));      // translucent black
            HEALTH_BG_PAINT.setStyle(Paint.Style.FILL);

            HEALTH_FG_PAINT.setStyle(Paint.Style.FILL);

            DIR_PAINT.setStrokeWidth(3);
        }


        @Override
        public void draw(Canvas canvas) {
            final float cx = world.mapX(x);
            final float cy = world.mapY(y);
            final float radius = Math.min(world.windowWidth, world.windowHeight) / 50f;

            /* ── body ────────────────────────────────────────────────────────── */
            int base = getRandomColorForId(getType().id);
            BODY_PAINT.setColor(base);
            OUTLINE_PAINT.setColor(Color.BLACK);

            canvas.drawCircle(cx, cy, radius, BODY_PAINT);
            canvas.drawCircle(cx, cy, radius, OUTLINE_PAINT);         // thin outline

            /* ── direction “nose” (short line) ──────────────────────────────── */
            double rad = Math.toRadians(direction);
            float nose = radius * 1.3f;
            canvas.drawLine(
                    cx, cy,
                    (float) (cx + Math.cos(rad) * nose),
                    (float) (cy + Math.sin(rad) * nose),
                    DIR_PAINT);

            /* ── health bar ─────────────────────────────────────────────────── */
            final float barWidth  = radius * 1.8f;      // wider than the circle
            final float barHeight = radius * 0.25f;
            final float barLeft   = cx - barWidth / 2;
            final float barTop    = cy - radius - barHeight - 4; // 4px gap above body

            // background
            canvas.drawRect(barLeft, barTop, barLeft + barWidth, barTop + barHeight,
                    HEALTH_BG_PAINT);

            // foreground (green→red gradient by health ratio)
            float healthRatio = (float) (health / getType().getHealthProperty().initHealth);
            healthRatio = Math.max(0, Math.min(1, healthRatio));

            int fgColor = Color.rgb(
                    (int) ((1 - healthRatio) * 255),      // red  ↑ when low
                    (int) (healthRatio * 200),            // green ↑ when healthy
                    50);                                  // constant dark-blue hint
            HEALTH_FG_PAINT.setColor(fgColor);

            canvas.drawRect(barLeft,
                    barTop,
                    barLeft + barWidth * healthRatio,
                    barTop + barHeight,
                    HEALTH_FG_PAINT);
        }


    @Override
    public Entity clone() {
        return new LivingEntity(x, y, world, type);
    }

    public void reproduce() {
        LivingEntity typ = new LivingEntity(Math.random() * type.multiplicationProperty.offsetMul + x,
                Math.random() * type.multiplicationProperty.offsetMul * ratio + y,
                world, type);
        typ.brain.setNeuralNetwork(brain.getNeuralNetwork().clone().mutate(0.01));

        world.addEntity(typ);
    }

    // Helper method: Deterministically generate a random color for a given id.
    private int getRandomColorForId(int id) {
        Random rand = new Random(id); // use id as seed so the same id returns the same color
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        return Color.rgb(r, g, b);
    }
    @Override
    public RectF getBoundingBox() {
        // Define a fixed radius for the living entity.
        // Here we use the same radius as the one used in draw().
        float radius = Math.min(world.windowWidth, world.windowHeight) / 50f;

        // Convert the normalized (x, y) coordinates to pixel coordinates.
        float centerX = world.mapX(x);
        float centerY = world.mapY(y);

        // Build a rectangle: left, top, right, bottom.
        return new RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );
    }

}
