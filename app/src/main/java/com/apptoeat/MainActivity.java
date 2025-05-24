package com.apptoeat;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apptoeat.env.types.EntityType;
import com.apptoeat.menus.WorldCreator;
import com.apptoeat.utils.WorldRepo;
import com.apptoeat.views.SimulationView;
import com.apptoeat.views.WorldCreatorActivity;
import com.apptoeat.views.adapters.WorldCreatorAdapter;
import com.apptoeat.views.adapters.WorldsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SimulationView simulationView;
    public static List<WorldCreator> worldCreators = new ArrayList<>();

    private Button btnNewWorld;
    private RecyclerView listWorlds;
    private WorldsAdapter adapter;

    @Override protected void onStart() {
        super.onStart();
        WorldRepo.fetchAll().addOnSuccessListener(list -> {
            MainActivity.worldCreators.clear();
            MainActivity.worldCreators.addAll(list);
            adapter.notifyDataSetChanged();
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.listWorlds);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WorldsAdapter(worldCreators, pos -> {
            WorldCreatorActivity.selectedWorldIndex = pos;
            startActivity(new Intent(this, WorldCreatorActivity.class));
        });
        rv.setAdapter(adapter);

        // Swipe to delete:
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView r, RecyclerView.ViewHolder vh,
                                  RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                WorldCreator wc = worldCreators.remove(pos);
                adapter.notifyItemRemoved(pos);

                WorldRepo.delete(wc)
                        .addOnFailureListener(e -> {
                            Log.e("MainActivity", "Delete failed", e);
                            Toast.makeText(MainActivity.this,
                                    "Delete failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        }).attachToRecyclerView(rv);

        FloatingActionButton fab = findViewById(R.id.fabAddWorld);
        fab.setOnClickListener(v -> {
            WorldCreator newWorld = new WorldCreator("World " + (worldCreators.size() + 1));
            worldCreators.add(newWorld);
            adapter.notifyItemInserted(worldCreators.size() - 1);
            WorldRepo.save(newWorld)
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    "Save failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
        });
    }

}