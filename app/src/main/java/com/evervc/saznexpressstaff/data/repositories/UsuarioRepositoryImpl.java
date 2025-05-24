package com.evervc.saznexpressstaff.data.repositories;

import com.evervc.saznexpressstaff.data.models.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsuarioRepositoryImpl implements UsuarioRepository {
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference("usuarios");
    private final String uid = FirebaseAuth.getInstance().getUid();

    @Override
    public Task<Void> crearUsuario(Usuario usuario) {
        return db.child(uid).setValue(usuario);
    }

    @Override
    public Task<Usuario> obtenerUsuarioActual() {
        return db.child(uid).get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                return task.getResult().getValue(Usuario.class);
            } else {
                return null;
            }
        });
    }

    @Override
    public Task<Void> actualizarToken(String nuevoToken) {
        return db.child(uid).child("tokenNotificaciones").setValue(nuevoToken);
    }

    @Override
    public Task<Void> actualizarEstado(String estado) {
        return db.child(uid).child("estado").setValue(estado);
    }
}
