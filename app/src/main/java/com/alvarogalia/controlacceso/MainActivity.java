package com.alvarogalia.controlacceso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvarogalia.controlacceso.Obj.Configuraciones;
import com.alvarogalia.controlacceso.Obj.Destino;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {


    private HorizontalScrollView linear;

    private String path = "rtsp://admin:Alvarito3@192.168.1.151/media/video1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final ConstraintLayout llMain = findViewById(R.id.llMain);
        llMain.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF")));


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Button botonIngresoVehiculo = findViewById(R.id.btnIngresoVehiculo);
        botonIngresoVehiculo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String fechahora = formatLong.format(timestamp);
                Intent intent = new Intent(MainActivity.this, Spotted.class);
                intent.putExtra("fechahora", fechahora);
                startActivity(intent);

            }
        });

        Button botonIngresoPersona = findViewById(R.id.btnIngresoPersona);
        botonIngresoPersona.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String fechahora = formatLong.format(timestamp);
                Intent intent = new Intent(MainActivity.this, IngresoPersona.class);
                intent.putExtra("fechahora", fechahora);
                startActivity(intent);
            }
        });

        Button btnConfiguracion = findViewById(R.id.btnConfiguración);
        btnConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.putExtra("DESTINO", "CONFIG");
                startActivity(intent);
            }
        });

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Cargando");
        progress.setMessage("Obteniendo información");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        final String holding = preferences.getString("HOLDING", "");
        final String ubicacion = preferences.getString("UBICACION", "");

        Query qrefConfiguraciones = database.getReference("HOLDING").child(holding).child("CONFIGURACIONES");
        qrefConfiguraciones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences.Editor edit = preferences.edit();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    edit.putString(snap.getKey(),  String.valueOf(snap.getValue()));
                }
                edit.commit();

                llMain.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF")));
                LinearLayout ll = findViewById(R.id.spottedList);
                for(int i = 0; i < ll.getChildCount(); i++){
                    View view = ll.getChildAt(i);

                    if(view.getClass().getName().endsWith("Button")){
                        Button boton = (Button) view;
                        boton.getBackground().setColorFilter(Color.parseColor(preferences.getString("COLOR_MAINBUTTONSBACKGROUND", "#FFFFFF")),PorterDuff.Mode.MULTIPLY);
                        boton.setTextColor(Color.parseColor(preferences.getString("COLOR_MAINBUTTONSTEXT", "#000000")));

                    }
                    if(view.getClass().getName().endsWith("TextView")){
                        TextView textview = (TextView)view;
                        textview.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINLABELBACKGROUND", "#FFFFFF")));
                        textview.setTextColor(Color.parseColor(preferences.getString("COLOR_MAINLABELTEXT", "#FFFFFF")));
                    }
                }

                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Button listadoIngresos = findViewById(R.id.btnListadoIngresos);
        listadoIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListadoIngresos.class);
                startActivity(intent);
            }
        });

    }
}
