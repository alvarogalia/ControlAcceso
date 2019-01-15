package com.alvarogalia.controlacceso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.alvarogalia.controlacceso.Obj.Destino;
import com.alvarogalia.controlacceso.Obj.VisitaPersona;
import com.alvarogalia.controlacceso.Util.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class IngresoPersona extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_persona);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Cargando");
        progress.setMessage("Obteniendo información");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final List<String> listDestino = new LinkedList<>();
        final String ppu = getIntent().getStringExtra("ppu");
        final String fechahora = getIntent().getStringExtra("fechahora");
        final String holding = preferences.getString("HOLDING", "");

        final ConstraintLayout llMain   = findViewById(R.id.llMain);
        final EditText txtRut           = findViewById(R.id.txtRut);
        final EditText txtDV            = findViewById(R.id.txtDV);
        final EditText txtNombre        = findViewById(R.id.txtNombre);
        final TextView textView1        = findViewById(R.id.ppuch1);
        final TextView textView2        = findViewById(R.id.ppuch2);
        final TextView textView3        = findViewById(R.id.ppuch3);
        final TextView textView4        = findViewById(R.id.ppuch4);
        final TextView textView5        = findViewById(R.id.ppuch5);
        final TextView textView6        = findViewById(R.id.ppuch6);
        final TextView tv1              = findViewById(R.id.lblRut);
        final TextView tv2              = findViewById(R.id.lblNombre);
        final TextView tv3              = findViewById(R.id.lblDestino);
        final TextView tv4              = findViewById(R.id.lblGuion);
        final Spinner spinner           = findViewById(R.id.spinDestino);
        final LinearLayout llParent     = findViewById(R.id.llParent);
        final LinearLayout llPPU        = findViewById(R.id.llPPU);
        final Button btnVolver          = findViewById(R.id.btnVolver);
        final Button btnSiguiente       = findViewById(R.id.btnSiguiente);

        String colorMainBackground  = preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF");
        String colorLabelBackground = preferences.getString("COLOR_MAINLABELBACKGROUND", "#FFFFFF");
        String colorLabelFont       = preferences.getString("COLOR_MAINLABELTEXT", "#000000");
        String colorTxtBackground   = preferences.getString("COLOR_TXTBACKGROUND", "#FFFFFF");
        String colorTxtText         = preferences.getString("COLOR_TXTTEXT", "#000000");
        String colorPPU             = preferences.getString("COLOR_PPUBACKGROUND", "#FFFFFF");
        String colorPPUText         = preferences.getString("COLOR_PPUTEXT", "#000000");
        String colorBtnNOBackground = preferences.getString("COLOR_BTNNO_BACKGROUND", "#FFFFFF");
        String colorBtnNOText       = preferences.getString("COLOR_BTNNO_TEXT", "#000000");
        String colorBtnSIBackground = preferences.getString("COLOR_BTNSI_BACKGROUND", "#FFFFFF");
        String colorBtnSIText       = preferences.getString("COLOR_BTNSI_TEXT", "#000000");

        btnSiguiente.getBackground().setColorFilter(Color.parseColor(colorBtnSIBackground),PorterDuff.Mode.MULTIPLY);
        btnSiguiente.setTextColor(Color.parseColor(colorBtnSIText));
        btnVolver.getBackground().setColorFilter(Color.parseColor(colorBtnNOBackground),PorterDuff.Mode.MULTIPLY);
        btnVolver.setTextColor(Color.parseColor(colorBtnNOText));
        llMain.setBackgroundColor(Color.parseColor(colorMainBackground));
        tv1.setBackgroundColor(Color.parseColor(colorLabelBackground));
        tv1.setTextColor(Color.parseColor(colorLabelFont));
        tv2.setBackgroundColor(Color.parseColor(colorLabelBackground));
        tv2.setTextColor(Color.parseColor(colorLabelFont));
        tv3.setBackgroundColor(Color.parseColor(colorLabelBackground));
        tv3.setTextColor(Color.parseColor(colorLabelFont));
        tv4.setBackgroundColor(Color.parseColor(colorLabelBackground));
        tv4.setTextColor(Color.parseColor(colorLabelFont));
        txtRut.setBackgroundColor(Color.parseColor(colorTxtBackground));
        txtRut.setTextColor(Color.parseColor(colorTxtText));
        txtDV.setBackgroundColor(Color.parseColor(colorTxtBackground));
        txtDV.setTextColor(Color.parseColor(colorTxtText));
        txtNombre.setBackgroundColor(Color.parseColor(colorTxtBackground));
        txtNombre.setTextColor(Color.parseColor(colorTxtText));
        llPPU.setBackgroundColor(Color.parseColor(colorPPU));
        textView1.setTextColor(Color.parseColor(colorPPUText));
        textView2.setTextColor(Color.parseColor(colorPPUText));
        textView3.setTextColor(Color.parseColor(colorPPUText));
        textView4.setTextColor(Color.parseColor(colorPPUText));
        textView5.setTextColor(Color.parseColor(colorPPUText));
        textView6.setTextColor(Color.parseColor(colorPPUText));

        if(ppu != null){
            textView1.setText(String.valueOf(ppu.charAt(0)));
            textView2.setText(String.valueOf(ppu.charAt(1)));
            textView3.setText(String.valueOf(ppu.charAt(2)));
            textView4.setText(String.valueOf(ppu.charAt(3)));
            textView5.setText(String.valueOf(ppu.charAt(4)));
            textView6.setText(String.valueOf(ppu.charAt(5)));

            if(!ppu.equals("      ")){
                textView1.setEnabled(false);
                textView2.setEnabled(false);
                textView3.setEnabled(false);
                textView4.setEnabled(false);
                textView5.setEnabled(false);
                textView6.setEnabled(false);
            }

            textView1.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(1)});
            textView2.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(1)});
            textView3.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(1)});
            textView4.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(1)});
            textView5.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(1)});
            textView6.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(1)});


        }else{
            llParent.removeView(llPPU);
        }


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query qDestino= database.getReference("HOLDING").child(holding).child("DESTINO");
        listDestino.add(" SELECCIONE DESTINO...");

        qDestino.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Destino destino = snap.getValue(Destino.class);
                    int lengthTipo = Integer.parseInt(preferences.getString("LENGTH_TIPO", "7"));
                    int lengthNumero = Integer.parseInt(preferences.getString("LENGTH_NUMERO", "3"));
                    int lengthContacto = Integer.parseInt(preferences.getString("LENGTH_CONTACTO", "25"));
                    int lengthFono = Integer.parseInt(preferences.getString("LENGTH_FONO", "11"));

                    listDestino.add(Utils.rightPad(destino.TIPO,lengthTipo)+" "+
                            Utils.rightPad(destino.NUMERO, lengthNumero)+" "+
                            Utils.rightPad(destino.CONTACTO, lengthContacto)+" "+
                            Utils.rightPad(destino.FONO, lengthFono));
                }
                Collections.sort(listDestino);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_large,listDestino);

                spinner.setAdapter(adapter);
                EditText nombre = findViewById(R.id.txtNombre);
                spinner.setMinimumHeight(nombre.getHeight());
                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String año = fechahora.split(" ")[0].split("-")[0];
        String mes = fechahora.split(" ")[0].split("-")[1];
        String dia = fechahora.split(" ")[0].split("-")[2];
        String hora = fechahora.split(" ")[1].split(":")[0];
        String minuto = fechahora.split(" ")[1].split(":")[1];
        String segundo = fechahora.split(" ")[1].split(":")[2];
        String milisegundo = fechahora.split(" ")[1].split(":")[3];

        final DatabaseReference refVisitaPersona = database.getReference("HOLDING").child(holding).child("VISITAS")
                .child(año).child(mes).child(dia).child(hora).child(minuto).child(segundo).child(milisegundo)
                .child("PERSONA");

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                final String destino = listDestino.get(spinner.getSelectedItemPosition());

                final String rutValido = Utils.validarRut(txtRut.getText().toString()+txtDV.getText().toString());

                if(rutValido.equals("OK") && spinner.getSelectedItemPosition()>0){
                    VisitaPersona visitaPersona = new VisitaPersona();
                    visitaPersona.rut = Integer.parseInt(txtRut.getText().toString());
                    visitaPersona.DV = txtDV.getText().toString();
                    visitaPersona.nombre = txtNombre.getText().toString();
                    visitaPersona.confirma = true;
                    visitaPersona.motivo = "OK";
                    visitaPersona.destino = destino.split("\\|")[0];

                    if(ppu == null){
                        refVisitaPersona.setValue(visitaPersona, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                finish();
                            }
                        });
                    }else{
                        refVisitaPersona.setValue(visitaPersona, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Intent intent = new Intent(IngresoPersona.this, IngresoVehiculo.class);
                                intent.putExtra("fechahora", fechahora);
                                intent.putExtra("ppu", textView1.getText().toString()+textView2.getText().toString()+textView3.getText().toString()+textView4.getText().toString()+
                                        textView5.getText().toString()+textView6.getText().toString());
                                startActivity(intent);
                            }
                        });
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(IngresoPersona.this);
                    builder.setTitle("Error campo obligatorio");
                    if(spinner.getSelectedItemPosition()==0){
                        builder.setMessage("Seleccione destino");
                        builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                spinner.performClick();
                            }
                        });
                    }
                    if(!rutValido.equals("OK")){
                        builder.setMessage(rutValido);
                        builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                txtDV.setText("");
                                txtDV.performClick();
                            }
                        });
                    }

                    builder.create();
                    builder.show();
                }
            }
        });


        btnVolver.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                final String motivo = "NO DEFINIDO";
                final AlertDialog.Builder builder = new AlertDialog.Builder(IngresoPersona.this);
                builder.setTitle("Cancelar registro de visita");
                builder.setMessage("Seleccione el motivo de la cancelación");

                final VisitaPersona visitaPersona = new VisitaPersona();

                final EditText txtRut = findViewById(R.id.txtRut);
                final EditText txtDV = findViewById(R.id.txtDV);
                final EditText txtNombre = findViewById(R.id.txtNombre);
                final String destino = listDestino.get(spinner.getSelectedItemPosition());

                Query qMotivoRechazo = database.getReference("HOLDING").child(holding).child("MOTIVORECHAZO");

                final RadioGroup rg = new RadioGroup(IngresoPersona.this);

                qMotivoRechazo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String value = (String)ds.getValue();
                            final RadioButton rb =  new RadioButton(IngresoPersona.this);
                            rb.setText(value);
                            rg.addView(rb);
                        }
                        builder.setView(rg);
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                builder.setNegativeButton("Cancelar", null);
                builder.setPositiveButton("Seleccionar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioButton rb = rg.findViewById(rg.getCheckedRadioButtonId());

                        if(rb == null){
                            return;
                        }else{
                            final ProgressDialog progress = new ProgressDialog(IngresoPersona.this);
                            progress.setTitle("Cargando");
                            progress.setMessage("Enviando información");
                            progress.setCancelable(false);
                            progress.show();

                            if(txtRut.getText().length() > 0){
                                visitaPersona.rut = Integer.parseInt(txtRut.getText().toString());
                            }

                            visitaPersona.motivo = visitaPersona.DV = txtDV.getText().toString();
                            visitaPersona.nombre = txtNombre.getText().toString();
                            visitaPersona.confirma = false;
                            visitaPersona.motivo = motivo;
                            visitaPersona.destino = destino.split("\\|")[0];
                            visitaPersona.motivo = rb.getText().toString();
                            refVisitaPersona.setValue(visitaPersona, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    progress.dismiss();
                                    finish();
                                }
                            });
                        }
                    }
                });
            }
        });

        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.hasFocus()){
                    InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }
}
