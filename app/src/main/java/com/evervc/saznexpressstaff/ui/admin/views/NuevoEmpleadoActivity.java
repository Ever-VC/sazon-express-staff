package com.evervc.saznexpressstaff.ui.admin.views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Usuario;
import com.evervc.saznexpressstaff.data.services.UsuarioService;
import com.evervc.saznexpressstaff.data.services.UsuarioServiceImpl;
import com.evervc.saznexpressstaff.ui.utils.SubidorImagenes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NuevoEmpleadoActivity extends AppCompatActivity {
    private String uidUsuarioAEditar = "__NULL__";
    private EditText etNombre, etCorreo, etTelefono, etFechaNacimiento;
    private Spinner spRoles;
    private DatabaseReference dbRef;
    private Usuario usuarioEdit = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_empleado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNombre = findViewById(R.id.etNombreNuevoEmpleado);
        etCorreo = findViewById(R.id.etCorreoNuevoEmpleado);
        etTelefono = findViewById(R.id.etTelefonoNuevoEmpleado);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        spRoles = findViewById(R.id.spRoles);
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios_pendientes");

        uidUsuarioAEditar = getIntent().getStringExtra("uidUsuarioAEditar");

        // Spinner de roles
        ArrayList<String> roles = new ArrayList<>();
        roles.add("administrador");
        roles.add("mesero");
        roles.add("cocinero");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRoles.setAdapter(adapter);

        // Si se ha recibido un uid para editar un usuario, carga la información
        if (!uidUsuarioAEditar.equals("uidUsuarioAEditar")) {
            UsuarioService usuarioService = new UsuarioServiceImpl();
            usuarioService.obtenerUsuarioPorId(uidUsuarioAEditar).addOnSuccessListener(usuario -> {
                if (!(usuario == null)) {
                    usuarioEdit = usuario;
                    runOnUiThread(() -> {
                        etNombre.setText(usuario.getNombre());
                        etFechaNacimiento.setText(usuario.getFechaNacimiento());
                        etTelefono.setText(usuario.getTelefono());
                        etCorreo.setText(usuario.getCorreo());
                    });
                }
            }).addOnFailureListener(e -> {
                System.out.println("Error al obtener usuarios: " + e.getMessage());
                Toast.makeText(this, "No se ha podido cargar la información.", Toast.LENGTH_SHORT).show();
                finish();
            });

        }

        // Selector de fecha
        etFechaNacimiento.setOnClickListener(v -> mostrarSelectorFecha());
    }

    private void mostrarSelectorFecha() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etFechaNacimiento.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addRegister(View view) {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        String rol = spRoles.getSelectedItem().toString();

        if (nombre.isEmpty() || telefono.isEmpty() || fechaNacimiento.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuarioEdit != null) {
            usuarioEdit.setNombre(nombre);
            usuarioEdit.setTelefono(telefono);
            usuarioEdit.setFechaNacimiento(fechaNacimiento);
            usuarioEdit.setRol(rol);

            UsuarioService usuarioService = new UsuarioServiceImpl();
            usuarioService.actualizarUsuario(usuarioEdit);

            Toast.makeText(NuevoEmpleadoActivity.this, "Empleado actualizado.", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();

        } else {
            String correo = etCorreo.getText().toString().trim();

            if (correo.isEmpty()) return;

            String token = generarTokenUnico();
            String fechaRegistro = LocalDateTime.now().toString();

            Uri imagenPorDefecto = obtenerUriDeDrawable(R.drawable.user);
            if (imagenPorDefecto == null) {
                Toast.makeText(this, "No se pudo cargar la imagen por defecto", Toast.LENGTH_SHORT).show();
                return;
            }

            String rutaStorage = "usuarios_pendientes/" + token + "/perfil.jpg";

            SubidorImagenes.subirImagen(rutaStorage, imagenPorDefecto, new SubidorImagenes.EscuchadorSubida() {
                @Override
                public void alSubir(String urlDescarga) {
                    Map<String, Object> nuevoEmpleado = new HashMap<>();
                    nuevoEmpleado.put("nombre", nombre);
                    nuevoEmpleado.put("correo", correo);
                    nuevoEmpleado.put("telefono", telefono);
                    nuevoEmpleado.put("fechaNacimiento", fechaNacimiento);
                    nuevoEmpleado.put("rol", rol);
                    nuevoEmpleado.put("fechaRegistro", fechaRegistro);
                    nuevoEmpleado.put("estado", "pendiente");
                    nuevoEmpleado.put("tokenRegistro", token);
                    nuevoEmpleado.put("imagenUrl", urlDescarga);

                    dbRef.child(token).setValue(nuevoEmpleado)
                            .addOnSuccessListener(unused -> {
                                enviarCorreoConToken(correo, token);
                                Toast.makeText(NuevoEmpleadoActivity.this, "Empleado registrado y correo enviado.", Toast.LENGTH_LONG).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(NuevoEmpleadoActivity.this, "Error al registrar empleado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }

                @Override
                public void alFallar(Exception e) {
                    Toast.makeText(NuevoEmpleadoActivity.this, "Error al subir imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private String generarTokenUnico() {
        SecureRandom random = new SecureRandom();
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(letras.charAt(random.nextInt(letras.length())));
        }
        return sb.toString();
    }

    public void enviarCorreoConToken(String correoDestino, String token) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // Formato de correo

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{correoDestino});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Token de acceso a Sazón Express Staff");
        intent.putExtra(Intent.EXTRA_TEXT, "Hola, se te ha registrado como empleado en Sazón Express.\n\nTu token de acceso es:\n\n" + token + "\n\nUtiliza este código para registrar tu cuenta con Google desde la aplicación.");

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo con..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No hay apps de correo instaladas.", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri obtenerUriDeDrawable(@DrawableRes int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);

        File tempFile = new File(getCacheDir(), "temp_user.png");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return FileProvider.getUriForFile(this, getPackageName() + ".provider", tempFile);
    }


}