package com.apptoeat.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apptoeat.DynamicSettingsActivity;
import com.apptoeat.MainActivity;
import com.apptoeat.R;
import com.apptoeat.env.types.EntityType;
import com.apptoeat.menus.WorldCreator;
import com.apptoeat.utils.WorldRepo;
import com.apptoeat.views.adapters.EntityTypeRecyclerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WorldCreatorActivity extends AppCompatActivity {

    public static int selectedWorldIndex = -1;

    public static WorldCreator getCurrentWorld() {
        return instance.currentWorld;
    }

    private static WorldCreatorActivity instance;

    private WorldCreator currentWorld;
    private RecyclerView recyclerEntityTypes;
    private FloatingActionButton btnNewEntityType;
    private MaterialButton btnLoadWorld;
    private EntityTypeRecyclerAdapter adapter;

    // We'll define a request code for editing
    private static final int REQ_EDIT_ENTITY = 100;

    private void persist() {
        WorldRepo.save(currentWorld)          // async save
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Save failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        persist();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        setContentView(R.layout.activity_world_creator);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Changed from ListView to RecyclerView
        recyclerEntityTypes = findViewById(R.id.recyclerEntityTypes);
        btnNewEntityType = findViewById(R.id.fabAddEntityType);
        btnLoadWorld = findViewById(R.id.btnLoadWorld);

        // Retrieve the selected world
        if (selectedWorldIndex >= 0 && selectedWorldIndex < MainActivity.worldCreators.size()) {
            currentWorld = MainActivity.worldCreators.get(selectedWorldIndex);
        } else {
            finish();
            return;
        }

        // Set up RecyclerView with LinearLayoutManager
        recyclerEntityTypes.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter with click listener
        adapter = new EntityTypeRecyclerAdapter(currentWorld.getEntityTypes(), position -> {
            // Handle item click - edit existing EntityType
            EntityType editing = currentWorld.getEntityTypes().get(position);
            DynamicSettingsActivity.entityTypeRef = editing;

            Intent i = new Intent(WorldCreatorActivity.this, DynamicSettingsActivity.class);
            startActivityForResult(i, REQ_EDIT_ENTITY);
        });
        recyclerEntityTypes.setAdapter(adapter);

        // Add swipe-to-delete functionality
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                // Remove the EntityType from the list
                EntityType removedType = currentWorld.getEntityTypes().remove(position);
                adapter.notifyItemRemoved(position);

                // Save the updated world
                persist();

                // Show a toast or snackbar to confirm deletion
                Toast.makeText(WorldCreatorActivity.this,
                        "Entity Type deleted",
                        Toast.LENGTH_SHORT).show();

                // Optional: Add undo functionality here using Snackbar
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerEntityTypes);

        // IMMEDIATELY ADD NEW EntityType, then open DynamicSettings
        btnNewEntityType.setOnClickListener(view -> {
            // 1) Create new
            EntityType newType = new EntityType();

            // 2) Add to the list so it's definitely stored
            currentWorld.getEntityTypes().add(newType);
            adapter.notifyItemInserted(currentWorld.getEntityTypes().size() - 1);

            // 3) Now let user edit in DynamicSettings
            DynamicSettingsActivity.entityTypeRef = newType;

            Intent i = new Intent(WorldCreatorActivity.this, DynamicSettingsActivity.class);
            startActivityForResult(i, REQ_EDIT_ENTITY);

            // Save after adding
            persist();
        });

        btnLoadWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch simulation activity.
                SimulationActivity.worldConfig = currentWorld;

                Intent intent = new Intent(WorldCreatorActivity.this, SimulationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the user saved changes in DynamicSettings
        if (requestCode == REQ_EDIT_ENTITY && resultCode == RESULT_OK) {
            // We already have the updated entity in currentWorld.getEntityTypes()
            // Just refresh the adapter
            adapter.notifyDataSetChanged();
        }
    }
}