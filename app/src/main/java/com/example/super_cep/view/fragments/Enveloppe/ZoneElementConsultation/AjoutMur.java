package com.example.super_cep.view.fragments.Enveloppe.ZoneElementConsultation;

import static android.app.Activity.RESULT_OK;

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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.databinding.FragmentAjoutMurBinding;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.model.Enveloppe.Mur;
import com.example.super_cep.view.ReleveViewModel;
import com.example.super_cep.view.SpinnerDataViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AjoutMur extends Fragment {

    private static final String NOM_ZONE = "param1";
    private static final int REQUEST_SELECT_PHOTO = 1;
    private static final int REQUEST_CAPTURE_PHOTO = 2;
    private String nomZone;

    private PhotoManager photoManager;
    public AjoutMur() {
        // Required empty public constructor
    }

    public static AjoutMur newInstance(String nomZone) {
        AjoutMur fragment = new AjoutMur();
        Bundle args = new Bundle();
        args.putString(NOM_ZONE, nomZone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomZone = requireArguments().getString(NOM_ZONE);
    }

    private FragmentAjoutMurBinding binding;

    private ReleveViewModel releveViewModel;
    private SpinnerDataViewModel spinnerDataViewModel;

    private ActivityResultLauncher<String> launcherGetPhoto;
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

        return binding.getRoot();

    }

    private void setupButtons() {
        binding.buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMurToReleve();
            }
        });


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
                                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), binding.buttonAjouterImage, "AjoutMur");
                                launcherGetPhoto.launch("image/*");
                                break;
                            case 1:
                                // Lancer l'activité de capture d'image
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                launcherCapturePhoto.launch(intent);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void updateSpinner() {
        spinnerDataViewModel = new ViewModelProvider(requireActivity()).get(SpinnerDataViewModel.class);
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeMur, "typeMur");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeDeMiseEnOeuvre, "typeDeMiseEnOeuvre");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerTypeIsolant, "typeIsolant");
        spinnerDataViewModel.updateSpinnerData(binding.spinnerNiveauIsolation, "niveauIsolation");
    }

    private void setupPhotoLaunchers() {
        launcherGetPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Uri selectedPhotoUri = result;
                Log.i("AjoutMur", "URI de la photo sélectionnée : " + selectedPhotoUri);
                addPhotoToReleve(selectedPhotoUri);
            }
        });

        launcherCapturePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Bitmap photoBitmap = (Bitmap) result.getData().getExtras().get("data");
                Log.i("AjoutMur", "Bitmap de la photo prise : " + photoBitmap);
                Uri photo = photoManager.savePhotoToStorage(photoBitmap);
                addPhotoToReleve(photo);
            }
        });
    }

    private void addPhotoToReleve(Uri selectedPhotoUri) {
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedPhotoUri, "image/*");
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
    }

    private void addMurToReleve() {
        try {
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

            releveViewModel.getReleve().getValue().getZone(nomZone).addZoneElement(mur);
            releveViewModel.forceUpdateReleve();
            getParentFragmentManager().popBackStack();
            getParentFragmentManager().popBackStack();

        }catch (Exception e){
            Snackbar.make(binding.getRoot(), "impossible d'ajouter le mur " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }


}