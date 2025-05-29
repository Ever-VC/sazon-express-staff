package com.evervc.saznexpressstaff.ui.admin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Comanda;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.evervc.saznexpressstaff.data.services.ComandaService;
import com.evervc.saznexpressstaff.data.services.ComandaServiceImpl;
import com.evervc.saznexpressstaff.data.services.UsuarioService;
import com.evervc.saznexpressstaff.data.services.UsuarioServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.functions.AlModificarComandas;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComandasAdaptador extends RecyclerView.Adapter<ComandasAdaptador.ComandasViewHolder> {
    private Context context;
    private List<Comanda> lstComandas;
    private AlModificarComandas listener;

    public ComandasAdaptador(Context context, List<Comanda> lstComandas, AlModificarComandas listener) {
        this.context = context;
        this.lstComandas = lstComandas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComandasAdaptador.ComandasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comanda_item_lista, parent, false);
        return new ComandasViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ComandasAdaptador.ComandasViewHolder holder, int position) {
        Comanda comanda = lstComandas.get(position);
        ComandaService service = new ComandaServiceImpl();
        holder.tvComandaCliente.setText("Cliente: " + comanda.getNombreCliente());
        // Verificar y poner un color según el estado
        holder.tvComandaEstado.setText("Estado: " + comanda.getEstado());
        if (comanda.getEstado().equals("en preparacion")) {
            holder.btnPasarComandaLista.setVisibility(View.VISIBLE);
            holder.tvComandaEstado.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
        } else if (comanda.getEstado().equals("lista")) {
            holder.tvComandaEstado.setTextColor(ContextCompat.getColor(context, R.color.ligth_green));
        }

        UsuarioService usuarioService = new UsuarioServiceImpl();
        usuarioService.obtenerUsuarioPorId(comanda.getUidEmpleado()).addOnSuccessListener(usuario -> {
            if (!(usuario == null)) {
                holder.tvComandaMesero.setText("Encargado: " + usuario.getNombre());
            }
        }).addOnFailureListener(e -> {
            System.out.println("Error al obtener usuarios: " + e.getMessage());
        });

        holder.btnPasarComandaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar a "lista"
                service.actualizarEstadoComanda(comanda.getId(), "lista", UsuarioSesion.obtenerUsuario().getId())
                        .addOnSuccessListener(aVoid -> {
                            listener.actualizarListaComanda();
                            Toast.makeText(context.getApplicationContext(), "Comanda lista", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> Toast.makeText(context.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        if (UsuarioSesion.obtenerUsuario().getRol().equals("mesero") || comanda.getEstado().equals("en preparacion") || comanda.getEstado().equals("lista")){
            holder.btnTomarComanda.setVisibility(View.GONE);
            holder.btnPasarComandaLista.setVisibility(View.GONE);
        } else {
            holder.btnTomarComanda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Cambiar a "en preparacion"
                    service.actualizarEstadoComanda(comanda.getId(), "en preparacion", UsuarioSesion.obtenerUsuario().getId())
                            .addOnSuccessListener(aVoid -> {
                                listener.actualizarListaComanda();
                                Toast.makeText(context.getApplicationContext(), "Comanda en preparación", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> Toast.makeText(context.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                }
            });
        }

        Map<String, ComandaPlatillo> mapaComandas = comanda.getPlatillos();
        List<ComandaPlatillo> lstComandasPlatillo = new ArrayList<>(mapaComandas.values());

        DetalleComandaAdaptador adaptador = new DetalleComandaAdaptador(context, lstComandasPlatillo);
        holder.rcvComandaPlatillos.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false));
        holder.rcvComandaPlatillos.setAdapter(adaptador);
    }

    @Override
    public int getItemCount() {
        return lstComandas.size();
    }

    public class ComandasViewHolder extends RecyclerView.ViewHolder {
        TextView tvComandaCliente, tvComandaMesero, tvComandaEstado;
        RecyclerView rcvComandaPlatillos;
        Button btnTomarComanda, btnPasarComandaLista;
        public ComandasViewHolder(@NonNull View itemView) {
            super(itemView);
            asociarElementosXml(itemView);
        }

        private void asociarElementosXml(View view) {
            tvComandaCliente = view.findViewById(R.id.tvComandaCliente);
            tvComandaMesero = view.findViewById(R.id.tvComandaMesero);
            tvComandaEstado = view.findViewById(R.id.tvComandaEstado);
            rcvComandaPlatillos = view.findViewById(R.id.rcvComandaPlatillos);
            btnTomarComanda = view.findViewById(R.id.btnTomarComanda);
            btnPasarComandaLista = view.findViewById(R.id.btnPasarComandaLista);
        }
    }
}
