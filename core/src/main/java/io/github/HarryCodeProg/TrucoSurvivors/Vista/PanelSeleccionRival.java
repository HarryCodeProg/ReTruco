package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.DatosRival;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PanelSeleccionRival {
    private static final float VELOCIDAD_SLIDE = 1800f;
    private static final float PANEL_X = 260f;
    private static final float PANEL_ANCHO = 760f;
    private static final float PANEL_Y = 40f;
    private static final float PANEL_ALTO = 480f; // Un poco más alto para que respiren las cartas

    private final Main game;
    private final Consumer<DatosRival> alElegirRival;
    private final GlyphLayout layout = new GlyphLayout();

    private ArrayList<DatosRival> listaRivales;
    private ArrayList<Boton> botonesJugar = new ArrayList<>();
    private int inicioIndice = 0;

    private float offsetY;
    private float offsetYObjetivo;
    private boolean cerrando = false;
    private Runnable alCerrarCompletamente;

    // Colores temáticos estilo Balatro (Ciega Pequeña, Grande, Jefe)
    private final Color colorAzul = new Color(0.18f, 0.45f, 0.85f, 1f);
    private final Color colorDorado = new Color(0.85f, 0.65f, 0.15f, 1f);
    private final Color colorRojo = new Color(0.75f, 0.22f, 0.22f, 1f);
    private final Color colorBloqueado = new Color(0.25f, 0.25f, 0.28f, 1f);

    public PanelSeleccionRival(Main game, Consumer<DatosRival> alElegirRival) {
        this.game = game;
        this.alElegirRival = alElegirRival;
        this.offsetY = -(PANEL_Y + PANEL_ALTO);
        this.offsetYObjetivo = 0f;
        inicializarRivales();
    }

    private void inicializarRivales() {
        listaRivales = game.getListaRivales();
        int indiceDesbloqueado = 0;
        for (int i = 0; i < listaRivales.size(); i++) {
            if (listaRivales.get(i).isDesbloqueado()) { indiceDesbloqueado = i; break; }
        }
        inicioIndice = (indiceDesbloqueado / 3) * 3;

        float anchoCuadro = 220;
        float espacio = 30;
        float xInicial = PANEL_X + 20;
        float yCuadro = PANEL_Y + 20;

        botonesJugar.clear();
        for (int slot = 0; slot < 3; slot++) {
            float cuadroX = xInicial + slot * (anchoCuadro + espacio);
            // Botón DORADO (Naranja) estilo Balatro para destacar la acción
            Boton btn = new Boton(cuadroX + (anchoCuadro - 170) / 2f, yCuadro - 15, 170, 50, "SELECCIONAR", Boton.TipoColor.DORADO, Accion.JUGAR_CARTA);
            botonesJugar.add(btn);
        }
    }

    public void updateAnimacion(float delta) {
        float diferencia = offsetYObjetivo - offsetY;
        if (Math.abs(diferencia) <= VELOCIDAD_SLIDE * delta) {
            offsetY = offsetYObjetivo;
            if (cerrando && offsetY == offsetYObjetivo && alCerrarCompletamente != null) {
                alCerrarCompletamente.run();
            }
        } else {
            offsetY += Math.signum(diferencia) * VELOCIDAD_SLIDE * delta;
        }
    }

    public boolean isAnimando() { return offsetY != offsetYObjetivo; }

    public void cerrar(Runnable alCerrarCompletamente) {
        this.cerrando = true;
        this.alCerrarCompletamente = alCerrarCompletamente;
        this.offsetYObjetivo = -(PANEL_Y + PANEL_ALTO);
    }

    public void update(float mouseWorldX, float mouseWorldY) {
        if (isAnimando()) return;
        for (int slot = 0; slot < 3; slot++) {
            int indice = inicioIndice + slot;
            if (indice < listaRivales.size()) {
                DatosRival rival = listaRivales.get(indice);
                Boton btn = botonesJugar.get(slot);
                btn.setHabilitado(rival.isDesbloqueado());
                btn.update(mouseWorldX, mouseWorldY - offsetY);
                if (btn.fueCliqueado(mouseWorldX, mouseWorldY)) {
                    alElegirRival.accept(rival);
                    return;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.end();
        Matrix4 original = batch.getProjectionMatrix().cpy();
        batch.setProjectionMatrix(original.cpy().translate(0, offsetY, 0));
        batch.begin();

        Texture pixel = game.getPixelBlanco();
        BitmapFont font = game.getFuentePrincipal(); // Usamos SIEMPRE la fuente pixelada

        // 1. Fondo contenedor sutil (para no opacar las cartas)
        dibujarRectRedondeado(batch, pixel, PANEL_X, PANEL_Y, PANEL_ANCHO, PANEL_ALTO, 16f, new Color(0.04f, 0.05f, 0.07f, 0.85f));

        // 2. Título Superior
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        String tituloPanel = "SELECCIONA TU RIVAL";
        layout.setText(font, tituloPanel);
        font.draw(batch, tituloPanel, PANEL_X + (PANEL_ANCHO - layout.width) / 2f, PANEL_Y + PANEL_ALTO - 20);
        font.getData().setScale(1f);

        float anchoCuadro = 220;
        float altoCuadro = 370;
        float espacio = 30;
        float xInicial = PANEL_X + 20;
        float yCuadro = PANEL_Y + 45;

        for (int slot = 0; slot < 3; slot++) {
            int indice = inicioIndice + slot;
            if (indice < listaRivales.size()) {
                DatosRival rival = listaRivales.get(indice);
                float cuadroX = xInicial + slot * (anchoCuadro + espacio);

                Color colorRival = obtenerColorRival(indice);
                Color colorBorde = rival.isDesbloqueado() ? colorRival : colorBloqueado;
                Color colorFondo = new Color(0.20f, 0.23f, 0.26f, 1f); // Gris pizarra azulado

                // A) Sombra Exterior
                dibujarRectRedondeado(batch, pixel, cuadroX + 8, yCuadro - 8, anchoCuadro, altoCuadro, 12f, new Color(0f, 0f, 0f, 0.4f));

                // B) Borde Exterior Grueso
                dibujarRectRedondeado(batch, pixel, cuadroX - 5, yCuadro - 5, anchoCuadro + 10, altoCuadro + 10, 14f, colorBorde);

                // C) Fondo Principal
                dibujarRectRedondeado(batch, pixel, cuadroX, yCuadro, anchoCuadro, altoCuadro, 10f, colorFondo);

                // D) Píldora de Nombre (Top Badge)
                float badgeW = anchoCuadro - 30;
                float badgeH = 35;
                float badgeX = cuadroX + 15;
                float badgeY = yCuadro + altoCuadro - 15 - badgeH;

                // Borde negro del badge
                dibujarRectRedondeado(batch, pixel, badgeX - 3, badgeY - 3, badgeW + 6, badgeH + 6, badgeH / 2f, new Color(0.1f, 0.1f, 0.1f, 1f));
                // Relleno del badge (mismo color que el borde exterior)
                dibujarRectRedondeado(batch, pixel, badgeX, badgeY, badgeW, badgeH, badgeH / 2f, colorBorde);

                // Texto Nombre con sombra dura
                String nombreRival = rival.getNombre();
                layout.setText(font, nombreRival);
                float textX = cuadroX + (anchoCuadro - layout.width) / 2f;
                float textY = badgeY + badgeH / 2f + layout.height / 2f;
                font.setColor(0.1f, 0.1f, 0.1f, 0.8f);
                font.draw(batch, nombreRival, textX + 2, textY - 2);
                font.setColor(Color.WHITE);
                font.draw(batch, nombreRival, textX, textY);

                // E) Avatar / Símbolo Circular (Simulando la ficha de Ciega)
                float iconSize = 85;
                float iconX = cuadroX + (anchoCuadro - iconSize) / 2f;
                float iconY = badgeY - 15 - iconSize;

                // Borde y fondo del icono
                dibujarRectRedondeado(batch, pixel, iconX, iconY, iconSize, iconSize, iconSize / 2f, colorBorde);
                dibujarRectRedondeado(batch, pixel, iconX + 5, iconY + 5, iconSize - 10, iconSize - 10, (iconSize - 10) / 2f, new Color(0.15f, 0.17f, 0.20f, 1f));

                // Inicial del rival como logo
                font.getData().setScale(2f);
                String inicial = nombreRival.substring(0, 1).toUpperCase();
                layout.setText(font, inicial);
                font.setColor(colorBorde);
                font.draw(batch, inicial, iconX + (iconSize - layout.width) / 2f, iconY + iconSize / 2f + layout.height / 2f);
                font.getData().setScale(1f);

                // F) Caja de Puntuación (Meta)
                float metaBoxY = iconY - 80;

                font.setColor(0.85f, 0.85f, 0.85f, 1f);
                layout.setText(font, "Anota al menos");
                font.draw(batch, "Anota al menos", cuadroX + (anchoCuadro - layout.width) / 2f, metaBoxY + 65);

                // Cajita hundida
                float metaBoxW = anchoCuadro - 50;
                float metaBoxH = 45;
                float metaBoxX = cuadroX + 25;
                dibujarRectRedondeado(batch, pixel, metaBoxX, metaBoxY, metaBoxW, metaBoxH, 8f, new Color(0.12f, 0.14f, 0.16f, 1f));

                // Número Meta
                String puntosTxt = String.valueOf((int) rival.getPuntosMeta());
                font.getData().setScale(1.4f);
                layout.setText(font, puntosTxt);
                font.setColor(rival.isDesbloqueado() ? colorRival : Color.GRAY);
                font.draw(batch, puntosTxt, cuadroX + (anchoCuadro - layout.width) / 2f, metaBoxY + metaBoxH / 2f + layout.height / 2f);
                font.getData().setScale(1f);

                // G) Descripción
                font.setColor(0.75f, 0.75f, 0.75f, 1f);
                font.getData().setScale(0.85f);
                font.draw(batch, rival.getDescripcion(), cuadroX + 15, metaBoxY - 15, anchoCuadro - 30, 1, true);
                font.getData().setScale(1f);
                font.setColor(Color.WHITE);

                // H) Renderizar Botón JUGAR (Solapando el borde inferior)
                botonesJugar.get(slot).render(batch);
            }
        }

        batch.end();
        batch.setProjectionMatrix(original);
        batch.begin();
    }

    private Color obtenerColorRival(int indice) {
        int patron = indice % 3;
        switch (patron) {
            case 0: return colorAzul;
            case 1: return colorDorado;
            case 2: return colorRojo;
            default: return colorAzul;
        }
    }

    // Herramienta interna para dibujar rectángulos redondeados perfectos
    private void dibujarRectRedondeado(SpriteBatch batch, Texture pixel, float x, float y, float width, float height, float radio, Color color) {
        if (width <= 0f || height <= 0f) return;
        radio = Math.min(radio, Math.min(width, height) / 2f);
        batch.setColor(color);
        batch.draw(pixel, x + radio, y, width - radio * 2f, height);
        batch.draw(pixel, x, y + radio, radio, height - radio * 2f);
        batch.draw(pixel, x + width - radio, y + radio, radio, height - radio * 2f);

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
}
