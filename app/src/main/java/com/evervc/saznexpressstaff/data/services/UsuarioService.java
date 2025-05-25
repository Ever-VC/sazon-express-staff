package com.evervc.saznexpressstaff.data.services;

import android.net.Uri;

import com.evervc.saznexpressstaff.data.models.Usuario;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface UsuarioService {
    Task<Void> crearUsuario(Usuario usuario, String password, Uri uriImagen);
    Task<List<Usuario>> obtenerUsuariosPorRol(String rol);
    Task<Usuario> obtenerUsuarioPorId(String uid);
    Task<Void> actualizarUsuario(Usuario usuario);
    Task<Void> eliminarUsuario(String uid);
    Task<Void> actualizarEstado(String uid, String nuevoEstado);
    Task<Usuario> obtenerUsuarioActual();
    Task<Void> loginConCorreo(String correo, String contrasenna);
    Task<Void> loginConGoogle(String idToken);
    Task<Void> validarCuentaGoogleConToken(String idToken, String token);


}
