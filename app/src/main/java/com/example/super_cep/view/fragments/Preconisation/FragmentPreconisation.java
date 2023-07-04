package com.example.super_cep.view.fragments.Preconisation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.ConfigDataViewModel;
import com.example.super_cep.databinding.FragmentPreconisationBinding;
import com.example.super_cep.model.Releve.Releve;

import java.io.File;
import java.util.List;

public class FragmentPreconisation extends Fragment {

    private FragmentPreconisationBinding binding;
    private ReleveViewModel releveViewModel;
    private ConfigDataViewModel configDataViewModel;
    private ActivityResultLauncher<Intent> launcherGetPhoto;
    private ActivityResultLauncher<Uri> takePictureLauncher ;

    private PhotoManager photoManager;

    private Uri currentUriTaken;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPreconisationBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        configDataViewModel = new ViewModelProvider(requireActivity()).get(ConfigDataViewModel.class);
        photoManager = new PhotoManager(getContext());
        setupPhotoLaunchers();
        setupRecyclerView();
        setupFabs();


        return binding.getRoot();
    }

    private void setupFabs() {
        binding.floatingActionButtonPreconisatioAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpNouvellePreconisation.create(getContext(), releveViewModel, configDataViewModel, new PopUpNouvellePreconisationListener() {
                    @Override
                    public void onValidate(List<String> preconisation) {
                        try {
                            releveViewModel.addPreconisations(preconisation);
                        }catch (Exception e){
                            Log.e("FragmentPreconisation", "onValidate: ", e);
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.floatingActionButtonPreconisationPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une boîte de dialogue pour choisir entre sélectionner une photo ou prendre une photo
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choisir une option");
                builder.setItems(new CharSequence[]{"Sélectionner une photo", "Prendre une photo"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:// Lancer l'activité de sélection de photo
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                launcherGetPhoto.launch(intent);
                                break;
                            case 1:
                                // Créez une intention pour prendre une photo
                                currentUriTaken = photoManager.getUriForNewImage();
                                takePictureLauncher.launch(currentUriTaken);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void setupRecyclerView() {
        List<String> preconisations = releveViewModel.getReleve().getValue().preconisations;
        PreconisationAdapter adapter = new PreconisationAdapter(preconisations.toArray(new String[0]), new PreconisationViewHolderListener() {
            @Override
            public void onPreconisationClicked(String preconisation) {
                // AlertDialog to delete preconisation
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Supprimer la préconisation ?");
                builder.setMessage("Êtes-vous sûr de vouloir supprimer cette préconisation ?");
                builder.setPositiveButton("Oui", (dialog, which) -> {
                    releveViewModel.deletePreconisation(preconisation);
                });
                builder.setNegativeButton("Non", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
            }

            @Override
            public void editPreconisation(String last, String preconisation) {
                try {
                    releveViewModel.editPreconisation(last, preconisation);
                }catch (Exception e){
                    Log.e("FragmentPreconisation", "editPreconisation: " + e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        releveViewModel.getReleve().observe(getViewLifecycleOwner(), releve -> {
            binding.recyclerViewPreconisation.post(new Runnable() {
                @Override
                public void run() {
                    adapter.update(releve.preconisations.toArray(new String[0]));
                }
            });
        });

        binding.recyclerViewPreconisation.setAdapter(adapter);
        binding.recyclerViewPreconisation.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    private void setupPhotoLaunchers() {

        launcherGetPhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);

                        try {
                            releveViewModel.addPreconisations(List.of(uri.toString()));
                        }catch (Exception e){
                            Log.e("FragmentPreconisation", "onValidate: ", e);
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), result -> {
                    if (result) {
                        releveViewModel.addPreconisations(List.of(currentUriTaken.toString()));
                    } else {
                        // La prise de l'image a échoué
                        Toast.makeText(getContext(), "La prise de l'image a échoué", Toast.LENGTH_SHORT).show();
                        //delete the file
                        File file = new File(currentUriTaken.getPath());
                        file.delete();
                    }
                });
    }


}