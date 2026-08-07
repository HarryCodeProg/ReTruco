package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.*;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.*;
import java.util.ArrayList;
import static io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout.*;

public class GameScreenV2 implements Screen {
    private Main game;
    private Jugador jugador;
    private Jugador rival;
    private DatosRival datosRival;
    private ArrayList<VistaCarta> cartasJugador;
    private ArrayList<VistaCarta> cartasRival;
    private ArrayList<VistaJoker> jokers;
    private ArrayList<VistaCarta> cartasMesaJugador;
    private ArrayList<VistaCarta> cartasMesaRival;
    private VistaMazo vistaMazo;
    private Juego juego;
    private ControladorCombate controladorCombate;
    private GestorAccion gestorAccion;
    private ControladorIARival controladorIARival;
    private GestorInputArrastrable<VistaCarta> gestorCartas;
    private GestorInputArrastrable<VistaJoker> gestorJokers;
    private final GestorReordenamiento gestorReordenamiento = new GestorReordenamiento();
    private GestorAnimacionesMano gestorAnimaciones;
    private GestorReparto gestorReparto;
    private HUDController hudController;
    private PanelPuntajes panelPuntajes;
    private Background fondoPlasma;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 mouseWorld;
    private int tocoJugar = 0;
    private boolean esperandoTransicion = false;
    private boolean iniciarNuevaRondaPendiente = false;
    private float tiempoNuevaRonda = 0f;
    private GestorAnimacionResolucion gestorAnimacionResolucion = new GestorAnimacionResolucion();
    private String textoFlotanteActual = null;
    private double puntosTrucoDisplay = 0;
    private double multTrucoDisplay = 1;
    private double puntosEnvidoDisplay = 0;
    private double multEnvidoDisplay = 1;
    float anchoCarta = 120;
    float separacion = 8;
    private enum EstadoPantalla { JUGANDO, VICTORIA, TIENDA, SELECCION_RIVAL }
    private PanelSeleccionRival panelSeleccionRival;
    private EstadoPantalla estado = EstadoPantalla.JUGANDO;
    private PanelTienda panelTienda;
    private boolean partidaIniciada = false;
    private Boton botonVenderJoker;
    private VistaJoker jokerConHoverActual;
    private final GestorVentaJoker gestorVentaJoker = new GestorVentaJoker();
    private GameRenderSystem renderSystem;
    private AreaElementos<VistaCarta> areaCartas;
    private AreaElementos<VistaJoker> areaJokers;

    public GameScreenV2(Main game, DatosRival datosRival) {
        this.game = game;
        this.datosRival = datosRival;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(1280, 720, camera);
        this.mouseWorld = new Vector3();
        this.panelPuntajes = new PanelPuntajes();
        this.gestorReparto = new GestorReparto();
        this.cartasMesaJugador = new ArrayList<>();
        this.cartasMesaRival = new ArrayList<>();
        botonVenderJoker = new Boton(0, 0, 150, 45, Boton.TipoColor.ROJO, Accion.VENDER_JOKER);
        botonVenderJoker.setVisible(false);
        float margenLateral = 220f;
        this.areaCartas = new AreaElementos<>(margenLateral, Y_MANO_JUGADOR, 1280f - margenLateral * 2, ALTO_CARTA, anchoCarta, ALTO_CARTA, separacion);
        this.areaJokers = new AreaElementos<>(margenLateral, Y_JOKERS, 1280f - margenLateral * 2, ALTO_JOKER, ANCHO_JOKER, ALTO_JOKER, SEPARACION_JOKER);
        this.renderSystem = new GameRenderSystem(game);
        iniciarShader();
        inicializarJuego();
        this.partidaIniciada = true;
    }

    public GameScreenV2(Main game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(1280, 720, camera);
        this.mouseWorld = new Vector3();
        this.panelPuntajes = new PanelPuntajes();
        this.gestorReparto = new GestorReparto();
        this.cartasMesaJugador = new ArrayList<>();
        this.cartasMesaRival = new ArrayList<>();
        botonVenderJoker = new Boton(0, 0, 150, 45, Boton.TipoColor.ROJO, Accion.VENDER_JOKER);
        botonVenderJoker.setVisible(false);
        float margenLateral = 220f;
        this.areaCartas = new AreaElementos<>(margenLateral, Y_MANO_JUGADOR, 1280f - margenLateral * 2, ALTO_CARTA, anchoCarta, ALTO_CARTA, separacion);
        this.areaJokers = new AreaElementos<>(margenLateral, Y_JOKERS, 1280f - margenLateral * 2, ALTO_JOKER, ANCHO_JOKER, ALTO_JOKER, SEPARACION_JOKER);
        this.fondoPlasma = new Background();
        iniciarShader();
        this.jugador = game.getPerfilJugador().getJugador();
        this.cartasJugador = new ArrayList<>();
        this.cartasRival = new ArrayList<>();
        this.jokers = new ArrayList<>();
        this.gestorJokers = new GestorInputArrastrable<>(jokers);
        this.renderSystem = new GameRenderSystem(game);
        for (int i = 0; i < jugador.getJokers().size(); i++) {
            VistaJoker view = new VistaJoker(jugador.getJokers().get(i), game.getAtlasJokers());
            view.setTamaño(ANCHO_JOKER, ALTO_JOKER);
            jokers.add(view);
        }
        organizarJokers();
        estado = EstadoPantalla.SELECCION_RIVAL;
        panelSeleccionRival = new PanelSeleccionRival(game, this::onPrimeraSeleccionRival);
    }

    private void onPrimeraSeleccionRival(DatosRival rivalElegido) {
        panelSeleccionRival.cerrar(() -> {
            this.datosRival = rivalElegido;
            inicializarJuego();
            this.partidaIniciada = true;
            estado = EstadoPantalla.JUGANDO;
        });
    }

    private void inicializarJuego() {
        this.fondoPlasma = new Background();
        this.jugador = game.getPerfilJugador().getJugador();
        this.rival = new Jugador(datosRival.getNombre());
        this.tocoJugar = 0;
        this.esperandoTransicion = false;
        this.iniciarNuevaRondaPendiente = false;
        this.tiempoNuevaRonda = 0f;
        int rivalesVencidos = datosRival.getIndice();
        this.fondoPlasma.setRivalesVencidos(rivalesVencidos);
        Mazo mazoRival = Juego.crearMazoRival(datosRival.getNivelDificultad());
        this.juego = new Juego(jugador, rival, mazoRival);
        this.juego.setPuntajeMeta(datosRival.getPuntosMeta());
        this.controladorCombate = new ControladorCombate(this, juego);
        this.controladorIARival = new ControladorIARival(juego, controladorCombate, this);
        this.cartasJugador = new ArrayList<>();
        this.cartasRival = new ArrayList<>();
        this.jokers = new ArrayList<>();
        this.cartasMesaJugador = new ArrayList<>();
        this.cartasMesaRival = new ArrayList<>();
        this.gestorCartas = new GestorInputArrastrable<>(cartasJugador);
        this.gestorJokers = new GestorInputArrastrable<>(jokers);
        this.gestorAnimaciones = new GestorAnimacionesMano(
            cartasJugador, cartasRival,
            cartasMesaJugador, cartasMesaRival,
            this::organizarCartas
        );
        this.gestorAccion = new GestorAccion(juego, this, controladorCombate, gestorCartas, gestorAnimaciones);
        ArrayList<Joker> jokersModelo = jugador.getJokers();
        for (int i = 0; i < jokersModelo.size(); i++) {
            Joker joker = jokersModelo.get(i);
            VistaJoker view = new VistaJoker(joker, game.getAtlasJokers());
            view.setTamaño(ANCHO_JOKER, ALTO_JOKER);
            jokers.add(view);
        }
        organizarJokers();
        gestorAnimaciones.iniciarTransicion(
            new ArrayList<>(jugador.getMano()),
            new ArrayList<>(rival.getMano())
        );
        float anchoMazo = 60f;
        float altoMazo = 88f;
        float posX = 1280f - anchoMazo - 25f;
        float posY = 130f;
        this.vistaMazo = new VistaMazo(posX, posY, anchoMazo, altoMazo, game.getAtlasCartas(), game.getFuentePrincipal(), game.getPixelBlanco());
        this.hudController = new HUDController(this.vistaMazo);
        organizarCartas();
    }

    public void iniciarNuevaRonda() {
        if (gestorAnimaciones.isEsperandoTransicion()) return;
        tocoJugar = 0;
        juego.setManoFinalizada(false);
        juego.irAlMazo();
        ArrayList<Carta> nuevasJugador = new ArrayList<>(jugador.getMano());
        ArrayList<Carta> nuevasRival   = new ArrayList<>(rival.getMano());
        gestorAnimaciones.iniciarTransicion(nuevasJugador, nuevasRival);
    }

    @Override
    public void render(float delta) {
        prepararFrame();
        if (estado == EstadoPantalla.TIENDA) {
            renderConTienda(delta);
            return;
        }
        if (estado == EstadoPantalla.SELECCION_RIVAL) {
            renderConSeleccionRival(delta);
            return;
        }
        if (iniciarNuevaRondaPendiente) {
            tiempoNuevaRonda -= delta;
            if (tiempoNuevaRonda <= 0f) {
                if (!gestorAnimaciones.isEsperandoTransicion()) {
                    iniciarNuevaRondaPendiente = false;
                    esperandoTransicion = false;
                    iniciarNuevaRonda();
                }
            }
        }
        gestorAnimaciones.update(delta);
        gestorAnimacionResolucion.update(delta);
        boolean puedeInteractuar = puedeInteractuar();
        hudController.update(mouseWorld, puedeInteractuar, gestorAccion);
        if (controladorIARival != null) {
            controladorIARival.update(puedeInteractuar);
        }
        gestorVentaJoker.update(mouseWorld.x, mouseWorld.y, jokers, jugador, (v) -> organizarJokers());
        VistaCarta cartaArrastradaAntes = gestorCartas.getArrastrado();
        VistaJoker jokerArrastradoAntes = gestorJokers.getArrastrado();
        gestorCartas.update(mouseWorld.x, mouseWorld.y, delta, puedeInteractuar);
        actualizarDragJokers(delta, puedeInteractuar);
        if (!puedeInteractuar) {
            for (VistaCarta c : new ArrayList<>(cartasJugador)) {
                c.update(mouseWorld.x, mouseWorld.y, delta);
            }
        }
        for (VistaCarta c : new ArrayList<>(cartasRival)) {
            c.update(mouseWorld.x, mouseWorld.y, delta);
        }
        actualizarCartasMesa(delta);
        hudController.actualizarSeleccion(juego, puedeInteractuar, gestorCartas, gestorJokers, cartasJugador, jokers);
        actualizarPreviewsCartas(puedeInteractuar);
        if (cartaArrastradaAntes != null && gestorCartas.getArrastrado() == null) {
            organizarCartas();
        }
        if (jokerArrastradoAntes != null && gestorJokers.getArrastrado() == null) {
            organizarJokers();
        }
        renderizar(delta);
    }

    private void renderizar(float delta) {
        renderSystem.render(
            delta, camera, fondoPlasma, panelPuntajes, PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y, juego, jugador, rival,
            cartasMesaJugador, cartasMesaRival,
            cartasRival, cartasJugador, jokers,
            gestorCartas, gestorJokers,
            vistaMazo, gestorVentaJoker, gestorAnimacionResolucion, puntosTrucoDisplay, multTrucoDisplay, puntosEnvidoDisplay,
            multEnvidoDisplay, textoFlotanteActual,
            () -> hudController.renderBotones(game.batch, game.getPixelBlancoRegion()),
            this::renderCartelJokerSiCorresponde
        );
    }

    private void renderConSeleccionRival(float delta) {
        ScreenUtils.clear(0.1f, 0.12f, 0.16f, 1f);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        game.batch.end();
        panelPuntajes.renderFondosYCajas(camera, PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y);
        actualizarDragJokers(delta, true);
        game.batch.begin();
        renderJokers();
        if (partidaIniciada) {
            panelPuntajes.renderTextos(
                game.batch, game.getFuentePrincipal(), juego, jugador, rival,
                PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y, gestorAnimacionResolucion,
                puntosTrucoDisplay, multTrucoDisplay, puntosEnvidoDisplay, multEnvidoDisplay
            );
        }
        game.batch.end();
        panelSeleccionRival.updateAnimacion(delta);
        panelSeleccionRival.update(mouseWorld.x, mouseWorld.y);
        game.batch.begin();
        panelSeleccionRival.render(game.batch);
        game.batch.end();
    }

    private void renderConTienda(float delta) {
        ScreenUtils.clear(0.1f, 0.12f, 0.16f, 1f);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        game.batch.end();
        panelPuntajes.renderFondosYCajas(camera, PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y);
        VistaJoker jokerArrastradoAntes = gestorJokers.getArrastrado();
        actualizarDragJokers(delta, true);
        gestorReordenamiento.previsualizarReordenamientoJokers(gestorJokers, jokers);
        if (jokerArrastradoAntes != null && gestorJokers.getArrastrado() == null) {
            organizarJokers();
        }
        game.batch.begin();
        renderJokers();
        panelPuntajes.renderTextos(
            game.batch, game.getFuentePrincipal(), juego, jugador, rival,
            PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y, gestorAnimacionResolucion,
            puntosTrucoDisplay, multTrucoDisplay, puntosEnvidoDisplay, multEnvidoDisplay
        );
        vistaMazo.render(game.batch, juego.getMazoJugador().getCartasRestantesOrdenadas(), juego.getMazoJugador().getTamañoMazo());
        game.batch.end();
        gestorVentaJoker.update(mouseWorld.x, mouseWorld.y, jokers, jugador, (v) -> organizarJokers());
        panelTienda.updateAnimacion(delta);
        panelTienda.update(mouseWorld.x, mouseWorld.y, delta);
        game.batch.begin();
        panelTienda.render(game.batch);
        botonVenderJoker.render(game.batch);
        renderCartelJokerSiCorresponde();
        game.batch.end();
    }

    private void prepararFrame() {
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private boolean puedeInteractuar() {
        return !gestorAnimaciones.isEsperandoTransicion() && !esperandoTransicion;
    }

    private void actualizarPreviewsCartas(boolean puedeInteractuar) {
        if (!puedeInteractuar) return;
        boolean cambio = gestorReordenamiento.previsualizarReordenamientoCartas(gestorCartas, cartasJugador);
        if (cambio) organizarCartas();
    }

    private void renderJokers() {
        jokerConHoverActual = null;
        for (VistaJoker j : jokers) {
            if (j != gestorJokers.getArrastrado()) {
                j.render(game.batch);
                if (j.isHover()) jokerConHoverActual = j;
            }
        }
        if (gestorJokers.getArrastrado() != null) {
            gestorJokers.getArrastrado().render(game.batch);
        }
    }

    private void renderCartelJokerSiCorresponde() {
        if (jokerConHoverActual != null) {
            jokerConHoverActual.renderCartelStats(game.batch, game);
        }
    }

    private void organizarCartas() {
        areaCartas.distribuir(cartasJugador, gestorCartas.getArrastrado());
        gestorReparto.organizarMano(cartasRival, Y_MANO_RIVAL, ANCHO_CARTA_RIVAL, separacion);
    }

    private void organizarJokers() {
        areaJokers.distribuir(jokers, gestorJokers.getArrastrado());
    }

    private void organizarMesa() {
        float inicioX = (Gdx.graphics.getWidth() - ANCHO_CARTA_MESA) / 2f - 200f;
        float pasoX = ANCHO_CARTA_MESA + 20f;
        for (int i = 0; i < cartasMesaJugador.size(); i++) {
            VistaCarta view = cartasMesaJugador.get(i);
            float posX = inicioX + i * pasoX;
            float posY = Y_MESA_JUGADOR;
            view.setPosition(posX, posY);
            view.setHandPosition(posX, posY);
        }
        for (int i = 0; i < cartasMesaRival.size(); i++) {
            VistaCarta view = cartasMesaRival.get(i);
            float posX = inicioX + i * pasoX;
            float posY = Y_MESA_RIVAL;
            view.setPosition(posX, posY);
            view.setHandPosition(posX, posY);
        }
    }

    public void jugarCarta(VistaCarta vistaCarta) {
        vistaCarta.setSeleccionada(false);
        vistaCarta.setTamaño(ANCHO_CARTA_MESA, ALTO_CARTA_MESA);
        gestorCartas.getSeleccionados().remove(vistaCarta);
        cartasJugador.remove(vistaCarta);
        cartasMesaJugador.add(vistaCarta);
        juego.agregarCartaJugador(vistaCarta.getCarta());
        organizarCartas();
        organizarMesa();
    }

    public void jugarCartaRival(VistaCarta vistaCarta) {
        vistaCarta.setTamaño(ANCHO_CARTA_MESA, ALTO_CARTA_MESA);
        cartasRival.remove(vistaCarta);
        vistaCarta.ponerBocaArriba(game.getAtlasCartas());
        cartasMesaRival.add(vistaCarta);
        juego.agregarCartaRival(vistaCarta.getCarta());
        organizarCartas();
        organizarMesa();
    }

    public void iniciarShader() {
        this.fondoPlasma = new Background();
    }

    private void actualizarCartasMesa(float delta) {
        for (VistaCarta c : new ArrayList<>(cartasMesaJugador)) {
            c.update(mouseWorld.x, mouseWorld.y, delta);
        }
        for (VistaCarta c : new ArrayList<>(cartasMesaRival)) {
            c.update(mouseWorld.x, mouseWorld.y, delta);
        }
    }

    private void actualizarDragJokers(float delta, boolean puedeInteractuar) {
        VistaJoker jokerArrastradoAntes = gestorJokers.getArrastrado();
        gestorJokers.update(mouseWorld.x, mouseWorld.y, delta, puedeInteractuar);
        if (puedeInteractuar) {
            boolean cambio = gestorReordenamiento.previsualizarReordenamientoJokers(gestorJokers, jokers);
            if (cambio) organizarJokers();
        }
        if (jokerArrastradoAntes != null && gestorJokers.getArrastrado() == null) {
            organizarJokers();
        }
    }

    public void iniciarAnimacionResolucion(ResolucionPuntaje resolucion, boolean esTruco, Runnable alTerminar) {
        gestorAnimacionResolucion.iniciar(resolucion,
            paso -> {
                textoFlotanteActual = paso.toString();
                resaltarPorOrigen(paso.origen);
                reproducirSonidoActivacion(paso.origen);
                if (esTruco) {
                    puntosTrucoDisplay = paso.chipsActual;
                    multTrucoDisplay = paso.multActual;
                } else {
                    puntosEnvidoDisplay = paso.chipsActual;
                    multEnvidoDisplay = paso.multActual;
                }
            },
            () -> {
                textoFlotanteActual = null;
                resaltarPorOrigen(null);
                if (esTruco) {
                    puntosTrucoDisplay = 0;
                    multTrucoDisplay = 1;
                } else {
                    puntosEnvidoDisplay = 0;
                    multEnvidoDisplay = 1;
                }
                alTerminar.run();
            }
        );
    }

    private void reproducirSonidoActivacion(String origen) {
        if (origen == null || origen.equals("Base")) return;
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        if (sonidos == null) return;
        boolean esJoker = jokers.stream().anyMatch(vj -> vj.getJoker().getNombre().equals(origen));
        if (esJoker) {
            sonidos.reproducirConVariacion("activar_joker");
        } else {
            sonidos.reproducirConVariacion("activar_carta");
        }
    }

    private void resaltarPorOrigen(String origen) {
        for (VistaJoker vj : jokers) {
            vj.setResaltado(origen != null && vj.getJoker().getNombre().equals(origen));
        }
        for (VistaCarta vc : cartasMesaJugador) {
            String nombreCarta = vc.getCarta().getNumero() + " de " + vc.getCarta().paloToString();
            vc.setResaltado(origen != null && nombreCarta.equals(origen));
        }
    }

    public void finalizarCombate(boolean victoriaJugador) {
        if (victoriaJugador) {
            game.getPerfilJugador().avanzarNivel();
            estado = EstadoPantalla.TIENDA;
            panelTienda = new PanelTienda(game, jugador, this::iniciarSalidaDeTienda);
        } else {
            game.setScreen(new SeleccionRivalScreen(game));
        }
    }

    private void iniciarSalidaDeTienda() {
        panelTienda.cerrar(() -> {
            estado = EstadoPantalla.SELECCION_RIVAL;
            panelSeleccionRival = new PanelSeleccionRival(game, this::onRivalElegido);
        });
    }

    private void onRivalElegido(DatosRival nuevoRival) {
        panelSeleccionRival.cerrar(() -> {
            this.datosRival = nuevoRival;
            estado = EstadoPantalla.JUGANDO;
            inicializarJuego();
        });
    }

    public void setTiempoNuevaRonda() { tiempoNuevaRonda = 2.5f; }
    public void setEsperandoTransicion(boolean esperandoTransicion) { this.esperandoTransicion = esperandoTransicion; }
    public void setIniciarNuevaRondaPendiente(boolean iniciarNuevaRondaPendiente) { this.iniciarNuevaRondaPendiente = iniciarNuevaRondaPendiente; }
    public void setTiempoNuevaRonda(float tiempoNuevaRonda) { this.tiempoNuevaRonda = tiempoNuevaRonda; }
    public ArrayList<VistaCarta> getCartasRival() { return cartasRival; }
    public int getTocoJugar() { return tocoJugar; }
    public void incrementarTocoJugar() { this.tocoJugar++; }
    public Main getGame() { return game; }
    public DatosRival getDatosRival() { return datosRival; }
    public Jugador getRival() { return rival; }
    public ArrayList<VistaCarta> getCartasJugador() { return cartasJugador; }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (panelPuntajes != null) {
            panelPuntajes.dispose();
            panelPuntajes = null;
        }
        if (fondoPlasma != null) {
            fondoPlasma.dispose();
            fondoPlasma = null;
        }
        if (cartasJugador != null) {
            for (VistaCarta carta : cartasJugador) carta.dispose();
            cartasJugador.clear();
        }
        if (cartasRival != null) {
            for (VistaCarta carta : cartasRival) carta.dispose();
            cartasRival.clear();
        }
        if (cartasMesaJugador != null) {
            for (VistaCarta carta : cartasMesaJugador) carta.dispose();
            cartasMesaJugador.clear();
        }
        if (cartasMesaRival != null) {
            for (VistaCarta carta : cartasMesaRival) carta.dispose();
            cartasMesaRival.clear();
        }
        if (jokers != null) {
            for (VistaJoker joker : jokers) joker.dispose();
            jokers.clear();
        }
    }

    @Override
    public void resize(int width, int height) {viewport.update(width, height, true);}
}
