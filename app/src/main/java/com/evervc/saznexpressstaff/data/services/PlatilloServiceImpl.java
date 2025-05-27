package com.evervc.saznexpressstaff.data.services;

import android.net.Uri;

import com.evervc.saznexpressstaff.data.models.Platillo;
import com.evervc.saznexpressstaff.data.repositories.PlatilloRepository;
import com.evervc.saznexpressstaff.data.repositories.PlatilloRepositoryImpl;
import com.evervc.saznexpressstaff.ui.utils.SubidorImagenes;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.UUID;

public class PlatilloServiceImpl implements PlatilloService{
    private final PlatilloRepository repo = new PlatilloRepositoryImpl();
    @Override
    public Task<Void> crearPlatillo(Platillo platillo, Uri imagenUri) {
        String id = UUID.randomUUID().toString();
        platillo.setId(id);

        String rutaImagen = "platillos/" + id + "/principal.jpg";

        return SubidorImagenes.subirImagenTask(rutaImagen, imagenUri)
                .continueWithTask(task -> {
                    platillo.setImagenUrl(task.getResult());
                    return repo.crearPlatillo(platillo);
                });
    }

    @Override
    public Task<List<Platillo>> obtenerTodosLosPlatillos() {
        return repo.obtenerTodosLosPlatillos();
    }

    @Override
    public Task<Void> actualizarPlatillo(Platillo platillo, Uri nuevaImagenUri) {
        if (nuevaImagenUri != null) {
            String rutaImagen = "platillos/" + platillo.getId() + "/principal.jpg";
            return SubidorImagenes.subirImagenTask(rutaImagen, nuevaImagenUri)
                    .continueWithTask(task -> {
                        platillo.setImagenUrl(task.getResult());
                        return repo.actualizarPlatillo(platillo);
                    });
        }
        return repo.actualizarPlatillo(platillo);
    }

    @Override
    public Task<Void> eliminarPlatillo(String id) {
        return repo.eliminarPlatillo(id);
    }

    @Override
    public Task<Void> actualizarDisponibilidad(String id, boolean disponible) {
        return repo.actualizarDisponibilidad(id, disponible);
    }
}
