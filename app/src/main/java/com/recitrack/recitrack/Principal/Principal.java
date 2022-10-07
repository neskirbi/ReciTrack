package com.recitrack.recitrack.Principal;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;

public interface Principal {
    interface PrincipalView{
        void Error(String error);
        void AbreDialogo();
        void CierraDialogo();


    }

    interface PrincipalPresenter{
        void GetOrdenes();
        void GetRemisiones();

        void GetObras();

        void CierraDialogo();

        void Error(String msn);

        void AbreDialogo();

        void IniciarRastreo(JSONArray datos);

        void Marcar(GoogleMap googleMap);


        void GuardaObras(JSONArray datos);

        void NoMoverMapa();

        void NoMoverMapaStop();
    }

    interface PrincipalInteractor{
        void GetObras();

        void GetRemisiones();
    }
}
