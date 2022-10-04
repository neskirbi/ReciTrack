package com.recitrack.recitrack.Model;

public class Orden {

    String id;
    int orden;

    public Orden() {

    }

    public Orden(String id, int orden) {
        this.id = id;
        this.orden = orden;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
