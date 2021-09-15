package com.example.tarea1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
// import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SecondActivity extends Activity {
    public static String eName = "name";
    public static String eLastName = "lastName";
    public static String eAge = "age";
    public static String eAddress = "address";
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

        E_Name.setText( fullname );
        E_Lastname.setText( lastname );
        E_Age.setText( age );
        E_Address.setText( address );

        // Crea una acción para el boton.
        intent.putExtra("Informacion", String.format( "%s %s registered.", fullname, lastname ));
        setResult(RESULT_OK, getIntent() );
        
        Button btnInformacion = findViewById(R.id.MoveActivityButton);
        btnInformacion.setOnClickListener( view -> {
            finish();
        } );
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
