package com.evervc.saznexpressstaff.data.repositories;

import com.evervc.saznexpressstaff.data.models.Platillo;
import com.google.android.gms.tasks.Task;
import java.util.List;

public interface PlatilloRepository {
    Task<Void> crearPlatillo(Platillo platillo);
    Task<List<Platillo>> obtenerTodosLosPlatillos();
    Task<Platillo> obtenerPlatilloPorId(String id);
    Task<Void> actualizarPlatillo(Platillo platillo);
    Task<Void> eliminarPlatillo(String id);
    Task<Void> actualizarDisponibilidad(String id, boolean disponible);
}
