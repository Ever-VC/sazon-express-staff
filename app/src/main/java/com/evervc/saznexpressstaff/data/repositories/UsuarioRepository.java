package com.evervc.saznexpressstaff.data.repositories;

import com.evervc.saznexpressstaff.data.models.Usuario;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface UsuarioRepository {
    Task<Void> crearUsuario(Usuario usuario);
    Task<Usuario> obtenerUsuarioActual();
    Task<Void> actualizarToken(String nuevoToken);
    Task<Void> actualizarEstado(String estado);
    Task<List<Usuario>> obtenerUsuariosPorRol(String rol);
    Task<List<Usuario>> obtenerTodosLosUsuarios();
    Task<Usuario> obtenerUsuarioPorId(String uid);
    Task<Void> actualizarUsuario(Usuario usuario);
    Task<Void> eliminarUsuario(String uid);
    Task<Void> actualizarCampo(String uid, String campo, String nuevoValor);
}
