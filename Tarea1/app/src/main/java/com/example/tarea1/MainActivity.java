package com.example.tarea1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TextView Name = findViewById(R.id.Name);
        TextView Lastname = findViewById(R.id.Lastname);
        TextView Age = findViewById(R.id.Age);
        TextView Address = findViewById(R.id.Address);

        EditText E_Name = findViewById(R.id.E_Name);
        EditText E_Lastname = findViewById(R.id.E_Lastname);
        EditText E_Age = findViewById(R.id.E_Age);
        EditText E_Address = findViewById(R.id.E_Address);

        ImageView Image = findViewById(R.id.Image);
    }
}
