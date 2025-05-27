package com.evervc.saznexpressstaff.ui.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Platillo;

import java.util.List;
import java.util.Locale;

public class PlatilloAdaptador extends RecyclerView.Adapter<PlatilloAdaptador.PlatilloViewHolder> {

    private List<Platillo> platillos;
    private Context context;
    private OnPlatilloListener mOnPlatilloListener;

    public interface OnPlatilloListener {
        void onPlatilloClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public PlatilloAdaptador(List<Platillo> platillos, Context context, OnPlatilloListener onPlatilloListener) {
        if (onPlatilloListener == null) {
            throw new IllegalArgumentException("OnPlatilloListener no puede ser null");
        }
        this.platillos = platillos;
        this.context = context;
        this.mOnPlatilloListener = onPlatilloListener;
    }

    @NonNull
    @Override
    public PlatilloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_platillo, parent, false);
        return new PlatilloViewHolder(view, mOnPlatilloListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlatilloViewHolder holder, int position) {
        Platillo platillo = platillos.get(position);

        holder.tvNombre.setText(platillo.getNombre());
        holder.tvDescripcion.setText(platillo.getDescripcion());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "$%.2f", platillo.getPrecio()));
        holder.tvCategoria.setText(platillo.getCategoria());

        Glide.with(context)
                .load(platillo.getImagenUrl())
                .placeholder(R.drawable.ic_restaurant)
                .into(holder.imgPlatillo);
    }

    @Override
    public int getItemCount() {
        return platillos.size();
    }

    public static class PlatilloViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgPlatillo;
        TextView tvNombre, tvDescripcion, tvPrecio, tvCategoria;
        ImageButton btnEditar, btnEliminar;
        OnPlatilloListener onPlatilloListener;

        public PlatilloViewHolder(@NonNull View itemView, OnPlatilloListener onPlatilloListener) {
            super(itemView);
            imgPlatillo = itemView.findViewById(R.id.imgPlatillo);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            this.onPlatilloListener = onPlatilloListener;

            itemView.setOnClickListener(this);
            btnEditar.setOnClickListener(this);
            btnEliminar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            if (v.getId() == R.id.btnEditar) {
                onPlatilloListener.onEditClick(position);
            } else if (v.getId() == R.id.btnEliminar) {
                onPlatilloListener.onDeleteClick(position);
            } else {
                onPlatilloListener.onPlatilloClick(position);
            }
        }
    }
}
