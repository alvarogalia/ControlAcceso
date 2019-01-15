package com.alvarogalia.controlacceso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alvarogalia.controlacceso.Obj.VisitaVehiculo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import segmented_control.widget.custom.android.com.segmentedcontrol.SegmentedControl;


public class IngresoVehiculo extends AppCompatActivity {

    List<String> listTipoVehiculo = new LinkedList<>();
    LinkedList<String> listColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_vehiculo);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Cargando");
        progress.setMessage("Obteniendo información");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final ConstraintLayout llMain = findViewById(R.id.llMain);
        llMain.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF")));

        final String holding = preferences.getString("HOLDING", "");

        final String ppu = getIntent().getStringExtra("ppu");
        String fechahora =  getIntent().getStringExtra("fechahora");

        String colorPPU = preferences.getString("COLOR_PPUBACKGROUND", "#FFFFFF");
        String colorPPUText = preferences.getString("COLOR_PPUTEXT", "#000000");


        final TextView textView1 = findViewById(R.id.ppuch1);
        textView1.setText(String.valueOf(ppu.charAt(0)));

        TextView textView2 = findViewById(R.id.ppuch2);
        textView2.setText(String.valueOf(ppu.charAt(1)));

        TextView textView3 = findViewById(R.id.ppuch3);
        textView3.setText(String.valueOf(ppu.charAt(2)));

        TextView textView4 = findViewById(R.id.ppuch4);
        textView4.setText(String.valueOf(ppu.charAt(3)));

        TextView textView5 = findViewById(R.id.ppuch5);
        textView5.setText(String.valueOf(ppu.charAt(4)));

        TextView textView6 = findViewById(R.id.ppuch6);
        textView6.setText(String.valueOf(ppu.charAt(5)));

        LinearLayout ll = findViewById(R.id.llPPU);
        ll.setBackgroundColor(Color.parseColor(colorPPU));

        textView1.setTextColor(Color.parseColor(colorPPUText));
        textView2.setTextColor(Color.parseColor(colorPPUText));
        textView3.setTextColor(Color.parseColor(colorPPUText));
        textView4.setTextColor(Color.parseColor(colorPPUText));
        textView5.setTextColor(Color.parseColor(colorPPUText));
        textView6.setTextColor(Color.parseColor(colorPPUText));

        textView1.setEnabled(false);
        textView2.setEnabled(false);
        textView3.setEnabled(false);
        textView4.setEnabled(false);
        textView5.setEnabled(false);
        textView6.setEnabled(false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        listColor = new LinkedList<>();

        Query qTVehic= database.getReference("TIPOVEHICULO").orderByValue();
        Query qColor= database.getReference("COLORVEHICULO").orderByValue();

        final SegmentedControl segmentedTipoVehiculo = findViewById(R.id.segmented_control);

        String año = fechahora.split(" ")[0].split("-")[0];
        String mes = fechahora.split(" ")[0].split("-")[1];
        String dia = fechahora.split(" ")[0].split("-")[2];
        String hora = fechahora.split(" ")[1].split(":")[0];
        String minuto = fechahora.split(" ")[1].split(":")[1];
        String segundo = fechahora.split(" ")[1].split(":")[2];
        String milisegundo = fechahora.split(" ")[1].split(":")[3];

        final DatabaseReference refVisitaVehiculo = database.getReference("HOLDING").child(holding).child("VISITAS")
                .child(año).child(mes).child(dia).child(hora).child(minuto).child(segundo).child(milisegundo)
                .child("VEHICULO");

        qTVehic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> listtVehic = (List<String>) dataSnapshot.getValue();
                for(String tVehic : listtVehic){
                    listTipoVehiculo.add(tVehic);
                }
                Collections.sort(listTipoVehiculo);
                segmentedTipoVehiculo.addSegments(listTipoVehiculo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        qColor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> lColor = (List<String>) dataSnapshot.getValue();
                for(String color : lColor){
                    listColor.add(color);
                }
                java.util.Collections.sort(listColor);
                for(String color : listColor){
                    ToggleButton tb = new ToggleButton(IngresoVehiculo.this);
                    tb.setBackgroundColor(Color.parseColor("#"+color.split(";")[1]));
                    tb.setText("");
                    tb.setTextOn(color.split(";")[0]);
                    tb.setTextOff("");
                    LinearLayout ll = findViewById(R.id.llColor);
                    tb.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            LinearLayout ll2 = findViewById(R.id.llColor);
                            for(int i = 0 ; i < ll2.getChildCount(); i++){
                                ToggleButton tb2 = (ToggleButton)ll2.getChildAt(i);
                                tb2.setChecked(false);
                            }
                            ToggleButton tbx = (ToggleButton)view;
                            if(tbx.getTextOn().equals("NEGRO")){
                                tbx.setTextColor(Color.parseColor("#FFFFFF"));
                            }
                            tbx.setChecked(true);
                        }
                    });
                    ll.addView(tb);
                }
                progress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button terminarRegistro = findViewById(R.id.btnTerminar);
        terminarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitaVehiculo visitaVehiculo = new VisitaVehiculo();

                LinearLayout ll = findViewById(R.id.llColor);
                for(int i = 0; i<ll.getChildCount();i++){
                    ToggleButton tb = (ToggleButton) ll.getChildAt(i);
                    if(tb.getText()!= ""){
                        visitaVehiculo.color = tb.getText().toString();
                    }
                }
                visitaVehiculo.ppu = ppu;
                int selected = segmentedTipoVehiculo.getSelectedAbsolutePosition();
                if(segmentedTipoVehiculo.hasSelectedSegment() && visitaVehiculo.color != null){
                    String tipoVehiculo = listTipoVehiculo.get(selected);
                    visitaVehiculo.tipoVehiculo = tipoVehiculo;
                    refVisitaVehiculo.setValue(visitaVehiculo);
                    finish();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(IngresoVehiculo.this);
                    builder.setTitle("Error campo obligatorio");
                    builder.setMessage("Complete información del vehículo");
                    builder.show();
                }
            }
        });
    }
}
