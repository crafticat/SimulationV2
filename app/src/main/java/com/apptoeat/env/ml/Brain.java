package com.apptoeat.env.ml;

import android.util.Pair;

import com.apptoeat.env.LivingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Brain {
    protected NeuralNetwork neuralNetwork;
    protected int amountTypes, amountDir;
    private int propertiesAmount;

    public Brain(int directions, int types, int properties) {
        // Array of [distance, type]
        int input = directions * types + directions;
        int hidden = input * 2;
        int output = directions;
        neuralNetwork = new NeuralNetwork(input + properties,hidden,output);
        this.propertiesAmount = properties;
        amountTypes = types;
        amountDir = directions;
    }

    public int getDirection(double[] distances, int[] types, double[] properties) {
        double[] input = new double[distances.length * amountTypes + distances.length + propertiesAmount];
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < amountTypes; j++) {
                input[i * amountTypes + j] = types[i] == j ? 1 : 0;
            }
        }
        for (int i = 0; i < distances.length; i++) {
            input[distances.length * amountTypes + i] = distances[i];
        }
        for (int i= 0 ; i < propertiesAmount;i++)
            input[distances.length * amountTypes + distances.length + i] = properties[i];

        double[] output = neuralNetwork.feedForward(input);

        int maxIndex = 0;
        double maxValue = output[0];
        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxValue) {
                maxValue = output[i];
                maxIndex = i;
            }

        }
        return maxIndex;
    }

    public Pair<Double, Integer> shootRay(LivingEntity entity, double direction) {
        double rad = Math.toRadians(direction);
        double dx = Math.cos(rad);
        double dy = Math.sin(rad);

        return entity.getWorld().shootRay(entity.getWorld().mapX(entity.getX()), entity.getWorld().mapY(entity.getY()), dx, dy, entity.getType().getId());
    }
    private static final double NO_HIT = 1.0;            // "nothing out there"

    public void handleMovement(LivingEntity entity, double dt) {
        double fov = entity.getType().getViewProperty().fov;
        double[] distances = new double[amountDir];
        int[] types = new int[amountDir];
        double[] newDirections = new double[amountDir];

        // at the top of Brain for convenience

// …inside handleMovement(…)
        double maxVisionPx = Math.hypot(
                entity.getWorld().getWindowHeight(),
                entity.getWorld().getWindowHeight());
        int iter = 0;

        for (double a = -fov; a <= fov; a += entity.getType().getViewProperty().acc) {
            Pair<Double, Integer> hit = shootRay(entity, a + entity.getDirection());

            /* distance ------------------------------------------------------ */
            double d;
            if (Double.isInfinite(hit.first)) {              // nothing hit
                d = NO_HIT;
            } else {
                d = Math.min(hit.first, maxVisionPx) / maxVisionPx;   // 0…1
            }
            distances[iter] = d;

            /* type ---------------------------------------------------------- */
            int t = hit.second;
            types[iter] = (t >= 0 && t < amountTypes) ? t : (amountTypes - 1); // “unknown” bucket

            newDirections[iter] = a + entity.getDirection();
            iter++;
        }


        int direction = getDirection(distances, types, entity.getProperties());
        entity.setDirection(newDirections[direction]);

        // Step forward
        entity.stepForward(dt);
    }
}
