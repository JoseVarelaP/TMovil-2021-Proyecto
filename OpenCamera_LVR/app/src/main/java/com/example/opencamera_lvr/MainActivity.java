package com.example.opencamera_lvr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;

import androidx.exifinterface.media.ExifInterface;

import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends Activity {

    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_WRITE_STORAGE = 100;
    public static final int REQUEST_CODE_LOCATION = 200;
    private static final float LOCATION_REFRESH_DISTANCE = 10;
    private static final long LOCATION_REFRESH_TIME = 2;
    private File fileImage;
    private MyThread myThread;
    private MyOnSuccessListener myClass;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TEST_MY", "OnResume");
        activeSuccessListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TEST_MY", "OnDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("TEST_MY", "OnPause");
//        if (myThread != null) {
//            if (myThread.isAlive()) {
//                Log.i("TEST_MY", "OnPause2");
//                // myThread.interrupt();
//            } else myThread = null;
//        }
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
            while (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
//            try {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    try {
//                        loadImages();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (ArrayIndexOutOfBoundsException exception) {
//                exception.printStackTrace();
//            }
//        }
//    }

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
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,String.valueOf(location.getLatitude()));
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,String.valueOf(location.getLongitude()));
                exifInterface.saveAttributes();

                //Nos sirve para comprobar que, efectivamente, los datos fueron agregados en la imagen
                Log.d("Latitud: ", exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
                Log.d("Longitud: ", exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));


                Toast.makeText(getBaseContext(), "Image Saved Successfully", Toast.LENGTH_LONG).show();
                // Reload the list to show the new images with the others.
                try {
                    loadImages();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException | NullPointerException e) {
                Toast.makeText(getBaseContext(), "Location Failed", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void loadImages() throws IOException {

        LinkedList<Long> ids = getIds();
        if (ids == null)
            return;

        myThread = new MyThread(this, ids);
        myThread.start();
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


    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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

    public LocationListener mLocationListener = location -> {
        Log.d("LocationListener","Pos changed.");
        Log.d("LocationListener",Double.toString(location.getLatitude()));
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Log.d("dispatchTakePictureInt", "Starting...");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("dispatchTakePictureInt", "Camera Activity Intent available.");
            // Create the File where the photo should go
            // File photoFile = null;
            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
                // Stop if no permission is given.
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
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
