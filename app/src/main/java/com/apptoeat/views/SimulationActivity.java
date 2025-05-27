package com.apptoeat.views;


import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.apptoeat.R;
import com.apptoeat.env.types.EntityType;
import com.apptoeat.menus.WorldCreator;
import com.google.android.material.appbar.MaterialToolbar;

public class SimulationActivity extends AppCompatActivity {

    // Static field to receive the world configuration from WorldCreatorActivity
    public static WorldCreator worldConfig;

    private SimulationView simulationView;

    private void showGroupInfoDialog() {
        if (worldConfig == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_group_info, null);

        TextView tvEntityTypes = dialogView.findViewById(R.id.tvEntityTypes);
        TextView tvEntityCounts = dialogView.findViewById(R.id.tvEntityCounts);

        StringBuilder entityInfo = new StringBuilder();
        for (EntityType entityType : worldConfig.getEntityTypes()) {
            entityInfo.append("• ").append(entityType.getName()).append("\n");
            entityInfo.append("  Base Amount: ").append(entityType.getCreationProperty().baseAmount).append("\n");
            entityInfo.append("  Type: ").append(entityType.getCreationProperty().getTypeEnum()).append("\n\n");
        }

        tvEntityTypes.setText(entityInfo.toString());
        tvEntityCounts.setText("Total Entity Types: " + worldConfig.getEntityTypes().size());

        builder.setView(dialogView)
                .setTitle("World Information")
                .setPositiveButton("Close", null)
                .show();
    }

    public class LivingEntity extends Entity {
        protected Brain brain;
        protected double direction;
        protected double hunger = getType().getHealthProperty().initFood;

        public void stepForward(double dt) {
            // חישוב תנועה ויישום כוחות גרר
            double drag = type.getViewProperty().drag;
            velX *= drag * dt;
            velY *= drag * dt;

            // תנועה קדימה
            velX += Math.cos(Math.toRadians(direction)) * type.getViewProperty().movementSpeed;
            velY += Math.sin(Math.toRadians(direction)) * type.getViewProperty().movementSpeed;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        ImageButton btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> showGroupInfoDialog());

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
