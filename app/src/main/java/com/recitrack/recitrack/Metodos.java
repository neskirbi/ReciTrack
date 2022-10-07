package com.recitrack.recitrack;

import static android.content.Context.ACTIVITY_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.recitrack.recitrack.DB.DB;
import com.recitrack.recitrack.Servicios.TrackingService;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Metodos {

    private Context context;

    public Metodos(Context context) {
        this.context=context;
    }

    public String GetUrl(){
        String url="";
        if(BuildConfig.DEBUG){

            url = context.getResources().getString(R.string.base_url_debug);
        }else{
            url = context.getResources().getString(R.string.base_url);
        }

        Log.i("Url","Url: "+url);
        return url;

    }

    public String GetUuid(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public String GetDate(){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return timeStamp;
    }

    public boolean ValidarLogin() {
        DB base = new DB(context);
        SQLiteDatabase db = base.getWritableDatabase();
        Cursor c =  db.rawQuery("SELECT * from clientes ",null);
        int filas=c.getCount();
        db.close();
        if(filas>0){
            return true;
        }
        return false;


    }


    public boolean Transportando() {
        try{
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();
            Cursor c =  db.rawQuery("SELECT * from citas ",null);
            int filas=c.getCount();
            db.close();
            if(filas>0){

                return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }



    }
    public void PedirPermisoGPS(Activity view) {

        /*
        * Para la Android 7 al 9
        * */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            int permsRequestCode = 10;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

            int location = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (location == PackageManager.PERMISSION_GRANTED ) {

                //IniciarServicioTracking();
            } else {


                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(context);
                builder.setTitle("GPS");
                builder.setMessage("Recitrack Transporte recolecta la localización para el seguimiento del transporte de los residuos de la construcción cuando la aplicación se encuentra abierta o cerrada.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        view.requestPermissions(perms, permsRequestCode);
                    }
                });
                builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Vibrar(VibrarPush());
                    }
                });
                builder.show();

            }

        }

        /*
         * Para la Android  10 o mayor
         * */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){


            int permsRequestCode = 1;
            String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};

            int location = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (location == PackageManager.PERMISSION_GRANTED ) {


            } else {
                MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(context);
                builder.setTitle("GPS");
                builder.setMessage("Recitrack Transporte recolecta la localización para el seguimiento del transporte de los residuos de la construcción cuando la aplicación se encuentra abierta o cerrada.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.requestPermissions(perms, permsRequestCode);
                    }
                });
                builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Vibrar(VibrarPush());
                    }
                });
                builder.show();

            }




        }


    }

    public void IniciarServicioTracking() {


        if (!isMyServiceRunning(TrackingService.class, context)) {

            Intent service1 = new Intent(context, TrackingService.class);
            service1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context,service1);
            }else{

                context.startService(service1);
            }


        }
    }

    public void DetenerServicioTracking() {
        Intent service1 = new Intent(context, TrackingService.class);
        context.stopService(service1);

    }

    public void CerrarSesion() {

        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            db.execSQL("DELETE from clientes ");
            db.close();
        }catch (Exception e){}




    }

    public boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void Vibrar(long[] pattern) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milisegundos
        //pattern = { 0, milli};
        v.vibrate(pattern, -1);
    }

    public long[] VibrarPush() {
        long[] pattern = {0, 70};
        return pattern;
    }

    @SuppressLint("Range")
    public String GetNombres(){
        String nombres="";
        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from clientes ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                nombres=c.getString(c.getColumnIndex("nombres"));

            }
            c.close();
            db.close();
        }catch (Exception e){}

        return nombres;
    }




    public int GetTipo(){
        String tipo="";
        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT tipo from choferes ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                tipo=c.getString(c.getColumnIndex("tipo"));

            }
            c.close();
            db.close();
        }catch (Exception e){}

        return  Integer.parseInt(tipo=="" ? "0" : tipo);
    }

    @SuppressLint("Range")
    public String GetApellidos(){
        String apellidos="";
        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from clientes ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                apellidos=c.getString(c.getColumnIndex("apellidos"));

            }
            c.close();
            db.close();
        }catch (Exception e){}

        return apellidos;
    }

    @SuppressLint("Range")
    public String GetIdChofer() {
        String id_chofer="";
        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from choferes ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                id_chofer=c.getString(c.getColumnIndex("id"));

            }
            c.close();
            db.close();
        }catch (Exception e){}

        return id_chofer;
    }

    public int NumeroCoordenadas() {
        DB base = new DB(context);
        SQLiteDatabase db = base.getWritableDatabase();
        Cursor c =  db.rawQuery("SELECT * from coordenadas ",null);
        int filas=c.getCount();
        db.close();

        return filas;


    }



    public String BitmaptoBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    @SuppressLint("Range")
    public CharSequence GetIdEmpresaTrasporte() {
        String id_empresatransporte="";
        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from choferes ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();

                id_empresatransporte=c.getString(c.getColumnIndex("id_empresatransporte"));

            }
            c.close();
            db.close();
        }catch (Exception e){}

        return id_empresatransporte;
    }

    public long GetTime() {
        if(BuildConfig.DEBUG){
            return 10000;
        }else{
            return 60000;
        }
    }

    public String GetVersion() {
        DB base = new DB(context);
        SQLiteDatabase db = base.getWritableDatabase();
        return db.getVersion()+"";
    }

    public boolean EnViaje() {
        DB base = new DB(context);
        SQLiteDatabase db = base.getWritableDatabase();

        Cursor c =  db.rawQuery("SELECT * from remisiones  ",null);
        if(c.getCount()==0){
            return false;
        }
        if(c.getCount()>0){
            return true;
        }
        return false;
    }

    public String GetIdRemision() {
        String id="";
        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from remisiones ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();
                id=c.getString(c.getColumnIndex("id"));
            }
            c.close();
            db.close();
        }catch (Exception e){

            return "";
        }
        return id;
    }

    public String GetIdCliente() {
        String id="";
        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from clientes ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();
                id=c.getString(c.getColumnIndex("id_cliente"));
            }
            c.close();
            db.close();
        }catch (Exception e){

            return "";
        }
        return id;
    }

    public String GetId() {
        String id="";
        try {
            DB base = new DB(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from clientes ",null);
            c.moveToFirst();
            if(c.getCount()>0){
                c.moveToFirst();
                id=c.getString(c.getColumnIndex("id"));
            }
            c.close();
            db.close();
        }catch (Exception e){

            return "";
        }
        return id;
    }

    public int GetAleatorio() {
        return (int) Math.round(Math.random()*10000);
    }
}
