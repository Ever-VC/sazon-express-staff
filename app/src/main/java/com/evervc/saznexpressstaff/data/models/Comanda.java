package com.evervc.saznexpressstaff.data.models;

import java.util.Map;

public class Comanda {
    private String id;
    private String token;
    private Map<String, ComandaPlatillo> platillos;
    private String nombreCliente;
    private String uidEmpleado;
    private String estado;
    private String fechaYHoraPedido;
    private String fechaYHoraEnCocina;
    private String fechaYHoraListo;
    private String uidCocinero;

    public Comanda() {
    }

    public Comanda(String id, String token, Map<String, ComandaPlatillo> platillos, String nombreCliente, String uidEmpleado, String estado, String fechaYHoraPedido, String fechaYHoraEnCocina, String fechaYHoraListo, String uidCocinero) {
        this.id = id;
        this.token = token;
        this.platillos = platillos;
        this.nombreCliente = nombreCliente;
        this.uidEmpleado = uidEmpleado;
        this.estado = estado;
        this.fechaYHoraPedido = fechaYHoraPedido;
        this.fechaYHoraEnCocina = fechaYHoraEnCocina;
        this.fechaYHoraListo = fechaYHoraListo;
        this.uidCocinero = uidCocinero;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, ComandaPlatillo> getPlatillos() {
        return platillos;
    }

    public void setPlatillos(Map<String, ComandaPlatillo> platillos) {
        this.platillos = platillos;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getUidEmpleado() {
        return uidEmpleado;
    }

    public void setUidEmpleado(String uidEmpleado) {
        this.uidEmpleado = uidEmpleado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaYHoraPedido() {
        return fechaYHoraPedido;
    }

    public void setFechaYHoraPedido(String fechaYHoraPedido) {
        this.fechaYHoraPedido = fechaYHoraPedido;
    }

    public String getFechaYHoraEnCocina() {
        return fechaYHoraEnCocina;
    }

    public void setFechaYHoraEnCocina(String fechaYHoraEnCocina) {
        this.fechaYHoraEnCocina = fechaYHoraEnCocina;
    }

    public String getFechaYHoraListo() {
        return fechaYHoraListo;
    }

    public void setFechaYHoraListo(String fechaYHoraListo) {
        this.fechaYHoraListo = fechaYHoraListo;
    }

    public String getUidCocinero() {
        return uidCocinero;
    }

    public void setUidCocinero(String uidCocinero) {
        this.uidCocinero = uidCocinero;
    }
}
