package com.example.super_cep.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.super_cep.controller.LocalisationProvider;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.databinding.FragmentBatimentBinding;
import com.example.super_cep.model.Releve.Releve;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.view.AideFragment;
import com.example.super_cep.view.MonthYearPicker;

import java.io.File;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Batiment extends Fragment implements AideFragment {

    public Batiment() {
        // Required empty public constructor
    }

    FragmentBatimentBinding binding;

    ReleveViewModel releveViewModel;

    LiveData<Releve> releve;

    PhotoManager photoManager;

    private LocalisationProvider localisationProvider;

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
                binding.editTextNomBatiment.setText(rlv.nomBatiment.isEmpty() ? "Batiment" : rlv.nomBatiment);
                binding.editTextNumberDecimalSurfaceTotal.setText(String.valueOf(rlv.surfaceTotale).replace(".", ","));
                binding.editTextNumberDecimalSurfaceChauffe.setText(String.valueOf(rlv.surfaceTotaleChauffe).replace(".", ","));
                binding.editTextMultiLineAdresse.setText(rlv.adresse);

                if(rlv.imageFacadeBatiment != null && !rlv.imageFacadeBatiment.isEmpty()){
                    Uri uri = Uri.parse(rlv.imageFacadeBatiment);
                    binding.imageViewFacade.setImageURI(uri);
                    binding.buttonAjouterImageFacade.setVisibility(View.GONE);
                }else{
                    binding.imageViewFacade.setImageResource(android.R.color.transparent);
                    binding.buttonAjouterImageFacade.setVisibility(View.VISIBLE);
                }

                if(rlv.imagePlanBatiment != null && !rlv.imagePlanBatiment.isEmpty()){
                    Uri uri = Uri.parse(rlv.imagePlanBatiment);
                    binding.imageViewPlan.setImageURI(uri);
                    binding.buttonAjouterImagePlan.setVisibility(View.GONE);
                }else{
                    binding.imageViewPlan.setImageResource(android.R.color.transparent);
                    binding.buttonAjouterImagePlan.setVisibility(View.VISIBLE);
                }

                releve.removeObserver(this);
                setSaveOnFocusChangeListeners();
            }
        });

        char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        binding.editTextNumberDecimalSurfaceTotal.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator));
        binding.editTextNumberDecimalSurfaceChauffe.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator));
        setupCalendar();
        setupButtonPhoto();
        setupFabLocation();


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        localisationProvider.onResume();
    }


    @Override
    public void onPause() {
        saveData();
        localisationProvider.onPause();
        super.onPause();
    }


    private void setupFabLocation() {
        localisationProvider = new LocalisationProvider(getActivity());

        binding.fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adresse = localisationProvider.getLocalisation();
                if(adresse != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Voulez-vous utiliser la localisation suivante : " + adresse + " ?");
                    builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            binding.editTextMultiLineAdresse.setText(adresse);
                            releveViewModel.setLocalisation(localisationProvider.getLatitudeLongitude());
                        }
                    });
                    builder.setNegativeButton("Non", null);
                    builder.show();
                }else{
                    Toast.makeText(getActivity(), "Impossible de récupérer la localisation", Toast.LENGTH_SHORT).show();
                }
            }
        });

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


        newText = binding.editTextNumberDecimalSurfaceTotal.getText().toString();
        if (!newText.equals(Float.toString(releve.getValue().surfaceTotale)) && !newText.equals("")) {
            releveViewModel.setSurfaceTotale(Float.parseFloat(newText.replace(",", ".")));
        }
        newText = binding.editTextNumberDecimalSurfaceChauffe.getText().toString();
        if (!newText.equals(Float.toString(releve.getValue().surfaceTotale)) && !newText.equals("")) {
            releveViewModel.setSurfaceTotaleChauffe(Float.parseFloat(newText.replace(",", ".")));
        }

        newText = binding.editTextNomBatiment.getText().toString();
        if (!newText.equals(releve.getValue().nomBatiment)) {
            releveViewModel.setNomBatiment(newText);
        }
    }

    private Uri currentUriTaken;

    private void setupButtonPhoto() {

        ActivityResultLauncher<Intent> launcherGetPhotoFacade = registerForActivityResult(
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
                        releve.imageFacadeBatiment = uri.toString();
                        releveViewModel.setReleve(releve);

                        binding.imageViewFacade.setImageURI(uri);
                        binding.buttonAjouterImageFacade.setVisibility(View.GONE);
                    }
                });

        ActivityResultLauncher<Intent> launcherGetPhotoPlan = registerForActivityResult(
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
                        releve.imagePlanBatiment = uri.toString();
                        releveViewModel.setReleve(releve);

                        binding.imageViewPlan.setImageURI(uri);
                        binding.buttonAjouterImagePlan.setVisibility(View.GONE);
                    }
                });
        ActivityResultLauncher<Uri> takePhotoFacadeLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), result -> {
                    if (result) {
                        Uri photo = currentUriTaken;
                        Releve releve = releveViewModel.getReleve().getValue();
                        releve.imageFacadeBatiment = photo.toString();
                        releveViewModel.setReleve(releve);
                        binding.imageViewFacade.setImageURI(photo);
                        binding.buttonAjouterImageFacade.setVisibility(View.GONE);
                    } else {
                        // La prise de l'image a échoué
                        Toast.makeText(getContext(), "La prise de l'image a échoué", Toast.LENGTH_SHORT).show();
                        //delete the file
                        File file = new File(currentUriTaken.getPath());
                        file.delete();
                    }
                });

        ActivityResultLauncher<Uri> takePhotoPlanLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), result -> {
                    if (result) {
                        Uri photo = currentUriTaken;
                        Releve releve = releveViewModel.getReleve().getValue();
                        releve.imagePlanBatiment = photo.toString();
                        releveViewModel.setReleve(releve);
                        binding.imageViewPlan.setImageURI(photo);
                        binding.buttonAjouterImagePlan.setVisibility(View.GONE);
                    } else {
                        // La prise de l'image a échoué
                        Toast.makeText(getContext(), "La prise de l'image a échoué", Toast.LENGTH_SHORT).show();
                        //delete the file
                        File file = new File(currentUriTaken.getPath());
                        file.delete();
                    }
                });
        View.OnClickListener newPhotoOnClickFacade = new View.OnClickListener() {
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
                                launcherGetPhotoFacade.launch(intent);
                                break;
                            case 1:
                                // Créez une intention pour prendre une photo
                                currentUriTaken = photoManager.getUriForNewImage();
                                takePhotoFacadeLauncher.launch(currentUriTaken);
                                break;
                        }
                    }
                });
                builder.show();
            }
        };

        View.OnClickListener newPhotoOnClickPlan = new View.OnClickListener() {
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
                                launcherGetPhotoPlan.launch(intent);
                                break;
                            case 1:
                                // Créez une intention pour prendre une photo
                                currentUriTaken = photoManager.getUriForNewImage();
                                takePhotoPlanLauncher.launch(currentUriTaken);
                                break;
                        }
                    }
                });
                builder.show();
            }
        };
        binding.buttonAjouterImageFacade.setOnClickListener(newPhotoOnClickFacade);
        binding.imageViewFacade.setOnClickListener(newPhotoOnClickFacade);
        binding.imageViewFacade.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // show a dialog to confirm the deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Supprimer l'image ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.imageViewFacade.setImageURI(null);
                        Releve releve = releveViewModel.getReleve().getValue();
                        releve.imageFacadeBatiment = null;
                        releveViewModel.setReleve(releve);
                        binding.buttonAjouterImageFacade.setVisibility(View.VISIBLE);
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
                return true;
            }
        });
        binding.buttonAjouterImagePlan.setOnClickListener(newPhotoOnClickPlan);
        binding.imageViewPlan.setOnClickListener(newPhotoOnClickPlan);
        binding.imageViewPlan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // show a dialog to confirm the deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Supprimer l'image ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.imageViewPlan.setImageURI(null);
                        Releve releve = releveViewModel.getReleve().getValue();
                        releve.imagePlanBatiment = null;
                        releveViewModel.setReleve(releve);
                        binding.buttonAjouterImagePlan.setVisibility(View.VISIBLE);
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
                return true;
            }
        });
    }



    private void setSaveOnFocusChangeListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {saveData();}
        };
        binding.editTextNomBatiment.addTextChangedListener(textWatcher);
        binding.editTextNumberDecimalSurfaceTotal.addTextChangedListener(textWatcher);
        binding.editTextNumberDecimalSurfaceChauffe.addTextChangedListener(textWatcher);
        binding.editTextMultiLineAdresse.addTextChangedListener(textWatcher);
    }


    @Override
    public void aide() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Besoin d'aide ? Pas de problème !");
        builder.setMessage("1. Explorez les détails de votre bâtiment : Vous pouvez découvrir les détails charmants du bâtiment tels que le nom, l'adresse, la surface totale chauffée, la date de construction et de rénovation. C'est comme un petit livre d'histoire pour chaque bâtiment.\n\n" +
                "2. Devenez l'architecte : Vous avez la possibilité de modifier les informations d'un bâtiment. N'hésitez pas à cliquer sur le champ que vous souhaitez modifier et donnez une nouvelle vie à votre bâtiment.\n\n" +
                "3. Laissez parler votre créativité : Ajoutez une photo du bâtiment en cliquant sur le bouton 'Ajouter Image'. Si une image existe déjà, vous pouvez la modifier en cliquant simplement dessus. Pour supprimer l'image existante, un appui long sur l'image et voilà, elle est partie !\n\n" +
                "4. On ne se perd jamais ici : Vous pouvez récupérer la localisation actuelle en cliquant sur le bouton de localisation. L'adresse récupérée sera ensuite affichée pour que vous puissiez confirmer. Cependant, si la localisation n'est pas disponible pour le moment, ne vous inquiétez pas ! Réessayez simplement plus tard.\n\n" +
                "5. Faites un voyage dans le temps : En cliquant sur le champ de la date, un sélecteur de date s'ouvrira vous permettant de sélectionner la date souhaitée. C'est un peu comme une machine à remonter le temps pour votre bâtiment !");
        builder.setPositiveButton("Génial, merci !", null);
        builder.show();
    }

}