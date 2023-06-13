package com.example.super_cep.view;

import android.os.Bundle;
import android.view.Menu;

import com.example.super_cep.R;
import com.example.super_cep.databinding.ActivityReleveBinding;
import com.example.super_cep.model.Enveloppe.Eclairage;
import com.example.super_cep.model.Enveloppe.Menuiserie;
import com.example.super_cep.model.Enveloppe.Sol;
import com.example.super_cep.model.Enveloppe.Toiture;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.model.Releve;
import com.example.super_cep.model.SpinnerDataProvider;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
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
        binding = ActivityReleveBinding.inflate(getLayoutInflater());

        setupReleve();
        setupSpinnerData();

        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarReleve.toolbar);
        setupNavBar();





    }

    private void setupSpinnerData() {
        SpinnerDataProvider spinnerDataProvider = new SpinnerDataProvider();
        spinnerDataViewModel = new ViewModelProvider(this).get(SpinnerDataViewModel.class);
        spinnerDataViewModel.setSpinnerData(spinnerDataProvider.getSpinnersData());
    }

    private void setupReleve() {
        releve = new Releve();

        List<ZoneElement> zoneElements1 = new ArrayList<>();
        zoneElements1.add(new Toiture("Toiture salon 1"));
        zoneElements1.add(new Sol("sol salon 1"));
        zoneElements1.add(new Eclairage("éclairage salon 1"));
        zoneElements1.add(new Sol("sol salon 1"));
        zoneElements1.add(new Menuiserie("fenêtre salon 1"));

        releve.addZone(new Zone("salon 1", zoneElements1));

        List<ZoneElement> zoneElements2 = new ArrayList<>();
        zoneElements2.add(new Toiture("Toiture chaufferie"));
        zoneElements2.add(new Sol("sol chaufferie"));
        zoneElements2.add(new Eclairage("éclairage chaufferie"));
        zoneElements2.add(new Menuiserie("fenêtre chaufferie"));
        releve.addZone(new Zone("chaufferie", zoneElements2));

        List<ZoneElement> zoneElements3 = new ArrayList<>();
        zoneElements3.add(new Toiture("Toiture cuisine"));
        zoneElements3.add(new Sol("sol cuisine"));
        zoneElements3.add(new Menuiserie("fenêtre cuisine"));
        zoneElements3.add(new Eclairage("éclairage cuisine"));
        zoneElements3.add(new Eclairage("éclairage cuisine"));
        zoneElements3.add(new Eclairage("éclairage cuisine"));
        releve.addZone(new Zone("cuisine", zoneElements3));

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