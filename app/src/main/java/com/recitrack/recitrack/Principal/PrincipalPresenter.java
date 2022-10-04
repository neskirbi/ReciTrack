package com.recitrack.recitrack.Principal;

import android.content.Context;

import com.recitrack.recitrack.Metodos;

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
