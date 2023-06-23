package com.example.super_cep.view.fragments.ExportData;

import android.content.Context;
import android.media.Image;
import android.net.Uri;

import com.example.super_cep.model.Export.PowerpointPlatformProvider;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AndroidPowerpointProvider implements PowerpointPlatformProvider {

    private Context context;

    public AndroidPowerpointProvider (Context context) {
        this.context = context;
    }

    @Override
    public byte[] getImagesByteFromPath(String path) {
        Uri uri = Uri.parse(path);
        // use content resolver to get image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }

            return baos.toByteArray();

        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                baos.close();
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
    }
}
