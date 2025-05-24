package com.apptoeat.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptoeat.R;
import com.apptoeat.env.types.EntityType;

import java.util.List;

public class EntityTypeRecyclerAdapter extends RecyclerView.Adapter<EntityTypeRecyclerAdapter.ViewHolder> {

    private final List<EntityType> entityTypes;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public EntityTypeRecyclerAdapter(List<EntityType> entityTypes, OnItemClickListener listener) {
        this.entityTypes = entityTypes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entity_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntityType entityType = entityTypes.get(position);

        // Set the entity type name
        holder.txtName.setText(entityType.getName());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entityTypes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tvEntityTypeInfo);
        }
    }
}