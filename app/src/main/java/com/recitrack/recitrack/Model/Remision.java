package com.recitrack.recitrack.Model;

public class Remision {
    String id="";
    String id_chofer="";
    String nombre="";
    String obra="";
    String obra_domicilio="";
    String producto="";
    String latitud="";
    String longitud="";
    String tipo    ="";
    String error="";

    public Remision() {
    }

    public Remision(String id, String id_chofer, String nombre, String obra, String obra_domicilio, String producto, String latitud, String longitud, String tipo, String error) {
        this.id = id;
        this.id_chofer = id_chofer;
        this.nombre = nombre;
        this.obra = obra;
        this.obra_domicilio = obra_domicilio;
        this.producto = producto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo = tipo;
        this.error = error;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_chofer() {
        return id_chofer;
    }

    public void setId_chofer(String id_chofer) {
        this.id_chofer = id_chofer;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObra() {
        return obra;
    }

    public void setObra(String obra) {
        this.obra = obra;
    }

    public String getObra_domicilio() {
        return obra_domicilio;
    }

    public void setObra_domicilio(String obra_domicilio) {
        this.obra_domicilio = obra_domicilio;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
