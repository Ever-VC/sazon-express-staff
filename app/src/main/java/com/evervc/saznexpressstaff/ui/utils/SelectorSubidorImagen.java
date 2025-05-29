package com.evervc.saznexpressstaff.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

public class SelectorSubidorImagen {
    public interface EscuchadorSubida {
        void alSubir(String url);
        void alFallar(Exception e);
    }

    private final Activity actividad;
    private final int codigo;
    private final String rutaStorage;
    private final EscuchadorSubida escuchador;
    private Uri uriSeleccionada;

    public SelectorSubidorImagen(Activity actividad, int codigo, String rutaStorage, EscuchadorSubida escuchador) {
        this.actividad = actividad;
        this.codigo = codigo;
        this.rutaStorage = rutaStorage;
        this.escuchador = escuchador;
    }
    public Uri getUriSeleccionada() {
        return uriSeleccionada;
    }

    public void iniciarSeleccion() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        actividad.startActivityForResult(intent, codigo);
    }

    /*public void procesarResultado(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == codigo && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            SubidorImagenes.subirImagen(rutaStorage, uri, new SubidorImagenes.EscuchadorSubida() {
                @Override
                public void alSubir(String url) {
                    escuchador.alSubir(url);
                }

                @Override
                public void alFallar(Exception e) {
                    escuchador.alFallar(e);
                }
            });
        }
    }*/
    public void procesarResultado(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == codigo && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            this.uriSeleccionada = uri; // IMPORTANTE: guardamos la URI seleccionada

            SubidorImagenes.subirImagen(rutaStorage, uri, new SubidorImagenes.EscuchadorSubida() {
                @Override
                public void alSubir(String url) {
                    escuchador.alSubir(url);
                }

                @Override
                public void alFallar(Exception e) {
                    escuchador.alFallar(e);
                }
            });
        }
    }
}
