package com.jjv.proyectointegradorv1.Fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjv.proyectointegradorv1.Adapters.DialogReserva;
import com.jjv.proyectointegradorv1.Adapters.Publicaciones_Adapter;
import com.jjv.proyectointegradorv1.Objects.Publicacion;
import com.jjv.proyectointegradorv1.R;

import java.util.ArrayList;

/**
 * Created by javi0 on 11/01/2017.
 */

public class Buscar_viaje extends Fragment implements AdapterView.OnItemClickListener {

    private final String TAG = Buscar_viaje.class.getSimpleName();
    private ListView listaPublicaciones;
    private ArrayList<Publicacion> publicaciones = new ArrayList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private ChildEventListener childEvent;
    private Publicacion publica;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar_viaje, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listaPublicaciones = (ListView) view.findViewById(R.id.lista_publicaciones);

        myRef = database.getReference("trip");// hacemos referencia a la rama donde se almacenan todos los viajes

        childEvent = new ChildEventListener() {
            Publicaciones_Adapter adapt;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                publica = dataSnapshot.getValue(Publicacion.class);
                publicaciones.add(publica);
                adapt=  new Publicaciones_Adapter(getContext(),publicaciones);
                listaPublicaciones.setAdapter(adapt);
                Log.d(TAG, publica.getOrigen());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                publica = dataSnapshot.getValue(Publicacion.class);
                publicaciones.add(publica);
                adapt=  new Publicaciones_Adapter(getContext(),publicaciones);
                listaPublicaciones.setAdapter(adapt);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
               /* publica = dataSnapshot.getValue(Publicacion.class);
                int pos = publicaciones.indexOf(publica);
                //prueba para comprobar
                Log.d(TAG,"borrado: "+pos+"  "+ publica.getDestino()+"  "+publica.getusuario()+"  "+publica.getOrigen()+"  "+publica.getPlazas()+"  "+publica.getPrecio()+"  "+publica.getFecha()+"  "+publica.getHora());

                adapt=  new Publicaciones_Adapter(getContext(),publicaciones);
                listaPublicaciones.setAdapter(adapt);
                */
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addChildEventListener(childEvent);


        Publicaciones_Adapter adapter = new Publicaciones_Adapter(getContext(),publicaciones);
        listaPublicaciones.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDialog(i);


    }

    private void showDialog(int pos){


        FragmentManager fm = getFragmentManager();
        DialogFragment diag = DialogReserva.newInstance(publicaciones.get(pos));

       // diag.show();
    }
}
