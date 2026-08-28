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

    public void render(float delta, OrthographicCamera camera, Background fondoPlasma, PanelPuntajes panelPuntajes,
                       float panelX, float panelY, Juego juego, Jugador jugador, Jugador rival,
                       ArrayList<VistaCarta> cartasMesaJugador, ArrayList<VistaCarta> cartasMesaRival,
                       ArrayList<VistaCarta> cartasRival, ArrayList<VistaCarta> cartasJugador, ArrayList<VistaJoker> jokers,
                       GestorInputArrastrable<VistaCarta> gestorCartas, GestorInputArrastrable<VistaJoker> gestorJokers,
                       VistaMazo vistaMazo, GestorVentaJoker gestorVentaJoker, GestorAnimacionResolucion gestorAnimacion,
                       double puntosTrucoDisplay, double multTrucoDisplay, double puntosEnvidoDisplay, double multEnvidoDisplay,
                       String textoFlotanteActual, Runnable renderBotones, Runnable renderCartelJoker
    ) {
        // 1. Limpieza de pantalla
        ScreenUtils.clear(0.1f, 0.12f, 0.16f, 1f);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        // 2. DIBUJAR SHADER DE FONDO
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        game.batch.end();
        // 3. PANEL DE PUNTAJES (Shapes con ShapeRenderer)
        panelPuntajes.renderFondosYCajas(camera, panelX, panelY);
        // 4. SPRITES Y TEXTOS (SpriteBatch)
        game.batch.begin();
        // Renderizado de textos del panel
        renderAreasJugador(game.batch, jugador, game.getPixelBlanco());
        renderContadoresAreas(game.batch, jugador, cartasJugador);
        panelPuntajes.renderTextos(
            game.batch, game.getFuentePrincipal(), juego, jugador, rival, panelX, panelY,
            gestorAnimacion, puntosTrucoDisplay, multTrucoDisplay, puntosEnvidoDisplay, multEnvidoDisplay);
        // Texto flotante superior (si aplica)
        if (textoFlotanteActual != null) {
            BitmapFont fuente = game.getFuentePrincipal();
            layout.setText(fuente, textoFlotanteActual);
            float tx = ((Gdx.graphics.getWidth() - layout.width) / 2f) + 200f;
            float ty = GameLayout.TECHO_MESA - 30f;
            fuente.setColor(Color.GOLD);
            fuente.draw(game.batch, textoFlotanteActual, tx, ty);
            fuente.setColor(Color.WHITE);
        }
        // Renderizado de entidades del juego
        renderMesa(cartasMesaJugador, cartasMesaRival);
        renderRival(cartasRival);
        // Pasamos el elemento arrastrado a renderJugador y renderJokers
        VistaCarta cartaArrastrada = gestorCartas != null ? gestorCartas.getArrastrado() : null;
        VistaJoker jokerArrastrado = gestorJokers != null ? gestorJokers.getArrastrado() : null;
        renderJugador(cartasJugador, cartaArrastrada);
        renderJokers(jokers, jokerArrastrado);
        // Venta de Jokers y Botones de acción
        gestorVentaJoker.render(game.batch);
        if (renderBotones != null) renderBotones.run();
        // Mazo y Carteles
        vistaMazo.render(game.batch, juego.getMazoJugador().getCartasRestantesOrdenadas(), juego.getMazoJugador().getTamañoMazo());
        if (renderCartelJoker != null) renderCartelJoker.run();
        game.batch.end();
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

    private void renderJugador(ArrayList<VistaCarta> cartasJugador, VistaCarta cartaArrastrada) {
        // Pasada 1: Dibuja las cartas quietas
        for (VistaCarta c : cartasJugador) {
            if (c != cartaArrastrada) {
                c.render(game.batch, game);
            }
        }
        // Pasada 2: Dibuja la carta arrastrada AL FINAL para que flote por encima
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

    private void renderAreasJugador(SpriteBatch batch, Jugador jugador, Texture pixelBlanco) {
        if (pixelBlanco == null || jugador == null) return;
        float areaX = MARGEN_AREA_LATERAL;
        float areaAncho = ANCHO_AREA_JUGADOR;
        // ÁREA DE CARTAS
        float cartasY = Y_MANO_JUGADOR - 10f;
        batch.setColor(0.05f, 0.05f, 0.08f, 0.45f);
        batch.draw(pixelBlanco, areaX, cartasY, areaAncho, ALTO_AREA_CARTAS);
        // Borde
        batch.setColor(0.25f, 0.28f, 0.35f, 0.7f);
        batch.draw(pixelBlanco, areaX, cartasY, areaAncho, 2f);
        batch.draw(pixelBlanco, areaX, cartasY + ALTO_AREA_CARTAS - 2f, areaAncho, 2f);
        batch.draw(pixelBlanco, areaX, cartasY, 2f, ALTO_AREA_CARTAS);
        batch.draw(pixelBlanco, areaX + areaAncho - 2f, cartasY, 2f, ALTO_AREA_CARTAS);
        dibujarEtiquetaArea(batch, "MANO", areaX + 12f, cartasY + ALTO_AREA_CARTAS - 8f, new Color(0.65f, 0.80f, 0.88f, 1f));
        // ÁREA DE JOKERS
        float jokersY = Y_JOKERS - 10f;
        batch.setColor(0.05f, 0.05f, 0.08f, 0.45f);
        batch.draw(pixelBlanco, areaX, jokersY, areaAncho, ALTO_AREA_JOKERS);
        batch.setColor(0.25f, 0.28f, 0.35f, 0.7f);
        batch.draw(pixelBlanco, areaX, jokersY, areaAncho, 2f);
        batch.draw(pixelBlanco, areaX, jokersY + ALTO_AREA_JOKERS - 2f, areaAncho, 2f);
        batch.draw(pixelBlanco, areaX, jokersY, 2f, ALTO_AREA_JOKERS);
        batch.draw(pixelBlanco, areaX + areaAncho - 2f, jokersY, 2f, ALTO_AREA_JOKERS);
        dibujarEtiquetaArea(batch, "JOKERS", areaX + 12f, jokersY + ALTO_AREA_JOKERS - 8f, new Color(0.95f, 0.75f, 0.28f, 1f));
        batch.setColor(Color.WHITE);
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
        font.setColor(new Color(0.98f, 0.82f, 0.36f, 1f));
        font.draw(batch, textoJokers, xContadorJokers, yJokers);
        font.setColor(Color.WHITE);
    }

    private void dibujarEtiquetaArea(SpriteBatch batch, String texto, float x, float y, Color color) {
        BitmapFont font = game.getFuentePrincipal();
        float escalaOriginal = font.getScaleX();
        font.getData().setScale(escalaOriginal * 0.62f);
        font.setColor(color);
        font.draw(batch, texto, x, y);
        font.getData().setScale(escalaOriginal);
        font.setColor(Color.WHITE);
    }
}
