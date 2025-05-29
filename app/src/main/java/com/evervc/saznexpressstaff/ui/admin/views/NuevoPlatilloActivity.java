package com.evervc.saznexpressstaff.ui.admin.views;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Platillo;
import com.evervc.saznexpressstaff.data.services.PlatilloService;
import com.evervc.saznexpressstaff.data.services.PlatilloServiceImpl;
import com.evervc.saznexpressstaff.ui.utils.SelectorSubidorImagen;
import com.evervc.saznexpressstaff.ui.utils.SubidorImagenes;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.UUID;

public class NuevoPlatilloActivity extends AppCompatActivity {
    private static final int CODIGO_SELECCION_IMAGEN = 100;

    private ImageView imgPlatillo;
    private TextInputEditText etNombre, etDescripcion, etPrecio;
    private Spinner spCategoria;
    private Button btnGuardar;
    private Uri imagenUri;
    private PlatilloService platilloService;
    private Platillo platilloEdit;
    private SelectorSubidorImagen selectorImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_platillo);

        platilloService = new PlatilloServiceImpl();

        imgPlatillo = findViewById(R.id.imgPlatillo);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        spCategoria = findViewById(R.id.spCategoria);
        btnGuardar = findViewById(R.id.btnGuardar);

        configurarSpinner();
        configurarSelectorImagen();

        platilloEdit = (Platillo) getIntent().getSerializableExtra("platillo");
        if (platilloEdit != null) {
            cargarDatosPlatillo();
        }

        btnGuardar.setOnClickListener(v -> guardarPlatillo());

        ViewCompat.setOnApplyWindowInsetsListener(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configurarSpinner() {
        ArrayList<String> categorias = new ArrayList<>();
        categorias.add("Entrada");
        categorias.add("Plato Principal");
        categorias.add("Postre");
        categorias.add("Bebida");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);
    }

    private void configurarSelectorImagen() {
        selectorImagen = new SelectorSubidorImagen(
                this,
                CODIGO_SELECCION_IMAGEN,
                "temp/platillos/" + UUID.randomUUID().toString(),
                new SelectorSubidorImagen.EscuchadorSubida() { // Usa el Escuchador correcto
                    @Override
                    public void alSubir(String url) {
                        // Esta función se llama cuando la imagen se sube exitosamente
                        // Pero no la necesitamos aquí porque manejamos la URI directamente
                    }

                    @Override
                    public void alFallar(Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(NuevoPlatilloActivity.this,
                                        "Error al subir imagen: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                    }
                });

        imgPlatillo.setOnClickListener(v -> selectorImagen.iniciarSeleccion());
    }

    private void cargarDatosPlatillo() {
        etNombre.setText(platilloEdit.getNombre());
        etDescripcion.setText(platilloEdit.getDescripcion());
        etPrecio.setText(String.valueOf(platilloEdit.getPrecio()));

        for (int i = 0; i < spCategoria.getCount(); i++) {
            if (spCategoria.getItemAtPosition(i).toString().equals(platilloEdit.getCategoria())) {
                spCategoria.setSelection(i);
                break;
            }
        }

        if (platilloEdit.getImagenUrl() != null && !platilloEdit.getImagenUrl().isEmpty()) {
            Glide.with(this)
                    .load(platilloEdit.getImagenUrl())
                    .into(imgPlatillo);
        }
    }

    private void guardarPlatillo() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String categoria = spCategoria.getSelectedItem().toString();

        if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (platilloEdit == null) {
            Platillo nuevoPlatillo = new Platillo();
            nuevoPlatillo.setId(UUID.randomUUID().toString());
            nuevoPlatillo.setNombre(nombre);
            nuevoPlatillo.setDescripcion(descripcion);
            nuevoPlatillo.setPrecio(precio);
            nuevoPlatillo.setCategoria(categoria);

            if (selectorImagen.getUriSeleccionada() != null) {
                platilloService.crearPlatillo(nuevoPlatillo, selectorImagen.getUriSeleccionada())
                        .addOnSuccessListener(aVoid -> {
                            setResult(Activity.RESULT_OK);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al crear platillo: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Seleccione una imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            platilloEdit.setNombre(nombre);
            platilloEdit.setDescripcion(descripcion);
            platilloEdit.setPrecio(precio);
            platilloEdit.setCategoria(categoria);

            platilloService.actualizarPlatillo(platilloEdit, selectorImagen.getUriSeleccionada())
                    .addOnSuccessListener(aVoid -> {
                        setResult(Activity.RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar platillo: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_SELECCION_IMAGEN && resultCode == RESULT_OK && data != null) {
            selectorImagen.procesarResultado(requestCode, resultCode, data);
            imgPlatillo.setImageURI(selectorImagen.getUriSeleccionada());
        }
    }
}
