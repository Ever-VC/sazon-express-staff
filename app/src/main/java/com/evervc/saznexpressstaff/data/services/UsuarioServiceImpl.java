package com.evervc.saznexpressstaff.data.services;

import android.net.Uri;
import android.os.Build;

import com.evervc.saznexpressstaff.data.models.Usuario;
import com.evervc.saznexpressstaff.data.repositories.UsuarioRepository;
import com.evervc.saznexpressstaff.data.repositories.UsuarioRepositoryImpl;
import com.evervc.saznexpressstaff.ui.utils.SubidorImagenes;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository repo = new UsuarioRepositoryImpl();

    @Override
    public Task<Void> loginConCorreo(String correo, String contrasenna) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        UsuarioRepository repo = new UsuarioRepositoryImpl();

        return auth.signInWithEmailAndPassword(correo, contrasenna)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) return Tasks.forException(task.getException());

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null || !user.isEmailVerified())
                        return Tasks.forException(new Exception("Debe verificar su correo."));

                    return repo.obtenerUsuarioActual()
                            .continueWith(usuarioTask -> {
                                Usuario usuario = usuarioTask.getResult();
                                if (usuario == null)
                                    throw new Exception("No se encontró información del usuario.");
                                UsuarioSesion.establecerUsuario(usuario);
                                return null;
                            });
                });
    }

    @Override
    public Task<Void> loginConGoogle(String idToken) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        UsuarioRepository repo = new UsuarioRepositoryImpl();

        return auth.signInWithCredential(credential)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        return Tasks.forException(task.getException());
                    }

                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser == null) {
                        return Tasks.forException(new Exception("No se obtuvo el usuario de Firebase"));
                    }

                    // Intentar obtener el usuario desde la base de datos
                    return repo.obtenerUsuarioPorId(firebaseUser.getUid())
                            .continueWith(usuarioTask -> {
                                Usuario existente = usuarioTask.getResult();
                                if (existente == null) {
                                    throw new Exception("Este usuario no está registrado en la base de datos. Contacte a un administrador.");
                                }

                                UsuarioSesion.establecerUsuario(existente);
                                return null;
                            });
                });
    }

    @Override
    public Task<Void> validarCuentaGoogleConToken(String idToken, String token) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        UsuarioRepository repo = new UsuarioRepositoryImpl();

        return auth.signInWithCredential(credential).continueWithTask(task -> {
            if (!task.isSuccessful()) return Tasks.forException(task.getException());

            FirebaseUser user = auth.getCurrentUser();
            if (user == null) return Tasks.forException(new Exception("No se obtuvo el usuario"));

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("usuarios_pendientes").child(token);
            return ref.get().continueWithTask(snapshotTask -> {
                if (!snapshotTask.getResult().exists())
                    return Tasks.forException(new Exception("Token no válido o expirado"));

                String correoEsperado = snapshotTask.getResult().child("correo").getValue(String.class);
                if (!correoEsperado.equals(user.getEmail()))
                    return Tasks.forException(new Exception("Correo no coincide con el token"));

                // Crear usuario definitivo
                Usuario nuevo = snapshotTask.getResult().getValue(Usuario.class);
                nuevo.setId(user.getUid());
                nuevo.setImagenUrl(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                nuevo.setEstado("activo");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nuevo.setFechaRegistro(LocalDateTime.now().toString());
                }

                return repo.crearUsuario(nuevo).continueWithTask(t -> {
                    if (!t.isSuccessful()) return Tasks.forException(t.getException());
                    UsuarioSesion.establecerUsuario(nuevo);
                    return ref.removeValue(); // Elimina el registro pendiente
                });
            });
        });
    }

    // Crear usuario (con auth, storage y base de datos)
    @Override
    public Task<Void> crearUsuario(Usuario usuario, String password, Uri uriImagen) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        return auth.createUserWithEmailAndPassword(usuario.getCorreo(), password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) return Tasks.forException(task.getException());

                    FirebaseUser nuevoUsuario = task.getResult().getUser();
                    if (nuevoUsuario == null) return Tasks.forException(new Exception("Error: Usuario no creado"));

                    // Enviar correo de verificación
                    return nuevoUsuario.sendEmailVerification().continueWithTask(verifTask -> {
                        if (!verifTask.isSuccessful())
                            return Tasks.forException(verifTask.getException());

                        // Si se envió el correo, continuamos con el flujo
                        String uid = nuevoUsuario.getUid();
                        usuario.setId(uid);
                        usuario.setEstado("activo");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            usuario.setFechaRegistro(LocalDateTime.now().toString());
                        }

                        String ruta = "usuarios/" + uid + "/perfil.jpg";

                        // Subir imagen a Storage
                        return SubidorImagenes.subirImagenTask(ruta, uriImagen)
                                .continueWithTask(imgTask -> {
                                    if (!imgTask.isSuccessful())
                                        return Tasks.forException(imgTask.getException());

                                    usuario.setImagenUrl(imgTask.getResult());
                                    return repo.crearUsuario(usuario);
                                });
                    });
                });
    }

    // Obtener todos los usuarios por rol
    public Task<List<Usuario>> obtenerUsuariosPorRol(String rol) {
        return repo.obtenerUsuariosPorRol(rol);
    }

    // Obtener un usuario por su ID
    public Task<Usuario> obtenerUsuarioPorId(String uid) {
        return repo.obtenerUsuarioPorId(uid);
    }

    // Actualizar un usuario (sin cambiar la imagen)
    public Task<Void> actualizarUsuario(Usuario usuario) {
        return repo.actualizarUsuario(usuario);
    }

    // Eliminar un usuario (auth y base)
    public Task<Void> eliminarUsuario(String uid) {
        return repo.eliminarUsuario(uid);
    }

    // Cambiar estado (activo/inactivo)
    public Task<Void> actualizarEstado(String uid, String nuevoEstado) {
        return repo.actualizarCampo(uid, "estado", nuevoEstado);
    }

    // Obtener usuario actual logueado
    public Task<Usuario> obtenerUsuarioActual() {
        return repo.obtenerUsuarioActual();
    }

    private String obtenerPrefijoRol(String rol) {
        switch (rol.toLowerCase()) {
            case "administrador":
                return "ADM";
            case "cocinero":
                return "COC";
            case "mesero":
                return "MES";
            default:
                return "EMP"; // Por si en el futuro se añade otro tipo de rol
        }
    }

}
