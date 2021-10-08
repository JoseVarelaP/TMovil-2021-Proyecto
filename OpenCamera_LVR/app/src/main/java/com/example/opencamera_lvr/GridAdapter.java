package com.example.opencamera_lvr;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<GridViewItem> items;

    public GridAdapter(Context context, List<GridViewItem> items) {
        this.items = items;
        // Get the inflater service from the main context given from the constuctor.
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int i) { return items.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if( convertView == null )
        {
            convertView = inflater.inflate(R.layout.grid_item, null);
        }

        ImageView imgView = (ImageView) convertView.findViewById(R.id.image);
        Bitmap img = items.get(position).getImage();

        if(img != null)
        {
            imgView.setImageBitmap(img);
        } else {
            // Nothing, we don't really need to show anything.
        }

        return convertView;
    }
}
