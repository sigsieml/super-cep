package com.example.super_cep.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.super_cep.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

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

    }
}