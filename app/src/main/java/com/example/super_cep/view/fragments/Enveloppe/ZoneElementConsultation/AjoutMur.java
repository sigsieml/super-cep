package com.example.super_cep.view.fragments.Enveloppe.ZoneElementConsultation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.databinding.FragmentAjoutMurBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementBinding;
import com.example.super_cep.databinding.ViewFooterZoneElementConsultationBinding;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.model.Enveloppe.Mur;
import com.example.super_cep.model.Enveloppe.ZoneElement;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.controller.SpinnerDataViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AjoutMur extends Fragment {

    private enum Mode{
        Ajout,
        Edition,
        Consultation
    }


    private static final String NOM_ZONE = "param1";

    private static final String NOM_ELEMENT = "param2";

    private static final String NOM_ANCIENNE_ZONE = "param3";
    private String nomZone;
    private String nomElement;

    private String nomAncienneZone;

    private Mode mode = Mode.Ajout;
    private PhotoManager photoManager;
    public AjoutMur() {
        // Required empty public constructor
    }

    public static AjoutMur newInstance(String nomZone) {
        return newInstance(nomZone, null, null);
    }

    public static AjoutMur newInstance(String nomZone, String nomElement){
        return newInstance(nomZone, null, nomElement);
    }

    public static AjoutMur newInstance(String nouvelleZone,String ancienneZone, String nomElement) {
        AjoutMur fragment = new AjoutMur();
        Bundle args = new Bundle();
        args.putString(NOM_ZONE, nouvelleZone);
        args.putString(NOM_ELEMENT, nomElement);
        args.putString(NOM_ANCIENNE_ZONE, ancienneZone);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomZone = requireArguments().getString(NOM_ZONE);
        if(getArguments().getString(NOM_ANCIENNE_ZONE) != null){
            mode = Mode.Edition;
            nomElement = getArguments().getString(NOM_ELEMENT);
            nomAncienneZone = getArguments().getString(NOM_ANCIENNE_ZONE);
        }else if(getArguments().getString(NOM_ELEMENT) != null){
            mode = Mode.Consultation;
            nomElement = getArguments().getString(NOM_ELEMENT);
        }

    }

    private FragmentAjoutMurBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ActivityResultLauncher<Intent>  launcherGetPhoto;
    private ActivityResultLauncher<Intent> launcherCapturePhoto;

    List<Uri> uriImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAjoutMurBinding.inflate(inflater, container, false);
        releveViewModel = new ViewModelProvider(requireActivity()).get(ReleveViewModel.class);
        photoManager = new PhotoManager(getContext());
        setupPhotoLaunchers();
        updateSpinner();
        setupButtons();

        if(mode == Mode.Ajout || mode == Mode.Edition){
            addFooterAjout();
        }

        try {
            if(mode == Mode.Consultation){
                ZoneElement zoneElement = releveViewModel.getReleve().getValue().getZone(nomZone).getZoneElement(nomElement);
                setMondeConsultation(zoneElement);
                addDataToView(zoneElement);
            }
            if(mode == Mode.Edition){
                ZoneElement zoneElement = releveViewModel.getReleve().getValue().getZone(nomAncienneZone).getZoneElement(nomElement);
                addDataToView(zoneElement);
            }
        }catch (Exception e){
            Log.e("AjoutZoneElement", "onCreateView: ", e);
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
                addZoneElementToReleve();
            }
        });

        binding.linearLayoutAjoutMur.addView(viewFooter.getRoot());
    }

    private void setMondeConsultation(ZoneElement zoneElement) {
        binding.textViewTitleMur.setText(zoneElement.getNom());
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
                editZoneElement();
            }
        });

        viewFooter.buttonSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releveViewModel.removeZoneElement(nomZone, nomElement);
                getParentFragmentManager().popBackStack();
            }
        });

        binding.linearLayoutAjoutMur.addView(viewFooter.getRoot());
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
                        String fileName = selectedPhotoUri.getLastPathSegment();
                        File photoFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
                        contentUri = FileProvider.getUriForFile(getContext(), "com.example.super_cep.fileprovider", photoFile);
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
            Log.e("AjoutZoneElement", "addPhotoToView: ", e);
            Toast.makeText(getContext(), "Erreur avec les images elles sont donc supprimer", Toast.LENGTH_SHORT).show();
            uriImages.remove(selectedPhotoUri);
            releveViewModel.getReleve().getValue().getZone(nomZone).getZoneElement(nomElement).uriImages = uriImages;
            releveViewModel.forceUpdateReleve();
        }

    }

    private void addZoneElementToReleve() {
        try {
            releveViewModel.getReleve().getValue().getZone(nomZone).addZoneElement(getZoneElementFromViews());
            releveViewModel.forceUpdateReleve();
            getParentFragmentManager().popBackStack();
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Toast.makeText(getContext(), "Impossible d'ajouter l'élément", Toast.LENGTH_SHORT).show();
        }
    }

    private void editZoneElement(){
        try {
            releveViewModel.editZoneElement(nomElement, nomZone, getZoneElementFromViews());
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateSpinner() {
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeMur, "typeMur");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeDeMiseEnOeuvre, "typeDeMiseEnOeuvre");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeIsolant, "typeIsolant");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerNiveauIsolation, "niveauIsolation");
    }


    private ZoneElement getZoneElementFromViews() {
        Mur mur = new Mur(
                binding.editTextNomMur.getText().toString(),
                binding.spinnerTypeMur.getSelectedItem().toString(),
                binding.spinnerTypeDeMiseEnOeuvre.getSelectedItem().toString(),
                binding.spinnerTypeIsolant.getSelectedItem().toString(),
                binding.spinnerNiveauIsolation.getSelectedItem().toString(),
                Float.parseFloat(binding.editTextNumberEpaisseurIsolant.getText().toString()),
                binding.checkBoxAVerifierMur.isChecked(),
                binding.editTextMultilineNoteMur.getText().toString(),
                uriImages
        );
        return mur;
    }

    private void addDataToView(ZoneElement zoneElement){
        Mur mur = (Mur) zoneElement;
        binding.editTextNomMur.setText(mur.getNom());
        binding.editTextNumberEpaisseurIsolant.setText(String.valueOf(mur.epaisseurIsolant));
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeMur, mur.typeMur);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeDeMiseEnOeuvre, mur.typeMiseEnOeuvre);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerTypeIsolant, mur.typeIsolant);
        spinnerDataViewModel.setSpinnerSelection(binding.spinnerNiveauIsolation, mur.niveauIsolation);
        binding.checkBoxAVerifierMur.setChecked(mur.aVerifier);
        binding.editTextMultilineNoteMur.setText(mur.note);

        for (Uri uri : mur.uriImages) {
            addPhotoToView(uri);
        }
    }



}