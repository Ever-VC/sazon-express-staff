package com.evervc.saznexpressstaff.data.repositories;

import com.evervc.saznexpressstaff.data.models.Platillo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.ArrayList;

public class PlatilloRepositoryImpl implements PlatilloRepository {
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference("platillos");

    @Override
    public Task<Void> crearPlatillo(Platillo platillo) {
        return db.child(platillo.getId()).setValue(platillo);
    }

    @Override
    public Task<List<Platillo>> obtenerTodosLosPlatillos() {
        return db.get().continueWith(task -> {
            List<Platillo> lista = new ArrayList<>();
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Platillo platillo = snapshot.getValue(Platillo.class);
                    if (platillo != null) {
                        lista.add(platillo);
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public Task<Platillo> obtenerPlatilloPorId(String id) {
        return db.child(id).get().continueWith(task -> {
            if (!task.isSuccessful()) throw task.getException();
            return task.getResult().getValue(Platillo.class);
        });
    }

    @Override
    public Task<Void> actualizarPlatillo(Platillo platillo) {
        return db.child(platillo.getId()).setValue(platillo);
    }

    @Override
    public Task<Void> eliminarPlatillo(String id) {
        return db.child(id).removeValue();
    }

    @Override
    public Task<Void> actualizarDisponibilidad(String id, boolean disponible) {
        return db.child(id).child("disponible").setValue(disponible);
    }
}
