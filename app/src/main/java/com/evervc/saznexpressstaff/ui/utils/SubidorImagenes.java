package com.evervc.saznexpressstaff.ui.utils;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SubidorImagenes {
    public interface EscuchadorSubida {
        void alSubir(String urlDescarga);
        void alFallar(Exception e);
    }

    /**
     * Sube una imagen a Firebase Storage y retorna la URL de descarga.
     * @param ruta Ruta de almacenamiento (ej: "usuarios/uid/perfil.jpg")
     * @param uriArchivo Uri de la imagen seleccionada
     * @param escuchador Callback con el resultado
     */
    public static void subirImagen(String ruta, Uri uriArchivo, EscuchadorSubida escuchador) {
        StorageReference referencia = FirebaseStorage.getInstance().getReference(ruta);

        referencia.putFile(uriArchivo)
                .addOnSuccessListener(tarea ->
                        referencia.getDownloadUrl().addOnSuccessListener(uri -> {
                            escuchador.alSubir(uri.toString());
                        }).addOnFailureListener(escuchador::alFallar)
                )
                .addOnFailureListener(escuchador::alFallar);
    }
}
