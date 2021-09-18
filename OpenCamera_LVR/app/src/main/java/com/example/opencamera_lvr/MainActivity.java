package com.example.opencamera_lvr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {

    public static final int REQUEST_CODE_CAMERA = 1001;
    public static final int REQUEST_CODE_ACTIVITY = 1;
    public static final String IMAGE = "image";

    ImageView  image;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}
