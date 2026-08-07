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

/**
 * Panel de seleccion de rival que se desliza sobre la mesa de GameScreenV2,
 * igual que PanelTienda: nunca hay cambio de Screen, solo slide up/down.
 */
public class PanelSeleccionRival {
    private static final float PANEL_X = 260f;
    private static final float PANEL_ANCHO = 1160f - PANEL_X;
    private static final float PANEL_Y = 40f;
    private static final float PANEL_ALTO = 460f;
    private static final float VELOCIDAD_SLIDE = 1800f;

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

    // Colores temáticos para el patrón (Verde, Amarillo, Rojo)
    private final Color colorVerde = new Color(0.2f, 0.85f, 0.3f, 1f);
    private final Color colorAmarillo = new Color(0.95f, 0.8f, 0.2f, 1f);
    private final Color colorRojo = new Color(0.9f, 0.25f, 0.25f, 1f);
    private final Color colorBloqueado = new Color(0.3f, 0.3f, 0.35f, 0.6f);

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
            Boton btn = new Boton(cuadroX + (anchoCuadro - 180) / 2f, yCuadro + 15, 180, 45, "JUGAR", Accion.JUGAR_CARTA);
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
        // 1. Fondo contenedor del Panel
        batch.setColor(0.06f, 0.07f, 0.1f, 0.94f);
        batch.draw(pixel, PANEL_X, PANEL_Y, PANEL_ANCHO, PANEL_ALTO);
        batch.setColor(1, 1, 1, 1);
        // 2. Título Superior
        BitmapFont fuenteTitulo = game.getFuenteTitulo();
        layout.setText(fuenteTitulo, "SELECCIONA TU RIVAL");
        fuenteTitulo.draw(batch, "SELECCIONA TU RIVAL", PANEL_X + (PANEL_ANCHO - layout.width) / 2f, PANEL_Y + PANEL_ALTO - 20);
        BitmapFont fuenteNormal = game.getFuentePrincipal();
        float anchoCuadro = 220;
        float espacio = 30;
        float xInicial = PANEL_X + 20;
        float yCuadro = PANEL_Y + 20;
        float altoCuadro = PANEL_ALTO - 80;
        for (int slot = 0; slot < 3; slot++) {
            int indice = inicioIndice + slot;
            if (indice < listaRivales.size()) {
                DatosRival rival = listaRivales.get(indice);
                float cuadroX = xInicial + slot * (anchoCuadro + espacio);
                // Obtener color (Verde, Amarillo, Rojo)
                Color colorRival = obtenerColorRival(indice);
                Color colorBorde = rival.isDesbloqueado() ? colorRival : colorBloqueado;
                // A) Borde exterior de 3px de grosor
                float grosorBorde = 3f;
                batch.setColor(colorBorde);
                batch.draw(pixel, cuadroX - grosorBorde, yCuadro - grosorBorde,
                    anchoCuadro + (grosorBorde * 2), altoCuadro + (grosorBorde * 2));
                // B) Fondo Principal del Cuadro
                if (rival.isDesbloqueado()) {
                    batch.setColor(0.10f, 0.10f, 0.14f, 0.92f);
                } else {
                    batch.setColor(0.06f, 0.06f, 0.08f, 0.85f);
                }
                batch.draw(pixel, cuadroX, yCuadro, anchoCuadro, altoCuadro);
                // C) Sombreo / Rectángulo de Cabecera para el Nombre
                batch.setColor(0.04f, 0.04f, 0.06f, 0.8f);
                batch.draw(pixel, cuadroX + 8, yCuadro + altoCuadro - 45, anchoCuadro - 16, 38);
                // Texto Nombre
                batch.setColor(Color.WHITE);
                layout.setText(fuenteNormal, rival.getNombre());
                fuenteNormal.draw(batch, rival.getNombre(), cuadroX + (anchoCuadro - layout.width) / 2f, yCuadro + altoCuadro - 20);
                // D) Sombreo / Rectángulo para la Descripción
                batch.setColor(0.03f, 0.03f, 0.05f, 0.5f);
                batch.draw(pixel, cuadroX + 10, yCuadro + 115, anchoCuadro - 20, 180);
                // Texto Descripción
                batch.setColor(Color.WHITE);
                fuenteNormal.draw(batch, rival.getDescripcion(), cuadroX + 18, yCuadro + 280, anchoCuadro - 36, 1, true);
                // E) Placa para la Meta (Mismo color de borde)
                batch.setColor(colorBorde);
                batch.draw(pixel, cuadroX + 15, yCuadro + 68, anchoCuadro - 30, 34);
                // Texto Meta
                String puntosTxt = "Meta: " + (int) rival.getPuntosMeta() + " pts";
                layout.setText(fuenteNormal, puntosTxt);
                batch.setColor(Color.WHITE);
                fuenteNormal.draw(batch, puntosTxt, cuadroX + (anchoCuadro - layout.width) / 2f, yCuadro + 91);
                // F) Renderizar Botón JUGAR
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
            case 0: return colorVerde;
            case 1: return colorAmarillo;
            case 2: return colorRojo;
            default: return colorVerde;
        }
    }
}
