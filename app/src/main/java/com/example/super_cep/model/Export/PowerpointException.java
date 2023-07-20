package com.example.super_cep.model.Export;

/**
 * Exception levée lors de la création d'un fichier Powerpoint
 * <p>
 *     Cette exception est levée lors de la création d'un fichier Powerpoint
 *     Elle est utilisée dans {@link com.example.super_cep.model.Export.PowerpointExporter}
 *     pour lever une exception lors de la création d'un fichier Powerpoint
 */
public class PowerpointException extends Exception{
   public PowerpointException(String message) {
      super(message);
   }
}
