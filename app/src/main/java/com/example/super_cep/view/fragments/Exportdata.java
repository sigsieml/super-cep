package com.example.super_cep.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.super_cep.R;
import com.example.super_cep.databinding.FragmentExportdataBinding;
import com.example.super_cep.model.Export.PowerpointExporter;
import com.example.super_cep.view.ReleveViewModel;

import java.io.FileOutputStream;
import java.io.IOException;

public class Exportdata extends Fragment {

    public Exportdata() {
        // Required empty public constructor
    }

    FragmentExportdataBinding binding;

    ReleveViewModel releveViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExportdataBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);

        binding.buttonExporterLesDonne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFile();
            }
        });
        return binding.getRoot();
    }


    // Request code for creating a PDF document.
    private static final int CREATE_FILE = 1;

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        intent.putExtra(Intent.EXTRA_TITLE, releveViewModel.getReleve().getValue().nomBatiment + "releve.pptx");
        startActivityForResult(intent, CREATE_FILE);
    }

    private void writeToFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = requireActivity().getContentResolver().
                    openFileDescriptor(uri, "w");
            PowerpointExporter powerpointExporter = new PowerpointExporter(getContext().getAssets());
            powerpointExporter.export(pfd.getFileDescriptor(), releveViewModel.getReleve().getValue());
            pfd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                writeToFile(uri);
            }
        }
    }




}