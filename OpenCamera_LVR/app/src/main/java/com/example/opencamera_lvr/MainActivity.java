package com.example.opencamera_lvr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import androidx.exifinterface.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

// import org.jetbrains.annotations.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends Activity {

    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_WRITE_STORAGE = 100;
    public static final int REQUEST_CODE_LOCATION = 200;
    public static final String IMAGE = "image";
    private File fileImage;
    private MyOnSuccessListener myClass;

    ImageView image;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        activeSuccessListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("OnCreate", "Hi men");

        // permission();

        setContentView(R.layout.activity_main);


        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        for (String str : permissions) {
            if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(permissions, REQUEST_CODE_WRITE_STORAGE);
            }
        }

        FloatingActionButton btnOpCam = findViewById(R.id.openCamera);

        btnOpCam.setOnClickListener(view -> {
            int permission = checkSelfPermission(Manifest.permission.CAMERA);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_CAMERA);
                return;
            }

            dispatchTakePictureIntent();
        });


        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                Log.i("MY_IMAGES", "YES MEN");
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
        GridAdapter adapter = new GridAdapter(this, gridItems);
        // Set the grid adapter.
        GridView gridView = (GridView) findViewById(R.id.ImageListing);
        gridView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle ext = data.getExtras();
//            Bitmap imageBitMap = (Bitmap) ext.get("data");
            //image.setImageBitmap(imageBitMap);
            if (!fileImage.exists()) {
                Log.i("MY_IMAGES", "Funaste");
                return;
            }

            try {
                ExifInterface exifInterface = new ExifInterface(fileImage);
                Location location = myClass.getLocation();
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE,String.valueOf(location.getLatitude()));
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE,String.valueOf(location.getLongitude()));
                exifInterface.saveAttributes();
                Toast.makeText(getBaseContext(), "Image Saved Successfully", Toast.LENGTH_LONG).show();
            } catch (IOException | NullPointerException e) {
                Toast.makeText(getBaseContext(), "Location Failed", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //byte[] byteArray = savedInstanceState.getByteArray(IMAGE);
        //Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //image.setImageBitmap (bitmap);
    }

    private byte[] convertImage2ByteArray(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = getBitmapFromDrawable(drawable);
        ByteArrayOutputStream byteAOut = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteAOut);

        return byteAOut.toByteArray();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permission() {
        int permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_STORAGE);
        }
    }

    private void loadImages() throws IOException {

        LinkedList<Long> ids = getIds();
        if (ids == null)
            return;

        ArrayList<GridViewItem> imageItems = loadImageGridData(ids);
        if (!imageItems.isEmpty()) {
            // Ok, now we have the array of Bitmaps to store, now we need to place them on the grid.
            setGridAdapter(imageItems);
        } else {
            // We've got no photos, inform the user about it.
            TextView noPhoto = findViewById(R.id.noPhoto);
            noPhoto.setVisibility(View.VISIBLE);
        }
    }


    private LinkedList<Long> getIds() {
        String[] columns = {MediaStore.Images.Media._ID};
        String order = MediaStore.Images.Media.DEFAULT_SORT_ORDER;
        String selection = MediaStore.Images.Media._ID;

        @SuppressLint("Recycle") Cursor cursor = getBaseContext()
                .getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, null, order);

        if (cursor == null) return null;

        LinkedList<Long> ids = new LinkedList<>();

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);

            int index2 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            long id = cursor.getLong(index2);
            ids.add(id);
        }

        cursor.close();

        return ids;
    }


    private Bitmap getBitmapFromId(long imageId) throws IOException {
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
        Bitmap bitmap;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            bitmap = this.getContentResolver().loadThumbnail(uri, new Size(100, 100), null);
        } else {
            Bitmap bitmap_aux = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap_aux, 100, 100);
        }
        return bitmap;
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File alt = new  File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
        Log.i("MY_IMAGES",alt.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            // File photoFile = null;
            fileImage = null;
            try {
                fileImage = createImageFile();
                Log.i("MY_IMAGES", fileImage.getAbsolutePath());
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (fileImage != null) {
                Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                        BuildConfig.APPLICATION_ID,
                        fileImage);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    public void activeSuccessListener() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myClass = new MyOnSuccessListener(getBaseContext());
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, myClass);
    }

}


class MyOnSuccessListener implements OnSuccessListener<Location> {
    Context context;
    private Location location;

    public MyOnSuccessListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            Toast.makeText(context,
                    String.format("long: %s, Lat: %s", location.getLongitude(),
                            location.getLatitude()), Toast.LENGTH_LONG).show();
            this.location = location;
        } else {
            Toast.makeText(context, "Nel Pa...", Toast.LENGTH_LONG).show();
        }
    }

    public Location getLocation() {
        return location;
    }
}
