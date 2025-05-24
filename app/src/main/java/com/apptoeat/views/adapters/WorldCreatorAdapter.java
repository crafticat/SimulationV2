package com.apptoeat.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;
import com.apptoeat.R;
import com.apptoeat.menus.WorldCreator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class WorldCreatorAdapter extends BaseAdapter {

    private Context context;
    private List<WorldCreator> worldList;
    private LayoutInflater inflater;

    public WorldCreatorAdapter(Context context, List<WorldCreator> worldList) {
        this.context = context;
        this.worldList = worldList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return worldList.size();
    }

    @Override
    public Object getItem(int position) {
        return worldList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 1) Extend our ViewHolder to reference all the new views
    private static class ViewHolder {
        TextView tvWorldName;
        TextView tvEntityCount;
        TextView tvWorldDescription;
        LinearProgressIndicator entityProgress;
        TextView tvProgressLabel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 2) Inflate item_world.xml if needed
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_world, parent, false);

            holder = new ViewHolder();
            holder.tvWorldName        = convertView.findViewById(R.id.tvWorldName);
            holder.tvEntityCount      = convertView.findViewById(R.id.tvEntityCount);
            holder.tvWorldDescription = convertView.findViewById(R.id.tvWorldDescription);
            holder.entityProgress     = convertView.findViewById(R.id.entityProgress);
            holder.tvProgressLabel    = convertView.findViewById(R.id.tvProgressLabel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 3) Get the current WorldCreator object
        WorldCreator wc = worldList.get(position);

        // 4) Populate the basic fields
        holder.tvWorldName.setText(wc.getName());
        holder.tvEntityCount.setText("Entities: " + wc.getEntityTypes().size());

        // Optional: Some descriptive text (you might store a getDescription() in WorldCreator)
        holder.tvWorldDescription.setText("Type: Procedural planet");
        // Or if you have wc.getDescription(), do: holder.tvWorldDescription.setText("Type: " + wc.getDescription());

        // 5) Setup progress indicator
        int currentCount = wc.getEntityTypes().size();
        int max = 100;  // or define your own logic for "max capacity"
        holder.entityProgress.setMax(max);
        holder.entityProgress.setProgress(currentCount);

        // 6) Show the progress label, e.g. "25/100 Entities"
        holder.tvProgressLabel.setText(currentCount + "/" + max + " Entities");

        return convertView;
    }
}
