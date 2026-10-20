package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolJokersTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolSantosTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaSanto;

import java.util.ArrayList;

public class ColeccionScreen {
    private final Main game;

    private final Boton botonJokers;
    private final Boton botonCartas;
    private final Boton botonSantos;
    private final Boton botonAnterior;
    private final Boton botonSiguiente;
    private final Boton botonVolver;

    private static final float X_INICIAL = 220f;
    private static final float Y_INICIAL = 430f;
    private static final float ESPACIO_X = 185f;
    private static final float ESPACIO_Y = 165f;

    private enum Tipo { JOKERS, CARTAS, SANTOS }
    private Tipo tipoActual = Tipo.JOKERS;
    private int paginaActual = 0;

    private final ArrayList<VistaCarta> vistasCartas = new ArrayList<>();
    private final ArrayList<VistaSanto> vistasSantos = new ArrayList<>();
    private final ArrayList<Joker> jokers = new ArrayList<>();
    private final ArrayList<Carta> cartas = new ArrayList<>();
    private final ArrayList<Santo> santos = new ArrayList<>();

    private static final int COLUMNAS = 5;
    private static final int POR_PAGINA = 15;
    private static final float ANCHO_ITEM = 100f;
    private static final float ALTO_ITEM = 150f;

    private final VistaJoker[] vistasJokers = new VistaJoker[150];

    // Variables para guardar el estado de hover en el update y usarlo en el render
    private VistaJoker jokerHover = null;
    private VistaCarta cartaHover = null;
    private VistaSanto santoHover = null;

    public ColeccionScreen(Main game) {
        this.game = game;
        // Botones (mismas posiciones que tenías)
        botonJokers = new Boton(350f, 595f, 170f, 45f, "JOKERS", Boton.TipoColor.DORADO, Accion.COLECCION);
        botonCartas = new Boton(555f, 595f, 170f, 45f, "CARTAS", Boton.TipoColor.CIAN, Accion.COLECCION);
        botonSantos = new Boton(760f, 595f, 170f, 45f, "SANTOS", Boton.TipoColor.VIOLETA, Accion.COLECCION);
        botonAnterior = new Boton(500f, 35f, 90f, 50f, "<-", Boton.TipoColor.CELESTE, Accion.COLECCION);
        botonSiguiente = new Boton(690f, 35f, 90f, 50f, "->", Boton.TipoColor.CELESTE, Accion.COLECCION);
        botonVolver = new Boton(40f, 35f, 130f, 50f, "VOLVER", Boton.TipoColor.BLANCO, Accion.OPCIONES);
        // Carga de datos
        PoolJokersTienda poolJokers = new PoolJokersTienda();
        jokers.addAll(poolJokers.crearTodos());
        PoolSantosTienda poolSantos = new PoolSantosTienda();
        santos.addAll(poolSantos.crearTodos());
        for (int numero = 1; numero <= 12; numero++) {
            for (Palo palo : Palo.values()) {
                cartas.add(new Carta(numero, palo));
            }
        }
        construirVistas();
    }

    public void abrir() {
        tipoActual = Tipo.JOKERS;
        paginaActual = 0;
    }

    private void construirVistas() {
        java.util.Arrays.fill(vistasJokers, null);
        vistasCartas.clear();
        vistasSantos.clear();
        for (Joker joker : jokers) {
            VistaJoker vista = new VistaJoker(joker, game.getAtlasJokers());
            vista.setTamaño(ANCHO_ITEM, ALTO_ITEM);
            int idx = joker.getId() - 1;
            if (idx >= 0 && idx < vistasJokers.length) {
                vistasJokers[idx] = vista;
            }
        }
        for (Carta carta : cartas) {
            VistaCarta vista = new VistaCarta(carta, false, game.getAtlasCartas());
            vista.setTamaño(ANCHO_ITEM, ALTO_ITEM);
            vistasCartas.add(vista);
        }
        for (Santo santo : santos) {
            VistaSanto vista = new VistaSanto(santo, game.getAtlasSantos());
            vista.setTamaño(ANCHO_ITEM, ALTO_ITEM);
            vistasSantos.add(vista);
        }
    }

    public boolean update(float delta, float mx, float my, boolean click) {
        botonJokers.update(mx, my);
        botonCartas.update(mx, my);
        botonSantos.update(mx, my);
        botonAnterior.update(mx, my);
        botonSiguiente.update(mx, my);
        botonVolver.update(mx, my);
        if (botonJokers.fueCliqueado(mx, my) && tipoActual != Tipo.JOKERS) {
            tipoActual = Tipo.JOKERS;
            paginaActual = 0;
        }
        if (botonCartas.fueCliqueado(mx, my) && tipoActual != Tipo.CARTAS) {
            tipoActual = Tipo.CARTAS;
            paginaActual = 0;
        }
        if (botonSantos.fueCliqueado(mx, my) && tipoActual != Tipo.SANTOS) {
            tipoActual = Tipo.SANTOS;
            paginaActual = 0;
        }
        int totalPaginas = obtenerTotalPaginas();
        if (botonAnterior.fueCliqueado(mx, my)) {
            paginaActual--;
            if (paginaActual < 0) paginaActual = totalPaginas - 1;
        }
        if (botonSiguiente.fueCliqueado(mx, my)) {
            paginaActual++;
            if (paginaActual >= totalPaginas) paginaActual = 0;
        }
        if (botonVolver.fueCliqueado(mx, my)) {
            return true; // Señal para que MainMenuScreen cierre este panel
        }
        // Actualizar las vistas y capturar quién tiene el hover
        actualizarVistas(mx, my, delta);
        return false;
    }

    // --- NUEVO MÉTODO RENDER ---
    public void render(SpriteBatch batch, float delta) {
        BitmapFont font = game.getFuenteTitulo();
        /*GlyphLayout titleLayout = new GlyphLayout(font, "COLECCIÓN");
        font.draw(batch, "COLECCIÓN", 640f - titleLayout.width / 2f, 700f);*/
        botonJokers.render(batch);
        botonCartas.render(batch);
        botonSantos.render(batch);
        dibujarVistasActuales(batch);
        botonAnterior.render(batch);
        BitmapFont fontPaginas = game.getFuenteBotones();
        String textoPaginas = (paginaActual + 1) + "/" + obtenerTotalPaginas();
        GlyphLayout layoutPaginas = new GlyphLayout(fontPaginas, textoPaginas);
        fontPaginas.draw(batch, textoPaginas, 640f - layoutPaginas.width / 2f, 67f);
        botonSiguiente.render(batch);
        botonVolver.render(batch);
        // Dibujar tooltips al final para que queden por encima de todo
        if (jokerHover != null) jokerHover.renderCartelStats(batch, game, null);
        if (cartaHover != null) cartaHover.renderCartelStats(batch, game);
        if (santoHover != null) santoHover.renderCartelStats(batch, game);
    }

    private int obtenerTotalPaginas() {
        int total = 0;
        switch (tipoActual) {
            case JOKERS: total = vistasJokers.length; break;
            case CARTAS: total = cartas.size(); break;
            case SANTOS: total = santos.size(); break;
        }
        return Math.max(1, (int) Math.ceil(total / (float) POR_PAGINA));
    }

    private void actualizarVistas(float mouseX, float mouseY, float delta) {
        jokerHover = null;
        cartaHover = null;
        santoHover = null;
        int inicio = paginaActual * POR_PAGINA;
        if (tipoActual == Tipo.JOKERS) {
            int fin = Math.min(inicio + POR_PAGINA, vistasJokers.length);
            for (int i = inicio; i < fin; i++) {
                if (vistasJokers[i] != null) {
                    vistasJokers[i].update(mouseX, mouseY, delta);
                    if (vistasJokers[i].isHover()) jokerHover = vistasJokers[i];
                }
            }
        } else if (tipoActual == Tipo.CARTAS) {
            int fin = Math.min(inicio + POR_PAGINA, vistasCartas.size());
            for (int i = inicio; i < fin; i++) {
                vistasCartas.get(i).update(mouseX, mouseY, delta);
                if (vistasCartas.get(i).isHover()) cartaHover = vistasCartas.get(i);
            }
        } else if (tipoActual == Tipo.SANTOS) {
            int fin = Math.min(inicio + POR_PAGINA, vistasSantos.size());
            for (int i = inicio; i < fin; i++) {
                vistasSantos.get(i).update(mouseX, mouseY, delta);
                if (vistasSantos.get(i).isHover()) santoHover = vistasSantos.get(i);
            }
        }
    }

    private void dibujarVistasActuales(SpriteBatch batch) {
        int inicio = paginaActual * POR_PAGINA;
        for (int posicion = 0; posicion < POR_PAGINA; posicion++) {
            int indice = inicio + posicion;
            int fila = posicion / COLUMNAS;
            int columna = posicion % COLUMNAS;
            float x = X_INICIAL + columna * ESPACIO_X;
            float y = Y_INICIAL - fila * ESPACIO_Y;
            if (tipoActual == Tipo.JOKERS) {
                if (indice < vistasJokers.length && vistasJokers[indice] != null) {
                    VistaJoker vista = vistasJokers[indice];
                    vista.setPosition(x, y);
                    vista.setTooltipLateral(fila == 1);
                    vista.render(batch);
                } else if (indice < vistasJokers.length) {
                    dibujarJokerBack(batch, x, y);
                }
            } else if (tipoActual == Tipo.CARTAS) {
                if (indice < vistasCartas.size()) {
                    VistaCarta vista = vistasCartas.get(indice);
                    vista.setPosition(x, y);
                    vista.setTooltipLateral(fila == 1);
                    vista.render(batch, game);
                }
            } else if (tipoActual == Tipo.SANTOS) {
                if (indice < vistasSantos.size()) {
                    VistaSanto vista = vistasSantos.get(indice);
                    vista.setPosition(x, y);
                    vista.setTooltipLateral(fila == 1);
                    vista.render(batch);
                }
            }
        }
    }

    private void dibujarJokerBack(SpriteBatch batch, float x, float y) {
        TextureRegion back = game.getAtlasJokers().findRegion("back");
        if (back == null) return;
        batch.setColor(Color.WHITE);
        batch.draw(back, x, y, ANCHO_ITEM / 2f, ALTO_ITEM / 2f, ANCHO_ITEM, ALTO_ITEM, 1f, 1f, 0f);
    }
}
