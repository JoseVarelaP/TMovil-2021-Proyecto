package com.example.opencamera_lvr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.LinkedList;

public class MainActivity extends Activity {

    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_ACTIVITY = 1;
    public static final int REQUEST_CODE_WRITE_STORAGE = 1002;
    public static final String IMAGE = "image";

    ImageView  image;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("OnCreate","Hi men");

        permission();

        setContentView(R.layout.activity_main);

        this.image = findViewById(R.id.imgCapture);

        Button btnOpCam = findViewById(R.id.btnCamera);

        btnOpCam.setOnClickListener(view -> {
            int permission = checkSelfPermission (Manifest.permission.CAMERA);
            if (permission != PackageManager.PERMISSION_GRANTED) { // if not, request it
                requestPermissions (new String [] { Manifest.permission.CAMERA },
                        REQUEST_CODE_CAMERA);
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
        });
        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ACTIVITY && resultCode == RESULT_OK) {
            Bundle ext = data.getExtras();
            Bitmap imageBitMap = (Bitmap) ext.get("data");
            image.setImageBitmap(imageBitMap);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (image.getDrawable() == null){
            return;
        }
        byte [] byteArray = convertImage2ByteArray(image);

        outState.putByteArray(IMAGE, byteArray);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        byte [] byteArray = savedInstanceState.getByteArray (IMAGE);
        Bitmap bitmap = BitmapFactory.decodeByteArray (byteArray, 0, byteArray.length);
        image.setImageBitmap (bitmap);
    }

    private byte [] convertImage2ByteArray (ImageView imageView) {
        Drawable drawable = imageView.getDrawable ();
        Bitmap bitmap = getBitmapFromDrawable (drawable);
        ByteArrayOutputStream byteAOut = new ByteArrayOutputStream ();
        bitmap.compress (Bitmap.CompressFormat.PNG, 100, byteAOut);

        return byteAOut.toByteArray ();
    }

    private Bitmap getBitmapFromDrawable (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return  ((BitmapDrawable) drawable).getBitmap ();
        }

        Bitmap bitmap = Bitmap.createBitmap (drawable.getIntrinsicWidth (), drawable.getIntrinsicHeight (), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas (bitmap);
        drawable.setBounds (0, 0, canvas.getWidth (), canvas.getHeight ());
        drawable.draw (canvas);

        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permission(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//            return;
        int permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_STORAGE);
        }
    }

    private void loadImages() throws IOException {
//        String [] columns = {MediaStore.Images.Media._ID};
//        String order = MediaStore.Images.Media.DEFAULT_SORT_ORDER;
//        String selection = MediaStore.Images.Media._ID;
//
//        @SuppressLint("Recycle") Cursor cursor = getBaseContext ()
//                .getContentResolver ()
//                .query (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, null, order);
//
//        if (cursor == null) return;
//
//        LinkedList<Long> ids = new LinkedList<>();
//
//        for (int i = 0; i < cursor.getCount(); i++) {
//            cursor.moveToPosition(i);
//
//            int index2 = cursor.getColumnIndexOrThrow (MediaStore.Images.Media._ID);
//            long id = cursor.getLong (index2);
//            ids.add(id);
//        }
//
//        cursor.close ();
//
//        for (Long s: ids) {
//            Log.i ("MY_IMAGES", s.toString());
//        }
//
//
//        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ids.getFirst());
//        Bitmap bitmap;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            bitmap = this.getContentResolver().loadThumbnail(uri, new Size(100, 100), null);
//            Log.i("MY_IMAGES", "NEW");
//        }else {
//            Bitmap bitmap_aux = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap_aux, 100, 100);
//            Log.i("MY_IMAGES", "OLD");
//        }
//
//        if (bitmap != null) {
//            Log.i("MY_IMAGES", "YES MEN");
//            image.setImageBitmap(bitmap);
//        } else {
//            Log.i("MY_IMAGES", uri.toString() + "NO MEN");
//        }

        LinkedList<Long> ids = getIds();
        if (ids == null)
            return;
        Bitmap bitmap = getBitmapFromId(ids.getFirst());
        if (bitmap != null) {
            Log.i("MY_IMAGES", "YES MEN");
            image.setImageBitmap(bitmap);
        } else {
            Log.i("MY_IMAGES", "NO MEN");
        }
    }


    private LinkedList<Long> getIds(){
        String [] columns = {MediaStore.Images.Media._ID};
        String order = MediaStore.Images.Media.DEFAULT_SORT_ORDER;
        String selection = MediaStore.Images.Media._ID;

        @SuppressLint("Recycle") Cursor cursor = getBaseContext ()
                .getContentResolver ()
                .query (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, null, order);

        if (cursor == null) return null;

        LinkedList<Long> ids = new LinkedList<>();

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);

            int index2 = cursor.getColumnIndexOrThrow (MediaStore.Images.Media._ID);
            long id = cursor.getLong (index2);
            ids.add(id);
        }

        cursor.close ();

        return ids;
    }


    private Bitmap getBitmapFromId(long imageId) throws IOException {
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
        Bitmap bitmap;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            bitmap = this.getContentResolver().loadThumbnail(uri, new Size(100, 100), null);
        }else {
            Bitmap bitmap_aux = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap_aux, 100, 100);
        }
        return bitmap;
    }

}
