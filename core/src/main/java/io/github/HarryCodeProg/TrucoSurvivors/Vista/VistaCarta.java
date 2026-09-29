package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Main;

public class VistaCarta implements Arrastrable{
    private Carta carta;
    private TextureRegion region;
    private float x;
    private float y;
    private float width = 120;
    private float height = 180;
    private boolean bocaAbajo;
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
    private static boolean ALGUN_DRAG_ACTIVO = false;
    private static final float UMBRAL_CLICK = 6f;
    private static final float OFFSET_SELECCIONADA = 30f;
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
    private boolean animando = false;
    private float animTargetX;
    private float animTargetY;
    private float velocidadAnim = 1400f;
    private Runnable accionAlTerminar;
    private boolean enModal = false;
    private boolean resaltado = false;
    private float pulso = 0f;
    // --- nuevos campos en VistaCarta ---
    private enum EstadoFlip { NINGUNO, GIRANDO_A_DORSO, MOSTRANDO_DORSO, GIRANDO_A_FRENTE }
    private EstadoFlip estadoFlip = EstadoFlip.NINGUNO;
    private float flipProgreso = 0f; // 0 a 1
    private static final float DURACION_MEDIO_FLIP = 0.25f;
    private Runnable onCargarNuevaVista; // callback: aplicar nuevo palo/número/región mientras está de dorso
    private Runnable alTerminarFlip;
    private TextureRegion regionDorso; // el "back" del atlas, necesita seteo desde afuera o atlas guardado
    private static final float PAUSA_EN_DORSO = 0.15f;
    private float tiempoEnDorso = 0f;
    // --- Variables para el efecto Balatro Tilt ---
    private float tiltX = 0f;
    private float tiltY = 0f;
    private float targetTiltX = 0f;
    private float targetTiltY = 0f;
    private static final float MAX_TILT = 25f; // Grados máximos de inclinación
    private static final float VELOCIDAD_TILT = 150f;
    private static final float RADIO_MARCO = 8f;
    private static final float GROSOR_MARCO = 2f;
    private boolean cartelHaciaAbajo = false;
    private boolean tooltipLateral = false;
    private final FisicaTiltArrastre fisicaTiltArrastre = new FisicaTiltArrastre();

    /** Ahora recibe el TextureAtlas compartido en vez de crear su propia Texture. */
    public VistaCarta(Carta carta, boolean bocaAbajo, TextureAtlas atlas) {
        this.carta = carta;
        this.x = 0;
        this.y = 0;
        this.targetX = 0;
        this.targetY = 0;
        this.bocaAbajo = bocaAbajo;
        if (bocaAbajo) {
            this.region = atlas.findRegion("back");
        } else {
            this.region = atlas.findRegion(carta.getNombreRegion());
        }
    }

    public void render(SpriteBatch batch, Main game) {
        if (estadoFlip != EstadoFlip.NINGUNO) {
            renderFlip(batch);
            return;
        }
        float drawY = y + visualOffsetY;
        float scaleExtra = resaltado ? 1f + (float)(Math.sin(pulso) * 0.06f) : 1f;
        batch.flush();
        Matrix4 matrixAnterior = batch.getTransformMatrix().cpy();
        Matrix4 matrixModificada = new Matrix4(matrixAnterior);
        float cx = x + (width * scaleExtra * scale) / 2f;
        float cy = drawY + (height * scaleExtra * scale) / 2f;
        matrixModificada.translate(cx, cy, -50f); // Empujamos en Z
        // Efecto 3D (Hover)
        if (tiltX != 0) matrixModificada.rotate(1, 0, 0, tiltX);
        if (tiltY != 0) matrixModificada.rotate(0, 1, 0, tiltY);
        // Efecto Péndulo 2D (Drag)
        if (rotation != 0) matrixModificada.rotate(0, 0, 1, rotation);
        matrixModificada.translate(-cx, -cy, 0f); // Volvemos
        batch.setTransformMatrix(matrixModificada);
        // 1. Dibujamos la sombra y el marco (¡Ahora van a girar junto con la cámara!)
        dibujarProfundidadYMarco(batch, game, drawY, scaleExtra);
        // 2. Dibujamos la carta
        batch.setColor(1f, 1f, 1f, 1f);
        // FIX: Le pasamos 0f a la rotación acá, porque la rotación ya está en la matriz!
        batch.draw(region, x, drawY, width / 2f, height / 2f, width * scaleExtra, height * scaleExtra, scale, scale, 0f);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.flush();
        batch.setTransformMatrix(matrixAnterior); // Restauramos la cámara
    }

    private void dibujarCartelStats(SpriteBatch batch, Main game, float drawY) {
        if (this.bocaAbajo) { return; }
        BitmapFont fontTitulo = game.getFuenteTooltipTitulo();
        BitmapFont fontDesc = game.getFuenteTooltipDescripcion();
        BitmapFont fontNumeros = game.getFuenteNumeros();
        GlyphLayout layout = new GlyphLayout();
        // --- 1. ACTIVAMOS EL MARKUP (Sin escalar las fuentes de texto) ---
        boolean markupOriginalTitulo = fontTitulo.getData().markupEnabled;
        boolean markupOriginalDesc = fontDesc.getData().markupEnabled;
        fontTitulo.getData().markupEnabled = true;
        fontDesc.getData().markupEnabled = true;
        float escalaNumerosOriginal = fontNumeros.getScaleX();
        fontNumeros.getData().setScale(escalaNumerosOriginal * 0.75f);
        // --- 2. COLORES Y TEXTOS ---
        Color colorPTruco = Boton.TipoColor.TURQUESA.base;
        Color colorTruco = Boton.TipoColor.CIAN.base;
        Color colorPEnvido = Boton.TipoColor.BRONCE.base;
        Color colorEnvido = Boton.TipoColor.CAFE.base;
        Color grisOscuro = new Color(0.15f, 0.15f, 0.17f, 1f);
        String lineaNombrePura = carta.getNumero() + " de " + carta.paloToString();
        String lineaNombre = io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo.colorearTexto(lineaNombrePura);
        String etiquetaValorTruco = "Valor Truco: ";
        String numeroValorTruco = String.valueOf((int) carta.getValorTrucoEfectivo());
        String etiquetaPuntosTruco = "Puntos Truco: ";
        String numeroPuntosTruco = String.valueOf((int) carta.getPuntosTrucoAporteEfectivo());
        String etiquetaValorEnvido = "Valor Envido: ";
        String numeroValorEnvido = String.valueOf((int) carta.getValorEnvidoEfectivo());
        String etiquetaPuntosEnvido = "Puntos Envido: ";
        String numeroPuntosEnvido = String.valueOf((int) carta.getPuntosEnvidoAporteEfectivo());
        // --- 3. MEDIMOS LOS TEXTOS ---
        layout.setText(fontTitulo, lineaNombre);
        float anchoNombre = layout.width;
        float maxAnchoTexto = anchoNombre;
        maxAnchoTexto = Math.max(maxAnchoTexto, medirLineaConNumero(layout, fontDesc, fontNumeros, etiquetaValorTruco, numeroValorTruco));
        maxAnchoTexto = Math.max(maxAnchoTexto, medirLineaConNumero(layout, fontDesc, fontNumeros, etiquetaPuntosTruco, numeroPuntosTruco));
        maxAnchoTexto = Math.max(maxAnchoTexto, medirLineaConNumero(layout, fontDesc, fontNumeros, etiquetaValorEnvido, numeroValorEnvido));
        maxAnchoTexto = Math.max(maxAnchoTexto, medirLineaConNumero(layout, fontDesc, fontNumeros, etiquetaPuntosEnvido, numeroPuntosEnvido));
        // --- 4. DIMENSIONES ---
        float paddingX = 14f;
        float paddingY = 12f;
        float espacioVertical = 7f;
        float descPadX = 10f;
        float descPadY = 8f;
        float altoLineaTitulo = fontTitulo.getLineHeight() + 4f;
        float altoLineaDesc = fontDesc.getLineHeight() + 4f;
        float anchoCartel = maxAnchoTexto + (paddingX * 2f);
        float altoBloqueStats = altoLineaDesc * 4f;
        float altoCartel = paddingY + altoLineaTitulo + espacioVertical + altoBloqueStats + (descPadY * 2f) + paddingY;
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
            cartelY = cartelHaciaAbajo ? drawY - altoCartel - 12f : drawY + actualHeight + 12f;
            if (!cartelHaciaAbajo && cartelY + altoCartel > 720f - 10f) {
                cartelY = drawY - altoCartel - 12f;
            }
        }
        // --- 5. RENDER DEL FONDO ---
        Texture pixelBlanco = game.getPixelBlanco();
        TooltipUI.dibujarFondo(batch, pixelBlanco, cartelX, cartelY, anchoCartel, altoCartel, UITheme.VERDE);
        // --- 6. RENDER DE TEXTOS ---
        float currentY = cartelY + altoCartel - paddingY;
        // Se le pasa Color.WHITE porque el markup de la carta ya se encarga de darle los colores a los palos
        TooltipUI.dibujarTitulo(batch, fontTitulo, lineaNombre, cartelX, currentY, anchoCartel, Color.WHITE);
        currentY -= altoLineaTitulo;
        currentY -= espacioVertical;
        // Caja blanca interna
        float boxX = cartelX + paddingX - descPadX;
        float boxW = (anchoCartel - paddingX * 2f) + (descPadX * 2f);
        float boxTop = currentY + descPadY;
        float boxH = altoBloqueStats + (descPadY * 2f);
        float boxY = boxTop - boxH;
        if (pixelBlanco != null) {
            TooltipUI.dibujarCajaBlancaInterna(batch, pixelBlanco, boxX, boxY, boxW, boxH);
        }
        float textoX = cartelX + paddingX;
        dibujarLineaConNumeroColoreado(batch, fontDesc, fontNumeros, layout, etiquetaValorTruco, numeroValorTruco, textoX, currentY, grisOscuro, colorTruco);
        currentY -= altoLineaDesc;
        dibujarLineaConNumeroColoreado(batch, fontDesc, fontNumeros, layout, etiquetaPuntosTruco, numeroPuntosTruco, textoX, currentY, grisOscuro, colorPTruco);
        currentY -= altoLineaDesc;
        dibujarLineaConNumeroColoreado(batch, fontDesc, fontNumeros, layout, etiquetaValorEnvido, numeroValorEnvido, textoX, currentY, grisOscuro, colorEnvido);
        currentY -= altoLineaDesc;
        dibujarLineaConNumeroColoreado(batch, fontDesc, fontNumeros, layout, etiquetaPuntosEnvido, numeroPuntosEnvido, textoX, currentY, grisOscuro, colorPEnvido);
        // --- 7. RESTAURAR ESTADOS ---
        fontTitulo.getData().markupEnabled = markupOriginalTitulo;
        fontDesc.getData().markupEnabled = markupOriginalDesc;
        fontNumeros.getData().setScale(escalaNumerosOriginal);
        fontNumeros.setColor(Color.WHITE);
    }

    public void setCartelHaciaAbajo(boolean haciaAbajo) { this.cartelHaciaAbajo = haciaAbajo; }

    /** Feedback puramente visual: sombra, halo de hover y marco de selección. */
    private void dibujarProfundidadYMarco(SpriteBatch batch, Main game, float drawY, float scaleExtra) {
        Texture pixel = game != null ? game.getPixelBlanco() : null;
        if (pixel == null || region == null) return;
        float ancho = width * scale * scaleExtra;
        float alto = height * scale * scaleExtra;
        float drawX = x + (width - ancho) / 2f;
        float baseY = drawY + (height - alto) / 2f;
        // SOMBRA PROFUNDA
        dibujarRectRedondeado(batch, pixel, drawX + 5f, baseY - 7f, ancho, alto, RADIO_MARCO + 1f, new Color(0f, 0f, 0f, 0.55f));
        // Segunda sombra más suave
        dibujarRectRedondeado(batch, pixel, drawX + 2f, baseY - 3f, ancho, alto, RADIO_MARCO, new Color(0f, 0f, 0f, 0.28f));
        // GLOW
        if (hover || seleccionada || resaltado) {
            Color colorGlow;
            if (seleccionada || resaltado) {
                colorGlow = new Color(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.16f);
            } else {
                colorGlow = new Color(UITheme.VERDE.r, UITheme.VERDE.g, UITheme.VERDE.b, 0.10f);
            }
            dibujarRectRedondeado(batch, pixel, drawX - 6f, baseY - 6f, ancho + 12f, alto + 12f, RADIO_MARCO + 3f, colorGlow);
            if (hover) {
                dibujarRectRedondeado(batch, pixel, drawX - 10f, baseY - 10f, ancho + 20f, alto + 20f, RADIO_MARCO + 5f, new Color(colorGlow.r, colorGlow.g, colorGlow.b, 0.055f));
            }
        }
        // BORDE EXTERIOR
        Color colorMarco;
        if (seleccionada || resaltado) {
            colorMarco = new Color(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.95f);
        } else if (hover) {
            colorMarco = new Color(UITheme.VERDE_HOVER.r, UITheme.VERDE_HOVER.g, UITheme.VERDE_HOVER.b, 0.90f);
        } else {
            colorMarco = new Color(UITheme.BORDE.r, UITheme.BORDE.g, UITheme.BORDE.b, 0.95f);
        }
        dibujarRectRedondeado(batch, pixel, drawX - GROSOR_MARCO, baseY - GROSOR_MARCO, ancho + GROSOR_MARCO * 2f, alto + GROSOR_MARCO * 2f, RADIO_MARCO + 1f, colorMarco);
        // MARCO INTERIOR OSCURO
        dibujarRectRedondeado(batch, pixel, drawX, baseY, ancho, alto, RADIO_MARCO, new Color(0.025f, 0.035f, 0.050f, 0.45f));
        // HIGHLIGHT SUPERIOR
        batch.setColor(1f, 1f, 1f, hover ? 0.24f : 0.10f);
        batch.draw(pixel, drawX + RADIO_MARCO, baseY + alto - 3f, ancho - RADIO_MARCO * 2f, 2f);
        // PEQUEÑO BRILLO LATERAL
        if (hover || seleccionada) {
            batch.setColor(colorMarco.r, colorMarco.g, colorMarco.b, 0.35f);
            batch.draw(pixel, drawX + 2f, baseY + RADIO_MARCO, 2f, alto - RADIO_MARCO * 2f);
        }
        batch.setColor(1f, 1f, 1f, 1f);
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

    private void renderFlip(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, 1f);
        float drawY = y + visualOffsetY;
        TextureRegion regionAMostrar = (estadoFlip == EstadoFlip.GIRANDO_A_DORSO) ? this.region : regionDorso;
        if (estadoFlip == EstadoFlip.GIRANDO_A_FRENTE) regionAMostrar = this.region;
        float progresoClamp = Math.min(flipProgreso, 1f);
        float scaleXFlip = (estadoFlip == EstadoFlip.GIRANDO_A_DORSO) ? (1f - progresoClamp) : progresoClamp;
        scaleXFlip = Math.max(scaleXFlip, 0.02f);
        batch.draw(regionAMostrar, x, drawY, width / 2f, height / 2f, width * scaleXFlip, height, 1f, 1f, 0f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void animarHacia(float destX, float destY, Runnable alTerminar) {
        this.animando = true;
        this.animTargetX = destX;
        this.animTargetY = destY;
        this.accionAlTerminar = alTerminar;
    }

    public boolean isAnimando() { return animando; }

    /** Dibuja "etiqueta" en colorEtiqueta seguido de "numero" en colorNumero, en la misma linea. */
    private float medirLineaConNumero(com.badlogic.gdx.graphics.g2d.GlyphLayout layout, BitmapFont font,
                                      BitmapFont fontNumeros, String etiqueta, String numero) {
        layout.setText(font, etiqueta);
        float anchoEtiqueta = layout.width;
        layout.setText(fontNumeros, numero);
        return anchoEtiqueta + layout.width;
    }

    private void dibujarLineaConNumeroColoreado(SpriteBatch batch, com.badlogic.gdx.graphics.g2d.BitmapFont font,
                                                com.badlogic.gdx.graphics.g2d.BitmapFont fontNumeros, com.badlogic.gdx.graphics.g2d.GlyphLayout layout, String etiqueta, String numero,
                                                float x, float y, com.badlogic.gdx.graphics.Color colorEtiqueta, com.badlogic.gdx.graphics.Color colorNumero) {
        font.setColor(colorEtiqueta);
        font.draw(batch, etiqueta, x, y);
        layout.setText(font, etiqueta);
        float anchoEtiqueta = layout.width;
        fontNumeros.setColor(colorNumero);
        fontNumeros.draw(batch, numero, x + anchoEtiqueta, y);
    }

    /** Ya no hace falta disponer nada: la textura del atlas es compartida y se dispone una sola vez en Main. */
    public void dispose() {
        // no-op — se deja el metodo para no romper los llamados existentes en GameScreen.dispose()
    }

    /** Cambiar boca abajo ahora solo cambia la region, no crea/destruye texturas. */
    public void cambiarBocaArriba(TextureAtlas atlas){
        this.bocaAbajo = false;
        this.region = atlas.findRegion(carta.getNombreRegion());
    }

    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setTamaño(float width, float height) { this.width = width; this.height = height; }
    public boolean isDragging() { return this.dragging; }
    public boolean soltoCarta() { return draggingAnterior && !dragging; }

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
                    if (seleccionada) sonidos.reproducirConVariacion("seleccionar");
                    else sonidos.reproducirConVariacion("deseleccionar");
                }
            }
            if (dragging) ALGUN_DRAG_ACTIVO = false;
            dragging = false;
        }
        if (dragging) {
            targetX = mouseX - dragOffsetX;
            targetY = mouseY - dragOffsetY;
        }
    }

    public void update(float mouseX, float mouseY, float delta) {
        if (estadoFlip != EstadoFlip.NINGUNO) {
            actualizarFlip(delta);
            return;
        }
        if (resaltado) {
            pulso += delta * 6f;
        }
        if (animando) {
            hover = false;
            dragging = false;
            targetScale = 0.8f;
            targetRotation = 45f;
            x = moverHacia(x, animTargetX, velocidadAnim * delta);
            y = moverHacia(y, animTargetY, velocidadAnim * delta);
            scale = moverHacia(scale, targetScale, 5f * delta);
            rotation = moverHacia(rotation, targetRotation, 500f * delta);
            visualOffsetY = moverHacia(visualOffsetY, 0f, 10f * delta);
            if (Math.abs(x - animTargetX) < 2f && Math.abs(y - animTargetY) < 2f) {
                animando = false;
                if (accionAlTerminar != null) {
                    accionAlTerminar.run();
                }
            }
            return;
        }
        if (!enModal) {
            if (!ALGUN_DRAG_ACTIVO) {
                hover = contiene(mouseX, mouseY);
            } else {
                hover = false;
            }
        } else {
            hover = contiene(mouseX, mouseY);
        }
        float offsetHover = hover ? OFFSET_HOVER : 0f;
        float offsetSeleccion = seleccionada ? OFFSET_SELECCIONADA : 0f;
        targetScale = hover ? ESCALA_HOVER : 1f;
        targetOffsetY = offsetHover + offsetSeleccion;
        // --- LÓGICA SEPARADA: ARRASTRE (Rotación 2D Z) vs HOVER (Inclinación 3D X/Y) ---
        if (dragging) {
            fisicaTiltArrastre.actualizarArrastre(mouseX, delta);
            targetRotation = fisicaTiltArrastre.getAngulo(); // El péndulo usa Z (Rotation normal)
            targetTiltX = 0f;
            targetTiltY = 0f; // Apagamos el 3D al arrastrar
        } else {
            fisicaTiltArrastre.actualizarSoltada(delta);
            if (fisicaTiltArrastre.estaActiva()) {
                targetRotation = fisicaTiltArrastre.getAngulo();
                targetTiltX = 0f;
                targetTiltY = 0f;
            } else if (hover && !animando) {
                targetRotation = 0f;
                float cx = x + (width * scale) / 2f;
                float cy = y + visualOffsetY + (height * scale) / 2f;
                float mouseDeltaX = (mouseX - cx) / ((width * scale) / 2f);
                float mouseDeltaY = (mouseY - cy) / ((height * scale) / 2f);
                mouseDeltaX = Math.max(-1f, Math.min(1f, mouseDeltaX));
                mouseDeltaY = Math.max(-1f, Math.min(1f, mouseDeltaY));
                targetTiltY = mouseDeltaX * MAX_TILT;  // Inclinación 3D
                targetTiltX = -mouseDeltaY * MAX_TILT; // Inclinación 3D
            } else {
                targetRotation = 0f;
                targetTiltX = 0f;
                targetTiltY = 0f;
            }
        }
        // Aplicamos suavizado al 3D
        tiltX = moverHacia(tiltX, targetTiltX, VELOCIDAD_TILT * delta);
        tiltY = moverHacia(tiltY, targetTiltY, VELOCIDAD_TILT * delta);
        // Aplicamos movimiento
        if (!dragging) {
            x = moverHacia(x, targetX, VELOCIDAD_POSICION * delta);
            y = moverHacia(y, targetY, VELOCIDAD_POSICION * delta);
            scale = moverHacia(scale, targetScale, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, targetOffsetY, VELOCIDAD_OFFSET * delta);
            // Si la física está soltándose, ya viene suavizada. Si no, suavizamos manual.
            if (fisicaTiltArrastre.estaActiva()) {
                rotation = targetRotation;
            } else {
                rotation = moverHacia(rotation, targetRotation, VELOCIDAD_ROTACION * delta);
            }
        } else {
            x = targetX;
            y = targetY;
            scale = moverHacia(scale, 1.2f, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, 0f, VELOCIDAD_OFFSET * delta);
            // FIX: Durante el arrastre, la rotación copia directamente a la física (que ya tiene inercia)
            rotation = targetRotation;
        }
    }

    private void actualizarFlip(float delta) {
        flipProgreso += delta / DURACION_MEDIO_FLIP;
        switch (estadoFlip) {
            case GIRANDO_A_DORSO:
                if (flipProgreso >= 1f) {
                    flipProgreso = 0f;
                    estadoFlip = EstadoFlip.MOSTRANDO_DORSO;
                    tiempoEnDorso = 0f;
                    if (onCargarNuevaVista != null) { onCargarNuevaVista.run(); onCargarNuevaVista = null; }
                }
                break;
            case MOSTRANDO_DORSO:
                tiempoEnDorso += delta;
                if (tiempoEnDorso >= PAUSA_EN_DORSO) {
                    estadoFlip = EstadoFlip.GIRANDO_A_FRENTE;
                    flipProgreso = 0f;
                }
                break;
            case GIRANDO_A_FRENTE:
                if (flipProgreso >= 1f) {
                    flipProgreso = 1f;
                    estadoFlip = EstadoFlip.NINGUNO; // termina el flip, vuelve al render normal
                    if (alTerminarFlip != null) {
                        Runnable callback = alTerminarFlip;
                        alTerminarFlip = null;
                        callback.run();
                    }
                }
                break;
            default:
                break;
        }
    }

    public void actualizarRegionDesdeCarta(TextureAtlas atlas) {
        TextureRegion nueva = bocaAbajo ? atlas.findRegion("back") : atlas.findRegion(carta.getNombreRegion());
        if (nueva != null) {
            this.region = nueva;
        }
    }

    private float moverHacia(float value, float target, float maxDelta) {
        float diferencia = target - value;
        if (Math.abs(diferencia) <= maxDelta) return target;
        return value + Math.signum(diferencia) * maxDelta;
    }

    public void setResaltado(boolean resaltado) {
        this.resaltado = resaltado;
        if (resaltado) pulso = 0f;
    }

    public void setHandPosition(float x, float y) {
        this.handX = x;
        this.handY = y;
        this.targetX = x;
        this.targetY = y;
    }

    public void setTargetRotation(float rotation) { this.targetRotation = rotation; }
    public boolean isHover() { return hover; }

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

    // Agregar en VistaCarta.java
    public void renderCartelStats(SpriteBatch batch, io.github.HarryCodeProg.TrucoSurvivors.Main game) {
        if (!hover || bocaAbajo || carta == null) return;
        float drawY = y + visualOffsetY;
        dibujarCartelStats(batch, game, drawY);
    }

    public void setEnModal(boolean enModal) { this.enModal = enModal; }

    public boolean llegoATarget() {
        return Math.abs(x - targetX) < 1f && Math.abs(y - targetY) < 1f;
    }

    public float getCentroX() { return x + (width * scale) / 2f; }
    public float getCentroY() { return y + (height * scale) / 2f; }
    public float getX() { return x; }
    public float getY() { return y; }
    public Carta getCarta() { return carta; }
    public boolean isSeleccionada() { return seleccionada; }
    public void setSeleccionada(boolean seleccionada) { this.seleccionada = seleccionada; }
    public float getAlto() { return height; }
    @Override
    public float getHandTargetX() {
        return this.targetX;
    }

    @Override
    public float getAncho() {
        return this.width;
    }

    public void iniciarFlip(TextureRegion regionDorso, Runnable onCargarNuevaVista) {
        iniciarFlip(regionDorso, onCargarNuevaVista, null);
    }

    /** Inicia el giro y avisa cuando la carta vuelve a mostrarse de frente. */
    public void iniciarFlip(TextureRegion regionDorso, Runnable onCargarNuevaVista, Runnable alTerminarFlip) {
        this.regionDorso = regionDorso;
        this.onCargarNuevaVista = onCargarNuevaVista;
        this.alTerminarFlip = alTerminarFlip;
        this.estadoFlip = EstadoFlip.GIRANDO_A_DORSO;
        this.flipProgreso = 0f;
    }

    public boolean isFlipeando() { return estadoFlip != EstadoFlip.NINGUNO; }

    public float getYConOffset() {
        return y + visualOffsetY;
    }

    public void limpiarHover() { this.hover = false; }

    public void setTooltipLateral(boolean lateral) { this.tooltipLateral = lateral; }
}
