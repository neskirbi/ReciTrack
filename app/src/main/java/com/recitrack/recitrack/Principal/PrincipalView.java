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
import com.google.android.gms.maps.UiSettings;
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



    private SensorManager sensorManager;
    private Sensor sensor;
    SensorEventListener mSensorEventListener=null;

    private double angulo;

    PrincipalPresenter principalPresenter;
    BottomNavigationView nav_entregar;
    GoogleMap googleMap;
    TextView nombres;
    SupportMapFragment mapFragment;


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


        mapFragment = (SupportMapFragment) getSupportFragmentManager() .findFragmentById(R.id.mapa);



        nombres=navigationView.getHeaderView(0).findViewById(R.id.nombre);

        nombres.setText(metodos.GetNombres()+" "+metodos.GetApellidos());


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
        mapFragment.getMapAsync(this);
        principalPresenter.GetRemisiones();


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
    public void onMapReady(GoogleMap Map) {
        googleMap=Map;

        //Verifico permisos
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Activa que funcione el puntero de mi posission
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);


        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                principalPresenter.Marcar(googleMap);

                handler.postDelayed(this, 2000);
            }
        },0);


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
    public void onBackPressed() {
        super.onBackPressed();
    }
}