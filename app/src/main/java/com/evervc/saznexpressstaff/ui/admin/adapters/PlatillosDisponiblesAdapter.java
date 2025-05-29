package com.evervc.saznexpressstaff.ui.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Platillo;
import com.evervc.saznexpressstaff.ui.utils.CarritoCompras;

import java.util.List;
import java.util.Locale;

public class PlatillosDisponiblesAdapter extends RecyclerView.Adapter<PlatillosDisponiblesAdapter.PlatillosDisponiblesViewHolder> {
    private Context context;
    private List<Platillo> lstPlatillos;
    public interface AlAgregarPlatillo {
        void actualizarCantidadPlatillos();
    }
    private AlAgregarPlatillo listener;
    public PlatillosDisponiblesAdapter(Context context, List<Platillo> lstPlatillos, AlAgregarPlatillo listener) {
        this.context = context;
        this.lstPlatillos = lstPlatillos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlatillosDisponiblesAdapter.PlatillosDisponiblesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.platillos_item_lista, parent, false);
        return new PlatillosDisponiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatillosDisponiblesAdapter.PlatillosDisponiblesViewHolder holder, int position) {
        Platillo platillo = lstPlatillos.get(position);
        Glide.with(context)
                .load(platillo.getImagenUrl())
                .placeholder(R.drawable.ic_restaurant)
                .into(holder.imgvFoodImage);
        holder.tvCategory.setText(platillo.getCategoria());
        holder.tvFoodName.setText(platillo.getNombre());
        holder.tvFoodPrice.setText(String.format(Locale.getDefault(), "$%.2f", platillo.getPrecio()));
        holder.tvFoodDescription.setText(platillo.getDescripcion());
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CarritoCompras.hayPlatillos()) {
                    CarritoCompras.inicializarLista();
                    CarritoCompras.agregatPlatillo(platillo);
                } else if (!CarritoCompras.obtenerListaPlatillos().contains(platillo)) {
                    CarritoCompras.agregatPlatillo(platillo);
                }
                listener.actualizarCantidadPlatillos();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstPlatillos.size();
    }

    public class PlatillosDisponiblesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgvFoodImage;
        private TextView tvFoodName, tvFoodDescription, tvCategory, tvFoodPrice;
        private Button btnAddToCart;
        public PlatillosDisponiblesViewHolder(@NonNull View itemView) {
            super(itemView);
            asociarElementosXml(itemView);
        }

        private void asociarElementosXml(View view) {
            imgvFoodImage = view.findViewById(R.id.imgvFoodImage);
            tvFoodName = view.findViewById(R.id.tvFoodName);
            tvFoodDescription = view.findViewById(R.id.tvFoodDescription);
            tvCategory = view.findViewById(R.id.tvCategory);
            tvFoodPrice = view.findViewById(R.id.tvFoodPrice);
            btnAddToCart = view.findViewById(R.id.btnAddToCart);
        }
    }
}
