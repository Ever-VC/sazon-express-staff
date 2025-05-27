package com.evervc.saznexpressstaff.ui.auth;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.evervc.saznexpressstaff.MainActivity;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Usuario;
import com.evervc.saznexpressstaff.data.repositories.UsuarioRepository;
import com.evervc.saznexpressstaff.data.repositories.UsuarioRepositoryImpl;
import com.evervc.saznexpressstaff.data.services.UsuarioService;
import com.evervc.saznexpressstaff.data.services.UsuarioServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.AdminHomeActivity;
import com.evervc.saznexpressstaff.ui.chef.ChefHomeActivity;
import com.evervc.saznexpressstaff.ui.utils.SelectorSubidorImagen;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;
import com.evervc.saznexpressstaff.ui.waiter.WaiterHomeActivity;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("usuarios");
    private SelectorSubidorImagen selectorImagen;
    private static final int CODIGO_SELECCION_IMAGEN = 1001;
    private EditText etEmailLogin, etPasswordLogin;
    private CredentialManager credentialManager;

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

        credentialManager = CredentialManager.create(getBaseContext());

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

        UsuarioService service = new UsuarioServiceImpl();
        service.loginConCorreo(correo, contrasenna).addOnSuccessListener(aVoid -> {
            redirigirPorRol(UsuarioSesion.obtenerUsuario().getRol());
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }


    public void loginConGoogle(View view) {
        iniciarGoogleAuth();
    }

    private void iniciarGoogleAuth() {
        GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("433659660280-55sj1kji3llfn91efckmiv7qpbqhqt0o.apps.googleusercontent.com")
                .build();

        GetCredentialRequest request = new GetCredentialRequest
                .Builder()
                .addCredentialOption(getGoogleIdOption)
                .build();
        //getBaseContext() en el contexto de lo siguiente
        credentialManager.getCredentialAsync(
                this,
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignIn(getCredentialResponse.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e("Error: ", e.getMessage());
                    }
                }
        );
    }

    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential && credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            CustomCredential customCredential = (CustomCredential) credential;
            Bundle credentialData = customCredential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

            UsuarioService service = new UsuarioServiceImpl();
            service.loginConGoogle(googleIdTokenCredential.getIdToken())
                    .addOnSuccessListener(v -> {
                        redirigirPorRol(UsuarioSesion.obtenerUsuario().getRol());
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("HA LLEGADO AL ERROR, AHORA VA A VALIDAR QUE ESTE EN LOS PENDIENTES");
                        if (e.getMessage().toLowerCase().contains("no está registrado")) {
                            pedirTokenDeRegistro(googleIdTokenCredential.getIdToken());
                        } else {
                            Toast.makeText(this, "Error al iniciar sesión con Google: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
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

    private void pedirTokenDeRegistro(String idToken) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese su token de acceso");

        final EditText input = new EditText(this);
        input.setHint("Ej: ABC12345");
        builder.setView(input);

        builder.setPositiveButton("Validar", (dialog, which) -> {
            String tokenIngresado = input.getText().toString().trim();
            if (tokenIngresado.isEmpty()) {
                Toast.makeText(this, "Debe ingresar el token", Toast.LENGTH_SHORT).show();
                return;
            }

            UsuarioService service = new UsuarioServiceImpl();
            service.validarCuentaGoogleConToken(idToken, tokenIngresado)
                    .addOnSuccessListener(v -> {
                        redirigirPorRol(UsuarioSesion.obtenerUsuario().getRol());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }


}