package com.example.opencamera_lvr;

import android.graphics.Bitmap;

public class GridViewItem {
    private String path;
    private Bitmap image;

    public GridViewItem(String path, Bitmap image)
    {
        this.path = path;
        this.image = image;
    }

    public String getPath() { return this.path; }
    public Bitmap getImage() { return this.image; }
}
