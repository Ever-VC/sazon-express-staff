package com.evervc.saznexpressstaff.data.models;

public class ComandaPlatillo {
    private String platilloId;
    private int cantidad;
    private String nota;

    public ComandaPlatillo() {
    }

    public ComandaPlatillo(String platilloId, int cantidad, String nota) {
        this.platilloId = platilloId;
        this.cantidad = Math.max(cantidad, 1);
        this.nota = nota;
    }

    public String getPlatilloId() {
        return platilloId;
    }

    public void setPlatilloId(String platilloId) {
        this.platilloId = platilloId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(cantidad, 1);
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}
