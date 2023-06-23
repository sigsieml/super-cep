package com.example.super_cep.model.Export;

public interface PowerpointPlatformProvider {

    public byte[] getImagesByteFromPath(String path);

    public boolean isStringAPath(String path);

    
}
