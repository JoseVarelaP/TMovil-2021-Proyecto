package com.example.tarea1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
// import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SecondActivity extends Activity {
    TextView E_Name, E_Lastname, E_Age, E_Address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        E_Name = findViewById(R.id.E_Name);
        E_Lastname = findViewById(R.id.E_Lastname);
        E_Age = findViewById(R.id.E_Age);
        E_Address = findViewById(R.id.E_Address);

        // Extrae la información del intento
        Intent intent = getIntent();
        if (intent == null) return;

        String fullname = intent.getStringExtra( MainActivity.eName );
        String lastname = intent.getStringExtra( MainActivity.eLastName );
        String age = intent.getStringExtra( MainActivity.eAge );
        String address = intent.getStringExtra( MainActivity.eAddress );

        E_Name.setText( String.format( "%s: %s", E_Name.getText() , fullname ) );
        E_Lastname.setText( String.format( "%s: %s", E_Lastname.getText() , lastname ) );
        E_Age.setText( String.format( "%s: %s", E_Age.getText() , age ) );
        E_Address.setText( String.format( "%s: %s", E_Address.getText() , address ) );
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("Save!!");
        outState.putString("eName", E_Name.getText().toString());
        outState.putString("eLastName", E_Lastname.getText().toString());
        outState.putString("eAge", E_Age.getText().toString());
        outState.putString("eAddress", E_Address.getText().toString());
    }
}
