package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Main;

import static io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion.COMPRAR_ITEM_TIENDA;

public class Boton {
    // --- NUEVO ENUM PARA SELECCIONAR EL COLOR CON PERSONALIDAD ---
    public enum TipoColor {
        CELESTE(UITheme.AZUL),
        BLANCO(UITheme.TEXTO_PRINCIPAL),
        AMARILLO(UITheme.DORADO),
        BORDO(UITheme.ROJO),
        ROJO(UITheme.ROJO),
        ROJO_OSCURO(UITheme.ROJO),
        NARANJA(UITheme.NARANJA),
        NARANJA_OSCURO(UITheme.NARANJA),
        VERDE(UITheme.TRUCO),
        VERDE_OSCURO(UITheme.TRUCO),
        VERDE_MENTA(UITheme.TRUCO),
        AZUL(UITheme.AZUL),
        AZUL_OSCURO(UITheme.AZUL),
        AZUL_MARINO(UITheme.AZUL),
        VIOLETA(UITheme.VIOLETA),
        LILA(UITheme.VIOLETA),
        ROSA(UITheme.VIOLETA),
        FUCSIA(UITheme.VIOLETA),
        CIAN(UITheme.ENVIDO),
        TURQUESA(UITheme.TURQUESA),
        DORADO(UITheme.DORADO),
        BRONCE(UITheme.DORADO),
        GRIS(UITheme.TEXTO_SECUNDARIO),
        GRIS_OSCURO(UITheme.PANEL_SECUNDARIO),
        NEGRO_SUAVE(UITheme.PANEL_PRINCIPAL),
        CREMA(UITheme.TEXTO_PRINCIPAL),
        MARRON(UITheme.NARANJA),
        CAFE(UITheme.NARANJA);
        public final Color base;
        TipoColor(Color base) {
            this.base = base;
        }
    }
    private Texture pixel;
    private String texto;
    private float x;
    private float y;
    private float width;
    private float height;
    private Accion accion;
    private boolean hover;
    private boolean pressed;
    private boolean habilitado = true;
    private BitmapFont font;
    private boolean fontPropia;
    private GlyphLayout layout;
    // Guardamos el tipo de color del botón (Celeste por defecto)
    private TipoColor tipoColor = TipoColor.CELESTE;
    private static final float GROSOR_BORDE = 2f;
    private static final float SOMBRA_Y = 5f;
    private static final float RADIO_ESQUINA = 7f;
    // Colores auxiliares para el procesamiento visual
    private Color colorFondoActual = new Color();
    private Color colorBordeActual = new Color();
    private Color colorTextoActual = new Color();
    private final Color colorGlowActual = new Color();
    private boolean visible = true;

    public Boton(float x, float y, float width, float height, Accion accion) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
        if (Main.getInstance() != null) {
            font = Main.getInstance().getFuenteBotones();
            fontPropia = false;
        } else {
            font = new BitmapFont();
            fontPropia = true;
        }
        layout = new GlyphLayout();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.accion = accion;
    }

    public Boton(float x, float y, float width, float height, String textoPersonalizado, Accion accion) {
        this(x, y, width, height, accion);
        this.texto = textoPersonalizado;
    }

    // --- NUEVO CONSTRUCTOR: Para pasarle el texto y el color que quieras ---
    public Boton(float x, float y, float width, float height, String textoPersonalizado, TipoColor color, Accion accion) {
        this(x, y, width, height, textoPersonalizado, accion);
        this.tipoColor = color;
    }

    // --- NUEVO CONSTRUCTOR: Por si no lleva texto personalizado pero sí color ---
    public Boton(float x, float y, float width, float height, TipoColor color, Accion accion) {
        this(x, y, width, height, accion);
        this.tipoColor = color;
    }

    public void render(SpriteBatch batch) {
        if (!visible) return;
        float drawX = x;
        float drawY = y;
        float drawWidth = width;
        float drawHeight = height;
        Color colorBase = obtenerColorBase();
        // ESTADO VISUAL
        if (!habilitado) {
            colorFondoActual.set(UITheme.BOTON_DESHABILITADO);
            colorBordeActual.set(UITheme.BOTON_DESHABILITADO_BORDE);
            colorTextoActual.set(UITheme.BOTON_DESHABILITADO_TEXTO);
        } else {
            colorFondoActual.set(colorBase);
            colorBordeActual.set(colorBase).mul(0.38f, 0.38f, 0.38f, 1f);
            if (tipoColor == TipoColor.BLANCO) {
                colorTextoActual.set(UITheme.BORDE);
            } else {
                colorTextoActual.set(UITheme.TEXTO_PRINCIPAL);
            }
            if (hover) {
                colorFondoActual.lerp(Color.WHITE, 0.10f);
                colorBordeActual.lerp(Color.WHITE, 0.08f);
            }
        }
        // PRESIONADO
        boolean estaPresionado = pressed && habilitado;
        if (estaPresionado) {
            float escala = 0.96f;
            drawWidth = width * escala;
            drawHeight = height * escala;
            drawX = x + (width - drawWidth) / 2f;
            drawY = y + (height - drawHeight) / 2f - 3f;
            colorFondoActual.mul(0.78f, 0.78f, 0.78f, 1f);
            colorBordeActual.mul(0.75f, 0.75f, 0.75f, 1f);
        }
        // GLOW HOVER
        if (hover && habilitado && !estaPresionado) {
            colorGlowActual.set(colorBase.r, colorBase.g, colorBase.b, 0.12f);
            dibujarRectRedondeado(batch, drawX - 5f, drawY - 5f, drawWidth + 10f, drawHeight + 10f, RADIO_ESQUINA + 2f, colorGlowActual);
            colorGlowActual.set(colorBase.r, colorBase.g, colorBase.b, 0.08f);
            dibujarRectRedondeado(batch, drawX - 9f, drawY - 9f, drawWidth + 18f, drawHeight + 18f, RADIO_ESQUINA + 4f, colorGlowActual);
        }
        // SOMBRA
        colorGlowActual.set(UITheme.SOMBRA);
        colorGlowActual.a = habilitado ? 0.70f : 0.35f;
        dibujarRectRedondeado(batch, drawX + 2f, drawY - SOMBRA_Y, drawWidth, drawHeight, RADIO_ESQUINA, colorGlowActual);
        // BORDE
        dibujarRectRedondeado(batch, drawX, drawY, drawWidth, drawHeight, RADIO_ESQUINA, colorBordeActual);
        // CUERPO
        dibujarRectRedondeado(batch, drawX + GROSOR_BORDE, drawY + GROSOR_BORDE, drawWidth - GROSOR_BORDE * 2f, drawHeight - GROSOR_BORDE * 2f, RADIO_ESQUINA - 1f, colorFondoActual);
        // HIGHLIGHT SUPERIOR
        if (habilitado && !estaPresionado) {
            batch.setColor(1f, 1f, 1f, hover ? 0.24f : 0.12f);
            batch.draw(pixel, drawX + 5f, drawY + drawHeight - 7f, drawWidth - 10f, 2f);
            batch.setColor(UITheme.BRILLO);
        }
        // SOMBRA INTERIOR INFERIOR
        if (!estaPresionado) {
            batch.setColor(UITheme.SOMBRA_SUAVE);
            batch.draw(pixel, drawX + 5f, drawY + 4f, drawWidth - 10f, 2f);
            batch.setColor(UITheme.BRILLO);
        }
        // TEXTO
        String textoRender = obtenerTexto();
        float escalaOriginal = font.getScaleX();
        font.getData().setScale(escalaOriginal * (height >= 48f ? 1.04f : 0.94f));
        layout.setText(font, textoRender);
        float textX = drawX + (drawWidth - layout.width) / 2f;
        float textY = drawY + (drawHeight + layout.height) / 2f - (estaPresionado ? 1f : 0f);
        font.setColor(colorTextoActual);
        font.draw(batch, textoRender, textX, textY);
        font.getData().setScale(escalaOriginal);
        font.setColor(UITheme.TEXTO_PRINCIPAL);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void update(float mouseX, float mouseY) {
        if (!visible) return;
        if (!habilitado) {
            hover = false;
            pressed = false;
            return;
        }
        hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        pressed = hover && Gdx.input.isButtonPressed(Input.Buttons.LEFT);
    }

    public boolean fueCliqueado(float mouseWorldX, float mouseWorldY) {
        if (!visible) return false;
        if (!habilitado) return false;
        boolean encima = mouseWorldX >= x && mouseWorldX <= x + width && mouseWorldY >= y && mouseWorldY <= y + height;
        return encima && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    public Accion getAccion() {
        return this.accion;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setTexto(String texto) { this.texto = texto; }

    // --- SETTER PARA CAMBIAR EL COLOR EN CALIENTE ---
    public void setTipoColor(TipoColor tipoColor) {
        this.tipoColor = tipoColor;
    }

    private String obtenerTexto() {
        if (this.texto != null && !this.texto.isEmpty()) {
            return this.texto;
        }
        switch (accion) {
            case ENVIDO: return "ENVIDO";
            case TRUCO: return "TRUCO";
            case IR_AL_MAZO: return "IR AL MAZO";
            case JUGAR_CARTA: return "JUGAR CARTA";
            case DESCARTAR: return "DESCARTAR";
            case RETRUCO: return "RE TRUCO";
            case VALE_CUATRO: return "VALE 4";
            case REAL_ENVIDO: return "REAL ENVIDO";
            case FALTA_ENVIDO: return "FALTA ENVIDO";
            case COMPRAR_ITEM_TIENDA: return "COMPRAR";
            case COMPRAR_Y_USAR_SANTO: return "COMPRAR Y USAR";
            case REROLL_CARTAS:
            case REROLL_JOKERS: return "REROLL";
            case CONTINUAR_TIENDA: return "CONTINUAR";
            default: return accion != null ? accion.name() : "";
        }
    }

    public void dispose() {
        pixel.dispose();
        if (fontPropia && font != null) font.dispose();
    }

    public float getX() {return x;}

    public float getY() {return y;}

    private Color obtenerColorBase() {
        return tipoColor.base;
    }

    private void dibujarRectRedondeado(SpriteBatch batch, float x, float y, float width, float height, float radio, Color color) {
        if (width <= 0f || height <= 0f) return;
        radio = Math.min(radio, Math.min(width, height) / 2f);
        int r = Math.max(1, (int) Math.ceil(radio));
        batch.setColor(color);
        // Centro
        batch.draw(pixel, x + radio, y, width - radio * 2f, height);
        // Laterales
        if (height > radio * 2f) {
            batch.draw(pixel, x, y + radio, radio, height - radio * 2f);
            batch.draw(pixel, x + width - radio, y + radio, radio, height - radio * 2f);
        }
        // Curvas superior e inferior
        for (int i = 0; i < r; i++) {
            float dy = i + 0.5f;
            float distancia = radio - dy;
            float raiz = (float) Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));
            float inset = radio - raiz;
            batch.draw(pixel, x + inset, y + i, width - inset * 2f, 1f);
            batch.draw(pixel, x + inset, y + height - i - 1f, width - inset * 2f, 1f);
        }
        batch.setColor(Color.WHITE);
    }

    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isVisible() { return visible; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getAncho() { return width; }
    public float getAlto() { return height; }
    public boolean isHovered() {return hover;}
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
}
