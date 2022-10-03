package com.recitrack.recitrack.Principal;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;


import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.recitrack.recitrack.Login.Login;
import com.recitrack.recitrack.databinding.ActivityMenuViewBinding;

import com.recitrack.recitrack.Login.LoginView;
import com.recitrack.recitrack.Metodos;

import com.recitrack.recitrack.R;



import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class PrincipalView extends AppCompatActivity  implements OnMapReadyCallback , NavigationView.OnNavigationItemSelectedListener, Principal.PrincipalView {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuViewBinding binding;
    NavigationView navigationView;
    Metodos metodos;
    Context context;



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
        //principalPresenter= new PrincipalPresenter(this,context);
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

        nombres.setText(metodos.GetNombres());

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

    @Override
    protected void onResume() {
        super.onResume();
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
        googleMap = googleMapt;
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 50000);
            }
        },0);

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


                        CamaraPosition(arg0.getLatitude(),arg0.getLongitude());



                }
            });
        }

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

        if(latitude!=0.0 && longitude!=0.0 ){
            LatLng ubicacion = new LatLng(latitude-0.0085, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ubicacion)      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing((float) angulo)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }






    @Override
    public void Error(String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }




}