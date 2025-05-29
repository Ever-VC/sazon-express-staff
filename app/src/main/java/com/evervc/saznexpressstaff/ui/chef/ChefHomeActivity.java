package com.evervc.saznexpressstaff.ui.chef;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.ui.admin.GestionComandasFragment;
import com.evervc.saznexpressstaff.ui.auth.LoginActivity;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChefHomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chef_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        asociarElementosXML();

        bottomNavigationC.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onSelectedItem(item);
            }
        });
    }

    private void asociarElementosXML() {
        bottomNavigationC = findViewById(R.id.bottomNavigationC);
    }

    private boolean onSelectedItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.comandas:
                cambiarFragmento(new GestionComandasFragment());
                return true;
            case R.id.historial:
                cambiarFragmento(new GestionComandasFragment());
                return false;
            case R.id.cerrar_sesion:
                cerrarSesion();
                return false;
        }
        return  false;
    }

    private void cambiarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fcvCocinero, fragment).commit();
    }

    private void cerrarSesion() {
        AlertDialog.Builder mensajeDeConfirmacion = new AlertDialog.Builder(this);
        mensajeDeConfirmacion.setTitle("¿Está seguro que desea cerrar sesión?");
        mensajeDeConfirmacion.setMessage("Será regresado a la pantalla de inicio de sesión.");
        mensajeDeConfirmacion.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    FirebaseAuth.getInstance().signOut();
                    UsuarioSesion.cerrarSesion();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                });
            }
        }).setNegativeButton("No", null).show();
    }
}