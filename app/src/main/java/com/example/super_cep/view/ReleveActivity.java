package com.example.super_cep.view;

import static com.example.super_cep.model.Export.PowerpointExporter.POWERPOINT_VIERGE_NAME;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.ReleveSaver;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.ActivityReleveBinding;
import com.example.super_cep.model.Export.JsonReleveManager;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.model.SpinnerData.ConfigDataProvider;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ReleveActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityReleveBinding binding;

    private Releve releve;
    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;


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
        ConfigDataProvider configDataProvider = null;
        try {
            configDataProvider = ConfigDataProvider.configDataProviderFromInputStream(getAssets().open(ConfigDataProvider.CONFIG_FILE_NAME));
            spinnerDataViewModel = new ViewModelProvider(this).get(SpinnerDataViewModel.class);
            spinnerDataViewModel.setSpinnerData(configDataProvider.configData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        Log.i("ReleveActivity", "onBackPressed: " + Navigation.findNavController(this, R.id.nav_host_fragment_content_main).getBackQueue().size());
        if(Navigation.findNavController(this, R.id.nav_host_fragment_content_main).getBackQueue().size() <= 2){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Voulez-vous arrêter le releve ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ReleveSaver releveSaver = new ReleveSaver(ReleveActivity.this);
                            if(releveViewModel.getReleve().getValue().nomBatiment == null || releveViewModel.getReleve().getValue().nomBatiment.isEmpty()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(ReleveActivity.this);
                                builder.setMessage("Le Releve n'a pas de nom de batiment et donc ne sera pas sauvegarder continuer ?");
                                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
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
        else{
            ReleveActivity.super.onBackPressed();
        }
    }
}