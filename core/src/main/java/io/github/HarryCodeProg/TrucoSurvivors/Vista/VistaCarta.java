package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
        float drawY = y + visualOffsetY;
        float scaleExtra = resaltado ? 1f + (float)(Math.sin(pulso) * 0.06f) : 1f;
        if (resaltado) {
            batch.setColor(1.25f, 1.15f, 0.6f, 1f); // tinte dorado, igual criterio que VistaJoker
        }
        batch.draw(region, x, drawY, width / 2f, height / 2f, width * scaleExtra, height * scaleExtra, scale, scale, rotation);
        if (resaltado) {
            batch.setColor(1f, 1f, 1f, 1f);
        }
        if (hover && !bocaAbajo && !dragging && carta != null) {
            dibujarCartelStats(batch, game, drawY);
        }
    }

    public void animarHacia(float destX, float destY, Runnable alTerminar) {
        this.animando = true;
        this.animTargetX = destX;
        this.animTargetY = destY;
        this.accionAlTerminar = alTerminar;
    }

    public boolean isAnimando() { return animando; }

    private void dibujarCartelStats(SpriteBatch batch, io.github.HarryCodeProg.TrucoSurvivors.Main game, float drawY) {
        if (this.bocaAbajo){return;}
        com.badlogic.gdx.graphics.g2d.BitmapFont font = game.getFuentePrincipal();
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        com.badlogic.gdx.graphics.Color colorPTruco = Boton.TipoColor.TURQUESA.base;
        com.badlogic.gdx.graphics.Color colorTruco = Boton.TipoColor.CIAN.base;
        com.badlogic.gdx.graphics.Color colorPEnvido = Boton.TipoColor.BRONCE.base;
        com.badlogic.gdx.graphics.Color colorEnvido = Boton.TipoColor.CAFE.base;
        com.badlogic.gdx.graphics.Color blanco = com.badlogic.gdx.graphics.Color.WHITE;
        String lineaNombre = carta.getNumero() + " de " + carta.paloToString();
        double valorTrucoReal = carta.getValorTrucoActual();
        double valorTrucoEfectivo = carta.getValorTrucoEfectivo();
        double valorEnvidoReal = carta.getValorEnvidoActual();
        double valorEnvidoEfectivo = carta.getValorEnvidoEfectivo();
        String etiquetaValorTruco = "Valor Truco: ";
        String numeroValorTruco = String.valueOf((int) valorTrucoReal);
        String etiquetaPuntosTruco = "Puntos Truco: ";
        String numeroPuntosTruco = String.valueOf((int) valorTrucoEfectivo);
        String etiquetaValorEnvido = "Valor Envido: ";
        String numeroValorEnvido = String.valueOf((int) valorEnvidoReal);
        String etiquetaPuntosEnvido = "Puntos Envido: ";
        String numeroPuntosEnvido = String.valueOf((int) valorEnvidoEfectivo);
        // Medimos el ancho maximo necesario entre todas las lineas (etiqueta + numero concatenados)
        layout.setText(font, lineaNombre);
        float maxAnchoTexto = layout.width;
        layout.setText(font, etiquetaValorTruco + numeroValorTruco);
        if (layout.width > maxAnchoTexto) maxAnchoTexto = layout.width;
        layout.setText(font, etiquetaPuntosTruco + numeroPuntosTruco);
        if (layout.width > maxAnchoTexto) maxAnchoTexto = layout.width;
        layout.setText(font, etiquetaValorEnvido + numeroValorEnvido);
        if (layout.width > maxAnchoTexto) maxAnchoTexto = layout.width;
        layout.setText(font, etiquetaPuntosEnvido + numeroPuntosEnvido);
        if (layout.width > maxAnchoTexto) maxAnchoTexto = layout.width;
        float paddingX = 15f;
        float paddingY = 12f;
        float altoLinea = font.getLineHeight() + 4f;
        float anchoCartel = maxAnchoTexto + (paddingX * 2);
        float altoCartel = (altoLinea * 5) + (paddingY * 1.5f); // 5 lineas ahora
        float actualWidth = width * scale;
        float actualHeight = height * scale;
        float cartelX = x + (actualWidth / 2f) - (anchoCartel / 2f);
        float cartelY = drawY + actualHeight + 12f;
        Texture pixelBlanco = game.getPixelBlanco();
        if (pixelBlanco != null) {
            batch.setColor(0.05f, 0.05f, 0.08f, 0.9f);
            batch.draw(pixelBlanco, cartelX, cartelY, anchoCartel, altoCartel);
            batch.setColor(0.3f, 0.3f, 0.4f, 0.8f);
            float grosorBorde = 1.5f;
            batch.draw(pixelBlanco, cartelX, cartelY, anchoCartel, grosorBorde);
            batch.draw(pixelBlanco, cartelX, cartelY + altoCartel - grosorBorde, anchoCartel, grosorBorde);
            batch.draw(pixelBlanco, cartelX, cartelY, grosorBorde, altoCartel);
            batch.draw(pixelBlanco, cartelX + anchoCartel - grosorBorde, cartelY, grosorBorde, altoCartel);
            batch.setColor(1, 1, 1, 1);
        }
        float textoX = cartelX + paddingX;
        float textoY = cartelY + altoCartel - paddingY;
        // Renglon 1: nombre, todo blanco
        font.setColor(blanco);
        font.draw(batch, lineaNombre, textoX, textoY);
        // Renglon 2: Valor Truco (etiqueta blanca + numero en color truco)
        dibujarLineaConNumeroColoreado(batch, font, layout, etiquetaValorTruco, numeroValorTruco, textoX, textoY - altoLinea, blanco, colorTruco);
        // Renglon 3: Puntos Truco (etiqueta blanca + numero en color truco)
        dibujarLineaConNumeroColoreado(batch, font, layout, etiquetaPuntosTruco, numeroPuntosTruco, textoX, textoY - altoLinea * 2, blanco, colorPTruco);
        // Renglon 4: Valor Envido (etiqueta blanca + numero en color envido)
        dibujarLineaConNumeroColoreado(batch, font, layout, etiquetaValorEnvido, numeroValorEnvido, textoX, textoY - altoLinea * 3, blanco, colorEnvido);
        // Renglon 5: Puntos Envido (etiqueta blanca + numero en color envido)
        dibujarLineaConNumeroColoreado(batch, font, layout, etiquetaPuntosEnvido, numeroPuntosEnvido, textoX, textoY - altoLinea * 4, blanco, colorPEnvido);
        font.setColor(blanco);
    }

    /** Dibuja "etiqueta" en colorEtiqueta seguido de "numero" en colorNumero, en la misma linea. */
    private void dibujarLineaConNumeroColoreado(SpriteBatch batch, com.badlogic.gdx.graphics.g2d.BitmapFont font,
                                                com.badlogic.gdx.graphics.g2d.GlyphLayout layout, String etiqueta, String numero,
                                                float x, float y, com.badlogic.gdx.graphics.Color colorEtiqueta, com.badlogic.gdx.graphics.Color colorNumero) {
        font.setColor(colorEtiqueta);
        font.draw(batch, etiqueta, x, y);
        layout.setText(font, etiqueta);
        float anchoEtiqueta = layout.width;
        font.setColor(colorNumero);
        font.draw(batch, numero, x + anchoEtiqueta, y);
    }

    /** Ya no hace falta disponer nada: la textura del atlas es compartida y se dispone una sola vez en Main. */
    public void dispose() {
        // no-op — se deja el metodo para no romper los llamados existentes en GameScreen.dispose()
    }

    /** Cambiar boca abajo ahora solo cambia la region, no crea/destruye texturas. */
    public void ponerBocaArriba(TextureAtlas atlas){
        this.bocaAbajo = false;
        this.region = atlas.findRegion(carta.getNombreRegion());
    }

    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setTamaño(float width, float height) { this.width = width; this.height = height; }
    public boolean isDragging() { return this.dragging; }
    public boolean soltoCarta() { return draggingAnterior && !dragging; }

    public void update(float mouseX, float mouseY, float delta) {
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
}
