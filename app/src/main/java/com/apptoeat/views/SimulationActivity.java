package com.apptoeat.views;


import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.apptoeat.R;
import com.apptoeat.menus.WorldCreator;
import com.google.android.material.appbar.MaterialToolbar;

public class SimulationActivity extends AppCompatActivity {

    // Static field to receive the world configuration from WorldCreatorActivity
    public static WorldCreator worldConfig;

    private SimulationView simulationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        /* toolbar ------------------------------------------------------- */
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // inside SimulationActivity.onCreate(), right after setContentView(...)
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(Color.TRANSPARENT);             // bar now glass
        window.setNavigationBarColor(Color.TRANSPARENT);         // optional

        /* (optional) make icons light or dark to match your scene */
        WindowInsetsControllerCompat ctl =
                WindowCompat.getInsetsController(window, window.getDecorView());
        ctl.setAppearanceLightStatusBars(false);   // false = white icons; true = dark


        /* surface view -------------------------------------------------- */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        simulationView = new SimulationView(this, dm.widthPixels, dm.heightPixels);
        if (worldConfig != null) simulationView.initEntitiesFromWorld(worldConfig);

        FrameLayout holder = findViewById(R.id.simContainer);
        holder.addView(simulationView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        simulationView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        simulationView.resumeSimulation();
    }
}
