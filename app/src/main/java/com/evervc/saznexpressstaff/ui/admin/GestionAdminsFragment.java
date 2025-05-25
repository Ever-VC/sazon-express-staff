package com.evervc.saznexpressstaff.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Usuario;
import com.evervc.saznexpressstaff.data.services.UsuarioService;
import com.evervc.saznexpressstaff.data.services.UsuarioServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.adapters.AdministradorAdaptador;
import com.evervc.saznexpressstaff.ui.admin.views.NuevoEmpleadoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class GestionAdminsFragment extends Fragment {
    private FloatingActionButton fabNuevoAdmin;
    private RecyclerView rcvRegistrosAdmin;
    private TextView tvMensajeInfo;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public GestionAdminsFragment() {
        // Required empty public constructor
    }

    public static GestionAdminsFragment newInstance(String param1, String param2) {
        GestionAdminsFragment fragment = new GestionAdminsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gestion_admins, container, false);
        asociarElementosXml(view);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) actualizarListaAdmins();
                }
        );

        fabNuevoAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nuevoEmpleado =new Intent(getContext(), NuevoEmpleadoActivity.class);
                activityResultLauncher.launch(nuevoEmpleado);
            }
        });

        actualizarListaAdmins();
        return view;
    }

    private void asociarElementosXml(View view) {
        fabNuevoAdmin = view.findViewById(R.id.fabNuevoAdmin);
        rcvRegistrosAdmin = view.findViewById(R.id.rcvRegistrosAdmin);
        tvMensajeInfo = view.findViewById(R.id.tvMensajeInfo);
    }

    private void actualizarListaAdmins() {
        UsuarioService servicio = new UsuarioServiceImpl();

        servicio.obtenerUsuariosPorRol("administrador").addOnSuccessListener(lista -> {
            if (!lista.isEmpty()) {
                tvMensajeInfo.setVisibility(View.GONE);

                AdministradorAdaptador adaptador = new AdministradorAdaptador(
                        lista,
                        this::actualizarListaAdmins,
                        getContext(),
                        getChildFragmentManager(),
                        activityResultLauncher
                );

                rcvRegistrosAdmin.setLayoutManager(new GridLayoutManager(
                        getContext(), 1, GridLayoutManager.VERTICAL, false
                ));
                rcvRegistrosAdmin.setAdapter(adaptador);
            } else {
                tvMensajeInfo.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {
            System.out.println("Error al obtener usuarios: " + e.getMessage());
            tvMensajeInfo.setVisibility(View.VISIBLE);
        });
    }

}