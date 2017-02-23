package com.jjv.proyectointegradorv1.Fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjv.proyectointegradorv1.Adapters.Publicaciones_Adapter;
import com.jjv.proyectointegradorv1.Adapters.Publicaciones_RV_adapter;
import com.jjv.proyectointegradorv1.Objects.Publicacion;
import com.jjv.proyectointegradorv1.R;

import java.util.ArrayList;

/**
 * Created by javi0 on 11/01/2017.
 */

public class Mis_viajes extends Fragment  {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ListView listaMisViajes;
    private Publicaciones_Adapter adapter;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbref;
    private ChildEventListener childEvent;
    private Publicacion publicacion;
    private ArrayList<Publicacion> publicaciones;
    private Dialog customDialog ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_viajes, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        publicaciones = new ArrayList<>();

        Log.i("NOMBRE USR:",user.getDisplayName());
        Log.i("UID USR:",user.getUid());
        dbref = database.getReference("user-trips/"+user.getUid());

        listaMisViajes = (ListView) view.findViewById(R.id.listMisViajes);

        childEvent = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                publicacion = dataSnapshot.getValue(Publicacion.class);
                publicaciones.add(publicacion);
                adapter = new Publicaciones_Adapter(view.getContext(),publicaciones);
                listaMisViajes.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                publicacion = dataSnapshot.getValue(Publicacion.class);
                publicaciones.add(publicacion);
                adapter = new Publicaciones_Adapter(view.getContext(),publicaciones);
                listaMisViajes.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbref.addChildEventListener(childEvent);
        adapter = new Publicaciones_Adapter(view.getContext(),publicaciones);
        listaMisViajes.setAdapter(adapter);
        listaMisViajes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(publicaciones.get(position));
            }
        });



    }

    private void showDialog(Publicacion p){

        customDialog =  new MisViajesDialog(getContext(), R.style.Theme_Dialog_Translucent, p);
        //deshabilitamos el título por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        customDialog.setCancelable(false);
        //establecemos el contenido de nuestro dialog
        //LayoutInflater factory = LayoutInflater.from(getContext());
        //View dView = factory.inflate(R.layout.dialog_fragment_reservar, null);
        customDialog.setContentView(R.layout.dialog_fragment_misviajes);
        customDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {

                if(i==KeyEvent.KEYCODE_BACK){
                    customDialog.dismiss();
                    return true;

                }
                return false;
            }
        });
        customDialog.show();
    }

}
