package com.recitrack.recitrack.Principal;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.recitrack.recitrack.Metodos;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PrincipalInteractor implements Principal.PrincipalInteractor {
    PrincipalPresenter principalPresenter;
    Context context;
    Metodos metodos;
    public PrincipalInteractor(PrincipalPresenter principalPresenter, Context context) {
        this.principalPresenter=principalPresenter;
        this.context=context;
        metodos=new Metodos(context);
    }

    @Override
    public void GetRemisiones() {
        principalPresenter.AbreDialogo();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PrincipalInterface remisiones=retrofit.create(PrincipalInterface.class);


        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        try {
            jsonObject.addProperty("id",metodos.GetIdCliente());
            jsonArray.add(jsonObject);
            Call<JsonArray> call= null;


            call = remisiones.Remisiones(jsonArray);

            Log.i("Remisiones"," \n\nRequest: "+jsonArray);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    JsonArray body= response.body();
                    if(body!=null){
                        Log.i("Remisiones"," \n\nBody: "+body.toString());
                        try {
                            Log.i("Remisiones",body.get(0).toString());
                            JSONObject jsonObject=new JSONObject(body.get(0).toString());
                            if(Integer.parseInt(jsonObject.getString("status"))==1){

                                principalPresenter.IniciarRastreo(jsonObject.getJSONArray("datos"));

                            }else{
                                Log.i("Remisiones","0");
                                principalPresenter.Error(jsonObject.getString("msn"));
                            }

                        }catch (Exception e){
                            Log.i("Remisiones",e.getMessage()+"");
                        }

                        principalPresenter.CierraDialogo();

                    }else{
                        Log.i("Remisiones"," \n\nCodigo:"+response.code()+" \n\nbody:"+body);
                        Toast.makeText(context, "Error de conexión "+response.code(), Toast.LENGTH_SHORT).show();
                        principalPresenter.CierraDialogo();
                    }

                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.i("Response2",": Error"+t.getMessage());
                    Toast.makeText(context, "Error de conexión "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    principalPresenter.CierraDialogo();
                }
            });
        } catch (Exception e) {
            Log.i("Remisiones",e.getMessage());
        }
    }


}
