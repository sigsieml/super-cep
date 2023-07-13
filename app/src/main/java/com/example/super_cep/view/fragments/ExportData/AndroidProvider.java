package com.example.super_cep.view.fragments.ExportData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.TypedValue;

import com.example.super_cep.model.Export.PlatformProvider;

import org.apache.poi.xslf.usermodel.XSLFTableCell;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AndroidProvider implements PlatformProvider {

    private Context context;

    public AndroidProvider(Context context) {
        this.context = context;
    }

    @Override
    public byte[] getImagesByteFromPath(String path) {
        if(path == null)
            return null;
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

    @Override
    public int getTextHeight(XSLFTableCell tc) {
        if(tc.getTextParagraphs().size() == 0 || tc.getTextParagraphs().get(0).getTextRuns().size() == 0)
            return 10;
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT); // Remplacez par la police que vous voulez utiliser
        // Il faut transformer la taille de police en pixels, car Android utilise les pixels comme unité de mesure
        int fontSizeInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                tc.getTextParagraphs().get(0).getTextRuns().get(0).getFontSize().floatValue(),
                context.getResources().getDisplayMetrics());
        paint.setTextSize(fontSizeInPixels);

        String text = tc.getText();
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        return bounds.height();
    }
}
