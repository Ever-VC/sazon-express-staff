package com.evervc.saznexpressstaff.data.services;

import com.evervc.saznexpressstaff.data.models.Comanda;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.evervc.saznexpressstaff.data.repositories.ComandaRepository;
import com.evervc.saznexpressstaff.data.repositories.ComandaRepositoryImpl;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class ComandaServiceImpl implements ComandaService {

    private final ComandaRepository comandaRepository = new ComandaRepositoryImpl();

    @Override
    public Task<Void> crearComanda(String nombreCliente, String uidEmpleado, Map<String, ComandaPlatillo> platillos) {
        Comanda comanda = new Comanda();
        comanda.setNombreCliente(nombreCliente);
        comanda.setUidEmpleado(uidEmpleado);
        comanda.setPlatillos(platillos);
        comanda.setEstado("pendiente");
        comanda.setToken(generarToken());
        comanda.setFechaYHoraPedido(obtenerFechaYHoraActual());
        comanda.setFechaYHoraEnCocina(null);
        comanda.setFechaYHoraListo(null);
        comanda.setUidCocinero(null);

        return comandaRepository.crearComanda(comanda);
    }

    @Override
    public Task<Void> actualizarEstadoComanda(String comandaId, String nuevoEstado, String uidCocinero) {
        return comandaRepository.actualizarEstadoComanda(comandaId, nuevoEstado, uidCocinero, obtenerFechaYHoraActual());
    }


    @Override
    public Task<List<Comanda>> obtenerComandasPorFecha(String fecha) {
        return comandaRepository.obtenerComandasPorFecha(fecha);
    }

    @Override
    public Task<List<Comanda>> obtenerTodasLasComandas() {
        return comandaRepository.obtenerTodasLasComandas();
    }

    @Override
    public Task<List<Comanda>> obtenerComandasPorEmpleado(String uidEmpleado) {
        return comandaRepository.obtenerComandasPorEmpleado(uidEmpleado);
    }

    @Override
    public Task<List<Comanda>> obtenerComandasPorCocinero(String uidCocinero) {
        return comandaRepository.obtenerComandasPorCocinero(uidCocinero);
    }

    @Override
    public Task<Map<String, Integer>> obtenerPlatillosMasVendidos(String fecha) {
        return comandaRepository.obtenerPlatillosMasVendidos(fecha);
    }

    private String generarToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder token = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    private String obtenerFechaYHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
    @Override
    public Task<Void> guardarPlatilloEnComanda(String comandaId, ComandaPlatillo platillo) {
        return comandaRepository.guardarPlatillo(comandaId, platillo);
    }

    @Override
    public Task<Void> eliminarPlatilloDeComanda(String comandaId, String platilloId) {
        return comandaRepository.eliminarPlatillo(comandaId, platilloId);
    }
}

