package com.example.tarea1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
// import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SecondActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TextView E_Name, E_Lastname, E_Age, E_Address;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        E_Name = findViewById(R.id.Name);
        E_Lastname = findViewById(R.id.Lastname);
        E_Age = findViewById(R.id.Age);
        E_Address = findViewById(R.id.Address);

        // Extrae la información del intento
        Intent intent = getIntent();
        if (intent == null) return;

        String fullname = intent.getStringExtra( MainActivity.NAME );
        String lastname = intent.getStringExtra( MainActivity.LASTNAME );
        String age = intent.getStringExtra( MainActivity.AGE );
        String address = intent.getStringExtra( MainActivity.ADDRESS );

        E_Name.setText( fullname );
        E_Lastname.setText( lastname );
        E_Age.setText( age );
        E_Address.setText( address );
    }
}
