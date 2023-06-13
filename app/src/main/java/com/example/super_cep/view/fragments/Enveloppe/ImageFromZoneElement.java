package com.example.super_cep.view.fragments.Enveloppe;

import com.example.super_cep.R;
import com.example.super_cep.model.Enveloppe.ZoneElement;

import java.util.HashMap;
import java.util.Map;

public class ImageFromZoneElement {



    final Map<String, Integer> images = new HashMap<String, Integer>();
    private static final int DEFAULT_IMAGE = R.drawable.ic_enveloppe_wall;

    public ImageFromZoneElement(){
        images.put("mur", R.drawable.ic_enveloppe_wall);
        images.put("toiture", R.drawable.ic_enveloppe_roof);
        images.put("menuiserie", R.drawable.ic_enveloppe_window);
        images.put("sol", R.drawable.ic_enveloppe_floor);
        images.put("éclairage", R.drawable.ic_enveloppe_light);
    }

    public int getImage(ZoneElement zoneElement){
        if(images.containsKey(zoneElement.getImage())){
            return images.get(zoneElement.getImage());
        }else{
            return DEFAULT_IMAGE;
        }
    }
}
