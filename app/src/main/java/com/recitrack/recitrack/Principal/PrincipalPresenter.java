package com.recitrack.recitrack.Principal;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
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
import androidx.arch.core.internal.SafeIterableMap;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.recitrack.recitrack.Metodos;
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

    private double angulo=0.0;
    BitmapDescriptor icon,iconobra;
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<MarkerOptions> marcadores=new ArrayList<>();
    ArrayList<DatabaseReference> references=new ArrayList<>();
    ArrayList<ChildEventListener> listeners=new ArrayList<>();
    ArrayList<String> descripciones=new ArrayList<>();
    GoogleMap googleMap;
    JSONArray obras;

    public PrincipalPresenter(PrincipalView principalView, Context context) {
        this.principalView=principalView;
        this.context=context;
        metodos=new Metodos(context);
        principalInteractor=new PrincipalInteractor(this,context);
        DisplayMetrics metrics = new DisplayMetrics();
        principalView.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int tamcamiones= (int) Math.round(metrics.heightPixels*0.04);
        int tamobras= (int) Math.round(metrics.heightPixels*0.09);
        icon= BitmapDescriptorFactory.fromBitmap(resizeMapIcons("olla",tamcamiones,tamcamiones));
        iconobra= BitmapDescriptorFactory.fromBitmap(resizeMapIcons("obra1",tamobras,tamobras));
        principalInteractor.GetObras();
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
        int tamini=ids.size();
        for(int i=0 ; i < datos.length() ; i++){

            try {


                if(ids.contains(datos.getJSONObject(i).getString("id")) ){
                    Log.i("QuieroQuitar ","ids saltando"+ids.size()+":"+datos.getJSONObject(i).getString("id"));
                    continue;
                }
                if(1==Integer.parseInt( datos.getJSONObject(i).getString("confirmacion"))){
                    Log.i("QuieroQuitar ","ids nullo"+ids.size()+":"+datos.getJSONObject(i).getString("id"));
                    ids.add(null);
                    marcadores.add(null);
                    descripciones.add(null);
                    listeners.add(null);
                    references.add(null);
                    continue;

                }
                Log.i("QuieroQuitar ","Agrega ids "+ids.size()+":"+datos.getJSONObject(i).getString("id"));
                ids.add(datos.getJSONObject(i).getString("id"));
                marcadores.add(null);
                descripciones.add(datos.getJSONObject(i).getString("descripcion"));


                String Tema="Remisiones/Tracking/"+datos.getJSONObject(i).getString("id");
                Log.i("Localizando ","Tema:"+"Remisiones/Tracking/"+datos.getJSONObject(i).getString("id"));
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference(Tema);//Sala de chat
                references.add(databaseReference);
                ChildEventListener listener;

                listener=new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                        Remision remision=snapshot.getValue(Remision.class);
                        Log.i("Localizando",remision.getId()+"");
                        Log.i("Localizando",remision.getLatitud()+"");

                        LatLng camion = new LatLng(Double.parseDouble(remision.getLatitud()), Double.parseDouble(remision.getLongitud()));

                        MarkerOptions option=new MarkerOptions().position(camion)
                                .title(remision.getObra()+"\n"+remision.getProducto())
                                // below line is use to add custom marker on our map.
                                .icon(icon);

                        for(int ii = 0 ; ii< ids.size() ;ii++){
                            if(remision.getId().equals(ids.get(ii))){
                                marcadores.remove(ii);
                                marcadores.add(ii,option);
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
                };
                listeners.add(listener);
                databaseReference.addChildEventListener(listener);
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

        for(int j=ids.size()-1;j>=0;j--){
            Log.i("QuieroQuitar ","tamanioo:"+ids.size()+" index:"+ j);
            if(ids.get(j)==null || !temp.contains(ids.get(j))){
                Log.i("QuieroQuitar ","ids Quitando "+ids.size()+":"+ ids.get(j));
                //NotificaEntega("Entregado",descripciones.get(j));
                if(references.get(j)!=null && listeners.get(j)!=null)
                references.get(j).removeEventListener(listeners.get(j));
                ids.remove(j);
                marcadores.remove(j);
                references.remove(j);
                listeners.remove(j);
                descripciones.remove(j);

            }
        }
    }


    public void Notificar(String title, String body, int icono, Intent intent, int id) {

/*

        intent.putExtra("NoreiniciarServicio", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //
        String NOTIFICATION_CHANNEL_ID = "Emergencia";

        RemoteViews myRemoteView = new RemoteViews(context.getPackageName(), R.layout.imagen_notification);

        Bitmap bit= BitmapFactory.decodeResource(context.getResources(),icono);
        myRemoteView.setImageViewBitmap(R.id.icono,bit);
        myRemoteView.setTextViewText(R.id.noti_titulo, title);
        myRemoteView.setTextViewText(R.id.noti_body, body);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(false)
                .setVibrate(new long[]{1000, 1000, 500, 1000})
                .setSound(defaultSoundUri)
                .setPriority(1)
                .setContentIntent(pendingIntent)
                .setContentInfo("info")
                .setContent(myRemoteView);

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

        notificationManager.notify(id, notificationBuilder.build());
        */
    }

    private void NotificaEntega(String entregado, String s) {
        Log.i("Notificando",entregado);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =" Entregas Recitrack";
            String description = "Notificaciones de entrega.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(metodos.GetUuid(), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        String NOTIFICATION_CHANNEL_ID = "Entregas";
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

            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {

                    lat=arg0.getLatitude();
                    lon=arg0.getLongitude();
                    Log.i("Moviendo","Lat:"+lat+" Lon:"+lon);


                }
            });
            CamaraPosition(lat,lon);
            PonerMarcadores();
        }


    }

    @Override
    public void Focus(double latitude, double longitude, double angulo) {
        lat=latitude;
        lon=longitude;
        this.angulo=angulo;
    }

    @Override
    public void GuardaObras(JSONArray datos) {
        obras=datos;
    }


    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public void CamaraPosition(double latitude, double longitude){
        latp=0.0;
        lonp=0.0;
        int conp=0;


        if(marcadores.size()>0){
            for(int i=0;i<marcadores.size();i++){
                if(marcadores.get(i)!=null){

                    latp=latp+marcadores.get(i).getPosition().latitude;
                    lonp=lonp+marcadores.get(i).getPosition().longitude;
                }else{
                    conp++;
                }
            }
            for(int i=0;i<obras.length();i++){
                try {

                    latp=latp+Double.parseDouble(obras.getJSONObject(i).getString("latitud"));
                    lonp=lonp+Double.parseDouble(obras.getJSONObject(i).getString("longitud"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            //latp+=latitude;
            //lonp+=longitude;
            latp=latp/(marcadores.size()+obras.length()-conp);
            lonp=lonp/(marcadores.size()+obras.length()-conp);

            Log.i("Marcando","Lat: "+latp+" lon:"+lonp);




            //if(obras.length()>0 && marcadores.size()>0){
                if(false){

                try {
                    LatLngBounds australiaBounds = null;
                    australiaBounds = new LatLngBounds(
                            new LatLng(Double.parseDouble(obras.getJSONObject(0).getString("latitud")), +Double.parseDouble(obras.getJSONObject(0).getString("longitud"))),
                            new LatLng(marcadores.get(0).getPosition().latitude, marcadores.get(0).getPosition().longitude)  );
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(australiaBounds.getCenter(), 10));
                } catch (Exception e) {
                    LatLngBounds australiaBounds = null;
                    try {
                        australiaBounds = new LatLngBounds(

                                new LatLng(marcadores.get(0).getPosition().latitude, marcadores.get(0).getPosition().longitude),
                                new LatLng(Double.parseDouble(obras.getJSONObject(0).getString("latitud")), +Double.parseDouble(obras.getJSONObject(0).getString("longitud"))));
                    } catch (Exception jsonException) {
                        jsonException.printStackTrace();
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(australiaBounds.getCenter(), 10));
                }

            }
            else{
                //Si quiero desplasar para arriba el punto
                // LatLng ubicacion = new LatLng(latitude-0.0085, longitude);
                LatLng ubicacion = new LatLng(latp, lonp);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(ubicacion)      // Sets the center of the map to Mountain View
                        .zoom(11)                   // Sets the zoom
                        .bearing((float) angulo)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }


    }

    private void PonerMarcadores() {
        googleMap.clear();


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


        for(int i = 0 ; i < marcadores.size() ; i++){

            if( marcadores.get(i)!=null){
                Log.i("PonerMarcadores","Tam marcadores:"+ marcadores.size()+" lat:"+marcadores.get(i).getPosition().latitude+" lon:"+marcadores.get(i).getPosition().longitude);
                googleMap.addMarker(marcadores.get(i));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(marcadores.get(i).getPosition()));
            }
        }
    }
}
