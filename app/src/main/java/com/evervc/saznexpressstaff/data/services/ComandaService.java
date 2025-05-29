package com.evervc.saznexpressstaff.data.services;

import com.evervc.saznexpressstaff.data.models.Comanda;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Map;

public interface ComandaService {
    Task<Void> crearComanda(String nombreCliente, String uidEmpleado, Map<String, ComandaPlatillo> platillos);
    Task<Void> actualizarEstadoComanda(String comandaId, String nuevoEstado, String uidCocinero);
    Task<List<Comanda>> obtenerComandasPorFecha(String fecha);
    Task<List<Comanda>> obtenerTodasLasComandas();
    Task<List<Comanda>> obtenerComandasPorEmpleado(String uidEmpleado);
    Task<List<Comanda>> obtenerComandasPorCocinero(String uidCocinero);
    Task<Map<String, Integer>> obtenerPlatillosMasVendidos(String fecha);
    Task<Void> guardarPlatilloEnComanda(String comandaId, ComandaPlatillo platillo);
    Task<Void> eliminarPlatilloDeComanda(String comandaId, String platilloId);
}
