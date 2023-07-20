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
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AndroidProvider implements PlatformProvider {

    private Context context;

    public AndroidProvider(Context context) {
        this.context = context;
    }

    @Override
    public byte[] getImagesByteFromPath(String path, int quality) {
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
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
        // Get the first paragraph in the cell
        XSLFTextParagraph paragraph = tc.getTextParagraphs().get(0);

        // Get the first run in the paragraph
        XSLFTextRun run = paragraph.getTextRuns().get(0);

        // Get the font size in points and convert to pixels
        double fontSizePoints = run.getFontSize();
        double deviceDensity = context.getResources().getDisplayMetrics().density;
        float fontSizePixels = (float) (fontSizePoints * 96 / 72);

        // Create a new Paint object and set its text size
        Paint paint = new Paint();
        paint.setTextSize(fontSizePixels);

        // Get the bounds of the text
        Rect bounds = new Rect();
        paint.getTextBounds(run.getRawText(), 0, run.getRawText().length(), bounds);

        return bounds.height();
    }
}
