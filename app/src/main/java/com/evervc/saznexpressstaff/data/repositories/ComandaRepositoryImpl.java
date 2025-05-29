package com.evervc.saznexpressstaff.data.repositories;

import com.evervc.saznexpressstaff.data.models.Comanda;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComandaRepositoryImpl implements ComandaRepository {

    private final DatabaseReference database;

    public ComandaRepositoryImpl() {
        this.database = FirebaseDatabase.getInstance().getReference("comandas");
    }

    @Override
    public Task<Void> crearComanda(Comanda comanda) {
        String id = database.push().getKey();
        if (id == null) {
            return Tasks.forException(new Exception("No se pudo generar ID de la comanda"));
        }

        comanda.setId(id);

        return database.child(id).setValue(comanda);
    }

    @Override
    public Task<Void> actualizarEstadoComanda(String comandaId, String nuevoEstado, String uidCocinero, String fecha) {
        DatabaseReference comandaRef = database.child(comandaId);
        Map<String, Object> updates = new HashMap<>();

        updates.put("estado", nuevoEstado);
        if (nuevoEstado.equalsIgnoreCase("en preparacion")) {
            updates.put("fechaYHoraEnCocina", fecha);
            updates.put("uidCocinero", uidCocinero);
        } else if (nuevoEstado.equalsIgnoreCase("listo")) {
            updates.put("fechaYHoraListo", fecha);
        }

        return comandaRef.updateChildren(updates);
    }

    @Override
    public Task<List<Comanda>> obtenerComandasPorFecha(String fecha) {
        return database.get().continueWith(task -> {
            List<Comanda> resultado = new ArrayList<>();
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Comanda comanda = snapshot.getValue(Comanda.class);
                    if (comanda != null && comanda.getFechaYHoraPedido() != null &&
                            comanda.getFechaYHoraPedido().startsWith(fecha)) {
                        resultado.add(comanda);
                    }
                }
            }
            return resultado;
        });
    }

    @Override
    public Task<List<Comanda>> obtenerTodasLasComandas() {
        return database.get().continueWith(task -> {
            List<Comanda> resultado = new ArrayList<>();
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Comanda comanda = snapshot.getValue(Comanda.class);
                    if (comanda != null) {
                        resultado.add(comanda);
                    }
                }
            }
            return resultado;
        });
    }

    @Override
    public Task<List<Comanda>> obtenerComandasPorEmpleado(String uidEmpleado) {
        return database.orderByChild("uidEmpleado").equalTo(uidEmpleado)
                .get()
                .continueWith(task -> {
                    List<Comanda> resultado = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            Comanda comanda = snapshot.getValue(Comanda.class);
                            if (comanda != null) resultado.add(comanda);
                        }
                    }
                    return resultado;
                });
    }

    @Override
    public Task<List<Comanda>> obtenerComandasPorCocinero(String uidCocinero) {
        return database.orderByChild("uidCocinero").equalTo(uidCocinero)
                .get()
                .continueWith(task -> {
                    List<Comanda> resultado = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            Comanda comanda = snapshot.getValue(Comanda.class);
                            if (comanda != null) resultado.add(comanda);
                        }
                    }
                    return resultado;
                });
    }

    @Override
    public Task<Map<String, Integer>> obtenerPlatillosMasVendidos(String fecha) {
        return database.get().continueWith(task -> {
            Map<String, Integer> conteo = new HashMap<>();
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Comanda comanda = snapshot.getValue(Comanda.class);
                    if (comanda != null && comanda.getFechaYHoraPedido() != null &&
                            comanda.getFechaYHoraPedido().startsWith(fecha)) {

                        for (ComandaPlatillo platillo : comanda.getPlatillos().values()) {
                            String id = platillo.getPlatilloId();
                            int cantidad = platillo.getCantidad();
                            conteo.put(id, conteo.getOrDefault(id, 0) + cantidad);
                        }
                    }
                }
            }
            return conteo;
        });
    }
    @Override
    public Task<Void> guardarPlatillo(String comandaId, ComandaPlatillo platillo) {
        return database.child(comandaId).child("platillos").child(platillo.getPlatilloId()).setValue(platillo);
    }

    @Override
    public Task<Void> eliminarPlatillo(String comandaId, String platilloId) {
        return database.child(comandaId).child("platillos").child(platilloId).removeValue();
    }
}

