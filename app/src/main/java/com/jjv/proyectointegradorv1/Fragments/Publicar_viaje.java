package com.jjv.proyectointegradorv1.Fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjv.proyectointegradorv1.Objects.Publicacion;
import com.jjv.proyectointegradorv1.R;
import com.jjv.proyectointegradorv1.UI.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by javi0 on 11/01/2017.
 */

public class Publicar_viaje extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    String[] plazas = {"1", "2", "3", "4"};
    TextView txt_origen, txt_destino, txt_fecha, txt_hora, txt_precio;
    Spinner sp_plazas;

    private DatabaseReference mDatabase;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat stf = new SimpleDateFormat("HH:mm");

    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publicarviaje, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String currentTime = stf.format(new Date());
        String currentDate = sdf.format(new Date());

        txt_origen = (TextView) view.findViewById(R.id.txt_origen);
        txt_destino = (TextView) view.findViewById(R.id.txt_distino);
        txt_fecha = (TextView) view.findViewById(R.id.txt_fecha);
        txt_precio = (TextView) view.findViewById(R.id.txt_precio);
        txt_hora = (TextView) view.findViewById(R.id.txt_hora);
        sp_plazas = (Spinner) view.findViewById(R.id.sp_plazas);



        txt_hora.setText(currentTime);
        txt_fecha.setText(currentDate);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, plazas);
        sp_plazas.setAdapter(adapter);
        //Atendemos al evento de que se pulse el campo de la fecha
        txt_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el fragment dialog que contendra el picker de la fecha
                DatepickerFragment newFragment = new DatepickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        //Dado que el evento onclick no se activa al hacer foco en el campo atendemos al evento on focused
        //para lanzar el picker cuando se haga haga click en el campo por primera vez
        txt_fecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (txt_fecha.isFocused()) {
                    DatepickerFragment newFragment = new DatepickerFragment();
                    newFragment.show(getFragmentManager(), "datePicker");
                }
            }
        });
        //Atendemos al evento de que se pulse el campo de la hora
        txt_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el fragment dialog que contendra el picker de la hora
                DialogFragment newFragment = new TimepickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        //Dado que el evento onclick no se activa al hacer foco en el campo atendemos al evento on focused
        //para lanzar el picker cuando se haga haga click en el campo por primera vez
        txt_hora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (txt_hora.isFocused()) {
                    DialogFragment newFragment = new TimepickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                }
            }
        });



        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                String origen = txt_origen.getText().toString();
                String destino = txt_destino.getText().toString();
                String fecha = txt_fecha.getText().toString();
                String hora = txt_hora.getText().toString();
                int plazas =  sp_plazas.getSelectedItemPosition()+1;
                String precio = txt_precio.getText().toString();
                if (precio.equals("")){
                    precio="0";
                }

                if(origen.equals("")||destino.equals("")){
                    Snackbar.make(getView(), getString(R.string.rellene_los_campos), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    //Publicacion viaje = new Publicacion(origen,destino,fecha,hora,plazas,precio);
                    String key = mDatabase.child("posts").push().getKey();
                    String userId = user.getUid();
                    Map<String, Object> viaje = new Publicacion(user.getDisplayName(), origen, destino, fecha, hora, plazas, precio,userId,key).toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/trip/" + key, viaje);
                    childUpdates.put("/user-trips/" + userId + "/" + key, viaje);
                    mDatabase.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(getView(), getString(R.string.publicacion_enviada), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            txt_origen.setText("");
                            txt_destino.setText("");
                            txt_hora.setText(stf.format(new Date()));
                            txt_fecha.setText(sdf.format(new Date()));
                            txt_precio.setText("");
                            ((MainActivity) getActivity()).selectPage(2);
                        }
                    });
                }
            }
        });

        Animation rotar;
        rotar= AnimationUtils.loadAnimation(getContext(),R.anim.rotar);
        fab.setAnimation(rotar);

    }

}
