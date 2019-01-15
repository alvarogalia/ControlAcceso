package com.alvarogalia.controlacceso;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alvarogalia.controlacceso.Obj.Visita;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ListadoIngresos extends AppCompatActivity {

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_ingresos);
        final TableLayout tblIngresos = findViewById(R.id.tblIngresos);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final ConstraintLayout llMain = findViewById(R.id.llMain);
        llMain.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF")));

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final String holding = preferences.getString("HOLDING", "");
        final String ubicacion = preferences.getString("UBICACION", "");

        final String colorTblBackground = preferences.getString("COLOR_TBL_BACKGROUND", "#FFFFFF");
        final String colorTblBackgroundHead = preferences.getString("COLOR_TBL_BACKGROUNDHEAD", "#FFFFFF");
        final String colorTblTextHead = preferences.getString("COLOR_TBL_TEXTHEAD", "#111111");
        final String colorTblText = preferences.getString("COLOR_TBL_TEXT", "#111111");

        SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fechahora = formatLong.format(timestamp);

        LinearLayout ll = findViewById(R.id.llPrincipal);
        setChildrens(ll);

        Query qrefHistNew = database.getReference("HOLDING").child(holding).child("VISITAS").child("TIMESTAMP").limitToLast(100);

        TableRow row = new TableRow(ListadoIngresos.this);

        row.setBackgroundColor(Color.parseColor(colorTblBackgroundHead));

        TextView tv0 = new TextView(ListadoIngresos.this);
        tv0.setText("FECHA HORA");

        TextView tv1 = new TextView(ListadoIngresos.this);
        tv1.setText("RUT");

        TextView tv2 = new TextView(ListadoIngresos.this);
        tv2.setText("NOMBRE");

        TextView tv3 = new TextView(ListadoIngresos.this);
        tv3.setText("DESTINO");

        tv0.setTextColor(Color.parseColor(colorTblTextHead));
        tv1.setTextColor(Color.parseColor(colorTblTextHead));
        tv2.setTextColor(Color.parseColor(colorTblTextHead));
        tv3.setTextColor(Color.parseColor(colorTblTextHead));

        row.addView(tv0);
        row.addView(tv1);
        row.addView(tv2);
        row.addView(tv3);

        tblIngresos.addView(row);

        qrefHistNew.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot ds, String string) {
                Visita visita = ds.getValue(Visita.class);

                TableRow row = new TableRow(ListadoIngresos.this);

                row.setBackgroundColor(Color.parseColor(colorTblBackground));

                TextView tv0 = new TextView(ListadoIngresos.this);
                tv0.setText(ds.getKey().toString());
                TextView tv1 = new TextView(ListadoIngresos.this);
                tv1.setText(visita.PERSONA.rut+"-"+visita.PERSONA.DV);
                TextView tv2 = new TextView(ListadoIngresos.this);
                tv2.setText(visita.PERSONA.nombre);
                TextView tv3 = new TextView(ListadoIngresos.this);
                tv3.setText(visita.PERSONA.destino);

                tv0.setTextColor(Color.parseColor(colorTblText));
                tv1.setTextColor(Color.parseColor(colorTblText));
                tv2.setTextColor(Color.parseColor(colorTblText));
                tv3.setTextColor(Color.parseColor(colorTblText));

                row.addView(tv0);
                row.addView(tv1);
                row.addView(tv2);
                row.addView(tv3);

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(ds.getKey());
                    }
                });
                tblIngresos.addView(row);
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {

            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {

            }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) {

            }

            @Override
            public void onCancelled(DatabaseError de) {

            }
        });
    }

    public void setChildrens(LinearLayout ll){
        for(int i = 0; i < ll.getChildCount(); i++){
            View view = ll.getChildAt(i);

            if(view.getClass().getName().endsWith("LinearLayout")){
                LinearLayout linear = (LinearLayout)view;
                setChildrens(linear);
            }

            if(view.getClass().getName().endsWith("Button")){
                Button boton = (Button) view;
                boton.getBackground().setColorFilter(Color.parseColor(preferences.getString("COLOR_MAINBUTTONSBACKGROUND", "#FFFFFF")),PorterDuff.Mode.MULTIPLY);
                boton.setTextColor(Color.parseColor(preferences.getString("COLOR_MAINBUTTONSTEXT", "#000000")));

            }
            if(view.getClass().getName().endsWith("TextView")){
                TextView textview = (TextView)view;
                textview.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINLABELBACKGROUND", "#FFFFFF")));
                textview.setTextColor(Color.parseColor(preferences.getString("COLOR_MAINLABELTEXT", "#FFFFFF")));
                System.out.println("Setting " + textview.getId() + " color " + preferences.getString("COLOR_MAINLABELTEXT", "#FFFFFF"));
            }
            if(view.getClass().getName().endsWith("EditText")){
                EditText editText = (EditText)view;
                String colorTxtBackground   = preferences.getString("COLOR_TXTBACKGROUND", "#FFFFFF");
                String colorTxtText         = preferences.getString("COLOR_TXTTEXT", "#000000");
                editText.setBackgroundColor(Color.parseColor(colorTxtBackground));
                editText.setTextColor(Color.parseColor(colorTxtText));
            }

        }
    }
}