package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.Background;

import java.util.ArrayList;

import static io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout.*;

public class GameRenderSystem {
    private final Main game;
    private final GlyphLayout layout = new GlyphLayout();

    public GameRenderSystem(Main game) {this.game = game;}

    public void render(float delta, float offsetY,
                       OrthographicCamera camera, Background fondoPlasma, PanelPuntajes panelPuntajes, float panelX, float panelY, Juego juego, Jugador jugador, Jugador rival,
                       ArrayList<VistaCarta> cartasMesaJugador, ArrayList<VistaCarta> cartasMesaRival, ArrayList<VistaCarta> cartasRival, ArrayList<VistaCarta> cartasJugador,
                       ArrayList<VistaJoker> jokers, GestorInputArrastrable<VistaCarta> gestorCartas, GestorInputArrastrable<VistaJoker> gestorJokers, VistaMazo vistaMazo,
                       GestorVentaJoker gestorVentaJoker, GestorAnimacionResolucion gestorAnimacion, double puntosTrucoDisplay, double multTrucoDisplay,
                       double puntosEnvidoDisplay, double multEnvidoDisplay, String textoFlotanteActual, Runnable renderBotones, Runnable renderCartelJoker)
    {
        camera.near = -1000f;
        camera.far = 1000f;
        camera.update();
        ScreenUtils.clear(0.1f, 0.12f, 0.16f, 1f);
        float originalY = camera.position.y;
        camera.position.y = 720f / 2f;
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        game.batch.end();
        // HUD and static elements (CÁMARA ESTÁTICA)
        camera.position.y = 720f / 2f;
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        // 1. Dibujamos los fondos del panel ANTES del batch.begin()
        panelPuntajes.renderFondosYCajas(camera, panelX, panelY);
        game.batch.begin();
        // 2. Dibujamos los textos del panel ADENTRO del batch
        panelPuntajes.renderTextos(game.batch, game.getFuentePrincipal(), juego, jugador, rival, panelX,
            panelY, gestorAnimacion, puntosTrucoDisplay, multTrucoDisplay, puntosEnvidoDisplay, multEnvidoDisplay);
        renderAreasJugador(game.batch, jugador, game.getPixelBlanco());
        renderContadoresAreas(game.batch, jugador, cartasJugador);
        VistaJoker jokerArrastrado = gestorJokers != null ? gestorJokers.getArrastrado() : null;
        renderJokers(jokers, jokerArrastrado);
        gestorVentaJoker.render(game.batch);
        if (renderBotones != null) {
            renderBotones.run();
        }
        if (vistaMazo != null && juego != null) {
            vistaMazo.render(
                game.batch,
                juego.getMazoJugador().getCartasRestantesOrdenadas(),
                juego.getMazoJugador().getTamañoMazo()
            );
        }
        if (renderCartelJoker != null) {
            renderCartelJoker.run();
        }
        game.batch.end();
        camera.position.y = 720f / 2f + offsetY;
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        if (textoFlotanteActual != null) {
            BitmapFont fuente = game.getFuentePrincipal();
            layout.setText(fuente, textoFlotanteActual);
            // (Arreglé los signos '-' que se habían perdido acá)
            float tx = ((Gdx.graphics.getWidth() - layout.width) / 2f) + 270f;
            float ty = GameLayout.TECHO_MESA - 30f;
            Color colorTexto = Color.WHITE;
            if (textoFlotanteActual.toLowerCase().contains("mult")) {
                colorTexto = io.github.HarryCodeProg.TrucoSurvivors.Estados.ColorMecanica.MULTIPLICADOR.getColor();
            } else if (textoFlotanteActual.toLowerCase().contains("puntos")) {
                if (textoFlotanteActual.toLowerCase().contains("envido")) {
                    colorTexto = io.github.HarryCodeProg.TrucoSurvivors.Estados.ColorMecanica.PUNTOS_ENVIDO.getColor();
                } else {
                    colorTexto = io.github.HarryCodeProg.TrucoSurvivors.Estados.ColorMecanica.PUNTOS_TRUCO.getColor();
                }
            }
            fuente.setColor(colorTexto);
            fuente.draw(game.batch, textoFlotanteActual, tx, ty);
            fuente.setColor(Color.WHITE);
        }
        renderMesa(cartasMesaJugador, cartasMesaRival);
        renderRival(cartasRival);
        VistaCarta cartaArrastrada = gestorCartas != null ? gestorCartas.getArrastrado() : null;
        renderJugador(cartasJugador, cartaArrastrada, camera);
        game.batch.end();
        // Restore camera
        camera.position.y = 720f / 2f;
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
    }

    private void renderMesa(ArrayList<VistaCarta> cartasMesaJugador, ArrayList<VistaCarta> cartasMesaRival) {
        for (VistaCarta c : cartasMesaJugador) {
            c.render(game.batch, game);
        }
        for (VistaCarta c : cartasMesaRival) {
            c.render(game.batch, game);
        }
    }

    private void renderRival(ArrayList<VistaCarta> cartasRival) {
        for (VistaCarta c : cartasRival) {
            c.render(game.batch, game);
        }
    }

    private void renderAreasJugador(SpriteBatch batch, Jugador jugador, Texture pixelBlanco) {
        if (pixelBlanco == null || jugador == null) return;
        float areaX = MARGEN_AREA_LATERAL;
        float areaAncho = ANCHO_AREA_JUGADOR;
        float radio = 14f;
        Color colorPanel = new Color(0.06f, 0.09f, 0.13f, 0.78f);
        float jokersY = Y_JOKERS - 10f;
        dibujarRectRedondeado(batch, pixelBlanco, areaX, jokersY, areaAncho, ALTO_AREA_JOKERS, radio, colorPanel);
        float cartasY = Y_MANO_JUGADOR - 10f;
        dibujarRectRedondeado(batch, pixelBlanco, areaX, cartasY, areaAncho, ALTO_AREA_CARTAS, radio, colorPanel);
        batch.setColor(Color.WHITE);
    }

    /**
     * Dibuja un rectángulo con esquinas redondeadas usando el pixel blanco.
     * El rectángulo se compone de un cuerpo central + franjas laterales +
     * filas de aproximación en las esquinas.
     */
    private void dibujarRectRedondeado(SpriteBatch batch, Texture pixel,
                                       float x, float y, float w, float h,
                                       float radio, Color color) {
        if (w <= 0f || h <= 0f) return;
        radio = Math.min(radio, Math.min(w, h) / 2f);
        batch.setColor(color);
        // Cuerpo central (columnas del medio, altura completa)
        batch.draw(pixel, x + radio, y, w - radio * 2f, h);
        // Franja izquierda (altura media, sin las esquinas)
        batch.draw(pixel, x, y + radio, radio, h - radio * 2f);
        // Franja derecha (altura media, sin las esquinas)
        batch.draw(pixel, x + w - radio, y + radio, radio, h - radio * 2f);
        // Esquinas redondeadas: 4 filas de 1px, cada una solo en su zona
        int pasos = Math.max(2, (int)radio);
        for (int i = 0; i < pasos; i++) {
            float dy = i + 0.5f;
            float distancia = radio - dy;
            float raiz = (float)Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));
            float inset = radio - raiz;
            float rowWidth = raiz;
            // Arriba-izquierda
            batch.draw(pixel, x + inset, y + h - i - 1f, rowWidth, 1f);
            // Arriba-derecha
            batch.draw(pixel, x + w - radio, y + h - i - 1f, rowWidth, 1f);
            // Abajo-izquierda
            batch.draw(pixel, x + inset, y + i, rowWidth, 1f);
            // Abajo-derecha
            batch.draw(pixel, x + w - radio, y + i, rowWidth, 1f);
        }
        batch.setColor(Color.WHITE);
    }

    // 1. Agregamos OrthographicCamera como parámetro
    private void renderJugador(ArrayList<VistaCarta> cartasJugador, VistaCarta cartaArrastrada, OrthographicCamera camera) {
        float areaX = MARGEN_AREA_LATERAL;
        float areaY = Y_MANO_JUGADOR - 10f;
        float areaAncho = ANCHO_AREA_JUGADOR;
        float areaAlto = ALTO_AREA_CARTAS;
        float bleed = 30f;
        com.badlogic.gdx.math.Rectangle clipBounds = new com.badlogic.gdx.math.Rectangle(areaX - bleed, areaY - bleed, areaAncho + (bleed * 2), areaAlto + (bleed * 2));
        com.badlogic.gdx.math.Rectangle scissors = new com.badlogic.gdx.math.Rectangle();
        game.batch.flush();
        // 2. Ahora pasamos "camera" directamente como primer argumento
        com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.calculateScissors(
            camera,
            game.batch.getTransformMatrix(),
            clipBounds,
            scissors
        );
        boolean pushed = com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.pushScissors(scissors);
        if (pushed) {
            for (VistaCarta c : cartasJugador) {
                if (c != cartaArrastrada) {
                    c.render(game.batch, game);
                }
            }
            game.batch.flush();
            com.badlogic.gdx.scenes.scene2d.utils.ScissorStack.popScissors();
        }
        if (cartaArrastrada != null) {
            cartaArrastrada.render(game.batch, game);
        }
    }

    private void renderJokers(ArrayList<VistaJoker> jokers, VistaJoker jokerArrastrado) {
        // Pasada 1: Dibuja los Jokers quietos con su cartel nativo si están en hover
        for (VistaJoker j : jokers) {
            if (j != jokerArrastrado) {
                j.render(game.batch);
            }
        }
        // Pasada 2: Dibuja el Joker arrastrado AL FINAL por encima de todo
        if (jokerArrastrado != null) {
            jokerArrastrado.render(game.batch);
        }
    }

    private void renderContadoresAreas(SpriteBatch batch, Jugador jugador, ArrayList<VistaCarta> cartasJugador) {
        BitmapFont font = game.getFuenteNumeros();
        String textoCartas = cartasJugador.size() + "/" + jugador.getTamañoMano();
        String textoJokers = jugador.getJokers().size() + "/" + jugador.getTamañoJokers();
        GlyphLayout layoutCartas = new GlyphLayout(font, textoCartas);
        GlyphLayout layoutJokers = new GlyphLayout(font, textoJokers);
        float centroX = MARGEN_AREA_LATERAL + ANCHO_AREA_JUGADOR / 2f;
        float yCartas = Y_MANO_JUGADOR - 18f;
        float yJokers = Y_JOKERS - 18f;
        float xContadorJokers = MARGEN_AREA_LATERAL + 8f;
        font.setColor(new Color(0.75f, 0.88f, 0.94f, 1f));
        font.draw(batch, textoCartas, centroX - layoutCartas.width / 2f, yCartas);
        font.setColor(Color.WHITE);
        font.draw(batch, textoJokers, xContadorJokers, yJokers);
        font.setColor(Color.WHITE);
    }

}
