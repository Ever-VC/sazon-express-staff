package com.evervc.saznexpressstaff.data.models;

public class Usuario {
    private String id; // UID de Firebase Auth
    private String nombre;
    private String fechaNacimiento; // formato "yyyy-MM-dd"
    private String rol; // "administrador", "mesero", "cocinero"
    private String correo;
    private String imagenUrl; // URL en Firebase Storage
    private String telefono;
    private String estado; // "activo" / "inactivo"
    private String fechaRegistro; // ISO 8601: "2024-05-23T16:45:00"
    private String tokenNotificaciones; // token FCM del dispositivo

    public Usuario() {
    }

    public Usuario(String id, String nombre, String fechaNacimiento, String rol, String correo, String imagenUrl, String telefono, String estado, String fechaRegistro, String tokenNotificaciones) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.correo = correo;
        this.imagenUrl = imagenUrl;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.tokenNotificaciones = tokenNotificaciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getTokenNotificaciones() {
        return tokenNotificaciones;
    }

    public void setTokenNotificaciones(String tokenNotificaciones) {
        this.tokenNotificaciones = tokenNotificaciones;
    }
}
