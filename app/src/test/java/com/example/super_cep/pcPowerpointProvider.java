package com.example.super_cep;

import com.example.super_cep.model.Export.PowerpointPlatformProvider;

import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class pcPowerpointProvider implements PowerpointPlatformProvider {
    @Override
    public byte[] getImagesByteFromPath(String path) {
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
}
