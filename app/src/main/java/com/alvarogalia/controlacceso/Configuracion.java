package com.alvarogalia.controlacceso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Configuracion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Cargando");
        progress.setMessage("Obteniendo información");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final ConstraintLayout llMain = findViewById(R.id.llMain);
        llMain.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF")));

        final String holding = preferences.getString("HOLDING", "");
        final String ubicacion = preferences.getString("UBICACION", "");

        final EditText txtHolding = findViewById(R.id.txtHolding);
        final EditText txtUbicacion = findViewById(R.id.txtUbicacion);

        final TextView lblHolding = findViewById(R.id.lblHolding);
        final TextView lblUbicacion = findViewById(R.id.lblUbicacion);

        final LinearLayout ll = findViewById(R.id.llConfigAdicional);

        final SharedPreferences.Editor editor = preferences.edit();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        txtHolding.setText(holding);
        txtUbicacion.setText(ubicacion);

        final  Button btnGuardar = findViewById(R.id.btnGuardar);

        Button btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final String colorLabelBackground = preferences.getString("COLOR_MAINLABELBACKGROUND", "#FFFFFF");
        final String colorLabelFont = preferences.getString("COLOR_MAINLABELTEXT", "#000000");
        final String colorTxtBackground = preferences.getString("COLOR_TXTBACKGROUND", "#FFFFFF");
        final String colorTxtText = preferences.getString("COLOR_TXTTEXT", "#000000");

        txtHolding.setBackgroundColor(Color.parseColor(colorTxtBackground));
        txtUbicacion.setBackgroundColor(Color.parseColor(colorTxtBackground));
        txtHolding.setTextColor(Color.parseColor(colorTxtText));
        txtUbicacion.setTextColor(Color.parseColor(colorTxtText));

        lblHolding.setBackgroundColor(Color.parseColor(colorLabelBackground));
        lblUbicacion.setBackgroundColor(Color.parseColor(colorLabelBackground));

        lblHolding.setTextColor(Color.parseColor(colorLabelFont));
        lblUbicacion.setTextColor(Color.parseColor(colorLabelFont));


        Query qrefConfiguraciones = database.getReference("HOLDING").child(holding).child("CONFIGURACIONES");
        qrefConfiguraciones.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences.Editor edit = preferences.edit();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    String key = snap.getKey();
                    String value = String.valueOf(snap.getValue());

                    boolean existe = false;

                    for(int i = 0; i < ll.getChildCount(); i++){
                        View view = ll.getChildAt(i);

                        if(view.getClass().getName().endsWith("EditText")){
                            EditText txt = (EditText)view;
                            if(txt.getTooltipText().equals(key)){
                                txt.setText(value);
                                existe = true;
                            }
                        }
                    }

                    if(!existe){
                        EditText txt = new EditText(Configuracion.this);
                        TextView lbl = new TextView(Configuracion.this);
                        lbl.setBackgroundColor(Color.parseColor(colorLabelBackground));
                        lbl.setTextColor(Color.parseColor(colorLabelFont));
                        lbl.setText(key);
                        txt.setBackgroundColor(Color.parseColor(colorTxtBackground));
                        txt.setTextColor(Color.parseColor(colorTxtText));
                        txt.setText(value);
                        txt.setTooltipText(key);
                        ll.addView(lbl);
                        ll.addView(txt);
                    }
                }
                edit.commit();
                llMain.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF")));

                btnGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String newHolding = txtHolding.getText().toString();
                        String newUbicacion = txtUbicacion.getText().toString();

                        editor.putString("HOLDING",newHolding);
                        editor.putString("UBICACION",newUbicacion);

                        editor.apply();
                        finish();

                        for(int i = 0; i < ll.getChildCount(); i++) {
                            View view = ll.getChildAt(i);
                            if (view.getClass().getName().endsWith("EditText")) {
                                EditText txt = (EditText)view;
                                Query qrefConfiguraciones = database.getReference("HOLDING").child(holding).child("CONFIGURACIONES").child(txt.getTooltipText().toString());
                                ((DatabaseReference) qrefConfiguraciones).setValue(txt.getText().toString());
                            }
                        }
                    }
                });

                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        txtHolding.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ll.removeAllViewsInLayout();
            }
        });

        Button btnNewConfig = findViewById(R.id.btnAddConfig);
        btnNewConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Configuracion.this);
                builder.setTitle("Nueva configuracion");
                builder.setMessage("Ingrese nombre de configuracion");
                final EditText txtBuilder = new EditText(Configuracion.this);
                builder.setView(txtBuilder);

                builder.setNegativeButton("Cancelar", null);
                builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText txt = new EditText(Configuracion.this);
                        TextView lbl = new TextView(Configuracion.this);
                        lbl.setBackgroundColor(Color.parseColor(colorLabelBackground));
                        lbl.setTextColor(Color.parseColor(colorLabelFont));
                        lbl.setText(txtBuilder.getText().toString());
                        txt.setBackgroundColor(Color.parseColor(colorTxtBackground));
                        txt.setTextColor(Color.parseColor(colorTxtText));
                        txt.setTooltipText(txtBuilder.getText().toString());
                        ll.addView(lbl);
                        ll.addView(txt);
                        txt.performClick();
                    }
                });

                builder.show();
            }
        });
    }


}
