package com.evervc.saznexpressstaff.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.saznexpressstaff.MainActivity;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Usuario;
import com.evervc.saznexpressstaff.data.repositories.UsuarioRepository;
import com.evervc.saznexpressstaff.data.repositories.UsuarioRepositoryImpl;
import com.evervc.saznexpressstaff.ui.admin.AdminHomeActivity;
import com.evervc.saznexpressstaff.ui.chef.ChefHomeActivity;
import com.evervc.saznexpressstaff.ui.utils.SelectorSubidorImagen;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;
import com.evervc.saznexpressstaff.ui.waiter.WaiterHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("usuarios");
    private SelectorSubidorImagen selectorImagen;
    private static final int CODIGO_SELECCION_IMAGEN = 1001;
    private EditText etEmailLogin, etPasswordLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        asociarElementosXml();

        crearAdministradorPorDefecto();
    }

    private void asociarElementosXml() {
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
    }

    private void crearAdministradorPorDefecto() {
        String email = "vc21033@ues.edu.sv", password = "12345678";
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful()) {
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(resultado -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    firebaseUser.sendEmailVerification()
                            .addOnCompleteListener(t -> {
                                if (t.isSuccessful()) {
                                    String uid = firebaseUser.getUid();
                                    String ruta = "usuarios/" + uid + "/perfil.jpg";

                                    selectorImagen = new SelectorSubidorImagen(
                                            this,
                                            CODIGO_SELECCION_IMAGEN,
                                            ruta,
                                            new SelectorSubidorImagen.EscuchadorSubida() {
                                                @Override
                                                public void alSubir(String url) {
                                                    Usuario admin = new Usuario(
                                                            uid,
                                                            "admin",
                                                            "1990-01-01",
                                                            "administrador",
                                                            "admin@sazon.com",
                                                            "ADM-0001",
                                                            url,
                                                            "0000-0000",
                                                            "activo",
                                                            "2025-05-23T00:00:00",
                                                            null
                                                    );

                                                    UsuarioRepository repositorio = new UsuarioRepositoryImpl();
                                                    repositorio.crearUsuario(admin).addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(getApplicationContext(), "Administrador creado correctamente, se envió correo de verificación.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                        finish();
                                                    });
                                                }

                                                @Override
                                                public void alFallar(Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    );

                                    // Iniciar selección de imagen
                                    selectorImagen.iniciarSeleccion();
                                } else {
                                    Toast.makeText(this, "No se pudo enviar verificación. Verifique el correo.", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (selectorImagen != null) {
            selectorImagen.procesarResultado(requestCode, resultCode, data);
        }
    }

    public void login(View view) {
        String correo = etEmailLogin.getText().toString();
        String contrasenna = etPasswordLogin.getText().toString();

        if (correo.isEmpty() || contrasenna.isEmpty()) {
            Toast.makeText(this, "Debe rellenar todos los campos del formulario", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(correo, contrasenna).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser usuarioFirebase = auth.getCurrentUser();
                if (usuarioFirebase != null && usuarioFirebase.isEmailVerified()) {
                    Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();

                    UsuarioRepository repo = new UsuarioRepositoryImpl();
                    repo.obtenerUsuarioActual().addOnSuccessListener(usuario -> {
                        if (usuario != null) {
                            UsuarioSesion.establecerUsuario(usuario);
                            redirigirPorRol(usuario.getRol());
                        } else {
                            Toast.makeText(this, "No se encontró información del usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Debe verificar su correo antes de iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginConGoogle(View view) {

    }

    private void redirigirPorRol(String rol) {
        Intent intent;
        switch (rol) {
            case "administrador":
                intent = new Intent(this, AdminHomeActivity.class);
                break;
            case "cocinero":
                intent = new Intent(this, ChefHomeActivity.class);
                break;
            case "mesero":
                intent = new Intent(this, WaiterHomeActivity.class);
                break;
            default:
                Toast.makeText(this, "Rol no reconocido: " + rol, Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }

}