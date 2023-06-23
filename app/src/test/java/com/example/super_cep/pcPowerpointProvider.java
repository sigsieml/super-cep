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
            return null;
        }
    }
}
