package com.example.super_cep.view.includeView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.super_cep.R;
import com.example.super_cep.controller.PhotoManager;
import com.example.super_cep.controller.ReleveViewModel;
import com.example.super_cep.databinding.ViewImageZoneElementBinding;
import com.example.super_cep.databinding.ViewPhotoBinding;

import java.util.ArrayList;
import java.util.List;

public class ViewPhoto extends View {

    ViewPhotoBinding binding;
    private ActivityResultLauncher<Intent> launcherGetPhoto;
    private ActivityResultLauncher<Intent> launcherCapturePhoto;
    private Fragment fragment;
    private List<Uri> uriImages = new ArrayList<>();

    private PhotoManager photoManager;


    public List<Uri> getUriImages() {
        return uriImages;
    }

    public ViewPhoto(ViewPhotoBinding binding, Fragment fragment) {
        super(binding.getRoot().getContext());
        this.binding = binding;
        this.fragment = fragment;
        this.photoManager = new PhotoManager(getContext());

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


    public void setupPhotoLaunchers() {

        launcherGetPhoto = fragment.registerForActivityResult(
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


        launcherCapturePhoto =  fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() != Activity.RESULT_OK){
                    return;
                }
                Bitmap photoBitmap = (Bitmap) result.getData().getExtras().get("data");

                Uri photo = photoManager.savePhotoToStorage(photoBitmap);
                addPhotoToView(photo);
            }
        });
    }

    public void addPhotoToView(Uri selectedPhotoUri) {
        try {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_image_zone_element, null);
            int widthInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthInDp, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            binding.linearLayoutImageViews.addView(view, 0);
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

                    fragment.startActivity(intent);
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
                            binding.linearLayoutImageViews.removeView(view);
                            uriImages.remove(selectedPhotoUri);
                        }
                    });
                    builder.setNegativeButton("Non", null);
                    builder.show();
                    return true;
                }
            });

            uriImages.add(selectedPhotoUri);


        }catch (Exception e){
            Log.e("Ajout image", "addPhotoToView: ", e);
            Toast.makeText(getContext(), "Erreur avec l'image elle est donc supprimer", Toast.LENGTH_SHORT).show();
        }

    }
}
