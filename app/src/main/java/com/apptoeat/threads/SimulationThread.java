package com.apptoeat.threads;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.apptoeat.views.SimulationView;

public class SimulationThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private SimulationView simulationView;
    private boolean running;
    public void setRunning(boolean running) {
        this.running = running;
    }

    public SimulationThread(SurfaceHolder surfaceHolder, SimulationView simulationView) {
        this.surfaceHolder = surfaceHolder;
        this.simulationView = simulationView;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (running) {
            // Check if the underlying surface is valid
            if (!surfaceHolder.getSurface().isValid()) {
                continue;
            }
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                // If canvas is null, skip this iteration to avoid drawing on a null canvas
                if (canvas == null) {
                    continue;
                }
                synchronized (surfaceHolder) {
                    simulationView.update();
                    simulationView.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

}
