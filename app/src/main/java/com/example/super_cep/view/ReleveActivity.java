package com.example.super_cep.view;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.ActivityReleveBinding;
import com.example.super_cep.model.Export.JsonReleveManager;
import com.example.super_cep.model.Releve;
import com.example.super_cep.model.SpinnerData.ConfigDataProvider;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.nio.file.Files;

public class ReleveActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityReleveBinding binding;

    private Releve releve;
    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReleveBinding.inflate(getLayoutInflater());
        askForPermissions();
        setupReleve();
        setupSpinnerData();

        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarReleve.toolbar);
        setupNavBar();
    }

    private void askForPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA},
                PackageManager.PERMISSION_GRANTED);
    }

    private void setupSpinnerData() {
        ConfigDataProvider configDataProvider = new ConfigDataProvider();
        spinnerDataViewModel = new ViewModelProvider(this).get(SpinnerDataViewModel.class);
        spinnerDataViewModel.setSpinnerData(configDataProvider.configData);
    }


    private void setupReleve() {
        //get relevepath from intent
        String relevePath = getIntent().getStringExtra("relevePath");
        if(relevePath != null){
            File releveFile = new File(getFilesDir(), relevePath);
            try {
                byte[] bytes = Files.readAllBytes(releveFile.toPath());
                String json = new String(bytes);
                releve =  JsonReleveManager.deserialize(json);
                if(releve == null)
                    throw new Exception("Impossible de récupérer le relevé");
            } catch (Exception e) {
                Log.e("ReleveActivity", "setupReleve: ", e);
                Toast.makeText(this, "Erreur lors de la lecture du fichier", Toast.LENGTH_SHORT).show();
                releve = new Releve();
            }
        }
        else{
            releve = new Releve();
        }


        releveViewModel = new ViewModelProvider(this).get(ReleveViewModel.class);
        releveViewModel.setReleve(releve);

    }

    private void setupNavBar() {
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_batiment, R.id.nav_enveloppes, R.id.nav_usage_et_occupation, R.id.nav_chauffages,
                R.id.nav_climatisation, R.id.nav_ventilation, R.id.nav_ecs, R.id.nav_approvisionnement_energetique,
                R.id.nav_remarques, R.id.nav_preconisations, R.id.nav_export_data)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}