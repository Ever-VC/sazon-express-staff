package com.evervc.saznexpressstaff.ui.admin.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Category;
import com.evervc.saznexpressstaff.data.models.Platillo;
import com.evervc.saznexpressstaff.data.services.PlatilloService;
import com.evervc.saznexpressstaff.data.services.PlatilloServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.adapters.CategoryAdapter;
import com.evervc.saznexpressstaff.ui.admin.adapters.PlatillosDisponiblesAdapter;
import com.evervc.saznexpressstaff.ui.utils.CarritoCompras;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComandaPlatilloActivity extends AppCompatActivity {
    private PlatilloService platilloService;
    private List<Platillo> listaPlatillos = new ArrayList<>();
    private RecyclerView rcvPlatillosFiltrados, rcvCategorias;
    private List<Platillo> lstFiltrada = new ArrayList<>();
    private TextView tvInfoComandaPlatillo, tvCardCount;
    private Button btnRegistrarComanda;
    private List<Category> lstCategorias;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comanda_platillo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        asociarElementosXml();
        lstCategorias = new ArrayList<>();
        platilloService = new PlatilloServiceImpl();

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
        );

        actualizarListaPlatillos();
        mostrarPlatillos();
        mostrarCategorias();
    }

    private void asociarElementosXml() {
        rcvPlatillosFiltrados = findViewById(R.id.rcvPlatillosFiltrados);
        tvInfoComandaPlatillo = findViewById(R.id.tvInfoComandaPlatillo);
        tvCardCount = findViewById(R.id.tvCardCount);
        rcvCategorias = findViewById(R.id.rcvCategorias);
    }

    private void actualizarListaPlatillos() {
        platilloService.obtenerTodosLosPlatillos().addOnSuccessListener(platillos -> {
            listaPlatillos.clear();
            if (platillos != null && !platillos.isEmpty()) {
                listaPlatillos.addAll(platillos.stream().filter(platillo -> !platillo.isDisponible()).collect(Collectors.toList()));
                if (!listaPlatillos.isEmpty()) {
                    tvInfoComandaPlatillo.setVisibility(View.GONE);
                } else {
                    tvInfoComandaPlatillo.setVisibility(View.VISIBLE);
                }
            } else {
                tvInfoComandaPlatillo.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {
            tvInfoComandaPlatillo.setVisibility(View.VISIBLE);
            Log.e("Error al cargar los platillos: ", e.getMessage());
        });
    }

    public void actualizarCantidadPlatillos() {
        if (CarritoCompras.hayPlatillos()) {
            tvCardCount.setText(String.valueOf(CarritoCompras.obtenerListaPlatillos().size()));
        }
    }

    private void mostrarCategorias() {
        lstCategorias.add(new Category("Todo", R.drawable.star));
        lstCategorias.add(new Category("Entrada", R.drawable.fried_chicken));
        lstCategorias.add(new Category("Bebida", R.drawable.drink));
        lstCategorias.add(new Category("Postre", R.drawable.cake));
        lstCategorias.add(new Category("Plato Principal", R.drawable.food));

        CategoryAdapter adapter = new CategoryAdapter(lstCategorias, this, this::selectCategory);
        rcvCategorias.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        rcvCategorias.setAdapter(adapter);
    }

    public void selectCategory(Category category) {
        platilloService.obtenerTodosLosPlatillos().addOnSuccessListener(platillos -> {
            listaPlatillos.clear();
            if (platillos != null && !platillos.isEmpty()) {
                if (category.getName().equals("Todo")) {
                    listaPlatillos.addAll(platillos.stream().filter(platillo -> !platillo.isDisponible()).collect(Collectors.toList()));
                    if (!listaPlatillos.isEmpty()) {
                        PlatillosDisponiblesAdapter adapter = new PlatillosDisponiblesAdapter(this, listaPlatillos, this::actualizarCantidadPlatillos);
                        rcvPlatillosFiltrados.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
                        rcvPlatillosFiltrados.setAdapter(adapter);
                        tvInfoComandaPlatillo.setVisibility(View.GONE);
                    } else {
                        tvInfoComandaPlatillo.setVisibility(View.VISIBLE);
                    }
                } else {
                    listaPlatillos.addAll(platillos.stream().filter(platillo -> !platillo.isDisponible() && platillo.getCategoria().equals(category.getName())).collect(Collectors.toList()));
                    if (!listaPlatillos.isEmpty()) {
                        PlatillosDisponiblesAdapter adapter = new PlatillosDisponiblesAdapter(this, listaPlatillos, this::actualizarCantidadPlatillos);
                        rcvPlatillosFiltrados.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
                        rcvPlatillosFiltrados.setAdapter(adapter);
                        tvInfoComandaPlatillo.setVisibility(View.GONE);
                    } else {
                        tvInfoComandaPlatillo.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                tvInfoComandaPlatillo.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {
            tvInfoComandaPlatillo.setVisibility(View.VISIBLE);
            Log.e("Error al cargar los platillos: ", e.getMessage());
        });
    }

    private void mostrarPlatillos() {
        PlatillosDisponiblesAdapter adapter = new PlatillosDisponiblesAdapter(this, listaPlatillos, this::actualizarCantidadPlatillos);
        rcvPlatillosFiltrados.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        rcvPlatillosFiltrados.setAdapter(adapter);
    }

    public void crearComanda(View view) {
        if (CarritoCompras.hayPlatillos()) {
            Intent crearComanda = new Intent(this, CrearComandaActivity.class);
            launcher.launch(crearComanda);
        } else {
            Toast.makeText(this, "Debe seleccionar al menos un producto.", Toast.LENGTH_SHORT).show();
        }
    }
}