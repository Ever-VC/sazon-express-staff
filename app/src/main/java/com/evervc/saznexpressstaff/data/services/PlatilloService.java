package com.evervc.saznexpressstaff.data.services;

import android.net.Uri;

import com.evervc.saznexpressstaff.data.models.Platillo;
import com.google.android.gms.tasks.Task;
import java.util.List;

public interface PlatilloService {
    Task<Void> crearPlatillo(Platillo platillo, Uri imagenUri);
    Task<List<Platillo>> obtenerTodosLosPlatillos();
    Task<Void> actualizarPlatillo(Platillo platillo, Uri nuevaImagenUri);
    Task<Void> eliminarPlatillo(String id);
    Task<Void> actualizarDisponibilidad(String id, boolean disponible);
}
