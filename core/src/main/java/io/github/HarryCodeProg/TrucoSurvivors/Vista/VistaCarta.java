*** Begin Patch
*** Update File: core/src/main/java/io/github/HarryCodeProg/TrucoSurvivors/Vista/VistaCarta.java
@@
-    /** Cambiar boca abajo ahora solo cambia la region, no crea/destruye texturas. */
-    public void cambiarBocaAbajo(TextureAtlas atlas){
-        this.bocaAbajo = false;
-        this.region = atlas.findRegion(carta.getNombreRegion());
-    }
+    /** Muestra la cara de la carta (la pone boca arriba). */
+    public void ponerBocaArriba(TextureAtlas atlas){
+        this.bocaAbajo = false;
+        this.region = atlas.findRegion(carta.getNombreRegion());
+    }
*** End Patch
