package com.example.super_cep.view.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.super_cep.controller.ReleveSaver;
import com.example.super_cep.databinding.ActivityMainBinding;
import com.example.super_cep.view.ReleveActivity;
import com.example.super_cep.view.SettingsActivity.SettingsActivity;


public class MainActivity extends AppCompatActivity implements ReleveRecentViewHolderListener {

    private ActivityMainBinding activityMainBinding;
    private ReleveRecentAdapter releveRecentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askForPermissions();
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.demarrerReleveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start ReleveActivity
                Intent intent = new Intent(MainActivity.this, ReleveActivity.class);
                startActivity(intent);
            }
        });

        activityMainBinding.modifierDonnesPreRemlisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        setupRecyclerView();



    }

    private void askForPermissions() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
    }
    @Override
    protected void onResume() {
        setupRecyclerView();
        super.onResume();
    }

    private void setupRecyclerView() {
        String[] files = new ReleveSaver(this).getAllReleve();
        releveRecentAdapter = new ReleveRecentAdapter(files, this);
        activityMainBinding.releveRecentRecyclerView.setAdapter(releveRecentAdapter);
        activityMainBinding.releveRecentRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
    }

    private void updateRecyclerView(){
        String[] files = new ReleveSaver(this).getAllReleve();
        releveRecentAdapter.updateReleves(files);
    }

    @Override
    public void onReleveFileClicked(String relevePath) {
        Intent intent = new Intent(this, ReleveActivity.class);
        intent.putExtra("relevePath", relevePath);
        startActivity(intent);
    }

    @Override
    public void onReleveFileLongClicked(String relevePath) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Supprimer le relevé ?");
        builder.setPositiveButton("Oui", (dialog, which) -> {
            new ReleveSaver(this).deleteReleve(relevePath);
            updateRecyclerView();
        });
        builder.setNegativeButton("Non", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();

    }
}