package com.evervc.saznexpressstaff.ui.utils;

import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.evervc.saznexpressstaff.data.models.Platillo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CarritoCompras {
    private static List<Platillo> lstPlatillos = null;
    private static List<ComandaPlatillo> lstComandaPlatillo = null;
    public static void agregatPlatillo(Platillo platillo) {
        lstPlatillos.add(platillo);
        lstComandaPlatillo.add(new ComandaPlatillo(platillo.getId(), 0, ""));
    }
    public static void eliminarPlatillo(Platillo platillo) {
        lstPlatillos.remove(platillo);
        lstComandaPlatillo.removeIf(comandaPlatillo -> Objects.equals(comandaPlatillo.getPlatilloId(), platillo.getId()));
    }
    public static List<Platillo> obtenerListaPlatillos() {
        return lstPlatillos;
    }

    public static List<ComandaPlatillo> obtenerListaComandaPlatillos() {
        return lstComandaPlatillo;
    }
    public static void inicializarLista() {
        lstPlatillos = new ArrayList<>();
        lstComandaPlatillo = new ArrayList<>();
    }
    public static boolean hayPlatillos() {
        return lstPlatillos != null;
    }
    public static void vaciar() {
        lstPlatillos.clear();
        lstComandaPlatillo.clear();
        lstPlatillos = null;
        lstComandaPlatillo = null;
    }
}
