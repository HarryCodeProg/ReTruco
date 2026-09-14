package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.ColorMecanica;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

public class VistaSanto implements Arrastrable {
    private Santo santo;
    private TextureRegion region;
    private float x, y, width = 70, height = 95;
    private boolean hover, dragging, draggingAnterior, seleccionada;
    private float handX, handY, targetX, targetY;
    private float visualOffsetY, targetOffsetY;
    private float rotation, targetRotation;
    private float scale = 1f, targetScale = 1f;
    private static boolean ALGUN_DRAG_ACTIVO = false;
    private static final float UMBRAL_CLICK = 6f;
    private static final float OFFSET_SELECCIONADA = 25f;
    private static final float OFFSET_HOVER = 8f;
    private static final float ESCALA_HOVER = 1.04f;
    private static final float VELOCIDAD_POSICION = 900f;
    private static final float VELOCIDAD_OFFSET = 250f;
    private static final float VELOCIDAD_ESCALA = 4f;
    private static final float VELOCIDAD_ROTACION = 360f;
    private float pressX, pressY, dragOffsetX, dragOffsetY;
    private boolean huboMovimientoSignificativo;
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

    public VistaSanto(Santo santo, TextureAtlas atlasSantos) {
        this.santo = santo;
        this.region = atlasSantos.findRegion(santo.getNombreRegion());
    }

    public Santo getSanto() { return santo; }
    public void setTamaño(float w, float h) { width = w; height = h; }
    public boolean isHover() { return hover; }
    public boolean isSeleccionada() { return seleccionada; }
    public void setSeleccionada(boolean s) { seleccionada = s; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setHandPosition(float x, float y) { handX = x; handY = y; targetX = x; targetY = y; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHandTargetX() { return targetX; }

    @Override
    public float getAncho() {
        return width;
    }

    public float getCentroX() { return x + (width * scale) / 2f; }

    public boolean contiene(float mx, float my) {
        float w = width * scale, h = height * scale;
        float drawY = y + visualOffsetY;
        return mx >= x && mx <= x + w && my >= drawY && my <= drawY + h;
    }

    @Override public boolean isDragging() { return dragging; }

    @Override
    public void update(float mouseX, float mouseY, float delta) {
        hover = !ALGUN_DRAG_ACTIVO && contiene(mouseX, mouseY);
        float offsetHover = hover ? OFFSET_HOVER : 0f;
        float offsetSel = seleccionada ? OFFSET_SELECCIONADA : 0f;
        targetScale = hover ? ESCALA_HOVER : 1f;
        targetOffsetY = offsetHover + offsetSel;
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
            x = targetX; y = targetY;
            scale = moverHacia(scale, 1.2f, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, 0f, VELOCIDAD_OFFSET * delta);
        }
    }

    private float moverHacia(float v, float t, float max) {
        float d = t - v;
        if (Math.abs(d) <= max) return t;
        return v + Math.signum(d) * max;
    }

    @Override
    public void input(float mouseX, float mouseY) {
        draggingAnterior = dragging;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && contiene(mouseX, mouseY)) {
            dragging = true; pressX = mouseX; pressY = mouseY;
            huboMovimientoSignificativo = false;
            dragOffsetX = mouseX - x; dragOffsetY = mouseY - y;
            ALGUN_DRAG_ACTIVO = true;
        }
        if (dragging) {
            float dx = mouseX - pressX, dy = mouseY - pressY;
            if (!huboMovimientoSignificativo && (Math.abs(dx) > UMBRAL_CLICK || Math.abs(dy) > UMBRAL_CLICK))
                huboMovimientoSignificativo = true;
        }
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (dragging && !huboMovimientoSignificativo) {
                seleccionada = !seleccionada;
                GestorSonidos s = Main.getInstance().getGestorSonidos();
                if (s != null) s.reproducirConVariacion(seleccionada ? "seleccionar" : "deseleccionar");
            }
            if (dragging) ALGUN_DRAG_ACTIVO = false;
            dragging = false;
        }
        if (dragging) { targetX = mouseX - dragOffsetX; targetY = mouseY - dragOffsetY; }
    }

    public void render(SpriteBatch batch) {
        float drawY = y + visualOffsetY;
        batch.flush();
        com.badlogic.gdx.math.Matrix4 matrixAnterior = batch.getTransformMatrix().cpy();
        if (tiltX != 0 || tiltY != 0) {
            com.badlogic.gdx.math.Matrix4 matrixTilt = new com.badlogic.gdx.math.Matrix4(matrixAnterior);
            float cx = x + (width * scale) / 2f;
            float cy = drawY + (height * scale) / 2f;
            matrixTilt.translate(cx, cy, -50f);
            matrixTilt.rotate(1, 0, 0, tiltX);
            matrixTilt.rotate(0, 1, 0, tiltY);
            matrixTilt.translate(-cx, -cy, 0f);
            batch.setTransformMatrix(matrixTilt);
        }
        Main game = Main.getInstance();
        // Marco, sombra y glow
        dibujarProfundidadYMarco(batch, game, drawY);
        // SANTO
        if (region != null) {
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(region, x, drawY, width / 2f, height / 2f, width, height, scale, scale, rotation);
        } else {
            Texture pixel = game != null ? game.getPixelBlanco() : null;
            if (pixel != null) {
                dibujarRectRedondeado(batch, pixel, x, drawY, width * scale, height * scale, RADIO_MARCO, new Color(0.12f, 0.10f, 0.18f, 1f));
                if (santo != null) {
                    com.badlogic.gdx.graphics.g2d.BitmapFont font = game.getFuentePrincipal();
                    font.setColor(UITheme.TEXTO_PRINCIPAL);
                    font.draw(batch, santo.getNombre(), x + 6f, drawY + height / 2f);
                    font.setColor(Color.WHITE);
                }
            }
        }
        batch.setColor(Color.WHITE);
        batch.flush();
        batch.setTransformMatrix(matrixAnterior);
        if (hover && !dragging && santo != null) {
            renderCartelStats(batch, game);
        }
    }

    private void dibujarProfundidadYMarco(SpriteBatch batch, Main game, float drawY) {
        // This remains unchanged for background visuals, only the tooltip changes
        if (game == null) return;
        Texture pixel = game.getPixelBlanco();
        if (pixel == null) return;
        float ancho = width * scale;
        float alto = height * scale;
        float drawX = x + (width - ancho) / 2f;
        float baseY = drawY + (height - alto) / 2f;
        Color colorSanto = UITheme.RAREZA_EPICO;
        // SOMBRA
        dibujarRectRedondeado(batch, pixel, drawX + 5f, baseY - 7f, ancho, alto, RADIO_MARCO + 2f, new Color(0f, 0f, 0f, 0.58f));
        // GLOW
        if (hover || seleccionada) {
            float intensidad = seleccionada ? 0.20f : 0.11f;
            dibujarRectRedondeado(batch, pixel, drawX - 7f, baseY - 7f, ancho + 14f, alto + 14f, RADIO_MARCO + 4f, new Color(colorSanto.r, colorSanto.g, colorSanto.b, intensidad));
            if (hover) {
                dibujarRectRedondeado(batch, pixel, drawX - 12f, baseY - 12f, ancho + 24f, alto + 24f, RADIO_MARCO + 7f, new Color(colorSanto.r, colorSanto.g, colorSanto.b, 0.045f));
            }
        }
        // COLOR DEL BORDE
        Color colorMarco;
        if (seleccionada) {
            colorMarco = UITheme.DORADO_BRILLANTE;
        } else if (hover) {
            colorMarco = new Color(colorSanto.r, colorSanto.g, colorSanto.b, 1f);
        } else {
            colorMarco = new Color(colorSanto.r * 0.72f, colorSanto.g * 0.72f, colorSanto.b * 0.72f, 0.95f);
        }
        // BORDE EXTERIOR
        dibujarRectRedondeado(batch, pixel, drawX - GROSOR_MARCO, baseY - GROSOR_MARCO, ancho + GROSOR_MARCO * 2f, alto + GROSOR_MARCO * 2f, RADIO_MARCO + 1f, colorMarco);
        // BORDE INTERIOR
        dibujarRectRedondeado(batch, pixel, drawX, baseY, ancho, alto, RADIO_MARCO, new Color(0.025f, 0.020f, 0.045f, 0.30f));
        // HIGHLIGHT SUPERIOR
        batch.setColor(1f, 1f, 1f, hover ? 0.24f : 0.10f);
        batch.draw(pixel, drawX + RADIO_MARCO, baseY + alto - 3f, ancho - RADIO_MARCO * 2f, 2f);
        // BRILLO LATERAL
        if (hover || seleccionada) {
            batch.setColor(colorMarco.r, colorMarco.g, colorMarco.b, 0.38f);
            batch.draw(pixel, drawX + 2f, baseY + RADIO_MARCO, 2f, alto - RADIO_MARCO * 2f);
        }
        batch.setColor(Color.WHITE);
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

    public void renderCartelStats(SpriteBatch batch, Main game) {
        if (!hover || santo == null || game == null) return;
        BitmapFont fontTitulo = game.getFuenteTooltipTitulo();
        BitmapFont fontDesc = game.getFuenteTooltipDescripcion();
        GlyphLayout layout = new GlyphLayout();
        // --- 1. ACTIVAMOS EL MARKUP ---
        boolean markupOriginalTitulo = fontTitulo.getData().markupEnabled;
        boolean markupOriginalDesc = fontDesc.getData().markupEnabled;
        fontTitulo.getData().markupEnabled = true;
        fontDesc.getData().markupEnabled = true;
        // --- 2. TEXTOS Y FILTROS ---
        String titulo = santo.getNombre();
        String descripcion = ColorMecanica.colorearTexto(Palo.colorearTexto(santo.getDescripcion()));
        // --- 3. ANCHO INTELIGENTE (Evita que las palabras se corten) ---
        float paddingX = 14f;
        float paddingY = 12f;
        float espacioVertical = 7f;
        float descPadX = 10f;
        float descPadY = 8f;
        float altoLinea = fontTitulo.getLineHeight();
        // A) Buscamos la palabra más larga del título para asegurar que nunca se rompa por la mitad
        float maxAnchoPalabra = 0f;
        String[] palabras = titulo.split(" ");
        for (String palabra : palabras) {
            layout.setText(fontTitulo, palabra);
            maxAnchoPalabra = Math.max(maxAnchoPalabra, layout.width);
        }
        // B) Definimos un ancho útil base (180f), pero si hay una palabra más larga, lo expandimos para que entre
        float anchoUtil = Math.max(180f, maxAnchoPalabra + 5f); // +5f de margen de seguridad
        // C) Si el título completo entra estirando la caja un poquito (hasta 240f), lo estiramos para que quede en 1 línea
        layout.setText(fontTitulo, titulo);
        if (layout.width > anchoUtil && layout.width <= 240f) {
            anchoUtil = layout.width + 5f;
        }
        // D) Medimos el título final (hará wrap automático si supera los 240f)
        layout.setText(fontTitulo, titulo, fontTitulo.getColor(), anchoUtil, com.badlogic.gdx.utils.Align.center, true);
        float altoTitulo = layout.height;
        // E) Medimos la descripción usando ese mismo ancho
        layout.setText(fontDesc, descripcion, fontDesc.getColor(), anchoUtil, com.badlogic.gdx.utils.Align.center, true);
        float altoDescripcion = layout.height;
        // Calculamos ancho y alto final del cartel
        float anchoCartel = anchoUtil + (paddingX * 2f);
        float altoCartel = paddingY + altoTitulo + espacioVertical + altoDescripcion + (descPadY * 2f) + paddingY;
        // --- 4. POSICIONAMIENTO INTELIGENTE ---
        float drawY = y + visualOffsetY;
        float actualWidth = width * scale;
        float actualHeight = height * scale;
        float cartelX, cartelY;
        if (tooltipLateral) {
            // Centrado verticalmente
            cartelY = drawY + (actualHeight / 2f) - (altoCartel / 2f);
            // Por defecto a la derecha
            cartelX = x + actualWidth + 12f;
            // Si choca con el borde derecho (columna 5), lo pasamos a la izquierda
            if (cartelX + anchoCartel > 1280f - 10f) {
                cartelX = x - anchoCartel - 12f;
            }
        } else {
            // Comportamiento normal (Arriba/Abajo)
            cartelX = x + (actualWidth / 2f) - (anchoCartel / 2f);
            cartelY = drawY + actualHeight + 12f;
            if (cartelY + altoCartel > 720f - 10f) {
                cartelY = drawY - altoCartel - 12f;
            }
        }
        // --- 5. RENDER DEL FONDO CON TOOLTIP UI ---
        com.badlogic.gdx.graphics.Texture pixelBlanco = game.getPixelBlanco();
        TooltipUI.dibujarFondo(batch, pixelBlanco, cartelX, cartelY, anchoCartel, altoCartel, UITheme.RAREZA_EPICO);
        // --- 6. RENDER DE TEXTOS ---
        float currentY = cartelY + altoCartel - paddingY;
        // Dibujamos el título (usando el ancho útil calculado)
        TooltipUI.dibujarTitulo(batch, fontTitulo, titulo, cartelX + paddingX, currentY, anchoUtil, UITheme.RAREZA_EPICO);
        currentY -= (altoTitulo + espacioVertical);
        // Caja blanca interna para la descripción
        float boxX = cartelX + paddingX - descPadX;
        float boxW = anchoCartel - (paddingX * 2f) + (descPadX * 2f);
        float boxTop = currentY + descPadY;
        float boxH = altoDescripcion + (descPadY * 2f);
        float boxY = boxTop - boxH;
        if (pixelBlanco != null) {
            TooltipUI.dibujarCajaBlancaInterna(batch, pixelBlanco, boxX, boxY, boxW, boxH);
        }
        // Dibujamos la descripción (letras oscuras)
        Color colorDescAnterior = fontDesc.getColor();
        fontDesc.setColor(0.08f, 0.08f, 0.08f, 1f);
        fontDesc.draw(batch, descripcion, cartelX + paddingX, currentY, anchoUtil, com.badlogic.gdx.utils.Align.center, true);
        // --- 7. RESTAURAR ESTADOS ---
        fontTitulo.getData().markupEnabled = markupOriginalTitulo;
        fontDesc.getData().markupEnabled = markupOriginalDesc;
        fontDesc.setColor(colorDescAnterior);
        batch.setColor(Color.WHITE);
    }

    public void setTooltipLateral(boolean lateral) { this.tooltipLateral = lateral; }

    public void limpiarHover() { this.hover = false; }

    public float getHeight() { return height; }

    public void dispose() {}
}
