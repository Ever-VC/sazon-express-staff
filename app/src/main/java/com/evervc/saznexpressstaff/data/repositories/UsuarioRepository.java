package com.evervc.saznexpressstaff.data.repositories;

import com.evervc.saznexpressstaff.data.models.Usuario;
import com.google.android.gms.tasks.Task;

public interface UsuarioRepository {
    Task<Void> crearUsuario(Usuario usuario);
    Task<Usuario> obtenerUsuarioActual();
    Task<Void> actualizarToken(String nuevoToken);
    Task<Void> actualizarEstado(String estado);
}
