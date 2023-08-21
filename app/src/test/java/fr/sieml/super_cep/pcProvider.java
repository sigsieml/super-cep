package fr.sieml.super_cep;

import fr.sieml.super_cep.model.Export.PlatformProvider;

import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawTextShape;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XSLFTableCell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class pcProvider implements PlatformProvider {
    @Override
    public byte[] getImagesByteFromPath(String path, int quality) {
        // Read the image
        File imageFile = new File(path);
        try {
            byte[] pictureData = IOUtils.toByteArray(new FileInputStream(imageFile));
            return pictureData;
        } catch (IOException e) {
            System.err.println("Error while reading image file");
            try {
                byte[] pictureData = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("712347014456672438.webp"));
                return pictureData;

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    @Override
    public boolean isStringAPath(String path) {
        return path.startsWith("C:") && (
                path.endsWith(".jpg") ||
                path.endsWith(".jpeg") ||
                path.endsWith(".png")
        );
    }

    @Override
    public int getTextHeight(XSLFTableCell tc) {
        DrawFactory df = DrawFactory.getInstance(null);
        DrawTextShape dts = df.getDrawable(tc);
        return (int) dts.getTextHeight();
    }
}
