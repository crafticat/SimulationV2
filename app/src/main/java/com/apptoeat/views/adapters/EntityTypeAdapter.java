package com.apptoeat.views.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import com.apptoeat.R;
import com.apptoeat.env.types.EntityType;

import java.util.List;
public class EntityTypeAdapter extends BaseAdapter {

    private Context context;
    private List<EntityType> entityTypeList;
    private LayoutInflater inflater;

    public EntityTypeAdapter(Context context, List<EntityType> entityTypeList) {
        this.context = context;
        this.entityTypeList = entityTypeList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return entityTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return entityTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView tvEntityTypeInfo;
        TextView tvEntityTypeDetails;
        // Or a progress bar, a switch, etc.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_entity_type, parent, false);
            holder = new ViewHolder();
            holder.tvEntityTypeInfo    = convertView.findViewById(R.id.tvEntityTypeInfo);
            holder.tvEntityTypeDetails = convertView.findViewById(R.id.tvEntityTypeDetails);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EntityType et = entityTypeList.get(position);

        holder.tvEntityTypeInfo.setText("EntityType: " + et.getId());
        // or some name property if you have it

        holder.tvEntityTypeDetails.setText("Type: " + et.getCreationProperty().getTypeEnum().toString());
        // or something else relevant

        return convertView;
    }
}
