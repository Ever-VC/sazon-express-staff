package com.evervc.saznexpressstaff.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.repositories.UsuarioRepository;
import com.evervc.saznexpressstaff.data.repositories.UsuarioRepositoryImpl;
import com.evervc.saznexpressstaff.ui.auth.LoginActivity;
import com.evervc.saznexpressstaff.ui.utils.UsuarioSesion;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar, R.string.Open, R.string.Close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeAdminFragment()).commit();
            navigationView.setCheckedItem(R.id.dashboard);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean manejarEvento = false;
        // Verifica la opcion seleccionada
        if(menuItem.getItemId()==R.id.dashboard){
            manejarEvento = true;
            cambiarFragmento(new HomeAdminFragment());
        }
        else if (menuItem.getItemId()==R.id.gestion_empleados) {
            manejarEvento = true;
            cambiarFragmento(new GestionAdminsFragment());
        }
        else if (menuItem.getItemId() == R.id.crud_platillos) {  // Nuevo manejo
            manejarEvento = true;
            cambiarFragmento(new GestionPlatillosFragment());
        }
        else if (menuItem.getItemId()==R.id.cerrar_sesion) {
            cerrarSesion();
        }
        else if (menuItem.getItemId() == R.id.crud_comandas) {
            // Llamas al fragmento de crud de comandas
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return manejarEvento;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_superior, menu); // inflar el menú manualmente

        // Acceder al ítem con seguridad
        MenuItem itemPerfil = menu.findItem(R.id.item_perfil);
        ImageView imagenPerfil = new ImageView(this);

        int tamanno = (int) getResources().getDimension(R.dimen.avatar_size);
        imagenPerfil.setLayoutParams(new Toolbar.LayoutParams(tamanno, tamanno));
        imagenPerfil.setScaleType(ImageView.ScaleType.CENTER_CROP);// ceterCrop
        imagenPerfil.setClipToOutline(true);
        imagenPerfil.setPadding(8, 8, 8, 8);

        String url = UsuarioSesion.obtenerUsuario().getImagenUrl();

        Glide.with(this)
                .load(url)
                .circleCrop()
                .into(imagenPerfil);

        itemPerfil.setActionView(imagenPerfil);

        imagenPerfil.setOnClickListener(v -> {
            Toast.makeText(this, "Yendo al perfil", Toast.LENGTH_SHORT).show();
        });

        return true;
    }

    private void cerrarSesion() {
        Handler handler = new Handler(Looper.getMainLooper());
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

    private void cambiarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}