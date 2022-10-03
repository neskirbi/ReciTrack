package com.recitrack.recitrack.Login;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.recitrack.recitrack.DB.DB;
import com.recitrack.recitrack.Metodos;


import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginInteractor implements Login.LoginInteractor {
    LoginPresenter loginPresenter;
    Context context;
    Metodos metodos;
    public LoginInteractor(LoginPresenter loginPresenter, Context context) {
        this.context=context;
        this.loginPresenter=loginPresenter;
        metodos=new Metodos(context);

    }


    @Override
    public void Validar(String mail, String pass) {
        loginPresenter.AbreDialogo();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginInterface cliente=retrofit.create(LoginInterface.class);


        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        try {
            jsonObject.addProperty("mail",mail);
            jsonObject.addProperty("pass",pass);
            jsonArray.add(jsonObject);
            Call<JsonArray> call= null;


            call = cliente.Login(jsonArray);

            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    JsonArray body= response.body();
                    if(body!=null){
                        Log.i("Login"," \n\nBody: "+body.toString());
                        try {
                            Log.i("Login",body.get(0).toString());
                            JSONObject jsonObject=new JSONObject(body.get(0).toString());
                            if(Integer.parseInt(jsonObject.getString("status"))==1){

                                Log.i("Login","1");
                                GuardarCliente(jsonObject.getJSONObject("datos"));
                                loginPresenter.LoginOk();
                            }else{
                                Log.i("Login","0");
                                loginPresenter.Error(jsonObject.getString("msn"));
                            }

                        }catch (Exception e){
                            Log.i("Login",e.getMessage()+"");
                        }

                        loginPresenter.CierraDialogo();

                    }else{
                        Log.i("Login"," \n\nCodigo:"+response.code()+" \n\nbody:"+body);
                        Toast.makeText(context, "Error de conexión "+response.code(), Toast.LENGTH_SHORT).show();
                        loginPresenter.CierraDialogo();
                    }

                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.i("Response2",": Error"+t.getMessage());
                    Toast.makeText(context, "Error de conexión "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    loginPresenter.CierraDialogo();
                }
            });
        } catch (Exception e) {
            Log.i("Login",e.getMessage());
        }

    }

    private void GuardarCliente(JSONObject jsonObject) {
        Log.i("Guardando",": Datos"+jsonObject);
        DB base = new DB(context);
        SQLiteDatabase db = base.getWritableDatabase();
        ContentValues cliente = new ContentValues();

        try {
            cliente.put("id", jsonObject.getString("id"));
            cliente.put("nombres", jsonObject.getString("nombres"));
            cliente.put("apellidos", jsonObject.getString("apellidos"));
            cliente.put("mail", jsonObject.getString("mail"));




            db.insert("clientes", null, cliente);
        } catch (JSONException e) {
            Log.i("guardando",e.getMessage()+"");
        }

        db.close();
    }
}
