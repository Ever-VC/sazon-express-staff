package com.evervc.saznexpressstaff.data.repositories;

import com.evervc.saznexpressstaff.data.models.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Task<List<Usuario>> obtenerUsuariosPorRol(String rol) {
        return db.orderByChild("rol").equalTo(rol).get()
                .continueWith(task -> {
                    List<Usuario> lista = new ArrayList<>();
                    System.out.println("Resultado crudo: " + task.getResult().toString());
                    if (task.isSuccessful() && task.getResult().exists()) {
                        for (DataSnapshot snap : task.getResult().getChildren()) {
                            Usuario usuario = snap.getValue(Usuario.class);
                            if (usuario != null) {
                                lista.add(usuario);
                            }
                        }
                    }
                    return lista;
                });
    }

    @Override
    public Task<Usuario> obtenerUsuarioPorId(String uid) {
        return db.child(uid).get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return task.getResult().getValue(Usuario.class);
                });
    }

    @Override
    public Task<Void> actualizarUsuario(Usuario usuario) {
        return db.child(usuario.getId()).setValue(usuario);
    }

    @Override
    public Task<Void> eliminarUsuario(String uid) {
        return db.child(uid).removeValue();
    }

    @Override
    public Task<Void> actualizarCampo(String uid, String campo, String nuevoValor) {
        return db.child(uid).child(campo).setValue(nuevoValor);
    }

}
