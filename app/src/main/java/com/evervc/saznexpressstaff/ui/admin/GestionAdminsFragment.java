package com.evervc.saznexpressstaff.ui.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evervc.saznexpressstaff.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GestionAdminsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GestionAdminsFragment extends Fragment {

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
        return view;
    }
}