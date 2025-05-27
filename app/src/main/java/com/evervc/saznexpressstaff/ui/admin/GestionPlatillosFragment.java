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
import com.evervc.saznexpressstaff.data.models.Platillo;
import com.evervc.saznexpressstaff.data.services.PlatilloService;
import com.evervc.saznexpressstaff.data.services.PlatilloServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.adapters.PlatilloAdaptador;
import com.evervc.saznexpressstaff.ui.admin.views.NuevoPlatilloActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class GestionPlatillosFragment extends Fragment implements PlatilloAdaptador.OnPlatilloListener {
    private FloatingActionButton fabNuevoPlatillo;
    private RecyclerView rcvPlatillos;
    private TextView tvMensajeInfo;
    private PlatilloService platilloService;
    private List<Platillo> listaPlatillos = new ArrayList<>();
    private PlatilloAdaptador adaptador;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    public GestionPlatillosFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        platilloService = new PlatilloServiceImpl();
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        actualizarListaPlatillos();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_platillos, container, false);
        fabNuevoPlatillo = view.findViewById(R.id.fabNuevoPlatillo);
        rcvPlatillos = view.findViewById(R.id.rcvPlatillos);
        tvMensajeInfo = view.findViewById(R.id.tvMensajeInfo);
        fabNuevoPlatillo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevoPlatilloActivity.class);
            activityResultLauncher.launch(intent);
        });

        configurarRecyclerView();
        actualizarListaPlatillos();

        return view;
    }
    private void configurarRecyclerView() {
        adaptador = new PlatilloAdaptador(listaPlatillos, getContext(), this);
        rcvPlatillos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rcvPlatillos.setAdapter(adaptador);
    }
    private void actualizarListaPlatillos() {
        platilloService.obtenerTodosLosPlatillos().addOnSuccessListener(platillos -> {
            listaPlatillos.clear();
            if (platillos != null && !platillos.isEmpty()) {
                listaPlatillos.addAll(platillos);
                tvMensajeInfo.setVisibility(View.GONE);
            } else {
                tvMensajeInfo.setVisibility(View.VISIBLE);
            }
            adaptador.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            tvMensajeInfo.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onPlatilloClick(int position) {
        // Mostrar detalles del platillo
    }

    @Override
    public void onEditClick(int position) {
        Platillo platillo = listaPlatillos.get(position);
        Intent intent = new Intent(getActivity(), NuevoPlatilloActivity.class);
        intent.putExtra("platillo", platillo);
        activityResultLauncher.launch(intent);

    }

    @Override
    public void onDeleteClick(int position) {
        Platillo platillo = listaPlatillos.get(position);
        platilloService.eliminarPlatillo(platillo.getId())
                .addOnSuccessListener(aVoid -> actualizarListaPlatillos())
                .addOnFailureListener(e -> {
                    // Mostrar error
                });
    }
}