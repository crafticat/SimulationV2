package com.apptoeat.views.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.apptoeat.R;
import com.apptoeat.menus.WorldCreator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class WorldsAdapter
        extends RecyclerView.Adapter<WorldsAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(int position);
    }

    private final List<WorldCreator> worlds;
    private final OnItemClick clickCb;

    public WorldsAdapter(List<WorldCreator> worlds, OnItemClick cb) {
        this.worlds = worlds;
        this.clickCb = cb;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorldName, tvEntityCount, tvWorldDescription, tvProgressLabel;
        LinearProgressIndicator entityProgress;

        public ViewHolder(View itemView, OnItemClick cb) {
            super(itemView);
            tvWorldName        = itemView.findViewById(R.id.tvWorldName);
            tvEntityCount      = itemView.findViewById(R.id.tvEntityCount);
            tvWorldDescription = itemView.findViewById(R.id.tvWorldDescription);
            entityProgress     = itemView.findViewById(R.id.entityProgress);
            tvProgressLabel    = itemView.findViewById(R.id.tvProgressLabel);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    cb.onClick(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_world, parent, false);
        return new ViewHolder(v, clickCb);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WorldCreator wc = worlds.get(position);

        holder.tvWorldName.setText(wc.getName());
        int count = wc.getEntityTypes().size();
        holder.tvEntityCount.setText("Entities: " + count);
        holder.tvWorldDescription.setText("Type: Procedural planet");

        holder.entityProgress.setMax(100);
        holder.entityProgress.setProgress(count);
        holder.tvProgressLabel.setText(count + "/100 Entities");
    }

    @Override
    public int getItemCount() {
        return worlds.size();
    }
}
