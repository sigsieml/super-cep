package com.example.super_cep.view.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.super_cep.databinding.ActivityMainBinding;
import com.example.super_cep.model.Export.JsonExporter;
import com.example.super_cep.model.Releve;
import com.example.super_cep.view.ReleveActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;


public class MainActivity extends AppCompatActivity implements ReleveRecentViewHolderListener {

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setupRecyclerView();



    }

    @Override
    protected void onResume() {
        setupRecyclerView();
        super.onResume();
    }

    private void setupRecyclerView() {
        String[] files = fileList();
        Log.i("MainActivity", "setupRecyclerView: " + files);
        activityMainBinding.releveRecentRecyclerView.setAdapter(new ReleveRecentAdapter(files, this));
        activityMainBinding.releveRecentRecyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
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
            File releveFile = new File(getFilesDir(), relevePath);
            releveFile.delete();
            dialog.dismiss();
        });
        builder.setNegativeButton("Non", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();

    }
}