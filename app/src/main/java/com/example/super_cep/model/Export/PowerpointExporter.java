package com.example.super_cep.model.Export;

import android.content.res.AssetManager;
import android.net.Uri;

import com.example.super_cep.model.Releve;

import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PowerpointExporter {

    public static final String POWERPOINT_VIERGE_NAME = "powerpointvierge.pptx";
    private AssetManager assetManager;

    public PowerpointExporter(AssetManager assetManager){
        this.assetManager = assetManager;
    }


    public void export(FileDescriptor file, Releve releve) {
        try {
            InputStream is = assetManager.open(POWERPOINT_VIERGE_NAME);
            XMLSlideShow ppt = new XMLSlideShow(is);

            for (XSLFSlide slide : ppt.getSlides()) {
                for (XSLFShape shape : slide) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        StringBuilder totalText = new StringBuilder();
                        List<XSLFTextRun> runs = new ArrayList<>();

                        // Concatenate all the text and save the text runs
                        for (XSLFTextParagraph paragraph : textShape) {
                            for (XSLFTextRun run : paragraph) {
                                totalText.append(run.getRawText());
                                runs.add(run);
                            }
                        }

                        // Perform the replacement
                        String replacedText = totalText.toString().replace("{{ nomBatiment }}", releve.nomBatiment);

                        // Split the replaced text into the original number of runs
                        int start = 0;
                        for (XSLFTextRun run : runs) {
                            int end = Math.min(start + run.getRawText().length(), replacedText.length());
                            run.setText(replacedText.substring(start, end));
                            start = end;
                        }
                    }
                }
            }
            try (FileOutputStream out = new FileOutputStream(file)) {
                ppt.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
