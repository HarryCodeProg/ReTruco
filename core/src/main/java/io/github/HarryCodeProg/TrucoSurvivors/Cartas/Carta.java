*** Begin Patch
*** Update File: core/src/main/java/io/github/HarryCodeProg/TrucoSurvivors/Cartas/Carta.java
@@
     public String getRutaImagen() {
         return "imagenesCartas/" +
             numero +
             "_" +
             palo.name().toLowerCase() +
-            ".PNG";
+            ".png";
     }
*** End Patch
