package com.recitrack.recitrack.Principal;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.recitrack.recitrack.Metodos;
import com.recitrack.recitrack.Model.Orden;

import org.json.JSONArray;

public class PrincipalPresenter implements Principal.PrincipalPresenter {
    PrincipalView principalView;
    Context context;
    Metodos metodos;
    PrincipalInteractor principalInteractor;
    public PrincipalPresenter(PrincipalView principalView, Context context) {
        this.principalView=principalView;
        this.context=context;
        metodos=new Metodos(context);
        principalInteractor=new PrincipalInteractor(this,context);
    }

    @Override
    public void GetOrdenes() {
        Log.i("Ordenes",metodos.GetIdCliente());
        FirebaseDatabase firebaseDatabaseAvisos = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabaseAvisos.getReference("Ordenes/Recitrack/"+metodos.GetIdCliente());//Sala de chat
        ChildEventListener listener ;
        listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.i("Ordenes","Orden:"+snapshot.getValue());
                switch (snapshot.getValue()+""){
                    case "1":
                        principalInteractor.GetRemisiones();
                        break;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.i("Ordenes","COrden:"+snapshot);
                switch (snapshot.getValue()+""){
                    case "1":
                        principalInteractor.GetRemisiones();

                        break;
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.addChildEventListener(listener);
    }

    @Override
    public void GetRemisiones() {
        principalInteractor.GetRemisiones();
    }

    @Override
    public void CierraDialogo() {
        principalView.CierraDialogo();
    }

    @Override
    public void Error(String msn) {

    }

    @Override
    public void AbreDialogo() {
        principalView.AbreDialogo();
    }

    @Override
    public void IniciarRastreo(JSONArray datos) {
        principalView.IniciarRastreo(datos);
    }
}
