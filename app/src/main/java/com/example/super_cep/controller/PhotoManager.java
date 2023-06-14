package com.example.super_cep.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoManager {

    private Context context;

    public PhotoManager(Context context) {
        this.context = context;
    }



    public Uri savePhotoToStorage(Bitmap photoBitmap) {
        // Vérifier si le dossier de photos existe, sinon le créer
        File photosDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Générer un nom de fichier unique pour la photo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        // Créer le fichier de sortie
        File photoFile = new File(photosDirectory, fileName);

        try {
            // Convertir le bitmap en fichier JPEG et le sauvegarder
            FileOutputStream outputStream = new FileOutputStream(photoFile);
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Ajouter la photo à la galerie
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(photoFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);

            // Afficher un message de succès
            Toast.makeText(context, "La photo a été sauvegardée avec succès dans le dossier de photos", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Afficher un message d'erreur
            Toast.makeText(context, "Erreur lors de la sauvegarde de la photo", Toast.LENGTH_SHORT).show();
        }
        return Uri.fromFile(photoFile);
    }
}
