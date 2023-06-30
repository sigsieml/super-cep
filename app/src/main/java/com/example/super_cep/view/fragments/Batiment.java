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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.databinding.FragmentBatimentBinding;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.view.MonthYearPicker;

import java.text.DecimalFormatSymbols;
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
                updateTextBox(binding.editTextDateDeConstruction, rlv.dateDeConstruction);
                updateTextBox(binding.editTextDateDeRenovation, rlv.dateDeDerniereRenovation);
                binding.editTextNomBatiment.setText(rlv.nomBatiment);
                binding.editTextNumberDecimalSurfaceTotalChauffe.setText(String.valueOf(rlv.surfaceTotaleChauffe).replace(".", ","));
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

        char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        binding.editTextNumberDecimalSurfaceTotalChauffe.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator));

        setupCalendar();
        setupButtonPhoto();


        return binding.getRoot();
    }



    private void setupCalendar() {
        binding.editTextDateDeConstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();


                Calendar calendar = releve.getValue().dateDeConstruction;
                if(calendar == null){
                    calendar = Calendar.getInstance();
                }
                MonthYearPicker pd = MonthYearPicker.newInstance(calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.YEAR));


                pd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        if(selectedYear == -1){
                            releveViewModel.setDateDeConstruction(null);
                            updateTextBox(binding.editTextDateDeConstruction, null);
                            return;
                        }
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                        releveViewModel.setDateDeConstruction(calendar);
                        updateTextBox(binding.editTextDateDeConstruction, calendar);
                    }
                });
                pd.show(getParentFragmentManager(), "MonthYearPicker");
            }
        });

        binding.editTextDateDeRenovation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();

                Calendar calendar = releve.getValue().dateDeDerniereRenovation;
                if(calendar == null){
                    calendar = Calendar.getInstance();
                }
                MonthYearPicker pd = MonthYearPicker.newInstance(calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.YEAR));

                pd.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        if(selectedYear == -1){
                            releveViewModel.setDateDeDerniereRenovation(null);
                            updateTextBox(binding.editTextDateDeRenovation, null);
                            return;
                        }
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                        releveViewModel.setDateDeDerniereRenovation(calendar);
                        updateTextBox(binding.editTextDateDeRenovation, calendar);
                    }
                });
                pd.show(getParentFragmentManager(), "MonthYearPicker");

            }
        });
    }

    private void updateTextBox(EditText editText, Calendar calendar) {
        if(calendar != null){
            editText.setText(new SimpleDateFormat("yyyy", Locale.FRANCE).format(calendar.getTime()));
        }else{
            editText.setText("Inconnu");
        }
    }


    private void saveData(){
        String newText;

        newText = binding.editTextMultiLineAdresse.getText().toString();
        if (!newText.equals(releve.getValue().adresse)) {
            releveViewModel.setAdresse(newText);
        }


        newText = binding.editTextNumberDecimalSurfaceTotalChauffe.getText().toString();
        if (!newText.equals(Float.toString(releve.getValue().surfaceTotaleChauffe)) && !newText.equals("")) {
            releveViewModel.setSurfaceTotaleChauffe(Float.parseFloat(newText.replace(",", ".")));
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

                        binding.imageView2.setImageURI(uri);
                        binding.buttonAjouterImage.setVisibility(View.GONE);
                    }
                });


        launcherCapturePhoto =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() != Activity.RESULT_OK){
                    return;
                }
                Bitmap photoBitmap = (Bitmap) result.getData().getExtras().get("data");
                Uri photo = photoManager.savePhotoToStorage(photoBitmap);
                Releve releve = releveViewModel.getReleve().getValue();
                releve.imageBatiment = photo.toString();
                releveViewModel.setReleve(releve);
                binding.imageView2.setImageURI(photo);
                binding.buttonAjouterImage.setVisibility(View.GONE);
            }
        });
        View.OnClickListener newPhotoOnClick = new View.OnClickListener() {
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
        };

        binding.buttonAjouterImage.setOnClickListener(newPhotoOnClick);
        binding.imageView2.setOnClickListener(newPhotoOnClick);

    }

    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }

}