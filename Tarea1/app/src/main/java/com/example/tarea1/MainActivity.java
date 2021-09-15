package com.example.tarea1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainActivity extends Activity {
    public static String eName = "name";
    public static String eLastName = "lastName";
    public static String eAge = "age";
    public static String eAddress = "address";

    EditText E_Name, E_Lastname, E_Age, E_Address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        E_Name = findViewById(R.id.E_Name);
        E_Lastname = findViewById(R.id.E_Lastname);
        E_Age = findViewById(R.id.E_Age);
        E_Address = findViewById(R.id.E_Address);

        ImageView Image = findViewById(R.id.Image);

        // Crea una acción para el boton.
        Button btnInformacion = findViewById(R.id.MoveActivityButton);
        btnInformacion.setOnClickListener( view -> {
            Intent intent = new Intent( getBaseContext(), SecondActivity.class );
            intent.putExtra( eName, E_Name.getText().toString() );
            intent.putExtra( eLastName, E_Lastname.getText().toString() );
            intent.putExtra( eAge, E_Age.getText().toString() );
            intent.putExtra( eAddress, E_Address.getText().toString() );

            startActivity(intent);
        } );
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("Restore!!");
        outState.putString("eName", E_Name.getText().toString());
        outState.putString("eLastName", E_Lastname.getText().toString());
        outState.putString("eAge", E_Age.getText().toString());
        outState.putString("eAddress", E_Address.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String name = savedInstanceState.getString("eName");
        E_Name.setText(name);

        String lastName = savedInstanceState.getString("eLastName");
        E_Name.setText(lastName);

        String age = savedInstanceState.getString("eAge");
        E_Name.setText(age);

        String address = savedInstanceState.getString("eAddress");
        E_Name.setText(address);
    }
}
