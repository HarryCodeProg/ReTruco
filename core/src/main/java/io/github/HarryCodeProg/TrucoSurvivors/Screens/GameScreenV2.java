package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private EstadoPantalla pendingEstado = null;
    private boolean aplicarAnimacionInicial = true; // evitar animación de repartos repetida
    // persistencia / visibilidad
    private boolean panelTiendaVisible = false;
    private boolean panelSeleccionVisible = false;
    private VistaJoker jokerCompradoAnimando = null;
    private float jokerCompraX;
    private float jokerCompraY;
    private float jokerCompraObjetivoX;
    private float jokerCompraObjetivoY;
    private static final float VELOCIDAD_ANIMACION_JOKER_COMPRA = 1400f;


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
        // evitar NPE en puedeInteractuar: inicializar gestorAnimaciones vacio mínimo
        this.gestorAnimaciones = new GestorAnimacionesMano(
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            this::organizarCartas
        );

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
        // evitar NPE en puedeInteractuar
        this.gestorAnimaciones = new GestorAnimacionesMano(
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            this::organizarCartas
        );
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
        // colocar instantáneamente al iniciar
        organizarJokers(true);
        estado = EstadoPantalla.SELECCION_RIVAL;
        panelSeleccionRival = new PanelSeleccionRival(game, this::onPrimeraSeleccionRival);
        panelSeleccionVisible = true;
        panelTiendaVisible = false;
    }

    private void inicializarJuego() {
        this.fondoPlasma = new Background();
        this.jugador = game.getPerfilJugador().getJugador();
        if (datosRival == null) datosRival = new DatosRival("Maty", "", 0, true, 0); // fallback
        this.rival = new Jugador(datosRival.getNombre());
        this.tocoJugar = 0;
        this.esperandoTransicion = false;
        this.iniciarNuevaRondaPendiente = false;
        this.tiempoNuevaRonda = 0f;
        int rivalesVencidos = datosRival.getIndice();
        if (panelPuntajes != null) {
            panelPuntajes.setRivalNombre(rival.getNombre());
        }
        this.fondoPlasma.setRivalesVencidos(rivalesVencidos);
        Mazo mazoRival = Juego.crearMazoRival(datosRival.getNivelDificultad());
        this.juego = new Juego(jugador, rival, mazoRival);
        this.juego.setPuntajeMeta(datosRival.getPuntosMeta());
        this.controladorCombate = new ControladorCombate(this, juego);
        this.controladorIARival = new ControladorIARival(juego, controladorCombate, this);
        this.cartasJugador = new ArrayList<>();
        this.cartasRival = new ArrayList<>();
        // Reusar vistas de jokers si es posible, sino recrear silenciosamente
        ArrayList<Joker> jokersModelo = jugador.getJokers();
        if (this.jokers == null) this.jokers = new ArrayList<>();
        if (this.jokers.size() == jokersModelo.size()) {
            for (int i = 0; i < jokersModelo.size(); i++) {
                VistaJoker view = this.jokers.get(i);
                view.setTamaño(ANCHO_JOKER, ALTO_JOKER);
            }
        } else {
            if (this.jokers != null) {
                for (VistaJoker v : this.jokers) v.dispose();
            }
            this.jokers = new ArrayList<>();
            for (Joker joker : jokersModelo) {
                VistaJoker view = new VistaJoker(joker, game.getAtlasJokers());
                view.setTamaño(ANCHO_JOKER, ALTO_JOKER);
                this.jokers.add(view);
            }
        }
        this.jokers = (this.jokers == null) ? new ArrayList<>() : this.jokers;
        this.cartasMesaJugador = new ArrayList<>();
        this.cartasMesaRival = new ArrayList<>();
        this.gestorCartas = new GestorInputArrastrable<>(cartasJugador);
        this.gestorJokers = new GestorInputArrastrable<>(jokers);
        // inicializar gestorAnimaciones con datos reales
        this.gestorAnimaciones = new GestorAnimacionesMano(
            cartasJugador, cartasRival,
            cartasMesaJugador, cartasMesaRival,
            this::organizarCartas
        );
        this.gestorAccion = new GestorAccion(juego, this, controladorCombate, gestorCartas, gestorAnimaciones);
        // Posicionar jokers sin animación al iniciar/reiniciar
        organizarJokers(true);
        // Cartas: si queremos animación sólo la primera vez, usar flag aplicarAnimacionInicial
        if (aplicarAnimacionInicial) {
            gestorAnimaciones.iniciarTransicion(
                new ArrayList<>(jugador.getMano()),
                new ArrayList<>(rival.getMano())
            );
            aplicarAnimacionInicial = false;
        }
        float anchoMazo = 60f;
        float altoMazo = 88f;
        float posX = 1280f - anchoMazo - 25f;
        float posY = 130f;
        this.vistaMazo = new VistaMazo(posX, posY, anchoMazo, altoMazo, game.getAtlasCartas(), game.getFuentePrincipal(), game.getPixelBlanco());
        this.hudController = new HUDController(this.vistaMazo);
        organizarCartas();
    }

    public void iniciarNuevaRonda() {
        if (gestorAnimaciones == null) return;
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
        actualizarJokersYVenta(delta);
        actualizarAnimacionCompraJoker(delta);
        if (estado == EstadoPantalla.TIENDA) {
            renderConTienda(delta);
            if (pendingEstado != null) { enterState(pendingEstado); pendingEstado = null; }
            return;
        }
        if (estado == EstadoPantalla.SELECCION_RIVAL) {
            renderConSeleccionRival(delta);
            if (pendingEstado != null) { enterState(pendingEstado); pendingEstado = null; }
            return;
        }
        if (iniciarNuevaRondaPendiente) {
            tiempoNuevaRonda -= delta;
            if (tiempoNuevaRonda <= 0f) {
                if (gestorAnimaciones == null || !gestorAnimaciones.isEsperandoTransicion()) {
                    iniciarNuevaRondaPendiente = false;
                    esperandoTransicion = false;
                    iniciarNuevaRonda();
                }
            }
        }
        if (gestorAnimaciones != null) gestorAnimaciones.update(delta);
        gestorAnimacionResolucion.update(delta);
        boolean puedeInteract = puedeInteractuar();
        hudController.update(mouseWorld, puedeInteract, gestorAccion);
        if (controladorIARival != null) controladorIARival.update(puedeInteract);
        // Actualizaciones comunes
        //gestorVentaJoker.update(mouseWorld.x, mouseWorld.y, jokers, jugador, (v) -> organizarJokers());
        VistaCarta cartaArrastradaAntes = gestorCartas.getArrastrado();
        VistaJoker jokerArrastradoAntes = gestorJokers.getArrastrado();
        gestorCartas.update(mouseWorld.x, mouseWorld.y, delta, puedeInteract);
        // En JUGANDO permitimos drops/reorder; aquí usamos canDrop según estado
        gestorJokers.update(mouseWorld.x, mouseWorld.y, delta, true); // input always
        //actualizarJokers(delta);
        if (!puedeInteract) {
            for (VistaCarta c : new ArrayList<>(cartasJugador)) c.update(mouseWorld.x, mouseWorld.y, delta);
        }
        for (VistaCarta c : new ArrayList<>(cartasRival)) c.update(mouseWorld.x, mouseWorld.y, delta);
        actualizarCartasMesa(delta);
        actualizarPreviewsCartas(puedeInteract);
        hudController.actualizarSeleccion(juego, puedeInteract, gestorCartas, gestorJokers, cartasJugador, jokers);
        if (cartaArrastradaAntes != null && gestorCartas.getArrastrado() == null) organizarCartas();
        if (jokerArrastradoAntes != null && gestorJokers.getArrastrado() == null) organizarJokers(true);
        renderizar(delta);
        if (pendingEstado != null) { enterState(pendingEstado); pendingEstado = null; }
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
        if (panelSeleccionRival != null && panelSeleccionVisible) {
            panelSeleccionRival.updateAnimacion(delta);
            panelSeleccionRival.update(mouseWorld.x, mouseWorld.y);
        }
        game.batch.begin();
        renderAreaJokers(game.batch, game.getPixelBlanco());
        renderContadoresAreas(game.batch, jugador, false);
        renderJokers();
        if (partidaIniciada) {
            panelPuntajes.renderTextos(   game.batch, game.getFuentePrincipal(), juego, jugador, rival,
                PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y, gestorAnimacionResolucion,
                puntosTrucoDisplay, multTrucoDisplay, puntosEnvidoDisplay, multEnvidoDisplay);
        }
        game.batch.end();
        game.batch.begin();
        if (panelSeleccionRival != null && panelSeleccionVisible) {
            panelSeleccionRival.render(game.batch);
        }
        gestorVentaJoker.render(game.batch);
        renderCartelJokerSiCorresponde();
        game.batch.end();
    }

    private void renderConTienda(float delta) {
        if (panelTienda == null) {
            if (jugador == null) jugador = game.getPerfilJugador() != null ? game.getPerfilJugador().getJugador() : null;
            panelTienda = new PanelTienda(
                game, jugador, this::iniciarSalidaDeTienda, this::iniciarAnimacionCompraJoker
            );
        }
        ScreenUtils.clear(0.1f, 0.12f, 0.16f, 1f);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        game.batch.end();
        panelPuntajes.renderFondosYCajas(camera, PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y);
        if (panelTienda != null && panelTiendaVisible) {
            panelTienda.updateAnimacion(delta);
            panelTienda.update(mouseWorld.x, mouseWorld.y, delta);
        }
        game.batch.begin();
        renderAreaJokers(game.batch, game.getPixelBlanco());
        renderContadoresAreas(game.batch, jugador, false);
        renderJokers();
        panelPuntajes.renderTextos(
            game.batch, game.getFuentePrincipal(), juego, jugador, rival,
            PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y, gestorAnimacionResolucion,
            puntosTrucoDisplay, multTrucoDisplay, puntosEnvidoDisplay, multEnvidoDisplay
        );
        if (juego != null && vistaMazo != null) {
            vistaMazo.render(game.batch, juego.getMazoJugador().getCartasRestantesOrdenadas(), juego.getMazoJugador().getTamañoMazo());
        }
        game.batch.end();
        game.batch.begin();
        if (panelTienda != null && panelTiendaVisible) {
            panelTienda.render(game.batch);
        }
        if (jokerCompradoAnimando != null) {
            jokerCompradoAnimando.render(game.batch);
        }
        gestorVentaJoker.render(game.batch);
        renderCartelJokerSiCorresponde();
        game.batch.end();
    }

    private void renderAreaCartas(SpriteBatch batch, Texture pixelBlanco) {
        float areaX = MARGEN_AREA_LATERAL;
        float areaAncho = ANCHO_AREA_JUGADOR;
        float cartasY = Y_MANO_JUGADOR - 10f;
        batch.setColor(0.05f, 0.05f, 0.08f, 0.45f);
        batch.draw(pixelBlanco, areaX, cartasY, areaAncho, ALTO_AREA_CARTAS);
        batch.setColor(0.25f, 0.28f, 0.35f, 0.7f);
        batch.draw(pixelBlanco, areaX, cartasY, areaAncho, 2f);
        batch.draw(pixelBlanco, areaX, cartasY + ALTO_AREA_CARTAS - 2f, areaAncho, 2f);
        batch.draw(pixelBlanco, areaX, cartasY, 2f, ALTO_AREA_CARTAS);
        batch.draw(pixelBlanco, areaX + areaAncho - 2f, cartasY, 2f, ALTO_AREA_CARTAS);
        batch.setColor(Color.WHITE);
    }

    private void renderAreaJokers(SpriteBatch batch, Texture pixelBlanco) {
        float areaX = MARGEN_AREA_LATERAL;
        float areaAncho = ANCHO_AREA_JUGADOR;
        float jokersY = Y_JOKERS - 10f;
        batch.setColor(0.05f, 0.05f, 0.08f, 0.45f);
        batch.draw(pixelBlanco, areaX, jokersY, areaAncho, ALTO_AREA_JOKERS);
        batch.setColor(0.25f, 0.28f, 0.35f, 0.7f);
        batch.draw(pixelBlanco, areaX, jokersY, areaAncho, 2f);
        batch.draw(pixelBlanco, areaX, jokersY + ALTO_AREA_JOKERS - 2f, areaAncho, 2f);
        batch.draw(pixelBlanco, areaX, jokersY, 2f, ALTO_AREA_JOKERS);
        batch.draw(pixelBlanco, areaX + areaAncho - 2f, jokersY, 2f, ALTO_AREA_JOKERS);
        batch.setColor(Color.WHITE);
    }

    private void renderContadoresAreas(SpriteBatch batch, Jugador jugador, boolean mostrarCartas) {
        BitmapFont font = game.getFuentePrincipal();
        String textoJokers = jokers.size() + "/" + jugador.getTamañoJokers();
        float xContador = MARGEN_AREA_LATERAL + 8f;
        float yJokers = Y_JOKERS - 18f;
        font.setColor(Color.WHITE);
        if (mostrarCartas) {
            String textoCartas = cartasJugador.size() + "/" + jugador.getTamañoMano();
            float yCartas = Y_MANO_JUGADOR - 18f;
            font.draw(batch, textoCartas, xContador, yCartas);
        }
        font.draw(batch, textoJokers, xContador, yJokers);
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
        boolean gestorEsperando = (gestorAnimaciones != null) ? gestorAnimaciones.isEsperandoTransicion() : false;
        return !gestorEsperando && !esperandoTransicion;
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
                if (j.contiene(mouseWorld.x, mouseWorld.y)) {
                    jokerConHoverActual = j;
                }
            }
        }
        if (gestorJokers.getArrastrado() != null) {
            gestorJokers.getArrastrado().render(game.batch);
        }
        botonVenderJoker.setVisible(jokerConHoverActual != null);
    }

    private void renderCartelJokerSiCorresponde() {
        // Buscamos si hay algún joker que tenga el mouse encima
        VistaJoker jokerConHover = null;
        for (VistaJoker j : jokers) {
            if (j.isHover() || j.contiene(mouseWorld.x, mouseWorld.y)) {
                jokerConHover = j;
                break;
            }
        }
        // Si encontramos uno, dibujamos su cartel de stats
        if (jokerConHover != null) {
            jokerConHover.renderCartelStats(game.batch, game);
        }
    }

    private void actualizarJokersYVenta(float delta) {
        actualizarJokers(delta); // ya update+reorder+snap, dejalo como esta
        gestorVentaJoker.update(mouseWorld.x, mouseWorld.y, jokers, jugador, (v) -> organizarJokers());
    }

    private void organizarJokers() { organizarJokers(false); }

    private void organizarJokers(boolean instant) {
        areaJokers.distribuir(jokers, gestorJokers.getArrastrado());
        if (instant) {
            for (VistaJoker v : jokers) {
                v.setPosition(v.getHandTargetX(), areaJokers.getY()); // requiere getHandTargetY() si tu VistaJoker guarda Y aparte; ver nota abajo
            }
        }
    }

    private void organizarCartas() {
        areaCartas.distribuir(cartasJugador, gestorCartas.getArrastrado());
        gestorReparto.organizarMano(cartasRival, Y_MANO_RIVAL, ANCHO_CARTA_RIVAL, separacion);
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
        vistaCarta.cambiarBocaArriba(game.getAtlasCartas());
        cartasMesaRival.add(vistaCarta);
        juego.agregarCartaRival(vistaCarta.getCarta());
        organizarCartas();
        organizarMesa();
    }

    public void iniciarShader() {
        this.fondoPlasma = new Background();
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

    public void iniciarAnimacionResolucion(ResolucionPuntaje resolucion, boolean esTruco, Runnable alTerminar) {
        if (resolucion == null) {
            if (alTerminar != null) alTerminar.run();
            return;
        }
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
                if (alTerminar != null) alTerminar.run();
            }
        );
    }

    private void reproducirSonidoActivacion(String origen) {
        if (origen == null || origen.equals("Base")) return;
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        if (sonidos == null) return;
        boolean esJoker = jokers.stream().anyMatch(vj -> vj.getJoker().getNombre().equals(origen));
        if (esJoker) sonidos.reproducirConVariacion("activar_joker");
        else sonidos.reproducirConVariacion("activar_carta");
    }

    private void resaltarPorOrigen(String origen) {
        for (VistaJoker vj : jokers) vj.setResaltado(origen != null && vj.getJoker().getNombre().equals(origen));
        for (VistaCarta vc : cartasMesaJugador) {
            String nombreCarta = vc.getCarta().getNumero() + " de " + vc.getCarta().paloToString();
            vc.setResaltado(origen != null && nombreCarta.equals(origen));
        }
    }

    private void actualizarCartasMesa(float delta) {
        for (VistaCarta c : new ArrayList<>(cartasMesaJugador)) c.update(mouseWorld.x, mouseWorld.y, delta);
        for (VistaCarta c : new ArrayList<>(cartasMesaRival)) c.update(mouseWorld.x, mouseWorld.y, delta);
    }

    /** Comportamiento de drag/reorden de jokers, IDÉNTICO en los 3 estados de pantalla (JUGANDO, TIENDA, SELECCION_RIVAL). */
    private void actualizarJokers(float delta) {
        VistaJoker antes = gestorJokers.getArrastrado();
        // 1. Actualizamos el estado interno (hover, escala, etc.) de CADA joker
        for (VistaJoker vj : jokers) {
            vj.update(mouseWorld.x, mouseWorld.y, delta);
        }
        // 2. Actualizamos el gestor de arrastre
        gestorJokers.update(mouseWorld.x, mouseWorld.y, delta, true);
        // 3. Previsualizamos si hay reordenamiento
        boolean cambio = gestorReordenamiento.previsualizarReordenamientoJokers(gestorJokers, jokers);
        if (cambio) organizarJokers();
        if (antes != null && gestorJokers.getArrastrado() == null) organizarJokers();
    }

    private void onPrimeraSeleccionRival(DatosRival rivalElegido) {
        panelSeleccionRival.cerrar(() -> {
            this.datosRival = rivalElegido;
            pendingEstado = EstadoPantalla.JUGANDO;
        });
    }

    private void onRivalElegido(DatosRival nuevoRival) {
        panelSeleccionRival.cerrar(() -> {
            this.datosRival = nuevoRival;
            pendingEstado = EstadoPantalla.JUGANDO;
        });
    }

    private void iniciarSalidaDeTienda() {
        panelTienda.cerrar(() -> pendingEstado = EstadoPantalla.SELECCION_RIVAL);
    }

    private void enterState(EstadoPantalla newState) {
        exitState(this.estado);
        switch (newState) {
            case SELECCION_RIVAL:
                panelSeleccionRival = new PanelSeleccionRival(game, this::onRivalElegido);
                panelSeleccionVisible = true;
                panelTiendaVisible = false;
                partidaIniciada = false;
                break;
            case TIENDA:
                panelTienda = new PanelTienda(game, jugador, this::iniciarSalidaDeTienda,
                    this::iniciarAnimacionCompraJoker);
                panelTiendaVisible = true;
                panelSeleccionVisible = false;
                break;
            case JUGANDO:
                panelTiendaVisible = false;
                panelSeleccionVisible = false;
                aplicarAnimacionInicial = true;
                inicializarJuego();
                textoFlotanteActual = null;
                puntosTrucoDisplay = 0;
                multTrucoDisplay = 1;
                puntosEnvidoDisplay = 0;
                multEnvidoDisplay = 1;
                gestorAnimacionResolucion = new GestorAnimacionResolucion();
                partidaIniciada = true;
                break;
            case VICTORIA:
                break;
        }
        this.estado = newState;
    }

    private void exitState(EstadoPantalla oldState) {
        if (oldState == null) return;
        switch (oldState) {
            case TIENDA:
                // mantengo panel en memoria para reutilizarlo; solo oculto
                panelTiendaVisible = false;
                break;
            case SELECCION_RIVAL:
                panelSeleccionVisible = false;
                break;
            default:
                break;
        }
    }

    public void finalizarCombate(boolean victoriaJugador) {
        if (victoriaJugador) {
            game.getPerfilJugador().avanzarNivel();
            pendingEstado = EstadoPantalla.TIENDA;
        } else {
            pendingEstado = EstadoPantalla.SELECCION_RIVAL;
        }
    }

    private void iniciarAnimacionCompraJoker(VistaItemTienda item) {
        Joker joker = item.getItem().getJoker();
        if (joker == null) return;
        // Vista temporal del Joker comprado
        jokerCompradoAnimando = new VistaJoker(joker, game.getAtlasJokers());
        jokerCompradoAnimando.setTamaño(ANCHO_JOKER, ALTO_JOKER);
        // La tienda está desplazada verticalmente.
        // La posición real de dibujo es Y + offset del panel.
        jokerCompraX = item.getX();
        jokerCompraY = item.getY() + panelTienda.getOffsetY();
        jokerCompradoAnimando.setPosition(jokerCompraX, jokerCompraY);
        // Calculamos cómo quedaría la fila con el nuevo Joker.
        ArrayList<VistaJoker> simulacion = new ArrayList<>(jokers);
        simulacion.add(jokerCompradoAnimando);
        areaJokers.distribuir(simulacion, null);
        jokerCompraObjetivoX = jokerCompradoAnimando.getHandTargetX();
        jokerCompraObjetivoY = jokerCompradoAnimando.getHandTargetY();
        // Dejamos preparados los Jokers existentes para su nueva posición.
        organizarJokersConNuevoSlot();
    }

    private void organizarJokersConNuevoSlot() {
        ArrayList<VistaJoker> simulacion = new ArrayList<>(jokers);
        if (jokerCompradoAnimando != null) {
            simulacion.add(jokerCompradoAnimando);
        }
        areaJokers.distribuir(simulacion, jokerCompradoAnimando);
    }

    private float moverHacia(float actual, float objetivo, float velocidad, float delta) {
        float distancia = objetivo - actual;
        if (Math.abs(distancia) <= velocidad * delta) {
            return objetivo;
        }
        return actual + Math.signum(distancia) * velocidad * delta;
    }

    private void actualizarAnimacionCompraJoker(float delta) {
        if (jokerCompradoAnimando == null) {
            return;
        }
        jokerCompraX = moverHacia(
            jokerCompraX,
            jokerCompraObjetivoX,
            VELOCIDAD_ANIMACION_JOKER_COMPRA,
            delta
        );
        jokerCompraY = moverHacia(
            jokerCompraY,
            jokerCompraObjetivoY,
            VELOCIDAD_ANIMACION_JOKER_COMPRA,
            delta
        );
        jokerCompradoAnimando.setPosition(jokerCompraX, jokerCompraY);
        if (jokerCompraX == jokerCompraObjetivoX
            && jokerCompraY == jokerCompraObjetivoY) {
            jokers.add(jokerCompradoAnimando);
            jokerCompradoAnimando = null;
            organizarJokers();
        }
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
        if (panelPuntajes != null) { panelPuntajes.dispose(); panelPuntajes = null; }
        if (fondoPlasma != null) { fondoPlasma.dispose(); fondoPlasma = null; }
        if (cartasJugador != null) { for (VistaCarta carta : cartasJugador) carta.dispose(); cartasJugador.clear(); }
        if (cartasRival != null) { for (VistaCarta carta : cartasRival) carta.dispose(); cartasRival.clear(); }
        if (cartasMesaJugador != null) { for (VistaCarta carta : cartasMesaJugador) carta.dispose(); cartasMesaJugador.clear(); }
        if (cartasMesaRival != null) { for (VistaCarta carta : cartasMesaRival) carta.dispose(); cartasMesaRival.clear(); }
        if (jokers != null) { for (VistaJoker joker : jokers) joker.dispose(); jokers.clear(); }
        if (gestorAnimaciones != null) { /* si tiene dispose impl, llamalo */ }
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }
}
