package com.recitrack.recitrack.Principal;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;


import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;

import com.recitrack.recitrack.Login.Login;
import com.recitrack.recitrack.Model.Remision;
import com.recitrack.recitrack.databinding.ActivityMenuViewBinding;

import com.recitrack.recitrack.Metodos;

import com.recitrack.recitrack.R;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PrincipalView extends AppCompatActivity  implements OnMapReadyCallback , NavigationView.OnNavigationItemSelectedListener, Principal.PrincipalView {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuViewBinding binding;
    NavigationView navigationView;
    Metodos metodos;
    Context context;
    Dialog dialog;
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<MarkerOptions> marcadores=new ArrayList<>();
    Double latp=0.0,lonp=0.0,lat=0.0,lon=0.0;



    private SensorManager sensorManager;
    private Sensor sensor;
    SensorEventListener mSensorEventListener=null;

    private double angulo;

    PrincipalPresenter principalPresenter;
    BottomNavigationView nav_entregar;
    GoogleMap googleMap;
    private TextView nombres;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        context=this;
        metodos=new Metodos(context);
        principalPresenter= new PrincipalPresenter(this,context);
        setSupportActionBar(binding.appBarMenuView.toolbar);
        /*binding.appBarMenuView.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_view);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //NavigationUI.setupWithNavController(navigationView, navController);






        nombres=navigationView.getHeaderView(0).findViewById(R.id.nombre);

        nombres.setText(metodos.GetNombres()+" "+metodos.GetApellidos());

        //Toast.makeText(this, "Version:"+metodos.GetVersion()+" Tipo: "+metodos.GetTipo(), Toast.LENGTH_SHORT).show();





        //iniciando sensor rotacion
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    float X_Axis = event.values[0];
                    float Y_Axis = event.values[1];
                    angulo = Math.atan2(X_Axis, Y_Axis)/(Math.PI/180)*-1;
                    Log.i("sensorgiro",""+angulo);


                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);




    }








    @Override
    protected void onStart() {
        super.onStart();

    }

    public void GetRemisiones(View view){
        metodos.Vibrar(metodos.VibrarPush());
        principalPresenter.GetRemisiones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        principalPresenter.GetOrdenes();
        metodos.PedirPermisoGPS(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("manu",""+id);




        if(R.id.nav_salir==id){
            metodos.CerrarSesion();
            startActivity(new Intent(context, Login.class));

        }

        return true;
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_view);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onMapReady(GoogleMap googleMapt) {
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(lat!=0.0 && lon!=0.0){
                    PonerMarcadores();
                    CamaraPosition(lat,lon);
                }

                handler.postDelayed(this, 5000);
            }
        },1000);

        googleMap = googleMapt;


        //if(lat.length()>0 && lon.length()>0)
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon))).title(nombre));

        //Verifico permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Activa que funcione el puntero de mi posission
        googleMap.setMyLocationEnabled(true);
        // Me ubica cuando tiene lat y lon
        if (googleMap != null) {


            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {


                    lat=arg0.getLatitude();
                    lon=arg0.getLongitude();



                }
            });
        }
        principalPresenter.GetRemisiones();
    }

    public void CargarMarcadoresObras(JsonArray viajes){
        for(int i=0 ; i < viajes.size() ; i++){
            try {
                JSONObject jsonObject=new JSONObject(viajes.get(i).toString());
                googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(jsonObject.get("latitud").toString()), Double.parseDouble(jsonObject.get("longitud").toString()))).title(jsonObject.get("obra").toString()));
                //Toast.makeText(context, ""+jsonObject.get("pedidos"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

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






    @Override
    public void Error(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void AbreDialogo() {
        dialog= ProgressDialog.show(context, "","Descargando la Información...", true);
        dialog.setCancelable(true);
    }

    @Override
    public void CierraDialogo() {

        dialog.dismiss();
    }

    @Override
    public void IniciarRastreo(JSONArray datos) {

        for(int i=0 ; datos.length()>i ; i++){

            try {
                Log.i("Localizando","IniciarRastreo:"+datos.getJSONObject(i).getString("id"));

                ids.add(i,datos.getJSONObject(i).getString("id"));
                marcadores.add(i,null);
                FirebaseDatabase firebaseDatabaseAvisos = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabaseAvisos.getReference("Remisiones/Tracking/"+datos.getJSONObject(i).getString("id"));//Sala de chat
                ChildEventListener listener;
                listener=new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                        Remision remision=snapshot.getValue(Remision.class);
                        Log.i("Localizando",remision.getLatitud()+"");

                        LatLng obra = new LatLng(Double.parseDouble(remision.getLatitud()), Double.parseDouble(remision.getLongitud()));
                        BitmapDescriptor icon=BitmapDescriptorFactory.fromBitmap(resizeMapIcons("olla",100,100));

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
                databaseReference.addChildEventListener(listener);
            } catch (JSONException e) {
                Log.i("Localizando",e.getMessage());
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

    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}