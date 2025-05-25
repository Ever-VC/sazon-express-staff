package com.evervc.saznexpressstaff.ui.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Usuario;
import com.evervc.saznexpressstaff.ui.admin.functions.AlModificarListaAdmins;

import java.util.List;

public class AdministradorAdaptador extends RecyclerView.Adapter<AdministradorAdaptador.AdministradorViewHolder> {
    private List<Usuario> lstAdministradores;
    private AlModificarListaAdmins listener;
    private Context context;
    private FragmentManager fragmentManager;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public AdministradorAdaptador(
            List<Usuario> lstAdministradores,
            AlModificarListaAdmins listener,
            Context context,
            FragmentManager fragmentManager,
            ActivityResultLauncher<Intent> activityResultLauncher) {
        this.lstAdministradores = lstAdministradores;
        this.listener = listener;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.activityResultLauncher = activityResultLauncher;
    }

    @NonNull
    @Override
    public AdministradorAdaptador.AdministradorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.empleado_item_lista, parent, false);
        return new AdministradorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorAdaptador.AdministradorViewHolder holder, int position) {
        Usuario usuario = lstAdministradores.get(position);
        holder.tvNombreEmpleado.setText(usuario.getNombre());
        holder.tvCorreo.setText(usuario.getCorreo());
        holder.tvCarnet.setText(usuario.getRol());

        String url = usuario.getImagenUrl();

        Glide.with(context)
                .load(url)
                .circleCrop()
                .into(holder.imgItem);

        holder.cvEmpleadoItemLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir el dialogo donde se visualiza el detalle del empleado
                System.out.println("---------------------------------");
                System.out.println("ABRIENDO EL DIALOGO");
                System.out.println("---------------------------------");
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstAdministradores.size();
    }

    public class AdministradorViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgItem;
        private TextView tvNombreEmpleado, tvCorreo, tvCarnet;
        private CardView cvEmpleadoItemLista;
        public AdministradorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvNombreEmpleado = itemView.findViewById(R.id.tvNombreEmpleado);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvCarnet = itemView.findViewById(R.id.tvCarnet);
            cvEmpleadoItemLista = itemView.findViewById(R.id.cvEmpleadoItemLista);
        }
    }
}
