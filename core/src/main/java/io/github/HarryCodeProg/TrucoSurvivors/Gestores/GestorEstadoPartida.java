package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.DatosRival;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado.DatosCarta;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado.DatosJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado.GestorConverterSerializacion;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.GameScreenV2;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;

import java.util.ArrayList;

public class GestorEstadoPartida {
    private final GameScreenV2 screen;

    private Juego juego;
    private ControladorCombate controladorCombate;
    private ControladorIARival controladorIARival;
    private GestorAccion gestorAccion;

    private int tocoJugar = 0;
    private boolean esperandoTransicion = false;
    private boolean iniciarNuevaRondaPendiente = false;
    private float tiempoNuevaRonda = 0f;

    public GestorEstadoPartida(GameScreenV2 screen) {
        this.screen = screen;
    }

    public void inicializar(Jugador jugador, Jugador rival, DatosRival datosRival,
                            GestorInputArrastrable<VistaCarta> gestorCartas,
                            GestorAnimacionesMano gestorAnimaciones) {
        this.tocoJugar = 0;
        this.esperandoTransicion = false;
        this.iniciarNuevaRondaPendiente = false;
        this.tiempoNuevaRonda = 0f;

        // Creamos el corazón de la lógica
        Mazo mazoRival = Juego.crearMazoRival(datosRival.getNivelDificultad());
        this.juego = new Juego(jugador, rival, mazoRival, false);
        this.juego.setPuntajeMeta(datosRival.getPuntosMeta());

        this.controladorCombate = new ControladorCombate(screen, juego);
        this.controladorIARival = new ControladorIARival(juego, controladorCombate, screen);
        this.gestorAccion = new GestorAccion(juego, screen, controladorCombate, gestorCartas, gestorAnimaciones);
    }

    public void update(float delta, boolean puedeInteractuar, boolean modalBloqueante) {
        // Manejo del temporizador para iniciar una nueva ronda
        if (iniciarNuevaRondaPendiente) {
            tiempoNuevaRonda -= delta;
            if (tiempoNuevaRonda <= 0f) {
                if (!screen.isEsperandoAnimacionTransicion()) {
                    iniciarNuevaRondaPendiente = false;
                    esperandoTransicion = false;
                    screen.iniciarNuevaRonda();
                }
            }
        }

        // Actualizamos la Inteligencia Artificial
        if (controladorIARival != null && !modalBloqueante) {
            controladorIARival.update(puedeInteractuar);
        }
    }

    // --- Getters y Setters delegados ---
    public void setTiempoNuevaRonda() { this.tiempoNuevaRonda = 2.5f; this.iniciarNuevaRondaPendiente = true; }
    public void setTiempoNuevaRonda(float tiempo) { this.tiempoNuevaRonda = tiempo; this.iniciarNuevaRondaPendiente = true; }
    public void setIniciarNuevaRondaPendiente(boolean pendiente) { this.iniciarNuevaRondaPendiente = pendiente; }

    public void setEsperandoTransicion(boolean esperando) { this.esperandoTransicion = esperando; }
    public boolean isEsperandoTransicion() { return esperandoTransicion; }

    public void incrementarTocoJugar() { this.tocoJugar++; }
    public int getTocoJugar() { return tocoJugar; }

    public Juego getJuego() { return juego; }
    public ControladorCombate getControladorCombate() { return controladorCombate; }
    public ControladorIARival getControladorIARival() { return controladorIARival; }
    public GestorAccion getGestorAccion() { return gestorAccion; }
    public void resetTocoJugar() {
        this.tocoJugar = 0;
    }

    public void inicializarDesdeGuardado(Jugador jugador, Jugador rival, DatosRival datosRival,
                                         GestorInputArrastrable<VistaCarta> gestorCartas,
                                         GestorAnimacionesMano gestorAnimaciones,
                                         DatosJuego datosJuego) {
        this.tocoJugar = 0;
        this.esperandoTransicion = false;
        this.iniciarNuevaRondaPendiente = false;
        this.tiempoNuevaRonda = 0f;
        Mazo mazoRival = Juego.crearMazoRival(datosRival.getNivelDificultad());
        this.juego = new Juego(jugador, rival, mazoRival, true); // true = saltar reparto inicial
        this.juego.setPuntajeMeta(datosRival.getPuntosMeta());
        ArrayList<Carta> manoRival = new ArrayList<>();
        for (DatosCarta dc : datosJuego.manoRival) manoRival.add(GestorConverterSerializacion.desdeDatos(dc));
        ArrayList<Carta> mesaJug = new ArrayList<>();
        for (DatosCarta dc : datosJuego.mesaJugador) mesaJug.add(GestorConverterSerializacion.desdeDatos(dc));
        ArrayList<Carta> mesaRiv = new ArrayList<>();
        for (DatosCarta dc : datosJuego.mesaRival) mesaRiv.add(GestorConverterSerializacion.desdeDatos(dc));
        this.juego.restaurarEstado(
            datosJuego.puntosJugador, datosJuego.puntosRival, datosJuego.jugadorEsMano,
            datosJuego.faseActual, datosJuego.rondaActual, datosJuego.descartesActuales,
            manoRival, mesaJug, mesaRiv,
            datosJuego.cartasJugadasTotal, datosJuego.cartasDescartadasTotal,
            datosJuego.cartasCompradasTotal, datosJuego.renovacionesTotal
        );
        this.controladorCombate = new ControladorCombate(screen, juego);
        this.controladorIARival = new ControladorIARival(juego, controladorCombate, screen);
        this.gestorAccion = new GestorAccion(juego, screen, controladorCombate, gestorCartas, gestorAnimaciones);
    }
}
