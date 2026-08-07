package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.HarryCodeProg.TrucoSurvivors.*;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EstadoCombate;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.*;
import io.github.HarryCodeProg.TrucoSurvivors.Rival.IARival;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.*;

import java.util.ArrayList;

import static io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout.*;

public class GameScreen implements Screen {
    private Main game;
    private Jugador jugador;
    private Jugador rival;
    private ArrayList<VistaCarta> cartasJugador;
    private ArrayList<VistaCarta> cartasRival;
    private ArrayList<VistaJoker> jokers;
    float anchoCarta = 120;
    float separacion = 8;
    private Rectangle zonaMesa;
    private ArrayList<VistaCarta> cartasMesaJugador;
    private ArrayList<VistaCarta> cartasMesaRival;
    private Juego juego;
    private GestorInputArrastrable<VistaCarta> gestorCartas;
    private GestorInputArrastrable<VistaJoker> gestorJokers;
    private int tocoJugar = 0;
    private int indicePreview = -1;
    private ArrayList<Float> slotsX = new ArrayList<>();
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 mouseWorld;
    private PanelPuntajes panelPuntajes;
    private GestorReparto gestorReparto;
    private boolean esperandoTransicion = false;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Texture hojaJokers;
    private DatosRival datosRival;
    private ArrayList<Float> slotsJokersX = new ArrayList<>();
    private int indicePreviewJoker = -1;
    private Background fondoPlasma;
    private final GestorReordenamiento gestor = new GestorReordenamiento();
    private GestorAnimacionesMano gestorAnimaciones;
    //private GestorBotones gestorBotones;
    private boolean iniciarNuevaRondaPendiente;
    private float tiempoNuevaRonda;
    private ControladorCombate controladorCombate;
    private GestorAccion gestorAccion;
    private IARival iaRival;

    // Encapsulamiento del estado del menú Envido
    private final EnvidoMenuState envidoMenuState = new EnvidoMenuState();

    private VistaMazo vistaMazo;
    private final GlyphLayout layout = new GlyphLayout();
    private GameBotones botones;
    private GestorAnimacionResolucion gestorAnimacionResolucion = new GestorAnimacionResolucion();
    private String textoFlotanteActual = null;
    private double puntosTrucoDisplay = 0;
    private double multTrucoDisplay = 1;
    private double puntosEnvidoDisplay = 0;
    private double multEnvidoDisplay = 1;

    public GameScreen(Main game, DatosRival datosRival) {
        this.game = game;
        this.datosRival = datosRival;
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        mouseWorld = new Vector3();
        panelPuntajes = new PanelPuntajes();
        gestorReparto = new GestorReparto();
        this.cartasMesaJugador = new ArrayList<>();
        this.cartasMesaRival = new ArrayList<>();
        botones = new GameBotones();

        zonaMesa = new Rectangle(0, 250, Gdx.graphics.getWidth(), 200);
        iniciarShader();
        inicializarJuego();
    }

    private void inicializarJuego() {
        this.fondoPlasma = new Background();
        // El jugador YA NO se crea de cero: viene persistente desde el perfil de la sesión,
        // con su mazo, jokers y cualquier modificación de cartas de rivales anteriores.
        jugador = game.getPerfilJugador().getJugador();
        // El rival sí es nuevo cada vez, con un mazo armado a medida de su dificultad.
        rival = new Jugador(datosRival.getNombre());

        int rivalesVencidos = datosRival.getIndice();
        this.fondoPlasma.setRivalesVencidos(rivalesVencidos);

        Mazo mazoRival = Juego.crearMazoRival(datosRival.getNivelDificultad()); // ver nota abajo si no tenes este metodo/campo todavia
        this.juego = new Juego(jugador, rival, mazoRival);
        //controladorCombate = new ControladorCombate(this, juego);
        this.juego.setPuntajeMeta(datosRival.getPuntosMeta());
        this.iaRival = new IARival(juego);
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
            () -> organizarCartas()
        );
        //this.gestorAccion = new GestorAccion(juego, this, controladorCombate, gestorCartas, gestorAnimaciones);
        ArrayList<Joker> jokersModelo = jugador.getJokers();
        for (int i = 0; i < jokersModelo.size(); i++) {
            Joker joker = jokersModelo.get(i);
            VistaJoker view = new VistaJoker(joker, game.getAtlasJokers());
            view.setTamaño(ANCHO_JOKER, ALTO_JOKER);
            view.setPosition(200 + (i * 100), Y_JOKERS);
            jokers.add(view);
        }
        gestorAnimaciones.iniciarTransicion(
            new ArrayList<>(jugador.getMano()),
            new ArrayList<>(rival.getMano())
        );
        float anchoMazo = 60f;
        float altoMazo = 88f;
        float posX = 1280f - anchoMazo - 25f;
        float posY = 130f;
        this.vistaMazo = new VistaMazo(posX, posY, anchoMazo, altoMazo, game.getAtlasCartas(), game.getFuentePrincipal(), game.getPixelBlanco());
        organizarCartas();
    }

    private Mazo crearMazoParaRival(DatosRival datos) {
        return Juego.crearMazoRival(datos.getIndice());
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
        actualizarBotones(puedeInteractuar);
        actualizarIA(puedeInteractuar);
        gestorCartas.update(mouseWorld.x, mouseWorld.y, delta, puedeInteractuar);
        gestorJokers.update(mouseWorld.x, mouseWorld.y, delta, puedeInteractuar);
        if (!puedeInteractuar) {
            for (int i = 0; i < cartasJugador.size(); i++) {
                cartasJugador.get(i).update(mouseWorld.x, mouseWorld.y, delta);
            }
        }
        for (int i = 0; i < cartasRival.size(); i++) {
            cartasRival.get(i).update(mouseWorld.x, mouseWorld.y, delta);
        }
        actualizarCartasMesa(delta);
        actualizarSeleccion(puedeInteractuar);
        actualizarPreviews(puedeInteractuar);
        renderizar(delta);
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

    private void actualizarBotones(boolean puedeInteractuar) {
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouseWorld);
        vistaMazo.update(mouseWorld.x, mouseWorld.y);
        if (Gdx.input.justTouched()) {
            if (vistaMazo.tocar(mouseWorld.x, mouseWorld.y)) {
                return;
            }
        }
        if (vistaMazo.isModalAbierto()) {
            botones.gestor.update(-1000, -1000);
            return;
        }
        if (puedeInteractuar) {
            Boton boton = botones.gestor.obtenerBotonCliqueado();
            if (boton != null) {
                if (boton == botones.envidoOpciones) {
                    envidoMenuState.alternar();
                } else {
                    if (boton == botones.envido || boton == botones.realEnvido || boton == botones.faltaEnvido) {
                        envidoMenuState.cerrar();
                    }
                    gestorAccion.ejecutarAccion(boton.getAccion());
                }
            } else if (Gdx.input.justTouched() && envidoMenuState.isAbierto()) {
                envidoMenuState.cerrar();
            }
        } else {
            envidoMenuState.cerrar();
        }
        botones.gestor.update(mouseWorld.x, mouseWorld.y);
    }

    private void actualizarPreviews(boolean puedeInteractuar) {
        if (!puedeInteractuar) {
            indicePreview = -1;
            indicePreviewJoker = -1;
            return;
        }
        if (gestorCartas.getArrastrado() != null) {
            indicePreview = gestor.calcularIndicePreview(gestorCartas.getArrastrado(), slotsX, anchoCarta);
            gestor.organizarPreview(cartasJugador, gestorCartas.getArrastrado(), indicePreview, anchoCarta,
                separacion, Y_MANO_JUGADOR, Gdx.graphics.getWidth());
            if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                gestorCartas.getArrastrado().input(mouseWorld.x, mouseWorld.y);
                if (indicePreview >= 0) {
                    cartasJugador.remove(gestorCartas.getArrastrado());
                    cartasJugador.add(indicePreview, gestorCartas.getArrastrado());
                }
                gestorCartas.soltar();
                indicePreview = -1;
                organizarCartas();
            }
        } else {
            indicePreview = -1;
        }
        if (gestorJokers.getArrastrado() != null) {
            indicePreviewJoker = gestor.calcularIndicePreview(gestorJokers.getArrastrado(), slotsJokersX, ANCHO_JOKER);
            gestor.organizarPreview(jokers, gestorJokers.getArrastrado(), indicePreviewJoker, ANCHO_JOKER,
                SEPARACION_JOKER, Y_JOKERS, Gdx.graphics.getWidth());
            if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                gestorJokers.getArrastrado().input(mouseWorld.x, mouseWorld.y);
                if (indicePreviewJoker >= 0) {
                    jokers.remove(gestorJokers.getArrastrado());
                    jokers.add(indicePreviewJoker, gestorJokers.getArrastrado());
                }
                gestorJokers.soltar();
                indicePreviewJoker = -1;
                organizarCartas();
            }
        } else {
            indicePreviewJoker = -1;
        }
    }

    private void actualizarSeleccion(boolean puedeInteractuar) {
        gestorJokers.getSeleccionados().clear();
        gestorCartas.getSeleccionados().clear();
        for (VistaJoker j : jokers) {
            if (j.isSeleccionada()) gestorJokers.getSeleccionados().add(j);
        }
        for (VistaCarta c : cartasJugador) {
            if (c.isSeleccionada()) gestorCartas.getSeleccionados().add(c);
        }
        botones.gestor.setHabilitado(Accion.JUGAR_CARTA, puedeInteractuar && gestorCartas.getSeleccionados().size() == 1);
        botones.gestor.setHabilitado(Accion.DESCARTAR, puedeInteractuar);
        botones.gestor.setHabilitado(Accion.IR_AL_MAZO, puedeInteractuar);
        botones.gestor.setHabilitado(Accion.TRUCO, puedeInteractuar);
        botones.gestor.setHabilitado(Accion.ENVIDO, puedeInteractuar);
        boolean hayCantoPendiente = juego.hayCantoEnvidoPendiente() || juego.hayCantoTrucoPendiente();
        boolean puedeEnvido = puedeInteractuar && juego.puedeCantarEnvidoNivel(juego.getJugador(), 1);
        boolean puedeReal   = puedeInteractuar && (juego.puedeEscalarEnvido(juego.getJugador()) || juego.puedeCantarEnvidoNivel(juego.getJugador(), 2));
        boolean puedeFalta  = puedeInteractuar && (juego.puedeEscalarEnvido(juego.getJugador()) || juego.puedeCantarEnvidoNivel(juego.getJugador(), 3));
        boolean sePuedeCantarAlgo = puedeEnvido || puedeReal || puedeFalta;
        boolean esTurnoJugador = juego.getTurnoActual().equals(juego.getJugador());
        botones.gestor.setHabilitado(Accion.JUGAR_CARTA, puedeInteractuar && esTurnoJugador && gestorCartas.getSeleccionados().size() == 1);
        botones.envidoOpciones.setHabilitado(sePuedeCantarAlgo);
        botones.envidoOpciones.setVisible(!hayCantoPendiente);
        boolean mostrarOpciones = envidoMenuState.debeMostrarOpciones(juego.hayCantoEnvidoPendiente());
        botones.envido.setHabilitado(puedeEnvido);
        botones.envido.setVisible(mostrarOpciones && puedeEnvido);
        botones.realEnvido.setHabilitado(puedeReal);
        botones.realEnvido.setVisible(mostrarOpciones && puedeReal);
        botones.faltaEnvido.setHabilitado(puedeFalta);
        botones.faltaEnvido.setVisible(mostrarOpciones && puedeFalta);
        int proximoTruco = juego.proximoNivelTrucoDisponible(juego.getJugador());
        botones.truco.setHabilitado(puedeInteractuar && proximoTruco == 1);
        botones.truco.setVisible(proximoTruco == 1 || (juego.getMesa().getMesaJugador().isEmpty() && juego.getMesa().getMesaRival().isEmpty()));
        botones.valeCuatro.setHabilitado(puedeInteractuar && juego.puedeEscalarTruco(juego.getJugador()));
        botones.valeCuatro.setVisible(juego.hayCantoTrucoPendiente() && juego.puedeEscalarTruco(juego.getJugador()));
        botones.quiero.setHabilitado(puedeInteractuar && hayCantoPendiente);
        botones.quiero.setVisible(hayCantoPendiente);
        botones.noQuiero.setHabilitado(puedeInteractuar && hayCantoPendiente);
        botones.noQuiero.setVisible(hayCantoPendiente);
    }

    private void renderizar(float delta) {
        ScreenUtils.clear(0.1f, 0.12f, 0.16f, 1f);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        game.batch.end();
        panelPuntajes.renderFondosYCajas(camera, PANEL_PUNTAJES_X, PANEL_PUNTAJES_Y);
        game.batch.begin();
        if (textoFlotanteActual != null) {
            BitmapFont font = game.getFuentePrincipal();
            GlyphLayout layout = new GlyphLayout();
            layout.setText(font, textoFlotanteActual);
            float tx = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float ty = GameLayout.TECHO_MESA + 60f;
            font.setColor(Color.GOLD);
            font.draw(game.batch, textoFlotanteActual, tx, ty);
            font.setColor(Color.WHITE);
        }
        renderMesa();
        renderRival();
        renderJugador();
        renderJokers();
        renderBotones();
        renderTextosPanelPuntajes();
        vistaMazo.render(game.batch, juego.getMazoJugador().getCartasRestantesOrdenadas(), juego.getMazoJugador().getTamañoMazo());
        game.batch.end();
    }

    private void renderTextosPanelPuntajes() {
        float y = PANEL_PUNTAJES_Y;
        // 1. Rival Truco
        float xSeparador = PANEL_PUNTAJES_X + PanelPuntajes.ANCHO_CAJA_BASE;
        dibujarTextoCentrado(String.valueOf((int) rival.getMultiplicadorTruco()), xSeparador + PanelPuntajes.ESPACIO_X, PanelPuntajes.ANCHO_CAJA_MULT, y, Color.WHITE);
        // (Opcional) Si la caja del rival no tiene base y solo tiene mult, podés omitir la X o dibujarla si tenés base del rival.
        y -= PanelPuntajes.ESPACIO_LINEA;
        // 2. Rival Envido
        dibujarTextoCentrado(String.valueOf((int) rival.getMultiplicadorEnvido()), xSeparador + PanelPuntajes.ESPACIO_X, PanelPuntajes.ANCHO_CAJA_MULT, y, Color.WHITE);
        y -= PanelPuntajes.ESPACIO_LINEA;
        // 3. Puntos Rival
        dibujarTextoCentrado(String.valueOf((int) juego.getPuntosRival()), PANEL_PUNTAJES_X, PanelPuntajes.ANCHO_CAJA_SIMPLE + 100f, y, Color.WHITE);
        y -= PanelPuntajes.ESPACIO_LINEA;
        // 4. Meta
        dibujarTextoCentrado(String.valueOf((int) juego.getPuntajeMeta()), PANEL_PUNTAJES_X, PanelPuntajes.ANCHO_CAJA_SIMPLE + 100f, y, Color.WHITE);
        y -= PanelPuntajes.ESPACIO_LINEA;
        // 5. Puntos Jugador
        dibujarTextoCentrado(String.valueOf((int) juego.getPuntosJugador()), PANEL_PUNTAJES_X, PanelPuntajes.ANCHO_CAJA_SIMPLE + 100f, y, Color.WHITE);
        y -= PanelPuntajes.ESPACIO_LINEA;
        // 6. Truco del Jugador (Base X Multiplicador)
        double puntosTrucoAMostrar = gestorAnimacionResolucion.isActiva() ? puntosTrucoDisplay : 0;
        double multTrucoAMostrar = gestorAnimacionResolucion.isActiva() ? multTrucoDisplay : 1;
        dibujarTextoCentrado(String.valueOf((int) puntosTrucoAMostrar), PANEL_PUNTAJES_X, PanelPuntajes.ANCHO_CAJA_BASE, y, Color.WHITE);
        // --- AQUÍ DIBUJAMOS LA X EN EL ESPACIO INTERMEDIO ---
        dibujarTextoCentrado("X", xSeparador, PanelPuntajes.ESPACIO_X, y, Color.WHITE);
        dibujarTextoCentrado(String.valueOf((int) multTrucoAMostrar), xSeparador + PanelPuntajes.ESPACIO_X, PanelPuntajes.ANCHO_CAJA_MULT, y, Color.WHITE);
        y -= PanelPuntajes.ESPACIO_LINEA;
        // 7. Envido del Jugador (Base X Multiplicador)
        double puntosEnvidoAMostrar = gestorAnimacionResolucion.isActiva() ? puntosEnvidoDisplay : 0;
        double multEnvidoAMostrar = gestorAnimacionResolucion.isActiva() ? multEnvidoDisplay : 1;
        dibujarTextoCentrado(String.valueOf((int) puntosEnvidoAMostrar), PANEL_PUNTAJES_X, PanelPuntajes.ANCHO_CAJA_BASE, y, Color.WHITE);
        // --- AQUÍ DIBUJAMOS LA X EN EL ESPACIO INTERMEDIO ---
        dibujarTextoCentrado("X", xSeparador, PanelPuntajes.ESPACIO_X, y, Color.WHITE);
        dibujarTextoCentrado(String.valueOf((int) multEnvidoAMostrar), xSeparador + PanelPuntajes.ESPACIO_X, PanelPuntajes.ANCHO_CAJA_MULT, y, Color.WHITE);
    }

    private void dibujarTextoCentrado(String texto, float x, float anchoCaja, float y, Color color) {
        game.getFuentePrincipal().setColor(color);
        layout.setText(game.getFuentePrincipal(), texto);
        float xTexto = x + (anchoCaja - layout.width) / 2f;
        float yTexto = y + (PanelPuntajes.ALTO_CAJA - layout.height) / 2f + layout.height;
        game.getFuentePrincipal().draw(game.batch, texto, xTexto, yTexto);
    }

    private void renderMesa() {
        for (VistaCarta c : cartasMesaRival) c.render(game.batch, game);
        for (VistaCarta c : cartasMesaJugador) c.render(game.batch, game);
    }

    private void renderRival() {
        for (int i = 0; i < cartasRival.size(); i++) {
            cartasRival.get(i).render(game.batch, game);
        }
    }

    private void renderJugador() {
        for (VistaCarta c : cartasJugador) {
            if (c != gestorCartas.getArrastrado()) c.render(game.batch, game);
        }
        if (gestorCartas.getArrastrado() != null) {
            gestorCartas.getArrastrado().render(game.batch, game);
        }
    }

    private void renderJokers() {
        VistaJoker conHover = null;
        for (VistaJoker j : jokers) {
            if (j != gestorJokers.getArrastrado()) {
                j.render(game.batch);
                if (j.isHover()) conHover = j;
            }
        }
        if (gestorJokers.getArrastrado() != null) {
            gestorJokers.getArrastrado().render(game.batch);
        }
        if (conHover != null) {
            conHover.renderCartelStats(game.batch, game);
        }
    }

    private void renderBotones() {
        int botonesVisibles = 0;
        if (botones.envido.isVisible()) botonesVisibles++;
        if (botones.realEnvido.isVisible()) botonesVisibles++;
        if (botones.faltaEnvido.isVisible()) botonesVisibles++;
        envidoMenuState.renderFondo(game.batch, game.getPixelBlanco(), botones.envidoOpciones, botonesVisibles);
        botones.gestor.render(game.batch);
    }

    private void organizarCartas() {
        if (gestorCartas.getArrastrado() != null) return;
        gestor.actualizarSlots(slotsX, cartasJugador.size(), anchoCarta, separacion, Gdx.graphics.getWidth());
        gestorReparto.organizarMano(cartasJugador, Y_MANO_JUGADOR, anchoCarta, separacion);
        gestorReparto.organizarMano(cartasRival, Y_MANO_RIVAL, ANCHO_CARTA_RIVAL, separacion);
        organizarJokers();
    }

    private void organizarJokers() {
        if (gestorJokers.getArrastrado() != null) return;
        int cantidad = jokers.size();
        if (cantidad == 0) return;
        float margenLateral = 220f;
        float anchoMaximo = Gdx.graphics.getWidth() - margenLateral * 2;
        float paso = GestorReordenamiento.calcularPaso(cantidad, ANCHO_JOKER, SEPARACION_JOKER, anchoMaximo);
        float anchoTotal = ANCHO_JOKER + (cantidad - 1) * paso;
        float inicioX = margenLateral + (anchoMaximo - anchoTotal) / 2f;
        for (int i = 0; i < cantidad; i++) {
            float x = inicioX + i * paso;
            jokers.get(i).setPosition(x, Y_JOKERS);
            jokers.get(i).setHandPosition(x, Y_JOKERS);
        }
        gestor.actualizarSlots(slotsJokersX, cantidad, ANCHO_JOKER, SEPARACION_JOKER, Gdx.graphics.getWidth());
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
        vistaCarta.cambiarBocaAbajo(game.getAtlasCartas());
        cartasMesaRival.add(vistaCarta);
        juego.agregarCartaRival(vistaCarta.getCarta());
        organizarCartas();
        organizarMesa();
    }

    public void iniciarShader() {
        hojaJokers = new Texture("Jokers.png");
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
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

    private void actualizarIA(boolean puedeInteractuar) {
        if (!puedeInteractuar) return;
        boolean huboRespuestaEnvido = juego.hayCantoEnvidoPendiente();
        boolean huboRespuestaTruco = juego.hayCantoTrucoPendiente();
        iaRival.actualizar();
        if (huboRespuestaEnvido && !juego.hayCantoEnvidoPendiente()) {
            controladorCombate.comprobarFinDelCombate();
        }
        if (huboRespuestaTruco && !juego.hayCantoTrucoPendiente()) {
            controladorCombate.comprobarFinDelCombate();
            if (juego.verificarEstadoCombate() == EstadoCombate.EN_PROGRESO
                && juego.isUltimaRespuestaTrucoFueNoQuiero()) {
                esperandoTransicion = true;
                iniciarNuevaRondaPendiente = true;
                tiempoNuevaRonda = 2.5f;
            }
        }
        controladorCombate.comprobarRival();
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

    /** Reproduce el sonido correspondiente segun si el paso que se esta animando es una carta o un joker. */
    private void reproducirSonidoActivacion(String origen) {
        if (origen == null || origen.equals("Base")) return; // el paso "Base" (estado inicial) no dispara sonido
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

    public void setTiempoNuevaRonda() { tiempoNuevaRonda = 2.5f; }
    public boolean isEsperandoTransicion() { return esperandoTransicion; }
    public void setEsperandoTransicion(boolean esperandoTransicion) { this.esperandoTransicion = esperandoTransicion; }
    public boolean isIniciarNuevaRondaPendiente() { return iniciarNuevaRondaPendiente; }
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
        if (hojaJokers != null) hojaJokers.dispose();
        panelPuntajes.dispose();
        for (VistaCarta carta : cartasJugador)     carta.dispose();
        for (VistaCarta carta : cartasRival)       carta.dispose();
        for (VistaCarta carta : cartasMesaJugador) carta.dispose();
        for (VistaCarta carta : cartasMesaRival)   carta.dispose();
        for (VistaJoker joker : jokers)            joker.dispose();
        if (fondoPlasma != null)   fondoPlasma.dispose();
        if (font != null)          font.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
