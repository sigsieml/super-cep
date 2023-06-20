package com.example.super_cep.view.fragments.Climatisation;

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

import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;
import com.example.super_cep.databinding.FragmentClimatisationAjoutBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.model.Climatisation;
import com.example.super_cep.model.Enveloppe.Zone;
import com.example.super_cep.view.Mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentClimatisationAjout extends Fragment {


    private static final String ARG_NOM_CLIMATISATION = "param2";

    private String nomClimatisation;


    private Mode mode = Mode.Ajout;
    private PhotoManager photoManager;
    public FragmentClimatisationAjout() {
        // Required empty public constructor
    }

    public static FragmentClimatisationAjout newInstance() {
        return newInstance(null);
    }


    public static FragmentClimatisationAjout newInstance(String nomClimatisation) {
        FragmentClimatisationAjout fragment = new FragmentClimatisationAjout();
        Bundle args = new Bundle();
        args.putString(ARG_NOM_CLIMATISATION, nomClimatisation);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(ARG_NOM_CLIMATISATION) != null){
            mode = Mode.Edition;
            nomClimatisation = getArguments().getString(ARG_NOM_CLIMATISATION);
        }

    }

    private FragmentClimatisationAjoutBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ActivityResultLauncher<Intent> launcherGetPhoto;
    private ActivityResultLauncher<Intent> launcherCapturePhoto;

    List<Uri> uriImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClimatisationAjoutBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        photoManager = new PhotoManager(getContext());
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        Map<String, List<String>> spinnerLiveData = spinnerDataViewModel.getSpinnerData().getValue();
        setupPhotoLaunchers();
        updateSpinner();
        addZonesToZonesList();
        setupButtons();

        if(mode == Mode.Ajout){
            addFooterAjout();
        }

        try {
            if(mode == Mode.Edition){
                Climatisation climatisation = releveViewModel.getReleve().getValue().climatisations.get(nomClimatisation);
                setModeEdition(climatisation);
                addDataToView(climatisation);
            }
        }catch (Exception e){
            Log.e("Ajout climatisation", "onCreateView: ", e);
            Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }

        return binding.getRoot();

    }

    private void addFooterAjout() {
        ViewFooterZoneElementBinding viewFooter = ViewFooterZoneElementBinding.inflate(getLayoutInflater());
        viewFooter.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        viewFooter.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClimatisationToReleve();
            }
        });

        binding.linearLayoutAjoutClimatisation.addView(viewFooter.getRoot());
    }

    private void setModeEdition(Climatisation climatisation) {
        binding.textViewTitleClimatisation.setText(climatisation.nom);
        ViewFooterZoneElementConsultationBinding viewFooter = ViewFooterZoneElementConsultationBinding.inflate(getLayoutInflater());
        viewFooter.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        viewFooter.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClimatisation();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeClimatisation(nomClimatisation);
                getParentFragmentManager().popBackStack();
            }
        });

        binding.linearLayoutAjoutClimatisation.addView(viewFooter.getRoot());
    }



    private void setupButtons() {
        binding.buttonAjouterImage.setOnClickListener(new View.OnClickListener() {
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
                        // Faites ce que vous devez faire avec l'URI ici
                        addPhotoToView(uri);

                    }
                });


        launcherCapturePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bitmap photoBitmap = (Bitmap) result.getData().getExtras().get("data");
                Uri photo = photoManager.savePhotoToStorage(photoBitmap);
                addPhotoToView(photo);
            }
        });
    }

    private void addPhotoToView(Uri selectedPhotoUri) {
        try {
            uriImages.add(selectedPhotoUri);

            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_image_zone_element, null);
            int widthInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthInDp, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            binding.linearLayoutImageViewsZoneElement.addView(view, 0);
            ViewImageZoneElementBinding viewImageZoneElementBinding = ViewImageZoneElementBinding.bind(view);
            viewImageZoneElementBinding.imageViewZoneElement.setImageURI(selectedPhotoUri);
            viewImageZoneElementBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtenez l'URI de FileProvider
                    Uri contentUri = null;
                    if (selectedPhotoUri.getScheme().equals("content")) {
                        contentUri = selectedPhotoUri;
                    } else if (selectedPhotoUri.getScheme().equals("file")) {
                        contentUri = photoManager.getFileProviderUri(selectedPhotoUri);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(contentUri, "image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(intent);
                }
            });

            viewImageZoneElementBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // show a dialog to confirm the deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Supprimer l'image ?");
                    builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            binding.linearLayoutImageViewsZoneElement.removeView(view);
                            uriImages.remove(selectedPhotoUri);
                        }
                    });
                    builder.setNegativeButton("Non", null);
                    builder.show();
                    return true;
                }
            });

        }catch (Exception e){
            Log.e("Ajout image", "addPhotoToView: ", e);
            Toast.makeText(getContext(), "Erreur avec l'image elle est donc supprimer", Toast.LENGTH_SHORT).show();
            uriImages.remove(selectedPhotoUri);
            releveViewModel.getReleve().getValue().climatisations.get(nomClimatisation).uriImages = uriImages;
            releveViewModel.forceUpdateReleve();
        }

    }

    private void addClimatisationToReleve() {
        try {
            releveViewModel.addClimatisation(getClimatisationFromViews());
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Log.e("Climatisation", "addClimatisationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void editClimatisation(){
        try {
            releveViewModel.editClimatisation(nomClimatisation, getClimatisationFromViews());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Log.e("Climatisation", "addClimatisationToReleve: ", e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getSelectedZones(){
        List<String> selectedZones = new ArrayList<>();
        for (int i = 0; i < binding.linearLayoutZones.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) binding.linearLayoutZones.getChildAt(i);
            if (checkBox.isChecked()){
                selectedZones.add(checkBox.getText().toString());
            }
        }
        return selectedZones;
    }

    private void addZonesToZonesList(){
        Zone[] zones = releveViewModel.getReleve().getValue().getZonesValues();
        for (int i = 0; i < zones.length; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(zones[i].nom);
            binding.linearLayoutZones.addView(checkBox);
        }
    }

    private void setSelectedZones(List<String> selectedZones){
        for (int i = 0; i < binding.linearLayoutZones.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) binding.linearLayoutZones.getChildAt(i);
            if (selectedZones.contains(checkBox.getText())){
                checkBox.setChecked(true);
            }
        }
    }

    private void updateSpinner() {
        spinnerDataViewModel.updateSpinnerData(binding.spinnerRegulations, "regulationClimatisation");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeClimatisation, "typeClimatisation");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerMarque, "marqueClimatisation");
    }

    private Climatisation getClimatisationFromViews(){
        Climatisation climatisation = new Climatisation(
                binding.editTextNomClimatisation.getText().toString(),
                binding.spinnerTypeClimatisation.getSelectedItem().toString(),
                binding.editTextNumberPuissance.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.editTextNumberPuissance.getText().toString()),
                binding.editTextNumberQuantite.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.editTextNumberQuantite.getText().toString()),
                binding.spinnerMarque.getSelectedItem().toString(),
                binding.editTextModele.getText().toString(),
                binding.spinnerRegulations.getSelectedItem().toString(),
                getSelectedZones(),
                uriImages,
                binding.checkBoxAVerifierClimatisation.isChecked(),
                binding.editTextMultilineNoteClimatisation.getText().toString()
        );

        return climatisation;
    }

    private void addDataToView(Climatisation climatisation){
        binding.editTextNomClimatisation.setText(climatisation.nom);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeClimatisation, climatisation.type);
        binding.editTextNumberPuissance.setText(String.valueOf(climatisation.puissance));
        binding.editTextNumberQuantite.setText(String.valueOf(climatisation.quantite));
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerMarque, climatisation.marque);
        binding.editTextModele.setText(climatisation.modele);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerRegulations, climatisation.regulation);
        binding.checkBoxAVerifierClimatisation.setChecked(climatisation.aVerifier);
        binding.editTextMultilineNoteClimatisation.setText(climatisation.note);

        setSelectedZones(climatisation.zones);
        for (Uri uri : climatisation.uriImages) {
            addPhotoToView(uri);
        }
    }
}