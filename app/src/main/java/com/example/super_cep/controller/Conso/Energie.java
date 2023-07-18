package com.example.super_cep.controller.Conso;

import org.apache.poi.xddf.usermodel.XDDFColor;

import java.awt.Color;

public enum Energie {
 ELECTRICITE("Electricité", hex2Color("#004579")),
 BUTANE("Butane", hex2Color("#fbe5d6")),
 RESEAU_DE_CHALEUR("Réseau de chaleur", hex2Color("#984807")),
 GAZ_NATUREL("Gaz Naturel", hex2Color("#f2c504")),
 BOIS_GRANULES("Bois Granulés", hex2Color("#86b918")),
 RESEAU_DE_FROID("Réseau de Froid", hex2Color("#dbeef4")),
 FIOUL("Fioul", hex2Color("#bf3fff")),
 BOIS_PLAQUETTES("Bois Plaquettes", hex2Color("#008000")),
 ESSENCE("Essence", hex2Color("#5d5da1")),
 PROPANE("Propane", hex2Color("#eb6b0a")),
 BOIS_BUCHES("Bois Bûches", hex2Color("#4f6228")),
 GAZOLE("Gazole", hex2Color("#ffd966"));


 public static final String[] ENERGIES = new String[]{
         "Electricité",
         "Butane",
         "Réseau de chaleur",
         "Gaz Naturel",
         "Bois Granulés",
         "Réseau de Froid",
         "Fioul",
         "Bois Plaquettes",
         "Essence",
         "Propane",
         "Bois Bûches",
         "Gazole"
 };

 public static final Color[] COLORS = new Color[]{
            hex2Color("#004579"),
            hex2Color("#fbe5d6"),
            hex2Color("#984807"),
            hex2Color("#f2c504"),
            hex2Color("#86b918"),
            hex2Color("#dbeef4"),
            hex2Color("#bf3fff"),
            hex2Color("#008000"),
            hex2Color("#5d5da1"),
            hex2Color("#eb6b0a"),
            hex2Color("#4f6228"),
            hex2Color("#ffd966")
 };

 public String nomEnergie = "";
 public Color color = Color.WHITE;
 Energie(String nomEnergie, Color color){
  this.nomEnergie = nomEnergie;
  this.color = color;
 }
 public XDDFColor getXDDFColor(){
   return convertAwtColorToXDDFColor(this.color);
 }

 public static XDDFColor convertAwtColorToXDDFColor(Color color) {
  byte[] rgb = new byte[3];
  rgb[0] = (byte) color.getRed();
  rgb[1] = (byte) color.getGreen();
  rgb[2] = (byte) color.getBlue();
  XDDFColor xddfColor = XDDFColor.from(rgb);
  return xddfColor;
 }
 public static java.awt.Color hex2Color(String colorStr) {
  int r = Integer.valueOf(colorStr.substring(1, 3), 16);
  int g = Integer.valueOf(colorStr.substring(3, 5), 16);
  int b = Integer.valueOf(colorStr.substring(5, 7), 16);
  return new java.awt.Color(r, g, b);
 }

}
