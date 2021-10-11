package com.example.opencamera_lvr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class MyThread extends Thread{
    Activity activity;
    LinkedList<Long> bitmaps;
    public MyThread(Activity activity, LinkedList<Long> bitmaps) {
        this.bitmaps = bitmaps;
        this.activity = activity;
    }
    @Override
    public void run() {
        try {
            loadImageGrid();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    void loadImageGrid() throws IOException {
        TextView loadP = activity.findViewById(R.id.loadPhoto);
        loadP.setVisibility(View.VISIBLE);
        ArrayList<GridViewItem> items = loadImageGridData(bitmaps);
        if (!items.isEmpty()) {
            activity.runOnUiThread(() -> {
                setGridAdapter(items);
                Toast.makeText(activity, "Successfully uploaded images",
                        Toast.LENGTH_LONG).show();
            });
        } else {
            TextView noPhoto = activity.findViewById(R.id.noPhoto);
            noPhoto.setVisibility(View.VISIBLE);
        }
        loadP.setVisibility(View.INVISIBLE);
    }

    ArrayList<GridViewItem> loadImageGridData(LinkedList<Long> imgArray) throws IOException {
        ArrayList<GridViewItem> Imgs = new ArrayList<>();
        // Check if the list we obtained has any kind of content.
        if (imgArray.isEmpty()) {
            return Imgs;
        }

        // First obtain the location of the pictures that are available from the user.
        for (Long path : imgArray) {
            Bitmap bitmap = getBitmapFromId(path);
            if (bitmap != null) {
                //Log.i("MY_IMAGES", "YES MEN");
                //image.setImageBitmap(bitmap);
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, path);
                Imgs.add(new GridViewItem(uri.getPath(), bitmap));
            } else {
                Log.i("MY_IMAGES", "NO MEN");
            }
        }

        return Imgs;
    }

    void setGridAdapter(ArrayList<GridViewItem> gridItems) {
        // Create the adapter that will be used on the grid.
        GridAdapter adapter = new GridAdapter(activity, gridItems);
        // Set the grid adapter.
        GridView gridView = (GridView) activity.findViewById(R.id.ImageListing);
        gridView.setAdapter(adapter);
    }

    private Bitmap getBitmapFromId(long imageId) throws IOException {
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
        Bitmap bitmap;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            bitmap = activity.getContentResolver().loadThumbnail(uri, new Size(100, 100), null);
        } else {
            Bitmap bitmap_aux = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap_aux, 100, 100);
        }
        return bitmap;
    }
}
