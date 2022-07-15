package com.nervion.sionmovil.Penalizacion;

public class PenalizacionAlmacen_Constructor {

    public PenalizacionAlmacen_Constructor() { /*Required empty public constructor*/ }

    public int paID;
    public String paFecha;
    public String paHora;
    public int paCantidad;
    public String paUsuarioPenalizado;
    public int paCantidadEscaneada;
    public String paObservaciones;
    public int paRestantes;

    public int getPaID() {
        return paID;
    }

    public void setPaID(int paID) {
        this.paID = paID;
    }

    public String getPaFecha() {
        return paFecha;
    }

    public void setPaFecha(String paFecha) {
        this.paFecha = paFecha;
    }

    public String getPaHora() {
        return paHora;
    }

    public void setPaHora(String paHora) {
        this.paHora = paHora;
    }

    public int getPaCantidad() {
        return paCantidad;
    }

    public void setPaCantidad(int paCantidad) {
        this.paCantidad = paCantidad;
    }

    public String getPaUsuarioPenalizado() {
        return paUsuarioPenalizado;
    }

    public void setPaUsuarioPenalizado(String paUsuarioPenalizado) {
        this.paUsuarioPenalizado = paUsuarioPenalizado;
    }

    public int getPaCantidadEscaneada() {
        return paCantidadEscaneada;
    }

    public void setPaCantidadEscaneada(int paCantidadEscaneada) {
        this.paCantidadEscaneada = paCantidadEscaneada;
    }

    public String getPaObservaciones() {
        return paObservaciones;
    }

    public void setPaObservaciones(String paObservaciones) {
        this.paObservaciones = paObservaciones;
    }

    public int getPaRestantes() {
        return paRestantes;
    }

    public void setPaRestantes(int paRestantes) {
        this.paRestantes = paRestantes;
    }
}
