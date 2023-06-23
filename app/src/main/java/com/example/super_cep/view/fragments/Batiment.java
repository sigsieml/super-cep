package com.example.super_cep.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.databinding.FragmentBatimentBinding;
import com.example.super_cep.model.Releve;
import com.example.super_cep.controller.ReleveViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Batiment extends Fragment {

    public Batiment() {
        // Required empty public constructor
    }

    FragmentBatimentBinding binding;

    ReleveViewModel releveViewModel;

    LiveData<Releve> releve;

    PhotoManager photoManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBatimentBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        releve = releveViewModel.getReleve();
        photoManager = new PhotoManager(getContext());

        releve.observe(getViewLifecycleOwner(), new Observer<Releve>() {
            @Override
            public void onChanged(Releve rlv) {
                binding.editTextDateDeRenovation.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(rlv.dateDeDerniereRenovation.getTime()));
                binding.editTextDateDeConstruction.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(rlv.dateDeConstruction.getTime()));
                binding.editTextNomBatiment.setText(rlv.nomBatiment);
                binding.editTextNumberDecimalSurfaceTotalChauffe.setText(String.valueOf(rlv.surfaceTotaleChauffe));
                binding.editTextMultiLineDescriptionBatiment.setText(rlv.description);
                binding.editTextMultiLineAdresse.setText(rlv.adresse);

                if(rlv.imageBatiment != null && !rlv.imageBatiment.isEmpty()){
                    Uri uri = Uri.parse(rlv.imageBatiment);
                    binding.imageView2.setImageURI(uri);
                    binding.buttonAjouterImage.setVisibility(View.GONE);
                }else{
                    binding.imageView2.setImageResource(android.R.color.transparent);
                    binding.buttonAjouterImage.setVisibility(View.VISIBLE);
                }


                releve.removeObserver(this);
            }
        });
        setupCalendar();
        setupButtonPhoto();


        return binding.getRoot();
    }


    private void setupCalendar() {
        binding.editTextDateDeConstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();

                final Calendar calendar = releve.getValue().dateDeConstruction;
                new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        updateTextBox(binding.editTextDateDeConstruction, calendar);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.editTextDateDeRenovation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();

                final Calendar calendar = releve.getValue().dateDeDerniereRenovation;
                new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        updateTextBox(binding.editTextDateDeRenovation, calendar);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateTextBox(EditText editTextDateDeConstruction, Calendar calendar) {
        editTextDateDeConstruction.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(calendar.getTime()));
    }


    private void saveData(){
        String newText;

        newText = binding.editTextMultiLineAdresse.getText().toString();
        if (!newText.equals(releve.getValue().adresse)) {
            releveViewModel.setAdresse(newText);
        }

        newText = binding.editTextMultiLineDescriptionBatiment.getText().toString();
        if (!newText.equals(releve.getValue().description)) {
            releveViewModel.setDescription(newText);
        }

        newText = binding.editTextNumberDecimalSurfaceTotalChauffe.getText().toString();
        if (!newText.equals(Float.toString(releve.getValue().surfaceTotaleChauffe)) && !newText.equals("")) {
            releveViewModel.setSurfaceTotaleChauffe(Float.parseFloat(newText));
        }

        newText = binding.editTextNomBatiment.getText().toString();
        if (!newText.equals(releve.getValue().nomBatiment)) {
            releveViewModel.setNomBatiment(newText);
        }
    }


    private void setupButtonPhoto() {

        ActivityResultLauncher<Intent> launcherGetPhoto;
        ActivityResultLauncher<Intent> launcherCapturePhoto;

        launcherGetPhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
                        // Faites ce que vous devez faire avec l'URI ici
                        Releve releve = releveViewModel.getReleve().getValue();
                        releve.imageBatiment = uri.toString();
                        releveViewModel.setReleve(releve);

                    }
                });


        launcherCapturePhoto =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bitmap photoBitmap = (Bitmap) result.getData().getExtras().get("data");
                Uri photo = photoManager.savePhotoToStorage(photoBitmap);
                Releve releve = releveViewModel.getReleve().getValue();
                releve.imageBatiment = photo.toString();
                releveViewModel.setReleve(releve);
            }
        });
        binding.buttonAjouterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }

}