package com.evervc.saznexpressstaff.ui.admin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Usuario;
import com.evervc.saznexpressstaff.data.services.UsuarioService;
import com.evervc.saznexpressstaff.data.services.UsuarioServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.functions.AlModificarListaAdmins;
import com.evervc.saznexpressstaff.ui.admin.views.NuevoEmpleadoActivity;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        holder.btnActualizarEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir el coso para cargar la info
                Intent actualizar = new Intent(context, NuevoEmpleadoActivity.class);
                actualizar.putExtra("uidUsuarioAEditar", usuario.getId());
                activityResultLauncher.launch(actualizar);
            }
        });

        holder.btnEliminarEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Eliminar el registro
                AlertDialog.Builder mensajeDeConfirmacion = new AlertDialog.Builder(context);
                mensajeDeConfirmacion.setTitle("¿Está seguro que desea eliminar el registro?");
                mensajeDeConfirmacion.setMessage("Esta acción no puede revertirse, por lo tanto los cambios serán permanentes.");
                mensajeDeConfirmacion.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UsuarioService usuarioService = new UsuarioServiceImpl();
                        if (usuario.getId().equals(UsuarioSesion.obtenerUsuario().getId())) {
                            Toast.makeText(context, "No se puede eliminar el usuario con el que ha iniciado sesión actualmente.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        usuarioService.eliminarUsuario(usuario.getId());
                        listener.actualizarListaAdmins();
                    }
                }).setNegativeButton("No", null).show();
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
        private ImageButton btnActualizarEmpleado, btnEliminarEmpleado;
        public AdministradorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvNombreEmpleado = itemView.findViewById(R.id.tvNombreEmpleado);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvCarnet = itemView.findViewById(R.id.tvCarnet);
            btnActualizarEmpleado = itemView.findViewById(R.id.btnActualizarEmpleado);
            btnEliminarEmpleado = itemView.findViewById(R.id.btnEliminarEmpleado);
        }
    }
}
