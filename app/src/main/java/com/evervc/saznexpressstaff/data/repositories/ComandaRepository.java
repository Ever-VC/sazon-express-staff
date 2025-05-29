package com.evervc.saznexpressstaff.data.repositories;

import com.evervc.saznexpressstaff.data.models.Comanda;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Map;

public interface ComandaRepository {
    Task<Void> crearComanda(Comanda comanda);
    Task<Void> actualizarEstadoComanda(String comandaId, String nuevoEstado, String uidCocinero, String fecha);
    Task<List<Comanda>> obtenerComandasPorFecha(String fecha);
    Task<List<Comanda>> obtenerComandasPorEmpleado(String uidEmpleado);
    Task<List<Comanda>> obtenerComandasPorCocinero(String uidCocinero);
    Task<List<Comanda>> obtenerTodasLasComandas();
    Task<Map<String, Integer>> obtenerPlatillosMasVendidos(String fecha);
    Task<Void> guardarPlatillo(String comandaId, ComandaPlatillo platillo);
    Task<Void> eliminarPlatillo(String comandaId, String platilloId);
}

