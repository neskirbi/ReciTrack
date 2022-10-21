package com.recitrack.recitrack.Model;

import android.location.GnssAntennaInfo;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.recitrack.recitrack.CostomClass.ListenerFB;

public class ModelFireBase {
    String id;
    MarkerOptions markerOptions;
    DatabaseReference databaseReference;
    ListenerFB listenerFB;
    String descripcion;

    public ModelFireBase() {
    }

    public ModelFireBase(String id, MarkerOptions markerOptions, DatabaseReference databaseReference, ListenerFB listenerFB, String descripcion) {
        this.id = id;
        this.markerOptions = markerOptions;
        this.databaseReference = databaseReference;
        this.listenerFB = listenerFB;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public ListenerFB getListenerFB() {
        return listenerFB;
    }

    public void setListenerFB(ListenerFB listenerFB) {
        this.listenerFB = listenerFB;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
