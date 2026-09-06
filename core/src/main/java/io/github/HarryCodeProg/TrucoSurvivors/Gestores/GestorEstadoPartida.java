package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.DatosRival;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.GameScreenV2;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;

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
        this.juego = new Juego(jugador, rival, mazoRival);
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
}
