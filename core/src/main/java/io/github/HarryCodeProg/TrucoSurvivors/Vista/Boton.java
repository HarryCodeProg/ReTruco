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

public class Boton {
    // --- NUEVO ENUM PARA SELECCIONAR EL COLOR CON PERSONALIDAD ---
    public enum TipoColor {
        CELESTE(new Color(0.2f, 0.5f, 0.8f, 1f)),
        BLANCO(new Color(0.85f, 0.85f, 0.85f, 1f)),
        AMARILLO(new Color(0.85f, 0.65f, 0.1f, 1f)),
        BORDO(new Color(0.5f, 0.1f, 0.15f, 1f)),
        ROJO(new Color(0.80f, 0.20f, 0.20f, 1f)),
        ROJO_OSCURO(new Color(0.55f, 0.12f, 0.12f, 1f)),
        NARANJA(new Color(0.90f, 0.45f, 0.10f, 1f)),
        NARANJA_OSCURO(new Color(0.75f, 0.35f, 0.08f, 1f)),
        VERDE(new Color(0.20f, 0.65f, 0.30f, 1f)),
        VERDE_OSCURO(new Color(0.12f, 0.45f, 0.20f, 1f)),
        VERDE_MENTA(new Color(0.35f, 0.85f, 0.65f, 1f)),
        AZUL(new Color(0.18f, 0.35f, 0.80f, 1f)),
        AZUL_OSCURO(new Color(0.10f, 0.18f, 0.45f, 1f)),
        AZUL_MARINO(new Color(0.08f, 0.12f, 0.28f, 1f)),
        VIOLETA(new Color(0.50f, 0.28f, 0.75f, 1f)),
        LILA(new Color(0.70f, 0.55f, 0.90f, 1f)),
        ROSA(new Color(0.90f, 0.45f, 0.65f, 1f)),
        FUCSIA(new Color(0.85f, 0.20f, 0.60f, 1f)),
        CIAN(new Color(0.15f, 0.75f, 0.85f, 1f)),
        TURQUESA(new Color(0.15f, 0.70f, 0.60f, 1f)),
        DORADO(new Color(0.92f, 0.75f, 0.20f, 1f)),
        BRONCE(new Color(0.70f, 0.45f, 0.22f, 1f)),
        GRIS(new Color(0.50f, 0.50f, 0.50f, 1f)),
        GRIS_OSCURO(new Color(0.25f, 0.25f, 0.25f, 1f)),
        NEGRO_SUAVE(new Color(0.12f, 0.12f, 0.12f, 1f)),
        CREMA(new Color(0.95f, 0.92f, 0.82f, 1f)),
        MARRON(new Color(0.45f, 0.28f, 0.15f, 1f)),
        CAFE(new Color(0.32f, 0.20f, 0.10f, 1f));
        public final Color base;
        TipoColor(Color base) { this.base = base; }
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
    private GlyphLayout layout;
    // Guardamos el tipo de color del botón (Celeste por defecto)
    private TipoColor tipoColor = TipoColor.CELESTE;
    private final float GROSOR_BORDE = 3f; // Grosor del borde estilo panelPuntaje
    // Colores auxiliares para el procesamiento visual
    private Color colorFondoActual = new Color();
    private Color colorBordeActual = new Color();
    private Color colorTextoActual = new Color();
    private boolean visible = true;

    public Boton(float x, float y, float width, float height, Accion accion) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
        font = new BitmapFont();
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
        // 1. Calcular lógica de colores dinámicos basados en el TipoColor elegido
        if (!habilitado) {
            colorFondoActual.set(0.3f, 0.3f, 0.3f, 1f);
            colorBordeActual.set(0.15f, 0.15f, 0.15f, 1f);
            colorTextoActual.set(0.5f, 0.5f, 0.5f, 1f);
        } else {
            // El borde siempre es una versión bastante más oscura del color base (Estilo cómic/panel)
            colorBordeActual.set(tipoColor.base).mul(0.3f, 0.3f, 0.3f, 1f);
            // Si el botón es blanco, el texto va en negro/oscuro para que se lea. Si no, va en blanco.
            if (tipoColor == TipoColor.BLANCO) {
                colorTextoActual.set(0.1f, 0.1f, 0.1f, 1f);
            } else {
                colorTextoActual.set(Color.WHITE);
            }
            if (hover) {
                // Brillo de hover: aclaramos el fondo un 25%
                colorFondoActual.set(tipoColor.base).add(0.15f, 0.15f, 0.15f, 0f);
            } else {
                colorFondoActual.set(tipoColor.base);
            }
        }
        // Lógica de presionado mecánico (Se achica un poco y baja)
        boolean estaPresionado = pressed && habilitado;
        if (estaPresionado) {
            float escala = 0.96f;
            drawWidth = width * escala;
            drawHeight = height * escala;
            drawX = x + (width - drawWidth) / 2f;
            drawY = y + (height - drawHeight) / 2f - 3f; // Se hunde hacia abajo
            // Oscurecemos el fondo en el click
            colorFondoActual.mul(0.7f, 0.7f, 0.7f, 1f);
        }
        // 2. DIBUJAR EL BORDE (Un rectángulo negro/oscuro de fondo más grande)
        batch.setColor(colorBordeActual);
        batch.draw(pixel, drawX, drawY, drawWidth, drawHeight);
        // 3. DIBUJAR EL FONDO (Un rectángulo más chico adentro del borde)
        batch.setColor(colorFondoActual);
        batch.draw(pixel,
            drawX + GROSOR_BORDE,
            drawY + GROSOR_BORDE,
            drawWidth - (GROSOR_BORDE * 2f),
            drawHeight - (GROSOR_BORDE * 2f)
        );
        // Restaurar color del batch para el texto
        batch.setColor(Color.WHITE);
        // 4. DIBUJAR EL TEXTO CENTRAL
        String textoRender = obtenerTexto();
        layout.setText(font, textoRender);
        float textX = drawX + (drawWidth - layout.width) / 2f;
        // Si está presionado bajamos el texto 1 píxel extra para acompañar el hundimiento mecánico
        float textY = drawY + (drawHeight + layout.height) / 2f - (estaPresionado ? 1f : 0f);
        font.setColor(colorTextoActual);
        font.draw(batch, textoRender, textX, textY);
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

    public void update() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        hover = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        pressed = hover && Gdx.input.isButtonPressed(Input.Buttons.LEFT);
    }

    public boolean fueCliqueado() {
        if (!visible) return false;
        if (!habilitado) return false;
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean encima = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        return encima && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
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
            default: return accion != null ? accion.name() : "";
        }
    }

    public void dispose() {
        pixel.dispose();
        font.dispose();
    }

    public float getX() {return x;}

    public float getY() {return y;}

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
