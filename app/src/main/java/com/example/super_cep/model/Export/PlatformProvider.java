package com.example.super_cep.model.Export;

import org.apache.poi.xslf.usermodel.XSLFTableCell;

/**
 * Interface pour fournir des méthodes spécifiques à la plateforme
 * <p>
 *     Elle est utilisée pour fournir des méthodes spécifiques à la plateforme
 *     comme par exemple la récupération d'images depuis un chemin de fichier
 *     ou la vérification qu'une chaine de caractère est un chemin de fichier
 *     ou encore la récupération de la hauteur d'un texte dans une cellule
 *     d'un tableau powerpoint
 *     <br>
 *     Cette interface est utilisée dans {@link com.example.super_cep.model.Export.PowerpointExporter}
 * </p>
 */
public interface PlatformProvider {

    byte[] getImagesByteFromPath(String path, int quality);

    boolean isStringAPath(String path);

    int getTextHeight(XSLFTableCell tc);
}
