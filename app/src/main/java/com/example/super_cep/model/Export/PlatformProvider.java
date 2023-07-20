package com.example.super_cep.model.Export;

import org.apache.poi.xslf.usermodel.XSLFTableCell;

/**
 * Interface pour fournir des méthodes spécifiques à la plateforme
 * <p>
 *     Cette interface est implémentée par {@link com.example.super_cep.model.Export.PlatformProviderAndroid}
 *     et {@link com.example.super_cep.model.Export.PlatformProviderDesktop}
 *     Elle est utilisée pour fournir des méthodes spécifiques à la plateforme
 *     comme par exemple la récupération d'images depuis un chemin de fichier
 *     ou la vérification qu'une chaine de caractère est un chemin de fichier
 *     ou encore la récupération de la hauteur d'un texte dans une cellule
 *     d'un tableau powerpoint
 *     <br>
 *     Cette interface est utilisée dans {@link com.example.super_cep.model.Export.PowerpointGenerator}
 * </p>
 */
public interface PlatformProvider {

    public byte[] getImagesByteFromPath(String path, int quality);

    public boolean isStringAPath(String path);

    public int getTextHeight(XSLFTableCell tc);
}
