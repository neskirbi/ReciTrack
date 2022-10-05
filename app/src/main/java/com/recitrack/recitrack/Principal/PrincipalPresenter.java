package com.recitrack.recitrack.Principal;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.recitrack.recitrack.Metodos;
import com.recitrack.recitrack.Model.Remision;

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
    BitmapDescriptor icon;
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<MarkerOptions> marcadores=new ArrayList<>();
    ArrayList<DatabaseReference> references=new ArrayList<>();
    ArrayList<ChildEventListener> listeners=new ArrayList<>();
    GoogleMap googleMap;

    public PrincipalPresenter(PrincipalView principalView, Context context) {
        this.principalView=principalView;
        this.context=context;
        metodos=new Metodos(context);
        principalInteractor=new PrincipalInteractor(this,context);

        icon= BitmapDescriptorFactory.fromBitmap(resizeMapIcons("olla",100,100));
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
        Revisar(datos);
        for(int i=0 ; datos.length()>i ; i++){

            try {
                Log.i("Localizando","IniciarRastreo:"+datos.getJSONObject(i).getString("id"));
                if(ids.contains(datos.getJSONObject(i).getString("id"))){
                    continue;
                }

                ids.add(i,datos.getJSONObject(i).getString("id"));
                marcadores.add(i,null);



                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Remisiones/Tracking/"+datos.getJSONObject(i).getString("id"));//Sala de chat
                references.add(i,databaseReference);
                ChildEventListener listener;

                listener=new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                        Remision remision=snapshot.getValue(Remision.class);
                        Log.i("Localizando",remision.getLatitud()+"");

                        LatLng obra = new LatLng(Double.parseDouble(remision.getLatitud()), Double.parseDouble(remision.getLongitud()));

                        MarkerOptions option=new MarkerOptions().position(obra)
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
                listeners.add(i,listener);
                databaseReference.addChildEventListener(listener);
                //databaseReference.removeValue();
            } catch (JSONException e) {
                Log.i("Localizando",e.getMessage());
            }
        }
    }

    private void Revisar(JSONArray datos) {
        ArrayList<String> temp=new ArrayList<>();
        for(int i=0 ; datos.length()>i ; i++){
            try {
                temp.add(i,datos.getJSONObject(i).getString("id"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(int j=0;j<ids.size();j++){
            if(!temp.contains(ids.get(j))){
                references.get(j).removeEventListener(listeners.get(j));
                ids.remove(j);
                marcadores.remove(j);
                references.remove(j);
                listeners.remove(j);

            }
        }
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
            latp+=latitude;
            lonp+=longitude;
            latp=latp/(marcadores.size()+1-conp);
            lonp=lonp/(marcadores.size()+1-conp);

            if(latitude!=0.0 && longitude!=0.0 ){
                //Si quiero desplasar para arriba el punto
                // LatLng ubicacion = new LatLng(latitude-0.0085, longitude);
                LatLng ubicacion = new LatLng(latp, lonp);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(ubicacion)      // Sets the center of the map to Mountain View
                        .zoom(12)                   // Sets the zoom
                        .bearing((float) angulo)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }


    }

    private void PonerMarcadores() {
        googleMap.clear();

        for(int i = 0 ; i < marcadores.size() ; i++){

            if( marcadores.get(i)!=null){
                Log.i("PonerMarcadores","Tam marcadores:"+ marcadores.size()+" lat:"+marcadores.get(i).getPosition().latitude+" lon:"+marcadores.get(i).getPosition().longitude);
                googleMap.addMarker(marcadores.get(i));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(marcadores.get(i).getPosition()));
            }
        }
    }
}
