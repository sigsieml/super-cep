package com.example.super_cep.model.Export;

import org.apache.poi.xslf.usermodel.XSLFTableCell;

public interface PlatformProvider {

    public byte[] getImagesByteFromPath(String path, int quality);

    public boolean isStringAPath(String path);

    public int getTextHeight(XSLFTableCell tc);
}
