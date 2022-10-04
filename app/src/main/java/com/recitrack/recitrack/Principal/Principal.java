package com.recitrack.recitrack.Principal;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

public interface Principal {
    interface PrincipalView{
        void Error(String error);
        void AbreDialogo();
        void CierraDialogo();

        void IniciarRastreo(JSONArray datos);
    }

    interface PrincipalPresenter{
        void GetOrdenes();
        void GetRemisiones();

        void CierraDialogo();

        void Error(String msn);

        void AbreDialogo();

        void IniciarRastreo(JSONArray datos);
    }

    interface PrincipalInteractor{
        void GetRemisiones();
    }
}
