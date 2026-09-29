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
    private static final float RADIO_MARCO = 8f;
    private static final float GROSOR_MARCO = 2f;
    private boolean tooltipLateral = false;
    private final FisicaTiltArrastre fisicaTiltArrastre = new FisicaTiltArrastre();

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
        float scaleExtra = resaltado ? 1f + (float)(Math.sin(pulso) * 0.06f) : 1f;
        Main game = Main.getInstance();
        // MATRIZ
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
        // MARCO / SOMBRA / GLOW
        dibujarProfundidadYMarco(batch, game, drawY, scaleExtra);
        // JOKER
        if (resaltado) {
            batch.setColor(1.08f, 1.02f, 0.82f, 1f);
        } else {
            batch.setColor(1f, 1f, 1f, 1f);
        }
        batch.draw(regionJoker, x, drawY, width / 2f, height / 2f, width * scaleExtra, height * scaleExtra, scale, scale, rotation);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.flush();
        batch.setTransformMatrix(matrixAnterior);
    }

    private void dibujarProfundidadYMarco(SpriteBatch batch, Main game, float drawY, float scaleExtra) {
        if (game == null || regionJoker == null) return;
        Texture pixel = game.getPixelBlanco();
        if (pixel == null) return;
        float ancho = width * scale * scaleExtra;
        float alto = height * scale * scaleExtra;
        float drawX = x + (width - ancho) / 2f;
        float baseY = drawY + (height - alto) / 2f;
        Color colorRareza = obtenerColorRareza();
        // SOMBRA
        dibujarRectRedondeado(batch, pixel, drawX + 5f, baseY - 7f, ancho, alto, RADIO_MARCO + 2f, new Color(0f, 0f, 0f, 0.58f));
        // GLOW SEGÚN RAREZA
        if (esRara()) {
            float intensidadGlow = obtenerIntensidadGlow();
            dibujarRectRedondeado(batch, pixel, drawX - 7f, baseY - 7f, ancho + 14f, alto + 14f, RADIO_MARCO + 4f, new Color(colorRareza.r, colorRareza.g, colorRareza.b, intensidadGlow));
            dibujarRectRedondeado(batch, pixel, drawX - 12f, baseY - 12f, ancho + 24f, alto + 24f, RADIO_MARCO + 7f, new Color(colorRareza.r, colorRareza.g, colorRareza.b, intensidadGlow * 0.40f));
        }
        // GLOW HOVER

        if (hover && !seleccionada) {
            dibujarRectRedondeado(batch, pixel, drawX - 5f, baseY - 5f, ancho + 10f, alto + 10f, RADIO_MARCO + 3f, new Color(colorRareza.r, colorRareza.g, colorRareza.b, 0.18f));
        }
        // COLOR DEL BORDE
        Color colorMarco;
        if (seleccionada || resaltado) {
            colorMarco = UITheme.DORADO_BRILLANTE;
        } else if (hover) {
            colorMarco = new Color(colorRareza.r, colorRareza.g, colorRareza.b, 1f);
        } else {
            colorMarco = new Color(colorRareza.r * 0.75f, colorRareza.g * 0.75f, colorRareza.b * 0.75f, 0.95f);
        }
        // BORDE EXTERIOR
        dibujarRectRedondeado(batch, pixel, drawX - GROSOR_MARCO, baseY - GROSOR_MARCO, ancho + GROSOR_MARCO * 2f, alto + GROSOR_MARCO * 2f, RADIO_MARCO + 1f, colorMarco);
        // BORDE INTERIOR
        dibujarRectRedondeado(batch, pixel, drawX, baseY, ancho, alto, RADIO_MARCO, new Color(0.015f, 0.022f, 0.035f, 0.35f));
        // HIGHLIGHT SUPERIOR
        batch.setColor(1f, 1f, 1f, hover ? 0.25f : 0.10f);
        batch.draw(pixel, drawX + RADIO_MARCO, baseY + alto - 3f, ancho - RADIO_MARCO * 2f, 2f);
        // BRILLO LATERAL
        if (hover || seleccionada || resaltado) {
            batch.setColor(colorMarco.r, colorMarco.g, colorMarco.b, 0.40f);
            batch.draw(pixel, drawX + 2f, baseY + RADIO_MARCO, 2f, alto - RADIO_MARCO * 2f);
        }
        batch.setColor(Color.WHITE);
    }

    private Color obtenerColorRareza() {
        if (joker == null || joker.getRareza() == null) return UITheme.RAREZA_COMUN;
        return UITheme.porNombreRareza(joker.getRareza().name());
    }

    private boolean esRara() {
        if (joker == null || joker.getRareza() == null) return false;
        String rareza = joker.getRareza().name().toLowerCase();
        return !rareza.contains("comun");
    }

    private void dibujarRectRedondeado(SpriteBatch batch, Texture pixel, float x, float y, float width, float height, float radio, Color color) {
        if (width <= 0f || height <= 0f) return;
        radio = Math.min(radio, Math.min(width, height) / 2f);
        batch.setColor(color);
        // Centro
        batch.draw(pixel, x + radio, y, width - radio * 2f, height);
        // Laterales
        batch.draw(pixel, x, y + radio, radio, height - radio * 2f);
        batch.draw(pixel, x + width - radio, y + radio, radio, height - radio * 2f);
        // Curvas
        int pasos = Math.max(2, (int) radio);
        for (int i = 0; i < pasos; i++) {
            float dy = i + 0.5f;
            float distancia = radio - dy;
            float raiz = (float) Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));
            float inset = radio - raiz;
            batch.draw(pixel, x + inset, y + i, width - inset * 2f, 1f);
            batch.draw(pixel, x + inset, y + height - i - 1f, width - inset * 2f, 1f);
        }
        batch.setColor(Color.WHITE);
    }

    private float obtenerIntensidadGlow() {
        if (joker == null || joker.getRareza() == null) return 0.05f;
        String rareza = joker.getRareza().name().toLowerCase();
        if (rareza.contains("legendario")) return 0.22f;
        if (rareza.contains("epico")) return 0.17f;
        if (rareza.contains("muy")) return 0.13f;
        if (rareza.contains("raro")) return 0.09f;
        return 0.05f;
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
            // --- NUEVO: Iniciamos la física de arrastre ---
            fisicaTiltArrastre.iniciarArrastre(mouseX);
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

    public void update(float mouseX, float mouseY, float delta) {
        if (resaltado) {
            pulso += delta * 6f;
            hover = false;
            return;
        }
        hover = !ALGUN_DRAG_ACTIVO && contiene(mouseX, mouseY);
        float offsetHover = hover ? OFFSET_HOVER : 0f;
        float offsetSeleccion = seleccionada ? OFFSET_SELECCIONADA : 0f;
        targetScale = hover ? ESCALA_HOVER : 1f;
        targetOffsetY = offsetHover + offsetSeleccion;
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
            rotation = moverHacia(rotation, 0f, VELOCIDAD_ROTACION * delta);
        } else {
            x = targetX;
            y = targetY;
            scale = moverHacia(scale, 1.2f, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, 0f, VELOCIDAD_OFFSET * delta);
        }
    }

    public void setResaltado(boolean resaltado) {
        this.resaltado = resaltado;
        if (resaltado) pulso = 0f;
    }

    private float moverHacia(float value, float target, float maxDelta) {
        float diferencia = target - value;
        if (Math.abs(diferencia) <= maxDelta) return target;
        return value + Math.signum(diferencia) * maxDelta;
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
        float hitboxX = x + (width - w) / 2f;
        float hitboxY = y + (height - h) / 2f;
        return mx >= hitboxX && mx <= hitboxX + w && my >= hitboxY && my <= hitboxY + h;
    }

    /*
    public boolean contiene(float mx, float my) {
        float w = width * scale;
        float h = height * scale;
        float drawY = y + visualOffsetY;
        return mx >= x && mx <= x + w && my >= drawY && my <= drawY + h;
    }*/

    private void dibujarCartelStats(SpriteBatch batch, Main game, Juego juego, float drawY) {
        BitmapFont fontTitulo = game.getFuenteTooltipTitulo();
        BitmapFont fontDesc = game.getFuenteTooltipDescripcion();
        GlyphLayout layout = new GlyphLayout();
        boolean markupOriginalTitulo = fontTitulo.getData().markupEnabled;
        boolean markupOriginalDesc = fontDesc.getData().markupEnabled;
        fontTitulo.getData().markupEnabled = true;
        fontDesc.getData().markupEnabled = true;
        String lineaNombre = joker.getNombre();
        // Usamos trim() para borrar cualquier \n sobrante que infle el alto de la caja
        String descripcionPura = joker.getDescripcionRenderizada(juego).trim();
        String descripcion = ColorMecanica.colorearTexto(Palo.colorearTexto(descripcionPura));
        String rarezaStr = joker.getRareza().toString().toUpperCase();
        float padEtiquetaX = 10f;
        float padEtiquetaY = 4f;
        // --- NUEVO SISTEMA DE ESPACIADO SECUENCIAL ---
        float gapTituloCaja = 6f;
        float descPadX = 10f;
        // 1. Aumentamos levemente el padding vertical de la caja
        float descPadY = 8f;
        float gapCajaBadges = 8f;
        float maxAnchoPalabra = 0f;
        String[] palabras = lineaNombre.split(" ");
        for (String palabra : palabras) {
            layout.setText(fontTitulo, palabra);
            maxAnchoPalabra = Math.max(maxAnchoPalabra, layout.width);
        }
        float anchoUtil = Math.max(180f, maxAnchoPalabra + 5f);
        layout.setText(fontTitulo, lineaNombre);
        if (layout.width > anchoUtil && layout.width <= 240f) {
            anchoUtil = layout.width + 5f;
        }
        float altoBadgeReal = fontDesc.getCapHeight() + (padEtiquetaY * 2f) + 10f;
        float espacioEntreBadges = 4f;
        float altoCategorias = 0;
        layout.setText(fontDesc, rarezaStr);
        anchoUtil = Math.max(anchoUtil, layout.width + (padEtiquetaX * 2f));
        for (CategoriaJoker cat : joker.getCategorias()) {
            layout.setText(fontDesc, cat.getTexto().toUpperCase());
            anchoUtil = Math.max(anchoUtil, layout.width + (padEtiquetaX * 2f));
            altoCategorias += espacioEntreBadges + altoBadgeReal;
        }
        layout.setText(fontTitulo, lineaNombre, fontTitulo.getColor(), anchoUtil, com.badlogic.gdx.utils.Align.center, true);
        float altoTitulo = layout.height;
        layout.setText(fontDesc, descripcion, fontDesc.getColor(), anchoUtil, com.badlogic.gdx.utils.Align.center, true);
        float altoDescripcion = layout.height;
        float paddingX = 14f;
        float paddingY = 12f;
        float altoCaja = altoDescripcion + (descPadY * 2f);
        float anchoCartel = anchoUtil + (paddingX * 2f);
        float altoCartel = paddingY + altoTitulo + gapTituloCaja + altoCaja + gapCajaBadges + altoBadgeReal + altoCategorias + paddingY;
        float actualWidth = width * scale;
        float actualHeight = height * scale;
        float cartelX, cartelY;
        if (tooltipLateral) {
            cartelY = drawY + (actualHeight / 2f) - (altoCartel / 2f);
            cartelX = x + actualWidth + 12f;
            if (cartelX + anchoCartel > 1280f - 10f) {
                cartelX = x - anchoCartel - 12f;
            }
        } else {
            cartelX = x + (actualWidth / 2f) - (anchoCartel / 2f);
            cartelY = drawY + actualHeight + 12f;
            if (cartelY + altoCartel > 720f - 10f) {
                cartelY = drawY - altoCartel - 12f;
            }
        }
        Color colorDeRareza = joker.getRareza().getColor();
        Texture pixelBlanco = game.getPixelBlanco();
        TooltipUI.dibujarFondo(batch, pixelBlanco, cartelX, cartelY, anchoCartel, altoCartel, colorDeRareza);
        // 1. DIBUJAMOS EL TÍTULO
        float currentY = cartelY + altoCartel - paddingY;
        TooltipUI.dibujarTitulo(batch, fontTitulo, lineaNombre, cartelX + paddingX, currentY, anchoUtil, colorDeRareza);
        currentY -= altoTitulo;
        currentY -= gapTituloCaja;
        // 2. DIBUJAMOS LA CAJA BLANCA
        float boxX = cartelX + paddingX - descPadX;
        float boxW = anchoCartel - (paddingX * 2f) + (descPadX * 2f);
        float boxTop = currentY;
        float boxH = altoCaja;
        float boxY = boxTop - boxH;
        if (pixelBlanco != null) {
            TooltipUI.dibujarCajaBlancaInterna(batch, pixelBlanco, boxX, boxY, boxW, boxH);
        }
        // 3. DIBUJAMOS EL TEXTO DE LA DESCRIPCIÓN
        Color colorDescAnterior = fontDesc.getColor();
        fontDesc.setColor(0.08f, 0.08f, 0.08f, 1f);
        // Alineación vertical exacta, removiendo el offset arbitrario de -3f
        float textY = boxTop - descPadY + 2f;
        fontDesc.draw(batch, descripcion, cartelX + paddingX, textY, anchoUtil, com.badlogic.gdx.utils.Align.center, true); // 4. DIBUJAMOS LOS BADGES
        currentY -= boxH;
        currentY -= gapCajaBadges;
        float centroCartelX = cartelX + (anchoCartel / 2f);
        currentY = TooltipUI.dibujarBadge(batch, fontDesc, pixelBlanco, rarezaStr, colorDeRareza, centroCartelX, currentY, padEtiquetaX, padEtiquetaY);
        for (CategoriaJoker cat : joker.getCategorias()) {
            currentY -= espacioEntreBadges;
            currentY = TooltipUI.dibujarBadge(batch, fontDesc, pixelBlanco, cat.getTexto().toUpperCase(), cat.getColor(), centroCartelX, currentY, padEtiquetaX, padEtiquetaY);
        }
        fontTitulo.getData().markupEnabled = markupOriginalTitulo;
        fontDesc.getData().markupEnabled = markupOriginalDesc;
        fontDesc.setColor(colorDescAnterior);
        batch.setColor(Color.WHITE);
    }

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

    public float getYConOffset() {
        return y + visualOffsetY;
    }

    public void limpiarHover() { this.hover = false; }

    public void setTooltipLateral(boolean lateral) { this.tooltipLateral = lateral; }
}
