package com.recitrack.recitrack.Principal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.recitrack.recitrack.CostomClass.ListenerFB;
import com.recitrack.recitrack.Metodos;
import com.recitrack.recitrack.Model.ModelFireBase;
import com.recitrack.recitrack.Model.Remision;
import com.recitrack.recitrack.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PrincipalPresenter implements Principal.PrincipalPresenter {
    PrincipalView principalView;
    Context context;
    Metodos metodos;
    PrincipalInteractor principalInteractor;

    Double latp=0.0,lonp=0.0,lat=0.0,lon=0.0;


    BitmapDescriptor icon,iconobra;

    ArrayList<ModelFireBase> modelFireBase=new ArrayList<>();


    GoogleMap googleMap;
    JSONArray obras=new JSONArray();
    int pantallaw=0,pantallah= 0;
    private boolean NoMover=false;
    Handler handlermap=null;
    Runnable run;

    public PrincipalPresenter(PrincipalView principalView, Context context) {
        this.principalView=principalView;
        this.context=context;
        metodos=new Metodos(context);
        principalInteractor=new PrincipalInteractor(this,context);
        DisplayMetrics metrics = new DisplayMetrics();
        principalView.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        pantallah=metrics.heightPixels;
        pantallaw=metrics.widthPixels;
        int tamcamiones= (int) Math.round(pantallah*0.04);
        int tamobras= (int) Math.round(pantallah*0.09);
        icon= BitmapDescriptorFactory.fromBitmap(resizeMapIcons("olla",tamcamiones,tamcamiones));
        iconobra= BitmapDescriptorFactory.fromBitmap(resizeMapIcons("obra1",tamobras,tamobras));

    }

    @Override
    public void GetOrdenes() {
        Log.i("Ordenes",metodos.GetIdCliente());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Ordenes/Recitrack/"+metodos.GetIdCliente());//Sala de chat
        ChildEventListener listener ;
        listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ProcesarOrden(snapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ProcesarOrden(snapshot);

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

    private void ProcesarOrden(DataSnapshot snapshot) {
        Log.i("Ordenes","Orden:"+snapshot.getValue());
        switch (snapshot.getValue()+""){
            case "1":
                Log.i("Ordenes","Ordenando:"+snapshot.getValue());
                principalInteractor.GetRemisiones();
            break;


        }


    }

    @Override
    public void GetRemisiones() {


        principalInteractor.GetRemisiones();
    }

    @Override
    public void GetObras() {

        principalInteractor.GetObras();

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.i("QuieroQuitar ","revisar");
        Revisar(datos);


        for(int i=0 ; i < datos.length() ; i++){

            try {

                boolean esta=false;
                for(int jj=0; jj<modelFireBase.size();jj++){
                    
                    if(modelFireBase.get(jj).getId().contains(datos.getJSONObject(i).getString("id")) ){
                        Log.i("QuieroQuitar ","ids saltando"+modelFireBase.size()+":"+datos.getJSONObject(i).getString("id"));
                        esta=true;
                    }
                }
                if (esta) {
                    continue;
                }


                
                if(1==Integer.parseInt( datos.getJSONObject(i).getString("confirmacion"))){
                    Log.i("QuieroQuitar ","ids nullo"+ modelFireBase.size()+":"+datos.getJSONObject(i).getString("id"));


                    continue;

                }
                Log.i("QuieroQuitar ","Agrega ids "+modelFireBase.size()+":"+datos.getJSONObject(i).getString("id"));

                Notificar("Pedido en Camino",datos.getJSONObject(i).getString("descripcion")+"\n"+datos.getJSONObject(i).getString("obra_domicilio"));




                String Tema="Remisiones/Tracking/"+datos.getJSONObject(i).getString("id");
                Log.i("Localizando ","Tema:"+"Remisiones/Tracking/"+datos.getJSONObject(i).getString("id"));
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference(Tema);//Sala de chat

                ListenerFB listener;

                listener=new ListenerFB() {
                    public MarkerOptions marker=new MarkerOptions().position(new LatLng(0.0,0.0))
                            .draggable(true)
                            .title("")

                            // below line is use to add custom marker on our map.
                            .icon(icon);
                    public Marker markert=null;
                    boolean Primera=true;
                    String descripcion="";
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                        Remision remision=snapshot.getValue(Remision.class);
                        Log.i("Localizando",remision.getId()+"");
                        Log.i("Localizando",remision.getLatitud()+"");

                        LatLng posicion = new LatLng(Double.parseDouble(remision.getLatitud()), Double.parseDouble(remision.getLongitud()));

                        Log.i("Marcador",remision.getProducto());
                        marker.position(posicion);
                        marker.title(remision.getObra()+'\n'+remision.getProducto());

                        descripcion=remision.getProducto();
                        if(Primera){
                            Primera=false;
                            markert = googleMap.addMarker(marker);
                        }else{
                            markert.setPosition(posicion);
                        }
                        for(int ii = 0 ; ii< modelFireBase.size() ;ii++){
                           if(remision.getId().equals(modelFireBase.get(ii).getId())){
                               modelFireBase.get(ii).setMarkerOptions(marker);
                           }
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


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

                    @Override
                    public void QuitarMarcador() {
                        if(markert!=null)
                        markert.remove();
                    }

                    @Override
                    public String Descripcion() {

                        return descripcion;
                    }
                };
                databaseReference.addChildEventListener(listener);


                modelFireBase.add(new ModelFireBase(datos.getJSONObject(i).getString("id"),null,databaseReference,listener,datos.getJSONObject(i).getString("descripcion")));

                Log.i("QuieroQuitar","IniciarRastreo:"+datos.getJSONObject(i).getString("id"));

            } catch (Exception e) {
                Log.i("QuieroQuitar","Error:"+e.getMessage());
            }
        }
    }

    private void Revisar(JSONArray datos) {
        Log.i("QuieroQuitar ","entrando:");
        ArrayList<String> temp=new ArrayList<>();
        for(int i=0 ; datos.length()>i ; i++){
            try {

                if(3==Integer.parseInt( datos.getJSONObject(i).getString("confirmacion"))){
                    Log.i("QuieroQuitar ","cargando temp: "+i+" Confirmacion: "+Integer.parseInt( datos.getJSONObject(i).getString("confirmacion")));
                    temp.add(datos.getJSONObject(i).getString("id"));
                }


            } catch (Exception e) {
                Log.i("QuieroQuitar", "REvisarError"+e.getMessage());
            }
        }

        for(int j=modelFireBase.size()-1;j>=0;j--){
            Log.i("QuieroQuitar ","tamanioo:"+modelFireBase.size()+" index:"+ j);
            if(modelFireBase.get(j).getId()==null || !temp.contains(modelFireBase.get(j).getId())){
                Log.i("QuieroQuitar ","ids Quitando "+modelFireBase.size()+":"+ modelFireBase.get(j));
                //NotificaEntega("Entregado",descripciones.get(j));
                if(modelFireBase.get(j).getDatabaseReference()!=null && modelFireBase.get(j).getListenerFB()!=null){
                    Notificar("Entrega Completa",modelFireBase.get(j).getListenerFB().Descripcion());
                    modelFireBase.get(j).getListenerFB().QuitarMarcador();
                    modelFireBase.get(j).getDatabaseReference().removeEventListener(modelFireBase.get(j).getListenerFB());
                }
                modelFireBase.remove(j);

            }
        }
    }


    public void Notificar(String title, String body) {




        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //
        String NOTIFICATION_CHANNEL_ID = "Emergencia";




        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setSmallIcon(R.drawable.icono)
                .setVibrate(new long[]{1000, 1000, 500, 1000})
                .setSound(defaultSoundUri)
                .setPriority(1)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setContentInfo("info");

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            notificationChannel.setDescription("Descripcion");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{1000, 1000, 500, 1000});
            notificationChannel.enableLights(true);
            notificationBuilder.setSound(sonido);
            notificationBuilder.setPriority(1);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        notificationManager.notify(metodos.GetAleatorio(), notificationBuilder.build());

    }

    private void NotificaEntega(String entregado, String s) {
        Log.i("Notificando",entregado);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="Entregas Recitrack";
            String description = "Notificaciones de entrega.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(metodos.GetUuid(), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        String NOTIFICATION_CHANNEL_ID = "Entregas Recitrack";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(entregado)
                .setContentText(s)
                .setVibrate(new long[]{1000, 1000, 500, 1000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

    }

    @Override
    public void Marcar(GoogleMap Map) {
        googleMap=Map;
        // Me ubica cuando tiene lat y lon
        if (googleMap != null) {
            final Handler handler= new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    CamaraPosition();

                    handler.postDelayed(this, 2000);
                }
            },0);


        }


    }


    @Override
    public void GuardaObras(JSONArray datos) {


        obras=datos;
        PonerMarcadoresObras();
    }

    @Override
    public void NoMoverMapa() {
        NoMover=true;
        handlermap= new Handler();
        run=new Runnable() {
            @Override
            public void run() {

                NoMover=false;

                //handler.postDelayed(this, 2000);
            }
        };
        handlermap.postDelayed(run,30000);
    }

    @Override
    public void NoMoverMapaStop() {
        if(handlermap!=null)
            handlermap.removeCallbacks(run);
    }


    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public void CamaraPosition(){

       if(NoMover){
           return;
       }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i=0;i<modelFireBase.size();i++){
            if(modelFireBase.get(i).getMarkerOptions()!=null){
                builder.include(modelFireBase.get(i).getMarkerOptions().getPosition());
            }
        }

        for(int i=0;i<obras.length();i++){
            try {
                builder.include(new LatLng(Double.parseDouble(obras.getJSONObject(i).getString("latitud")),Double.parseDouble(obras.getJSONObject(i).getString("longitud"))));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        if(obras.length()>0 || modelFireBase.size()>0){
            Log.i("Marcando","Lat: "+latp+" lon:"+lonp);
            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) Math.round(pantallah*0.05)));

        }



    }

    private void PonerMarcadoresObras() {
        //googleMap.clear();


        if(obras!=null){
            for(int i = 0 ; i < obras.length() ; i++){

                try {
                    LatLng obra = null;
                    obra = new LatLng(Double.parseDouble(obras.getJSONObject(i).getString("latitud")), Double.parseDouble(obras.getJSONObject(i).getString("longitud")));
                    MarkerOptions option=new MarkerOptions().position(obra)
                            .title(obras.getJSONObject(i).getString("obra"))
                            // below line is use to add custom marker on our map.
                            .icon(iconobra);
                    googleMap.addMarker(option);
                    Log.i("GetObras","Obra: "+obras.getJSONObject(i).getString("obra"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }


        
    }
}


