package com.evervc.saznexpressstaff.ui.admin.adapters;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.evervc.saznexpressstaff.data.models.Platillo;
import com.evervc.saznexpressstaff.ui.admin.functions.AlModificarDetalleComanda;
import com.evervc.saznexpressstaff.ui.utils.CarritoCompras;

import java.util.List;

public class DetallesOrdenAdaptador extends RecyclerView.Adapter<DetallesOrdenAdaptador.DetalleOrdenViewHolder> {
    private List<Platillo> lstPlatillos;
    private List<ComandaPlatillo> lstComandaPlatillos;
    private Context context;
    public interface AlModificarCantidad {
        void actualizarTotales();
    }
    private AlModificarDetalleComanda listener;
    private AlModificarCantidad listener2;

    public DetallesOrdenAdaptador(Context context, AlModificarDetalleComanda listener, AlModificarCantidad listener2) {
        this.context = context;
        this.listener = listener;
        this.lstPlatillos = CarritoCompras.obtenerListaPlatillos();
        this.lstComandaPlatillos = CarritoCompras.obtenerListaComandaPlatillos();
        this.listener2 = listener2;
    }

    @NonNull
    @Override
    public DetallesOrdenAdaptador.DetalleOrdenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_cart_list, parent, false);
        return new DetalleOrdenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetallesOrdenAdaptador.DetalleOrdenViewHolder holder, int position) {
        Platillo platillo = lstPlatillos.get(position);
        ComandaPlatillo comandaPlatillo = lstComandaPlatillos.get(position);
        holder.tvShoppingCartName.setText(platillo.getNombre());
        holder.tvProductCountShopping.setText(String.valueOf(comandaPlatillo.getCantidad()));
        holder.etNotaPorPlatillo.setText(comandaPlatillo.getNota());
        holder.tvShoppingCartDescription.setText(platillo.getDescripcion());
        String precio = "$" + platillo.getPrecio();
        holder.tvShoppingCartPrice.setText(precio);
        Glide.with(context)
                .load(platillo.getImagenUrl())
                .placeholder(R.drawable.ic_restaurant)
                .into(holder.imgvShoppingCart);
        holder.ibtnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comandaPlatillo.getCantidad() == 1) {
                    CarritoCompras.eliminarPlatillo(platillo);
                } else {
                    comandaPlatillo.setCantidad(comandaPlatillo.getCantidad() - 1);
                }
                listener2.actualizarTotales();
                listener.actualizarListaDetalleComanda();
            }
        });
        holder.ibtnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comandaPlatillo.setCantidad(comandaPlatillo.getCantidad() + 1);
                listener.actualizarListaDetalleComanda();
                listener2.actualizarTotales();
            }
        });

        if (holder.etNotaPorPlatillo.getTag() instanceof TextWatcher) {
            holder.etNotaPorPlatillo.removeTextChangedListener((TextWatcher) holder.etNotaPorPlatillo.getTag());
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                comandaPlatillo.setNota(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        holder.etNotaPorPlatillo.addTextChangedListener(watcher);
        holder.etNotaPorPlatillo.setTag(watcher);
    }

    @Override
    public int getItemCount() {
        return lstPlatillos.size();
    }

    public class DetalleOrdenViewHolder extends RecyclerView.ViewHolder {
        private TextView tvShoppingCartName, tvShoppingCartDescription, tvShoppingCartPrice, tvProductCountShopping;
        private ImageView imgvShoppingCart;
        private ImageButton ibtnMinus, ibtnPlus;
        private EditText etNotaPorPlatillo;
        public DetalleOrdenViewHolder(@NonNull View itemView) {
            super(itemView);
            asociarElementosXml(itemView);
        }

        private void asociarElementosXml(View view) {
            tvShoppingCartName = view.findViewById(R.id.tvShoppingCartName);
            tvShoppingCartDescription = view.findViewById(R.id.tvShoppingCartDescription);
            tvShoppingCartPrice = view.findViewById(R.id.tvShoppingCartPrice);
            tvProductCountShopping = view.findViewById(R.id.tvProductCountShopping);
            imgvShoppingCart = view.findViewById(R.id.imgvShoppingCart);
            ibtnMinus = view.findViewById(R.id.ibtnMinus);
            ibtnPlus = view.findViewById(R.id.ibtnPlus);
            etNotaPorPlatillo = view.findViewById(R.id.etNotaPorPlatillo);
        }
    }
}
