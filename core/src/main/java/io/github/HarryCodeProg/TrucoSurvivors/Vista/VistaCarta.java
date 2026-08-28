package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
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
    private float escala = 1f;
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
    private TextureRegion regionDorso; // el "back" del atlas, necesita seteo desde afuera o atlas guardado
    private static final float PAUSA_EN_DORSO = 0.15f;
    private float tiempoEnDorso = 0f;

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

    public void render(SpriteBatch batch, io.github.HarryCodeProg.TrucoSurvivors.Main game) {
        if (estadoFlip != EstadoFlip.NINGUNO) {
            renderFlip(batch);
            return;
        }
        float drawY = y + visualOffsetY;
        float scaleExtra = resaltado ? 1f + (float)(Math.sin(pulso) * 0.06f) : 1f;
        dibujarProfundidadYMarco(batch, game, drawY, scaleExtra);
        if (resaltado) {
            batch.setColor(1.25f, 1.15f, 0.6f, 1f);
        } else {
            batch.setColor(1f, 1f, 1f, 1f);
        }
        batch.draw(region, x, drawY, width / 2f, height / 2f, width * scaleExtra, height * scaleExtra, scale, scale, rotation);
        batch.setColor(1f, 1f, 1f, 1f);
        if (hover && !bocaAbajo && !dragging && carta != null) {
            dibujarCartelStats(batch, game, drawY);
        }
    }

    /** Feedback puramente visual: sombra, halo de hover y marco de selección. */
    private void dibujarProfundidadYMarco(SpriteBatch batch, io.github.HarryCodeProg.TrucoSurvivors.Main game,
                                          float drawY, float scaleExtra) {
        Texture pixel = game != null ? game.getPixelBlanco() : null;
        if (pixel == null || region == null) return;
        float ancho = width * scale * scaleExtra;
        float alto = height * scale * scaleExtra;
        float drawX = x + (width - ancho) / 2f;
        float baseY = drawY + (height - alto) / 2f;
        batch.setColor(0.01f, 0.01f, 0.02f, 0.50f);
        batch.draw(pixel, drawX + 4f, baseY - 5f, ancho, alto);
        if (hover || seleccionada || resaltado) {
            if (seleccionada || resaltado) batch.setColor(0.95f, 0.72f, 0.20f, 0.95f);
            else batch.setColor(0.25f, 0.88f, 0.76f, 0.85f);
            float grosor = seleccionada ? 3f : 2f;
            batch.draw(pixel, drawX - grosor, baseY - grosor, ancho + grosor * 2f, grosor);
            batch.draw(pixel, drawX - grosor, baseY + alto, ancho + grosor * 2f, grosor);
            batch.draw(pixel, drawX - grosor, baseY, grosor, alto);
            batch.draw(pixel, drawX + ancho, baseY, grosor, alto);
        }
        batch.setColor(1f, 1f, 1f, 1f);
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

    private void dibujarCartelStats(SpriteBatch batch, Main game, float drawY) {
        if (this.bocaAbajo) { return; }
        BitmapFont font = game.getFuentePrincipal();
        BitmapFont fontNumeros = game.getFuenteNumeros();
        GlyphLayout layout = new GlyphLayout();
        // --- 1. ACHICAMOS LA FUENTE Y ACTIVAMOS EL MARKUP ---
        float originalScaleX = font.getScaleX();
        float originalScaleY = font.getScaleY();
        boolean markupOriginal = font.getData().markupEnabled;
        font.getData().setScale(originalScaleX * 0.8f, originalScaleY * 0.8f);
        font.getData().markupEnabled = true;
        float escalaNumerosOriginal = fontNumeros.getScaleX();
        fontNumeros.getData().setScale(escalaNumerosOriginal * 0.75f);
        // --- 2. COLORES Y TEXTOS ---
        Color colorPTruco = Boton.TipoColor.TURQUESA.base;
        Color colorTruco = Boton.TipoColor.CIAN.base;
        Color colorPEnvido = Boton.TipoColor.BRONCE.base;
        Color colorEnvido = Boton.TipoColor.CAFE.base;
        Color grisClaro = new com.badlogic.gdx.graphics.Color(0.92f, 0.92f, 0.92f, 1f);
        // Armamos el título y lo pasamos por el filtro de colores (Ej: "1 de [#HEX]ESPADA[]")
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
        // --- 3. MEDIMOS LOS TEXTOS PARA EL TAMAÑO DE LA CAJA ---
        layout.setText(font, lineaNombre);
        float maxAnchoTexto = layout.width;
        maxAnchoTexto = Math.max(maxAnchoTexto, medirLineaConNumero(layout, font, fontNumeros, etiquetaValorTruco, numeroValorTruco));
        maxAnchoTexto = Math.max(maxAnchoTexto, medirLineaConNumero(layout, font, fontNumeros, etiquetaPuntosTruco, numeroPuntosTruco));
        maxAnchoTexto = Math.max(maxAnchoTexto, medirLineaConNumero(layout, font, fontNumeros, etiquetaValorEnvido, numeroValorEnvido));
        maxAnchoTexto = Math.max(maxAnchoTexto, medirLineaConNumero(layout, font, fontNumeros, etiquetaPuntosEnvido, numeroPuntosEnvido));
        // --- 4. DIMENSIONES Y POSICIONAMIENTO (Estilo Compacto) ---
        float paddingX = 14f;
        float paddingY = 12f;
        float altoLinea = font.getLineHeight() + 4f;
        float anchoCartel = maxAnchoTexto + (paddingX * 2);
        float altoCartel = (altoLinea * 5) + (paddingY * 2f);
        float actualWidth = width * scale;
        float actualHeight = height * scale;
        float cartelX = x + (actualWidth / 2f) - (anchoCartel / 2f);
        float cartelY = drawY + actualHeight + 12f;
        // Si se escapa por arriba de la pantalla, lo tiramos para abajo
        if (cartelY + altoCartel > com.badlogic.gdx.Gdx.graphics.getHeight() - 10f) {
            cartelY = drawY - altoCartel - 12f;
        }
        // --- 5. RENDER DEL FONDO (TEMA OSCURO) ---
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
            batch.setColor(1f, 1f, 1f, 1f);
        }
        // --- 6. RENDER DE TEXTOS ---
        float textoX = cartelX + paddingX;
        float currentY = cartelY + altoCartel - paddingY;
        // Renglón 1: Nombre (Centrado, el palo ya se pinta solo por el markup)
        layout.setText(font, lineaNombre);
        font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        font.draw(batch, lineaNombre, cartelX + (anchoCartel - layout.width) / 2f, currentY);
        currentY -= altoLinea;
        // Renglones de stats (Etiquetas en gris claro y los números mantienen su color de mecánica)
        dibujarLineaConNumeroColoreado(batch, font, fontNumeros, layout, etiquetaValorTruco, numeroValorTruco, textoX, currentY, grisClaro, colorTruco);
        currentY -= altoLinea;
        dibujarLineaConNumeroColoreado(batch, font, fontNumeros, layout, etiquetaPuntosTruco, numeroPuntosTruco, textoX, currentY, grisClaro, colorPTruco);
        currentY -= altoLinea;
        dibujarLineaConNumeroColoreado(batch, font, fontNumeros, layout, etiquetaValorEnvido, numeroValorEnvido, textoX, currentY, grisClaro, colorEnvido);
        currentY -= altoLinea;
        dibujarLineaConNumeroColoreado(batch, font, fontNumeros, layout, etiquetaPuntosEnvido, numeroPuntosEnvido, textoX, currentY, grisClaro, colorPEnvido);
        // --- 7. RESTAURAR ESTADOS ---
        font.getData().setScale(originalScaleX, originalScaleY);
        font.getData().markupEnabled = markupOriginal;
        font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        fontNumeros.getData().setScale(escalaNumerosOriginal);
        fontNumeros.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    }

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

    public void update(float mouseX, float mouseY, float delta) {
        if (estadoFlip != EstadoFlip.NINGUNO) {
            actualizarFlip(delta);
            return; // mientras flipea, no procesar hover/drag normal
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
            hover = contiene(mouseX, mouseY); // cada carta calcula su hover individual
        }
        float offsetHover = hover ? OFFSET_HOVER : 0f;
        float offsetSeleccion = seleccionada ? OFFSET_SELECCIONADA : 0f;
        targetScale = hover ? ESCALA_HOVER : 1f;
        targetOffsetY = offsetHover + offsetSeleccion;
        if (hover) {
            targetRotation = 0f;
        }
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
        // si no se encuentra, se mantiene la región anterior en vez de quedar en null (evita crash/corrupción visual)
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

    public void setResaltado(boolean resaltado) {
        this.resaltado = resaltado;
        if (resaltado) pulso = 0f;
    }

    public boolean isResaltado() { return resaltado; }

    public boolean estaEnZona(Rectangle rect) {
        return rect.contains(x + width / 2f, y + height / 2f);
    }

    public void setHandPosition(float x, float y) {
        this.handX = x;
        this.handY = y;
        this.targetX = x;
        this.targetY = y;
    }

    public void volverAMano() { targetX = handX; targetY = handY; }
    public void setTargetRotation(float rotation) { this.targetRotation = rotation; }
    public boolean isHover() { return hover; }

    public boolean contiene(float mx, float my) {
        float w = width * scale;
        float h = height * scale;
        float drawY = y + visualOffsetY;
        return mx >= x && mx <= x + w && my >= drawY && my <= drawY + h;
    }

    // Agregar en VistaCarta.java
    public void renderCartelStats(SpriteBatch batch, io.github.HarryCodeProg.TrucoSurvivors.Main game) {
        if (!hover || bocaAbajo || carta == null) return;
        float drawY = y + visualOffsetY;
        dibujarCartelStats(batch, game, drawY);
    }

    public void setEnModal(boolean enModal) { this.enModal = enModal; }

    /** Restringe targetX/targetY (mientras se arrastra) a un rectángulo. No hace nada si no está dragging. */
    public void clampArea(float minX, float minY, float maxX, float maxY) {
        if (!dragging) return;
        float maxTX = maxX - width;
        float maxTY = maxY - height;
        if (targetX < minX) targetX = minX;
        if (targetX > maxTX) targetX = maxTX;
        if (targetY < minY) targetY = minY;
        if (targetY > maxTY) targetY = maxTY;
    }

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

    /**
     * Inicia la animación de flip: gira a dorso, ejecuta onCargarNuevaVista (para actualizar la región
     * a la nueva carta), y gira de vuelta a frente. Igual patrón para cambio de palo o de número.
     */
    public void iniciarFlip(TextureRegion regionDorso, Runnable onCargarNuevaVista) {
        this.regionDorso = regionDorso;
        this.onCargarNuevaVista = onCargarNuevaVista;
        this.estadoFlip = EstadoFlip.GIRANDO_A_DORSO;
        this.flipProgreso = 0f;
    }

    public boolean isFlipeando() { return estadoFlip != EstadoFlip.NINGUNO; }
}
