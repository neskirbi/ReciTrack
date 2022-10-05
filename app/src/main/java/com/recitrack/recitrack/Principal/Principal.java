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

        void CierraDialogo();

        void Error(String msn);

        void AbreDialogo();

        void IniciarRastreo(JSONArray datos);

        void Marcar(GoogleMap googleMap);

        void Focus(double arg0Latitude, double latitude, double longitude);
    }

    interface PrincipalInteractor{
        void GetRemisiones();
    }
}
