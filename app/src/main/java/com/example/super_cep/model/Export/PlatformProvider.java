package com.example.super_cep.model.Export;

public interface PlatformProvider {

    public byte[] getImagesByteFromPath(String path);

    public boolean isStringAPath(String path);

    
}
