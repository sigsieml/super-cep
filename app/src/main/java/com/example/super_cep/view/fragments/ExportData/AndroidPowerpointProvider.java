package com.example.super_cep.view.fragments.ExportData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return out.toByteArray();
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

    @Override
    public boolean isStringAPath(String path) {
        return path.startsWith("content://") || path.startsWith("file://");
    }
}
