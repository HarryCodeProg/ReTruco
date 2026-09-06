package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.ColorMecanica;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class VistaJoker implements Arrastrable{
    private Joker joker;
    private TextureRegion regionJoker; // Este es nuestro recorte oficial del spritesheet
    private float x;
    private float y;
    private float width = 70;  // Respetamos el ANCHO_JOKER de GameScreen
    private float height = 80; // Respetamos el ALTO_JOKER de GameScreen
    private boolean hover;
    private boolean dragging;
    private boolean draggingAnterior;
    private float handX;
    private float handY;
    private float targetX;
    private float targetY;
    private float visualOffsetY;
    private float targetOffsetY;
    private float rotation;
    private float targetRotation;
    private float scale = 1f;
    private float targetScale = 1f;
    private boolean seleccionada = false;
    private static final float UMBRAL_CLICK = 6f;
    private static final float OFFSET_SELECCIONADA = 25f;
    private static final float OFFSET_HOVER = 8f;
    private static final float ESCALA_HOVER = 1.04f;
    private static final float VELOCIDAD_POSICION = 900f;
    private static final float VELOCIDAD_OFFSET = 250f;
    private static final float VELOCIDAD_ESCALA = 4f;
    private static final float VELOCIDAD_ROTACION = 360f;
    private float pressX;
    private float pressY;
    private boolean huboMovimientoSignificativo;
    private float dragOffsetX;
    private float dragOffsetY;
    private static boolean ALGUN_DRAG_ACTIVO = false;
    private boolean resaltado = false;
    private float pulso = 0f;
    // --- Variables para el efecto Balatro Tilt ---
    private float tiltX = 0f;
    private float tiltY = 0f;
    private float targetTiltX = 0f;
    private float targetTiltY = 0f;
    private static final float MAX_TILT = 25f;
    private static final float VELOCIDAD_TILT = 150f;

    public VistaJoker(Joker joker, TextureAtlas atlas) {
        this.joker = joker;
        String nombreBuscado = joker.getNombreRegion();
        this.regionJoker = atlas.findRegion(nombreBuscado);
        if (this.regionJoker == null) {
            System.err.println("ERROR: No se encontró la región '" + nombreBuscado + "' en el atlas.");
            System.out.println("Regiones disponibles en el atlas de Jokers:");
            for (TextureAtlas.AtlasRegion reg : atlas.getRegions()) {
                System.out.println("  -> " + reg.name);
            }
        }
    }

    public void render(SpriteBatch batch) {
        float drawY = y + visualOffsetY;
        float scaleExtra = resaltado ? 1f + (float)(Math.sin(pulso) * 0.08f) : 1f;
        // ------
        batch.flush();
        com.badlogic.gdx.math.Matrix4 matrixAnterior = batch.getTransformMatrix().cpy();
        if (tiltX != 0 || tiltY != 0) {
            com.badlogic.gdx.math.Matrix4 matrixTilt = new com.badlogic.gdx.math.Matrix4(matrixAnterior);
            float cx = x + (width * scaleExtra * scale) / 2f;
            float cy = drawY + (height * scaleExtra * scale) / 2f;
            matrixTilt.translate(cx, cy, -50f);
            matrixTilt.rotate(1, 0, 0, tiltX);
            matrixTilt.rotate(0, 1, 0, tiltY);
            matrixTilt.translate(-cx, -cy, 0f);
            batch.setTransformMatrix(matrixTilt);
        }
        // -----------------------
        if (resaltado) {
            batch.setColor(1.3f, 1.15f, 0.6f, 1f); // tinte dorado/brillante mientras actua
        }
        batch.draw(regionJoker, x, drawY, width / 2f, height / 2f, width * scaleExtra, height * scaleExtra, scale, scale, rotation);
        if (resaltado) {
            batch.setColor(Color.WHITE); // reset
        }
        batch.flush();
        batch.setTransformMatrix(matrixAnterior);
    }

    public void renderCartelStats(SpriteBatch batch, io.github.HarryCodeProg.TrucoSurvivors.Main game, Juego juego) {
        if (!hover || dragging || joker == null) return;
        float drawY = y + visualOffsetY;
        dibujarCartelStats(batch, game, juego, drawY);
    }

    public boolean isHover() { return hover; }

    public void dispose() {
        // La textura de la hoja la maneja y libera GameScreen, acá no destruimos nada.
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setTamaño(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public boolean isDragging() { return this.dragging; }

    public void update(float mouseX, float mouseY, float delta) {
        if (resaltado) {
            pulso += delta * 6f; // velocidad del latido
        }
        hover = !ALGUN_DRAG_ACTIVO && contiene(mouseX, mouseY);
        float offsetHover = hover ? OFFSET_HOVER : 0f;
        float offsetSeleccion = seleccionada ? OFFSET_SELECCIONADA : 0f;
        targetScale = hover ? ESCALA_HOVER : 1f;
        targetOffsetY = offsetHover + offsetSeleccion;
        if (hover) targetRotation = 0f;
        if (!ALGUN_DRAG_ACTIVO) {
            hover = contiene(mouseX, mouseY);
        } else {
            hover = false;
        }
        if (hover) targetRotation = 0f;
        if (hover && !dragging) {
            float cx = x + (width * scale) / 2f;
            float cy = y + visualOffsetY + (height * scale) / 2f;
            float mouseDeltaX = (mouseX - cx) / ((width * scale) / 2f);
            float mouseDeltaY = (mouseY - cy) / ((height * scale) / 2f);
            mouseDeltaX = Math.max(-1f, Math.min(1f, mouseDeltaX));
            mouseDeltaY = Math.max(-1f, Math.min(1f, mouseDeltaY));
            targetTiltY = mouseDeltaX * MAX_TILT;
            targetTiltX = -mouseDeltaY * MAX_TILT;
        } else {
            targetTiltX = 0f;
            targetTiltY = 0f;
        }
        tiltX = moverHacia(tiltX, targetTiltX, VELOCIDAD_TILT * delta);
        tiltY = moverHacia(tiltY, targetTiltY, VELOCIDAD_TILT * delta);
        if (!dragging) {
            x = moverHacia(x, targetX, VELOCIDAD_POSICION * delta);
            y = moverHacia(y, targetY, VELOCIDAD_POSICION * delta);
            scale = moverHacia(scale, targetScale, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, targetOffsetY, VELOCIDAD_OFFSET * delta);
            rotation = moverHacia(rotation, targetRotation, VELOCIDAD_ROTACION * delta);
        } else {
            x = targetX;
            y = targetY;
            scale = moverHacia(scale, 1.2f, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, 0f, VELOCIDAD_OFFSET * delta);
        }
    }

    private float moverHacia(float value, float target, float maxDelta) {
        float diferencia = target - value;
        if (Math.abs(diferencia) <= maxDelta) return target;
        return value + Math.signum(diferencia) * maxDelta;
    }

    public void input(float mouseX, float mouseY) {
        draggingAnterior = dragging;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && contiene(mouseX, mouseY)) {
            dragging = true;
            pressX = mouseX;
            pressY = mouseY;
            huboMovimientoSignificativo = false;
            dragOffsetX = mouseX - x;
            dragOffsetY = mouseY - y;
            ALGUN_DRAG_ACTIVO = true;
        }
        if (dragging) {
            float dx = mouseX - pressX;
            float dy = mouseY - pressY;
            if (!huboMovimientoSignificativo && (Math.abs(dx) > UMBRAL_CLICK || Math.abs(dy) > UMBRAL_CLICK)) {
                huboMovimientoSignificativo = true;
            }
        }
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (dragging && !huboMovimientoSignificativo) {
                seleccionada = !seleccionada;
                GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
                if (sonidos != null) {
                    if (seleccionada) {
                        sonidos.reproducirConVariacion("seleccionar");
                    } else {
                        sonidos.reproducirConVariacion("deseleccionar");
                    }
                }
            }
            if (dragging) {
                ALGUN_DRAG_ACTIVO = false;
            }
            dragging = false;
        }
        if (dragging) {
            targetX = mouseX - dragOffsetX;
            targetY = mouseY - dragOffsetY;
        }
    }

    public void setHandPosition(float x, float y) {
        this.handX = x;
        this.handY = y;
        this.targetX = x;
        this.targetY = y;
    }

    public boolean contiene(float mx, float my) {
        float w = width * scale;
        float h = height * scale;
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private void dibujarCartelStats(SpriteBatch batch, Main game, Juego juego, float drawY) {
        BitmapFont font = game.getFuentePrincipal();
        GlyphLayout layout = new GlyphLayout();
        // --- 1. ACHICAMOS LA FUENTE Y ACTIVAMOS EL MARKUP ---
        float originalScaleX = font.getScaleX();
        float originalScaleY = font.getScaleY();
        boolean markupOriginal = font.getData().markupEnabled; // Guardamos cómo estaba antes
        font.getData().setScale(originalScaleX * 0.8f, originalScaleY * 0.8f);
        font.getData().markupEnabled = true; // ¡ESTO HACE QUE LEA LOS COLORES!
        // Datos del Joker
        String lineaNombre = joker.getNombre();
        String descripcionPura = joker.getDescripcionRenderizada(juego);
        //String descripcion = Palo.colorearTexto(descripcionPura);
        String descripcion = ColorMecanica.colorearTexto(Palo.colorearTexto(descripcionPura));
        String rarezaStr = joker.getRareza().toString().toUpperCase();
        // --- 2. MEDIDAS MÁS CHICAS PARA COMPACTAR EL CARTEL ---
        float ANCHO_MAX_DESC = 175f;
        float padEtiquetaX = 10f;
        float padEtiquetaY = 4f;
        float espacioVertical = 7f;
        // Medimos los textos para calcular el tamaño del cartel
        layout.setText(font, lineaNombre);
        float anchoNombre = layout.width;
        layout.setText(font, descripcion, font.getColor(), ANCHO_MAX_DESC, com.badlogic.gdx.utils.Align.center, true);
        float altoDescripcion = layout.height;
        layout.setText(font, rarezaStr);
        float maxAnchoTexto = Math.max(ANCHO_MAX_DESC, anchoNombre);
        float altoBadge = font.getCapHeight() + (padEtiquetaY * 2f);
        float altoCategorias = 0;
        for (CategoriaJoker cat : joker.getCategorias()) {
            layout.setText(font, cat.getTexto().toUpperCase());
            maxAnchoTexto = Math.max(maxAnchoTexto, layout.width + (padEtiquetaX * 2f));
            altoCategorias += altoBadge + espacioVertical;
        }
        // Dimensiones finales del cartel
        float paddingX = 14f;
        float paddingY = 12f;
        float altoLinea = font.getLineHeight();
        float anchoCartel = maxAnchoTexto + (paddingX * 2f);
        float altoCartel = paddingY + altoLinea + espacioVertical + altoDescripcion + (espacioVertical * 2f) + altoBadge + altoCategorias + paddingY;
        // Posicionamiento
        float actualWidth = width * scale;
        float cartelX = x + (actualWidth / 2f) - (anchoCartel / 2f);
        float cartelY = drawY + (height * scale) + 12f;
        if (cartelY + altoCartel > Gdx.graphics.getHeight()) {
            cartelY = drawY - altoCartel - 12f;
        }
        // --- 3. RENDER DEL FONDO (TEMA OSCURO) ---
        Texture pixelBlanco = game.getPixelBlanco();
        if (pixelBlanco != null) {
            batch.setColor(0.12f, 0.13f, 0.15f, 0.98f); // Fondo Negro Claro
            batch.draw(pixelBlanco, cartelX, cartelY, anchoCartel, altoCartel);
            batch.setColor(0.28f, 0.30f, 0.35f, 1f); // Borde
            float grosorBorde = 2.5f;
            batch.draw(pixelBlanco, cartelX, cartelY, anchoCartel, grosorBorde);
            batch.draw(pixelBlanco, cartelX, cartelY + altoCartel - grosorBorde, anchoCartel, grosorBorde);
            batch.draw(pixelBlanco, cartelX, cartelY, grosorBorde, altoCartel);
            batch.draw(pixelBlanco, cartelX + anchoCartel - grosorBorde, cartelY, grosorBorde, altoCartel);
            batch.setColor(0.18f, 0.20f, 0.22f, 1f); // Sombra interior
            batch.draw(pixelBlanco, cartelX + grosorBorde, cartelY + grosorBorde, anchoCartel - (grosorBorde*2), 1.5f);
        }
        // --- 4. RENDER DE TEXTOS ---
        float currentY = cartelY + altoCartel - paddingY;
        // Obtengo el color de la rareza para usarlo en el nombre y en el badge
        Color colorDeRareza = joker.getRareza().getColor();
        // Nombre
        font.setColor(colorDeRareza);
        layout.setText(font, lineaNombre);
        font.draw(batch, lineaNombre, cartelX + (anchoCartel - layout.width) / 2f, currentY);
        currentY -= (altoLinea + espacioVertical);
        // Descripción
        font.setColor(0.92f, 0.92f, 0.92f, 1f);
        font.draw(batch, descripcion, cartelX + paddingX, currentY, anchoCartel - (paddingX * 2f), com.badlogic.gdx.utils.Align.center, true);
        currentY -= (altoDescripcion + espacioVertical * 1.5f);
        // Renderizado de Etiquetas/Badges
        currentY = dibujarBadge(batch, font, pixelBlanco, rarezaStr, colorDeRareza, cartelX, anchoCartel, currentY, padEtiquetaX, padEtiquetaY);
        for (CategoriaJoker cat : joker.getCategorias()) {
            currentY -= espacioVertical;
            currentY = dibujarBadge(batch, font, pixelBlanco, cat.getTexto().toUpperCase(), cat.getColor(), cartelX, anchoCartel, currentY, padEtiquetaX, padEtiquetaY);
        }
        // --- 5. RESTAURAMOS EL TAMAÑO ORIGINAL DE LA FUENTE Y LOS COLORES ---
        font.getData().setScale(originalScaleX, originalScaleY);
        font.getData().markupEnabled = markupOriginal; // <--- APAGAMOS EL MARKUP
        font.setColor(Color.WHITE);
        batch.setColor(Color.WHITE);
    }

    private float dibujarBadge(SpriteBatch batch, BitmapFont font, Texture pixel, String texto, Color colorFondo, float cartelX, float cartelW, float yTop, float padX, float padY) {
        GlyphLayout layout = new GlyphLayout(font, texto);
        float badgeW = layout.width + (padX * 2f);
        float badgeH = font.getCapHeight() + (padY * 2f);
        float badgeX = cartelX + (cartelW - badgeW) / 2f;
        float badgeY = yTop - badgeH;
        if (pixel != null) {
            batch.setColor(colorFondo);
            batch.draw(pixel, badgeX, badgeY, badgeW, badgeH);
            // Sombra del badge (un poco más oscura para destacar sobre el fondo negro)
            batch.setColor(0f, 0f, 0f, 0.3f);
            batch.draw(pixel, badgeX, badgeY, badgeW, 1.5f);
        }
        font.setColor(Color.WHITE);
        font.draw(batch, texto, badgeX + padX, yTop - padY);
        return badgeY;
    }

    public void setResaltado(boolean resaltado) {
        this.resaltado = resaltado;
        if (resaltado) pulso = 0f;
    }

    public boolean isResaltado() { return resaltado; }


    public float getCentroX() { return x + (width * scale) / 2f; }
    public float getX() { return x; }
    public float getY() { return y; }
    public Joker getJoker() { return joker; }
    public boolean isSeleccionada() { return seleccionada; }
    public void setSeleccionada(boolean seleccionada) { this.seleccionada = seleccionada; }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public float getHandTargetX() {
        return this.handX;
    }

    @Override
    public float getAncho() {
        return this.width;
    }

    public float getHandTargetY() {
        return handY;
    }
}
