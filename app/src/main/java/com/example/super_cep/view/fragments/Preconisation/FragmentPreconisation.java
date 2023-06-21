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

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentPreconisationBinding;
import com.example.super_cep.model.Enveloppe.Toiture;

import java.util.ArrayList;
import java.util.List;

public class FragmentPreconisation extends Fragment {

    private FragmentPreconisationBinding binding;
    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;
    private ActivityResultLauncher<Intent> launcherGetPhoto;
    private ActivityResultLauncher<Intent> launcherCapturePhoto;

    private PhotoManager photoManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPreconisationBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
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
                PopUpNouvellePreconisation.create(getContext(), releveViewModel, spinnerDataViewModel, new PopUpNouvellePreconisationListener() {
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
                                // Lancer l'activité de capture d'image
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                launcherCapturePhoto.launch(intent2);
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


        launcherCapturePhoto =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bitmap photoBitmap = (Bitmap) result.getData().getExtras().get("data");
                Uri photo = photoManager.savePhotoToStorage(photoBitmap);
                try {
                    releveViewModel.addPreconisations(List.of(photo.toString()));
                }catch (Exception e){
                    Log.e("FragmentPreconisation", "onValidate: ", e);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}