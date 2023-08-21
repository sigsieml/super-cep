package fr.sieml.super_cep.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoManager {

    private Context context;

    public PhotoManager(Context context) {
        this.context = context;
    }

    public Uri getUriForNewImage(){
        // Add a specific media item.
        ContentResolver resolver = context.getContentResolver();

// Find all audio files on the primary external storage device.
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media
                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // Générer un nom de fichier unique pour la photo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";


        ContentValues newImageDetails = new ContentValues();
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME,
                fileName);
        newImageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");

        Uri imageUri = resolver
                .insert(imageCollection, newImageDetails);
        return imageUri;
    }



    public boolean doesImageExist(Uri uri) {
        ContentResolver cr = context.getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = cr.query(uri, projection, null, null, null);
        boolean imageExists = false;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                imageExists = true;
            }
            cursor.close();
        }
        return imageExists;
    }

}
