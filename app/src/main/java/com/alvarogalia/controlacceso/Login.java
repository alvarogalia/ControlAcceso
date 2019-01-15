package com.alvarogalia.controlacceso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final String destino = getIntent().getStringExtra("DESTINO");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String password = preferences.getString("ADMINPASSWORD", "asdf1234");

        final EditText txtPassword = findViewById(R.id.txtPassword);
        Button btn = findViewById(R.id.btnIngresar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPassword.getText().toString().equals(password)){
                    if(destino.equals("CONFIG")){
                        Intent intent = new Intent(Login.this, Configuracion.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}
