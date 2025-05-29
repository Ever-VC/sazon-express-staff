package com.evervc.saznexpressstaff.ui.admin;

import static android.app.Activity.RESULT_OK;

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
import android.widget.Button;
import android.widget.Toast;

import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Comanda;
import com.evervc.saznexpressstaff.data.models.ComandaPlatillo;
import com.evervc.saznexpressstaff.data.services.ComandaService;
import com.evervc.saznexpressstaff.data.services.ComandaServiceImpl;
import com.evervc.saznexpressstaff.ui.admin.adapters.ComandasAdaptador;
import com.evervc.saznexpressstaff.ui.admin.views.ComandaPlatilloActivity;
import com.evervc.saznexpressstaff.ui.utils.CarritoCompras;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestionComandasFragment extends Fragment {
    private FloatingActionButton fabNuevaComanda;
    private RecyclerView rcvRegistrosComandas;
    private ActivityResultLauncher<Intent> launcher;
    private Button btnComandasPendientes, btnComandaEnPreparacion, btnComandaLista;
    private List<Comanda> lstComandasFiltrado = new ArrayList<>();
    private int estado = -1;

    public GestionComandasFragment() {
        // Required empty public constructor
    }

    public static GestionComandasFragment newInstance(String param1, String param2) {
        GestionComandasFragment fragment = new GestionComandasFragment();
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
        View view = inflater.inflate(R.layout.fragment_gestion_comandas, container, false);

        asociarElementosXml(view);

        if (UsuarioSesion.obtenerUsuario().getRol().equals("mesero")) {
            btnComandasPendientes.setVisibility(View.GONE);
            btnComandaEnPreparacion.setVisibility(View.GONE);
            btnComandaLista.setVisibility(View.GONE);
        } else if (UsuarioSesion.obtenerUsuario().getRol().equals("cocinero")) {
            btnComandaLista.setVisibility(View.GONE);
        }

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (estado == 0) {
                            actualizarComandasPendientes();
                        } else if (estado == 1) {
                            actualizarComandasEnPreparacion();
                        } else if (estado == 2) {
                            actualizarComandasListas();
                        } else {
                            actualizarComandas();
                        }

                    }
                }
        );

        btnComandasPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado = 0;
                actualizarComandasPendientes();
            }
        });

        btnComandaEnPreparacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado = 1;
                actualizarComandasEnPreparacion();
            }
        });

        btnComandaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado = 2;
                actualizarComandasListas();
            }
        });

        actualizarComandas();

        fabNuevaComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent verPlatillos = new Intent(getContext(), ComandaPlatilloActivity.class);
                launcher.launch(verPlatillos);
            }
        });

        return view;
    }

    private void asociarElementosXml(View view) {
        fabNuevaComanda = view.findViewById(R.id.fabNuevaComanda);
        rcvRegistrosComandas = view.findViewById(R.id.rcvRegistrosComandas);
        btnComandasPendientes = view.findViewById(R.id.btnComandasPendientes);
        btnComandaEnPreparacion = view.findViewById(R.id.btnComandaEnPreparacion);
        btnComandaLista = view.findViewById(R.id.btnComandaLista);
    }

    public void actualizarComandas() {
        ComandaService comandaService = new ComandaServiceImpl();

        comandaService.obtenerTodasLasComandas()
                .addOnSuccessListener(comandas -> {
                    lstComandasFiltrado.clear();
                    lstComandasFiltrado = comandas;
                    ComandasAdaptador adaptador = new ComandasAdaptador(getContext(), lstComandasFiltrado, this::actualizarComandas);
                    rcvRegistrosComandas.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                    rcvRegistrosComandas.setAdapter(adaptador);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al obtener comandas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    public void actualizarComandasPendientes() {
        ComandaService comandaService = new ComandaServiceImpl();

        comandaService.obtenerTodasLasComandas()
                .addOnSuccessListener(comandas -> {
                    lstComandasFiltrado.clear();
                    lstComandasFiltrado.addAll(comandas.stream().filter(comanda -> comanda.getEstado().equals("pendiente")).collect(Collectors.toList()));
                    ComandasAdaptador adaptador = new ComandasAdaptador(getContext(), lstComandasFiltrado, this::actualizarComandas);
                    rcvRegistrosComandas.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                    rcvRegistrosComandas.setAdapter(adaptador);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al obtener comandas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    public void actualizarComandasListas() {
        ComandaService comandaService = new ComandaServiceImpl();

        comandaService.obtenerTodasLasComandas()
                .addOnSuccessListener(comandas -> {
                    lstComandasFiltrado.clear();
                    lstComandasFiltrado.addAll(comandas.stream().filter(comanda -> comanda.getEstado().equals("lista")).collect(Collectors.toList()));
                    ComandasAdaptador adaptador = new ComandasAdaptador(getContext(), lstComandasFiltrado, this::actualizarComandas);
                    rcvRegistrosComandas.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                    rcvRegistrosComandas.setAdapter(adaptador);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al obtener comandas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    public void actualizarComandasEnPreparacion() {
        ComandaService comandaService = new ComandaServiceImpl();

        comandaService.obtenerTodasLasComandas()
                .addOnSuccessListener(comandas -> {
                    lstComandasFiltrado.clear();
                    lstComandasFiltrado.addAll(comandas.stream().filter(comanda -> comanda.getEstado().equals("en preparacion")).collect(Collectors.toList()));
                    ComandasAdaptador adaptador = new ComandasAdaptador(getContext(), lstComandasFiltrado, this::actualizarComandas);
                    rcvRegistrosComandas.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                    rcvRegistrosComandas.setAdapter(adaptador);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al obtener comandas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}