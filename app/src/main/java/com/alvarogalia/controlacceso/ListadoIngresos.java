package com.alvarogalia.controlacceso;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alvarogalia.controlacceso.Obj.Visita;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ListadoIngresos extends AppCompatActivity {

    EditText txtDesdeAño;
    EditText txtDesdeMes;
    EditText txtDesdeDia;
    EditText txtDesdeHora;
    EditText txtDesdeMinuto;
    EditText txtHastaAño;
    EditText txtHastaMes;
    EditText txtHastaDia;
    EditText txtHastaHora;
    EditText txtHastaMinuto;

    String colorTblBackground;
    String colorTblBackgroundHead;
    String colorTblTextHead;
    String colorTblText;

    FirebaseDatabase database;
    String holding;
    String ubicacion;
    TableLayout tblIngresos;

    SharedPreferences preferences;
    private String año;
    private String mes;
    private String dia;
    private String hora;
    private String minuto;
    private String segundo;
    private String milisegundo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_ingresos);
        tblIngresos = findViewById(R.id.tblIngresos);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final ConstraintLayout llMain = findViewById(R.id.llMain);
        txtDesdeAño = findViewById(R.id.txtDesdeAño);
        txtDesdeMes = findViewById(R.id.txtDesdeMes);
        txtDesdeDia = findViewById(R.id.txtDesdeDia);
        txtDesdeHora = findViewById(R.id.txtDesdeHora);
        txtDesdeMinuto = findViewById(R.id.txtDesdeMinuto);

        txtHastaAño = findViewById(R.id.txtHastaAño);
        txtHastaMes = findViewById(R.id.txtHastaMes);
        txtHastaDia = findViewById(R.id.txtHastaDia);
        txtHastaHora = findViewById(R.id.txtHastaHora);
        txtHastaMinuto = findViewById(R.id.txtHastaMinuto);

        llMain.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF")));

        database = FirebaseDatabase.getInstance();

        holding = preferences.getString("HOLDING", "");
        ubicacion = preferences.getString("UBICACION", "");

        colorTblBackground = preferences.getString("COLOR_TBL_BACKGROUND", "#FFFFFF");
        colorTblBackgroundHead = preferences.getString("COLOR_TBL_BACKGROUNDHEAD", "#FFFFFF");
        colorTblTextHead = preferences.getString("COLOR_TBL_TEXTHEAD", "#111111");
        colorTblText = preferences.getString("COLOR_TBL_TEXT", "#111111");

        SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fechahora = formatLong.format(timestamp);

        LinearLayout ll = findViewById(R.id.llPrincipal);
        setChildrens(ll);

        año = fechahora.split(" ")[0].split("-")[0];
        mes = fechahora.split(" ")[0].split("-")[1];
        dia = fechahora.split(" ")[0].split("-")[2];
        hora = fechahora.split(" ")[1].split(":")[0];
        minuto = fechahora.split(" ")[1].split(":")[1];

        txtDesdeAño.setText(año);
        txtDesdeMes.setText(mes);
        txtDesdeDia.setText(dia);

        año = txtDesdeAño.getText().toString().trim();
        if(año.trim().equals("")){año = null;}
        mes = txtDesdeMes.getText().toString().trim();
        if(mes.trim().equals("")){mes = null;}
        dia = txtDesdeDia.getText().toString().trim();
        if(dia.trim().equals("")){dia = null;}
        hora = txtDesdeHora.getText().toString().trim();
        if(hora.trim().equals("")){hora = null;}
        minuto = txtDesdeMinuto.getText().toString().trim();
        if(minuto.trim().equals("")){minuto = null;}
        setRows();



        Button btnFiltrar = findViewById(R.id.btnFiltrar);
        btnFiltrar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    año = txtDesdeAño.getText().toString().trim();
                    if(año.trim().equals("")){año = null;}
                    mes = txtDesdeMes.getText().toString().trim();
                    if(mes.trim().equals("")){mes = null;}
                    dia = txtDesdeDia.getText().toString().trim();
                    if(dia.trim().equals("")){dia = null;}
                    hora = txtDesdeHora.getText().toString().trim();
                    if(hora.trim().equals("")){hora = null;}
                    minuto = txtDesdeMinuto.getText().toString().trim();
                    if(minuto.trim().equals("")){minuto = null;}
                    setRows();
                }
                return true;
            }
        });
    }

    public void setRows(){

        tblIngresos.removeAllViews();


        TableRow rowCabecera = new TableRow(ListadoIngresos.this);

        rowCabecera.setBackgroundColor(Color.parseColor(colorTblBackgroundHead));

        TextView tv0 = new TextView(ListadoIngresos.this);
        TextView tv1 = new TextView(ListadoIngresos.this);
        TextView tv2 = new TextView(ListadoIngresos.this);
        TextView tv3 = new TextView(ListadoIngresos.this);
        TextView tv4 = new TextView(ListadoIngresos.this);
        TextView tv5 = new TextView(ListadoIngresos.this);
        TextView tv6 = new TextView(ListadoIngresos.this);

        tv0.setText("FECHA\nHORA");
        tv1.setText("RUT");
        tv2.setText("NOMBRE");
        tv3.setText("DESTINO");
        tv4.setText("PPU");
        tv5.setText("TIPO VEHICULO");
        tv6.setText("COLOR");

        tv0.setTextColor(Color.parseColor(colorTblTextHead));
        tv1.setTextColor(Color.parseColor(colorTblTextHead));
        tv2.setTextColor(Color.parseColor(colorTblTextHead));
        tv3.setTextColor(Color.parseColor(colorTblTextHead));
        tv4.setTextColor(Color.parseColor(colorTblTextHead));
        tv5.setTextColor(Color.parseColor(colorTblTextHead));
        tv6.setTextColor(Color.parseColor(colorTblTextHead));

        rowCabecera.addView(tv0);
        rowCabecera.addView(new Space(this));
        rowCabecera.addView(tv1);
        rowCabecera.addView(new Space(this));
        rowCabecera.addView(tv2);
        rowCabecera.addView(new Space(this));
        rowCabecera.addView(tv3);
        rowCabecera.addView(new Space(this));
        rowCabecera.addView(tv4);
        rowCabecera.addView(new Space(this));
        rowCabecera.addView(tv5);
        rowCabecera.addView(new Space(this));
        rowCabecera.addView(tv6);

        tblIngresos.addView(rowCabecera);

        setLayoutStyle(1, tv0);
        setLayoutStyle(1, tv1);
        setLayoutStyle(1, tv2);
        setLayoutStyle(1, tv3);
        setLayoutStyle(1, tv4);
        setLayoutStyle(1, tv5);
        setLayoutStyle(1, tv6);

        DatabaseReference qrefHistNew = database.getReference("HOLDING").child(holding).child("VISITAS");
        if(año != null){
            qrefHistNew = qrefHistNew.child(año);
            if(mes != null){
                qrefHistNew = qrefHistNew.child(mes);
                if(dia != null){
                    qrefHistNew = qrefHistNew.child(dia);
                    if(hora != null){
                        qrefHistNew = qrefHistNew.child(hora);
                        if(minuto != null){
                            qrefHistNew = qrefHistNew.child(minuto);
                        }
                    }
                }
            }
        }

        qrefHistNew.addListenerForSingleValueEvent(new ValueEventListener() {



            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getRef().getPath().toString());

                if(minuto != null){
                    minuto = dataSnapshot.getKey();
                        for(DataSnapshot segundos : dataSnapshot.getChildren()){
                            segundo = segundos.getKey();
                            for(DataSnapshot millis : segundos.getChildren()){
                                Visita visita = millis.getValue(Visita.class);
                                addVisita(visita);
                            }
                        }
                }else{
                    if(hora != null){
                        hora = dataSnapshot.getKey();
                            for(DataSnapshot minutos : dataSnapshot.getChildren()){
                                minuto = minutos.getKey();
                                for(DataSnapshot segundos : minutos.getChildren()){
                                    segundo = segundos.getKey();
                                    for(DataSnapshot millis : segundos.getChildren()){
                                        Visita visita = millis.getValue(Visita.class);
                                        addVisita(visita);
                                    }
                                }
                            }
                    }else{
                        if(dia != null){
                            dia = dataSnapshot.getKey();
                                for(DataSnapshot horas : dataSnapshot.getChildren()){
                                    hora = horas.getKey();
                                    for(DataSnapshot minutos : horas.getChildren()){
                                        minuto = minutos.getKey();
                                        for(DataSnapshot segundos : minutos.getChildren()){
                                            segundo = segundos.getKey();
                                            for(DataSnapshot millis : segundos.getChildren()){
                                                Visita visita = millis.getValue(Visita.class);
                                                addVisita(visita);
                                            }
                                        }
                                    }
                                }
                        }else{
                            if(mes != null){
                                mes = dataSnapshot.getKey();
                                    for(DataSnapshot dias : dataSnapshot.getChildren()){
                                        dia = dias.getKey();
                                        for(DataSnapshot horas : dias.getChildren()){
                                            hora = horas.getKey();
                                            for(DataSnapshot minutos : horas.getChildren()){
                                                minuto = minutos.getKey();
                                                for(DataSnapshot segundos : minutos.getChildren()){
                                                    segundo = segundos.getKey();
                                                    for(DataSnapshot millis : segundos.getChildren()){
                                                        Visita visita = millis.getValue(Visita.class);
                                                        addVisita(visita);
                                                    }
                                                }
                                            }
                                        }
                                }
                            }else{
                                if(año != null){
                                    año = dataSnapshot.getKey();
                                        for(DataSnapshot meses : dataSnapshot.getChildren()){
                                            mes = meses.getKey();
                                            for(DataSnapshot dias : meses.getChildren()){
                                                dia = dias.getKey();
                                                for(DataSnapshot horas : dias.getChildren()){
                                                    hora = horas.getKey();
                                                    for(DataSnapshot minutos : horas.getChildren()){
                                                        minuto = minutos.getKey();
                                                        for(DataSnapshot segundos : minutos.getChildren()){
                                                            segundo = segundos.getKey();
                                                            for(DataSnapshot millis : segundos.getChildren()){
                                                                Visita visita = millis.getValue(Visita.class);
                                                                addVisita(visita);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                }else{
                                    for(DataSnapshot años : dataSnapshot.getChildren()){
                                        año = años.getKey();
                                        for(DataSnapshot meses : años.getChildren()){
                                            mes = meses.getKey();
                                            for(DataSnapshot dias : meses.getChildren()){
                                                dia = dias.getKey();
                                                for(DataSnapshot horas : dias.getChildren()){
                                                    hora = horas.getKey();
                                                    for(DataSnapshot minutos : horas.getChildren()){
                                                        minuto = minutos.getKey();
                                                        for(DataSnapshot segundos : minutos.getChildren()){
                                                            segundo = segundos.getKey();
                                                            for(DataSnapshot millis : segundos.getChildren()){
                                                                Visita visita = millis.getValue(Visita.class);
                                                                addVisita(visita);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                System.out.println("CANTIDAD ROWS:" + tblIngresos.getChildCount());
                for(int i = 0; i < tblIngresos.getChildCount();i++){
                    if(tblIngresos.getChildAt(i).getClass().getName().endsWith("TableRow")){
                        TableRow tableRow = (TableRow) tblIngresos.getChildAt(i);
                        System.out.println("CANTIDAD CELLS:" + tableRow.getChildCount());
                        for(int j = 0; j < tableRow.getChildCount();j++){
                            if(tableRow.getChildAt(j).getClass().getName().endsWith("TextView")){
                                TextView textView = (TextView) tableRow.getChildAt(j);
                                setLayoutStyle(1, textView);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {

            }
        });
    }
    public void addVisita(Visita visita){

        TableRow row = new TableRow(ListadoIngresos.this);
        TextView tv0 = new TextView(ListadoIngresos.this);

        tv0.setText(año+"-"+mes+"-"+dia+"\n"+hora+":"+minuto+":"+segundo);
        TextView tv1 = new TextView(ListadoIngresos.this);
        TextView tv2 = new TextView(ListadoIngresos.this);
        TextView tv3 = new TextView(ListadoIngresos.this);

        if(visita.PERSONA != null){
            tv1.setText(visita.PERSONA.rut+"-"+visita.PERSONA.DV);
            tv2.setText(visita.PERSONA.nombre);
            tv3.setText(visita.PERSONA.destino);
        }

        TextView tv4 = new TextView(ListadoIngresos.this);
        TextView tv5 = new TextView(ListadoIngresos.this);
        TextView tv6 = new TextView(ListadoIngresos.this);

        if(visita.VEHICULO != null){

            tv4.setText(visita.VEHICULO.ppu);
            tv5.setText(visita.VEHICULO.tipoVehiculo);
            tv6.setText(visita.VEHICULO.color);
        }

        row.addView(tv0);
        row.addView(new Space(this));
        row.addView(tv1);
        row.addView(new Space(this));
        row.addView(tv2);
        row.addView(new Space(this));
        row.addView(tv3);
        row.addView(new Space(this));
        row.addView(tv4);
        row.addView(new Space(this));
        row.addView(tv5);
        row.addView(new Space(this));
        row.addView(tv6);


        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(año+"-"+mes+"-"+dia+" "+hora+":"+minuto+":"+segundo);
            }
        });
        tblIngresos.addView(row);


    }

    private void setLayoutStyle(int i, TextView tv) {
        int rowHeight = Integer.parseInt(preferences.getString("TABLE_ROW_HEIGHT", "80"));
        int rowTextSize = Integer.parseInt(preferences.getString("TABLE_ROW_FONT_SIZE", "15"));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv.getLayoutParams();
        params.setMargins(i,i,i,i);
        tv.setLayoutParams(params);

        tv.setTextColor(Color.parseColor(colorTblText));
        tv.setBackgroundColor(Color.parseColor(colorTblBackground));
        tv.setHeight(rowHeight);
        tv.setTextSize(rowTextSize);
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