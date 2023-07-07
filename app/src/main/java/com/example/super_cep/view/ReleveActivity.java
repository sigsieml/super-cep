package com.example.super_cep.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.ConfigDataProvider;
import com.example.super_cep.controller.LocalisationProvider;
import com.example.super_cep.controller.ReleveSaver;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.ConfigDataViewModel;
import com.example.super_cep.databinding.ActivityReleveBinding;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.ConfigData.ConfigData;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ReleveActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityReleveBinding binding;

    private Releve releve;
    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;
    private LocalisationProvider localisationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build());

        binding = ActivityReleveBinding.inflate(getLayoutInflater());
        askForPermissions();
        setupReleve();
        setupSpinnerData();
        setupLocalisation();

        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarReleve.toolbar);
        setupNavBar();
    }

    private void setupLocalisation() {
        localisationProvider = new LocalisationProvider(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        localisationProvider.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        localisationProvider.onPause();
    }

    private void askForPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PackageManager.PERMISSION_GRANTED);
    }

    private void setupSpinnerData() {
        ConfigDataProvider configDataProvider = new ConfigDataProvider(this);
        ConfigData configData = configDataProvider.getConfigData();
        configDataViewModel = new ViewModelProvider(this).get(ConfigDataViewModel.class);
        configDataViewModel.setSpinnerData(configData.map);
    }


    private void setupReleve() {
        //get relevepath from intent
        String relevePath = getIntent().getStringExtra("relevePath");
        if(relevePath != null){
            releve = new ReleveSaver(this).readReleve(relevePath);
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
        navigationView.getHeaderView(0).setOnClickListener((view) -> alertMessageArretDuReleve());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_batiment, R.id.nav_enveloppes, R.id.nav_usage_et_occupation, R.id.nav_chauffages,
                R.id.nav_climatisation, R.id.nav_ventilation, R.id.nav_ecs, R.id.nav_approvisionnement_energetique,
                R.id.nav_remarques, R.id.nav_preconisations, R.id.nav_export_data)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        final Context context = this;
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                ReleveSaver releveSaver = new ReleveSaver(context);
                try {
                    releveSaver.saveReleve(releveViewModel.getReleve().getValue());
                }catch (Exception e){
                    Log.e("ReleveActivity", "onDestinationChanged: ", e);
                }
            }
        });
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() ==  R.id.action_save){

                ReleveSaver releveSaver = new ReleveSaver(this);
                try {
                    releveSaver.saveReleve(releveViewModel.getReleve().getValue());
                    Toast.makeText(this, "Relevé sauvegardé", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("ReleveActivity", "onOptionsItemSelected: ", e);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Log.i("ReleveActivity", "onBackPressed: " + Navigation.findNavController(this, R.id.nav_host_fragment_content_main).getBackQueue().size());
        if(Navigation.findNavController(this, R.id.nav_host_fragment_content_main).getBackQueue().size() <= 2){
            alertMessageArretDuReleve();
        }
        else{
            ReleveActivity.super.onBackPressed();
        }
    }

    private void alertMessageArretDuReleve() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez-vous arrêter le releve ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ReleveSaver releveSaver = new ReleveSaver(ReleveActivity.this);
                        if(releveViewModel.getReleve().getValue().nomBatiment == null || releveViewModel.getReleve().getValue().nomBatiment.isEmpty()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReleveActivity.this);
                            builder.setMessage("Le relevé n'a pas de nom de bâtiment et donc ne sera pas sauvegardée. Continuer ?");
                            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // load MainActivity
                                    ReleveActivity.super.onBackPressed();
                                    finish();
                                }
                            });
                            builder.setNegativeButton("Non", null);
                            builder.show();
                        }else{
                            releveSaver.saveReleve(releveViewModel.getReleve().getValue());
                            ReleveActivity.super.onBackPressed();
                            finish();
                        }
                    }
                })
                .setNegativeButton("Non", null)
                .show();

    }
}