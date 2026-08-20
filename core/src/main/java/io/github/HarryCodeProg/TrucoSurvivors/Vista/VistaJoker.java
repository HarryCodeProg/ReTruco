package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
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
        if (resaltado) {
            batch.setColor(1.3f, 1.15f, 0.6f, 1f); // tinte dorado/brillante mientras actua
        }
        batch.draw(regionJoker, x, drawY, width / 2f, height / 2f, width * scaleExtra, height * scaleExtra, scale, scale, rotation);
        if (resaltado) {
            batch.setColor(Color.WHITE); // reset
        }
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

    private void dibujarCartelStats(SpriteBatch batch, io.github.HarryCodeProg.TrucoSurvivors.Main game, Juego juego, float drawY){
        com.badlogic.gdx.graphics.g2d.BitmapFont font = game.getFuentePrincipal();
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        // 1. Datos del Joker
        String lineaNombre = joker.getNombre();
        String descripcion = joker.getDescripcionRenderizada(juego);
        String rarezaStr = joker.getRareza().toString();
        float ANCHO_MAX_DESC = 220f;
        // Variables para el diseño de las etiquetas (badges)
        float padEtiquetaX = 6f;
        float padEtiquetaY = 4f;
        float espacioVerticalEntreRasgos = 6f; // Separación vertical entre las cajitas
        // 2. Medimos Nombre, Rareza y Descripción
        layout.setText(font, lineaNombre);
        float anchoNombre = layout.width;
        layout.setText(font, rarezaStr);
        float anchoRarezaConPad = layout.width + (padEtiquetaX * 2f);
        layout.setText(font, descripcion, font.getColor(), ANCHO_MAX_DESC, com.badlogic.gdx.utils.Align.left, true);
        float altoDescripcion = layout.height;
        //  Medimos los rasgos verticalmente buscando cuál es el más ancho individualmente
        float maxAnchoRasgo = 0f;
        for (CategoriaJoker cat : joker.getCategorias()) {
            layout.setText(font, cat.getTexto());
            float anchoEsteRasgo = layout.width + (padEtiquetaX * 2f);
            if (anchoEsteRasgo > maxAnchoRasgo) {
                maxAnchoRasgo = anchoEsteRasgo;
            }
        }
        // El ancho final ahora controla que ningún rasgo individual desborde
        float maxAnchoTexto = Math.max(ANCHO_MAX_DESC, Math.max(anchoNombre, Math.max(anchoRarezaConPad, maxAnchoRasgo)));
        // Márgenes internos del cartel principal
        float paddingX = 16f;
        float paddingY = 14f;
        float altoLinea = font.getLineHeight() + 6f;
        float anchoCartel = maxAnchoTexto + (paddingX * 2f);
        //  ALTO DINÁMICO: Sumamos un renglón por cada rasgo que tenga el Joker
        int cantidadRasgos = joker.getCategorias().size();
        float altoCartel = altoLinea + altoDescripcion + altoLinea + (altoLinea * cantidadRasgos) + (paddingY * 2.5f) + 20f;
        // 3. Posicionamiento abajo del Joker
        float actualWidth = width * scale;
        float cartelX = x + (actualWidth / 2f) - (anchoCartel / 2f);
        float cartelY = drawY - altoCartel - 12f;
        // 4. Render del Fondo del Cartel Principal
        Texture pixelBlanco = game.getPixelBlanco();
        if (pixelBlanco != null) {
            batch.setColor(0.05f, 0.05f, 0.08f, 0.92f);
            batch.draw(pixelBlanco, cartelX, cartelY, anchoCartel, altoCartel);
            batch.setColor(0.25f, 0.28f, 0.35f, 0.8f);
            float grosorBorde = 1.5f;
            batch.draw(pixelBlanco, cartelX, cartelY, anchoCartel, grosorBorde);
            batch.draw(pixelBlanco, cartelX, cartelY + altoCartel - grosorBorde, anchoCartel, grosorBorde);
            batch.draw(pixelBlanco, cartelX, cartelY, grosorBorde, altoCartel);
            batch.draw(pixelBlanco, cartelX + anchoCartel - grosorBorde, cartelY, grosorBorde, altoCartel);
        }
        // 5. Renderizado de Textos de arriba hacia abajo
        float textoX = cartelX + paddingX;
        float textoY = cartelY + altoCartel - paddingY;
        // Renglón 1: Nombre (Color de la rareza)
        font.setColor(obtenerColorRareza(joker.getRareza().toString()));
        font.draw(batch, lineaNombre, textoX, textoY);
        // Renglón 2: Descripción (Blanco)
        font.setColor(1f, 1f, 1f, 1f);
        float yDesc = textoY - altoLinea;
        font.draw(batch, descripcion, textoX, yDesc, ANCHO_MAX_DESC, com.badlogic.gdx.utils.Align.left, true);
        // Renglón 3: Rareza (Con rectángulo de fondo translúcido)
        float yRareza = yDesc - altoDescripcion - 22f;
        Color colorRareza = obtenerColorRareza(joker.getRareza().toString());
        if (pixelBlanco != null) {
            layout.setText(font, rarezaStr);
            float fondoW = layout.width + (padEtiquetaX * 2f);
            float fondoH = font.getCapHeight() + (padEtiquetaY * 2f);
            batch.setColor(colorRareza.r, colorRareza.g, colorRareza.b, 0.15f);
            batch.draw(pixelBlanco, textoX, yRareza - padEtiquetaY - 2f, fondoW, fondoH);
        }
        font.setColor(colorRareza);
        font.draw(batch, rarezaStr, textoX + padEtiquetaX, yRareza + font.getCapHeight());
        // Renglón 4: Rasgos/Categorías (Uno abajo del otro)
        if (!joker.getCategorias().isEmpty()) {
            // El primer rasgo arranca abajo de la rareza
            float yRasgoActual = yRareza - altoLinea - 8f;
            for (CategoriaJoker cat : joker.getCategorias()) {
                String textoCat = cat.getTexto();
                Color colorCat = obtenerColorCategoria(cat);
                layout.setText(font, textoCat);
                float fondoW = layout.width + (padEtiquetaX * 2f);
                float fondoH = font.getCapHeight() + (padEtiquetaY * 2f);
                if (pixelBlanco != null) {
                    batch.setColor(colorCat.r, colorCat.g, colorCat.b, 0.18f);
                    batch.draw(pixelBlanco, textoX, yRasgoActual - padEtiquetaY - 2f, fondoW, fondoH);
                }
                font.setColor(colorCat);
                font.draw(batch, textoCat, textoX + padEtiquetaX, yRasgoActual + font.getCapHeight());
                //  En lugar de mover la X, bajamos la Y para el siguiente rasgo
                yRasgoActual -= (altoLinea + espacioVerticalEntreRasgos);
            }
        }
        // Reset de seguridad del batch e hilos
        font.setColor(1f, 1f, 1f, 1f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void setResaltado(boolean resaltado) {
        this.resaltado = resaltado;
        if (resaltado) pulso = 0f;
    }

    public boolean isResaltado() { return resaltado; }

    /** Asigna colores vistosos temáticos a cada rasgo */
    private Color obtenerColorCategoria(CategoriaJoker cat) {
        switch (cat) {
            case ANIMAL:      return new Color(0.3f, 0.85f, 0.4f, 1f);  // Verde Naturaleza
            case AMIGABLE:    return new Color(1f, 0.6f, 0.75f, 1f);   // Rosa Pastel
            case AGUA:        return new Color(0.2f, 0.6f, 1f, 1f);     // Azul Océano
            case COMIDA:      return new Color(0.9f, 0.55f, 0.2f, 1f);  // Naranja Crujiente
            case DULCE:       return new Color(0.85f, 0.4f, 0.9f, 1f);  // Magenta/Caramelo
            case BEBIDA:      return new Color(0.4f, 0.9f, 0.9f, 1f);   // Turquesa/Refresco
            case AMARGO:      return new Color(0.5f, 0.4f, 0.3f, 1f);   // Marrón Café/Mate
            case TRADICIONAL: return new Color(0.85f, 0.85f, 0.5f, 1f); // Beige Antiguo
            default:          return new Color(1f, 1f, 1f, 1f);
        }
    }

    /** Método utilitario para asignar colores vistosos a la rareza al estilo Balatro */
    private Color obtenerColorRareza(String rareza) {
        String r = rareza.toUpperCase();
        if (r.contains("COMUN") || r.contains("COMMON")) {
            return new Color(0.4f, 0.7f, 1f, 1f); // Celeste / Azul claro común
        } else if (r.contains("RARO") || r.contains("RARE")) {
            return new Color(1f, 0.25f, 0.25f, 1f); // Rojo brillante raro
        } else if (r.contains("LEGENDARIO") || r.contains("LEGENDARY")) {
            return new Color(0.75f, 0.3f, 0.9f, 1f); // Púrpura épico / legendario
        } else {
            return new Color(0.6f, 0.6f, 0.6f, 1f); // Gris por defecto
        }
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
}
