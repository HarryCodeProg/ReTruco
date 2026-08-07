package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.Background;

import java.util.ArrayList;

public class GameRenderSystem {
    private final Main game;
    private final GlyphLayout layout = new GlyphLayout();

    public GameRenderSystem(Main game) {
        this.game = game;
    }

    public void render(
        float delta, OrthographicCamera camera, Background fondoPlasma, PanelPuntajes panelPuntajes,
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
        panelPuntajes.renderTextos(
            game.batch, game.getFuentePrincipal(), juego, jugador, rival, panelX, panelY,
            gestorAnimacion, puntosTrucoDisplay, multTrucoDisplay, puntosEnvidoDisplay, multEnvidoDisplay);
        // Texto flotante superior (si aplica)
        if (textoFlotanteActual != null) {
            BitmapFont fuente = game.getFuentePrincipal();
            layout.setText(fuente, textoFlotanteActual);
            float tx = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float ty = GameLayout.TECHO_MESA + 60f;
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
        // Pasada 1: Dibuja los Jokers quietos
        for (VistaJoker j : jokers) {
            if (j != jokerArrastrado) {
                j.render(game.batch);
            }
        }
        // Pasada 2: Dibuja el Joker arrastrado AL FINAL
        if (jokerArrastrado != null) {
            jokerArrastrado.render(game.batch);
        }
    }
}
