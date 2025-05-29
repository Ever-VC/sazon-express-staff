package com.evervc.saznexpressstaff.ui.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Comanda;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.evervc.saznexpressstaff.data.models.Platillo;
import com.evervc.saznexpressstaff.data.services.ComandaService;
import com.evervc.saznexpressstaff.data.services.ComandaServiceImpl;
import com.evervc.saznexpressstaff.data.services.PlatilloService;
import com.evervc.saznexpressstaff.data.services.PlatilloServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.functions.AlModificarDetalleComanda;

import java.util.List;

public class DetalleComandaAdaptador extends RecyclerView.Adapter<DetalleComandaAdaptador.DetalleComandaViewHolder> {
    private Context context;
    private List<ComandaPlatillo> lstDetallesComandas;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private AlModificarDetalleComanda listener;

    public DetalleComandaAdaptador(Context context, List<ComandaPlatillo> lstDetallesComandas, ActivityResultLauncher<Intent> activityResultLauncher, AlModificarDetalleComanda listener) {
        this.context = context;
        this.lstDetallesComandas = lstDetallesComandas;
        this.activityResultLauncher = activityResultLauncher;
        this.listener = listener;
    }

    public DetalleComandaAdaptador(Context context, List<ComandaPlatillo> lstDetallesComandas) {
        this.context = context;
        this.lstDetallesComandas = lstDetallesComandas;
    }

    @NonNull
    @Override
    public DetalleComandaAdaptador.DetalleComandaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detalle_comanda_item_lista, parent, false);
        return new DetalleComandaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetalleComandaAdaptador.DetalleComandaViewHolder holder, int position) {
        ComandaPlatillo comandaPlatillo = lstDetallesComandas.get(position);
        ComandaService comandaService = new ComandaServiceImpl();

        // Obtiene la url de la imagen del platillo
        PlatilloService service = new PlatilloServiceImpl();
        service.obtenerPlatilloPorId(comandaPlatillo.getPlatilloId()).addOnSuccessListener(platillo -> {
            if (platillo != null) {
                Glide.with(context)
                        .load(platillo.getImagenUrl())
                        .placeholder(R.drawable.ic_restaurant)
                        .into(holder.imgvComandaPlatillo);
                holder.tvComandaNombrePlatillo.setText(platillo.getNombre());
                String cantidad = "Cantidad: " + comandaPlatillo.getCantidad();
                holder.tvComandaPlatilloCantidad.setText(cantidad);
                holder.tvComandaNota.setText("Nota: " + comandaPlatillo.getNota());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstDetallesComandas.size();
    }

    public class DetalleComandaViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgvComandaPlatillo;
        private TextView tvComandaNombrePlatillo, tvComandaPlatilloCantidad, tvComandaNota;
        public DetalleComandaViewHolder(@NonNull View itemView) {
            super(itemView);
            asociarElementosXMl(itemView);
        }

        private void asociarElementosXMl(View view) {
            imgvComandaPlatillo = view.findViewById(R.id.imgvComandaPlatillo);
            tvComandaNombrePlatillo = view.findViewById(R.id.tvComandaNombrePlatillo);
            tvComandaPlatilloCantidad = view.findViewById(R.id.tvComandaPlatilloCantidad);
            tvComandaNota = view.findViewById(R.id.tvComandaNota);
        }
    }
}
