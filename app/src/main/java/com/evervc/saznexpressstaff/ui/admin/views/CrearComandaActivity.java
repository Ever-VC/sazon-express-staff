package com.evervc.saznexpressstaff.ui.admin.views;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Comanda;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.evervc.saznexpressstaff.data.models.Platillo;
import com.evervc.saznexpressstaff.data.services.ComandaService;
import com.evervc.saznexpressstaff.data.services.ComandaServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.GestionComandasFragment;
import com.evervc.saznexpressstaff.ui.admin.adapters.DetallesOrdenAdaptador;
import com.evervc.saznexpressstaff.ui.utils.CarritoCompras;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;

import java.util.Formattable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearComandaActivity extends AppCompatActivity {
    private RecyclerView rcvShoppingCartItems;
    private EditText etNombreClienteComanda;
    private TextView tvSubTotal, tvTotalcost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_comanda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        asociarElementosXml();
        actualizarTotales();
        actualizarOrden();
    }

    private void asociarElementosXml() {
        rcvShoppingCartItems = findViewById(R.id.rcvShoppingCartItems);
        etNombreClienteComanda = findViewById(R.id.etNombreClienteComanda);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvTotalcost = findViewById(R.id.tvTotalcost);
    }

    private void actualizarOrden() {
        if (CarritoCompras.hayPlatillos() && !CarritoCompras.obtenerListaPlatillos().isEmpty()) {
            DetallesOrdenAdaptador adaptor = new DetallesOrdenAdaptador(this, this::actualizarOrden, this::actualizarTotales);
            rcvShoppingCartItems.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
            rcvShoppingCartItems.setAdapter(adaptor);
        } else {
            Toast.makeText(this, "No hay platillos agregados...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void guardarComanda(View view) {
        if (CarritoCompras.hayPlatillos() && !CarritoCompras.obtenerListaPlatillos().isEmpty()) {
            String nombreCliente = etNombreClienteComanda.getText().toString().trim();
            if (nombreCliente.isEmpty()) {
                Toast.makeText(this, "Debe ingresar el nombre del cliente.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<ComandaPlatillo> lista = CarritoCompras.obtenerListaComandaPlatillos();
            Map<String, ComandaPlatillo> mapaPlatillos = new HashMap<>();
            for (ComandaPlatillo cp : lista) {
                mapaPlatillos.put(cp.getPlatilloId(), cp);
            }

            ComandaService service = new ComandaServiceImpl();
            service.crearComanda(
                    nombreCliente,
                    UsuarioSesion.obtenerUsuario().getId(),
                    mapaPlatillos
            ).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Comanda creada correctamente", Toast.LENGTH_SHORT).show();
                CarritoCompras.vaciar();
                setResult(RESULT_OK);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al crear comanda: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }

    private void actualizarTotales() {
        double total = 0.0;
        int index = 0;
        for (ComandaPlatillo comandaPlatillo : CarritoCompras.obtenerListaComandaPlatillos()) {
            total = total + CarritoCompras.obtenerListaPlatillos().get(index).getPrecio() * comandaPlatillo.getCantidad();
            index ++;
        }
        tvSubTotal.setText(String.valueOf(total));
        tvTotalcost.setText(String.valueOf(total));
    }

}