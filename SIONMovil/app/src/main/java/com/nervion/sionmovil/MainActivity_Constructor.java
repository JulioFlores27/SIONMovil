package com.nervion.sionmovil;

public class MainActivity_Constructor {
    public String tituloModulo;
    public int imagenModulo;

    public MainActivity_Constructor(String tituloModulo, int imagenModulo) {
        this.tituloModulo = tituloModulo;
        this.imagenModulo = imagenModulo;
    }

    public String getTituloModulo() {
        return tituloModulo;
    }

    public int getImagenModulo() {
        return imagenModulo;
    }
}
