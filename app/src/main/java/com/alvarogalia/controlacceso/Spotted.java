package com.alvarogalia.controlacceso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvarogalia.controlacceso.Obj.ButtonSpotted;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

public class Spotted extends AppCompatActivity {

    LinkedList<ButtonSpotted> buttons;
    LinearLayout ll;
    String fechahora = "";
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotted);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        ll = findViewById(R.id.hll_spotted);

        final ConstraintLayout llMain = findViewById(R.id.llMain);
        llMain.setBackgroundColor(Color.parseColor(preferences.getString("COLOR_MAINBACKGROUND", "#FFFFFF")));

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final String holding = preferences.getString("HOLDING", "");
        final String ubicacion = preferences.getString("UBICACION", "");

        fechahora =  getIntent().getStringExtra("fechahora");

        buttons = new LinkedList<>();


        Button button = new Button(Spotted.this);


        button.setText("OTRA...");
        button.setWidth(300);
        button.setHeight(100);
        button.setTextSize(24);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Spotted.this, IngresoPersona.class);
                intent.putExtra("ppu", "      ");
                intent.putExtra("fechahora", fechahora);
                startActivity(intent);
            }
        });
        ll.addView(button);


        Query qrefHistNew = database.getReference("HOLDING").child(holding).child("UBICACION").child(ubicacion).child("SPOTTED").orderByChild("cantidad");//.limitToLast(9);


        qrefHistNew.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                final com.alvarogalia.controlacceso.Obj.Spotted spot = ds.getValue(com.alvarogalia.controlacceso.Obj.Spotted.class);

                removeButtons();

                ButtonSpotted bs = new ButtonSpotted();
                bs.cantidad = spot.getCantidad();
                bs.ppu = spot.getPpu();

                Button buttonll = new Button(Spotted.this);
                buttonll.setWidth(300);
                buttonll.setHeight(100);
                buttonll.setTextSize(24);
                bs.button = buttonll;

                buttons.add(bs);

                createButtons();
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {
                removeButtons();
                final com.alvarogalia.controlacceso.Obj.Spotted spot = ds.getValue(com.alvarogalia.controlacceso.Obj.Spotted.class);
                for(ButtonSpotted b : buttons){
                    if(b.ppu == spot.getPpu()){
                        b.cantidad = spot.getCantidad();
                    }
                }
                createButtons();
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                removeButtons();
                final com.alvarogalia.controlacceso.Obj.Spotted spot = ds.getValue(com.alvarogalia.controlacceso.Obj.Spotted.class);
                for(ButtonSpotted b : buttons){
                    if(b.ppu == spot.getPpu()){
                        buttons.remove(b);
                    }
                }
                createButtons();
            }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) {

            }

            @Override
            public void onCancelled(DatabaseError de) {

            }
        });

    }

    public void removeButtons(){
        for(ButtonSpotted b : buttons){
            ll.removeView(b.button);
        }
    }
    public void createButtons(){
        int cantidad = 0;
        for(ButtonSpotted b : buttons){
            cantidad = cantidad + b.cantidad;
        }

        Comparator<ButtonSpotted> ButtonSpottedComparator = new Comparator<ButtonSpotted>() {
            @Override
            public int compare(ButtonSpotted o1, ButtonSpotted o2) {
                return o2.cantidad-o1.cantidad;
            }
        };

        Collections.sort(buttons, ButtonSpottedComparator);

        for(final ButtonSpotted b : buttons){
            float porcentaje = (b.cantidad * 100)/cantidad;
            b.button.setText(b.ppu+" "+Math.round(porcentaje)+"%");
            b.button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(Spotted.this, IngresoPersona.class);
                    intent.putExtra("ppu", b.ppu);
                    intent.putExtra("fechahora", fechahora);
                    startActivity(intent);
                }
            });
            ll.addView(b.button);
        }

        for(int i = 0; i < ll.getChildCount(); i++){
            View view = ll.getChildAt(i);

            if(view.getClass().getName().endsWith("Button")){
                Button boton = (Button) view;
                boton.getBackground().setColorFilter(Color.parseColor(preferences.getString("COLOR_MAINBUTTONSBACKGROUND", "#FFFFFF")),PorterDuff.Mode.MULTIPLY);
                boton.setTextColor(Color.parseColor(preferences.getString("COLOR_MAINBUTTONSTEXT", "#000000")));
            }
            if(view.getClass().getName().endsWith("TextView")){
                TextView textview = (TextView)view;
                textview.getBackground().setColorFilter(Color.parseColor(preferences.getString("COLOR_MAINLABELBACKGROUND", "#FFFFFF")),PorterDuff.Mode.MULTIPLY);
                textview.setTextColor(Color.parseColor(preferences.getString("COLOR_MAINLABELTEXT", "#FFFFFF")));
            }
        }
    }
}
