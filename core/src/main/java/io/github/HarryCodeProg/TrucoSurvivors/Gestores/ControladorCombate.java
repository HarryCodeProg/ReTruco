package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.utils.Timer;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EstadoCombate;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ConfiguracionEconomia;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.GameScreenV2;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;

public class ControladorCombate {
    private final GameScreenV2 screen;
    private final Juego juego;

    public ControladorCombate(GameScreenV2 screen, Juego juego) {
        this.screen = screen;
        this.juego = juego;
    }

    public void comprobarFinDelCombate() {
        EstadoCombate estado = juego.verificarEstadoCombate();
        if (estado == EstadoCombate.VICTORIA_JUGADOR) {
            Jugador jugadorGanador = juego.getJugador();
            int interes = jugadorGanador.calcularInteres();
            jugadorGanador.sumarPesos(ConfiguracionEconomia.DINERO_POR_VICTORIA + interes);

            screen.setEsperandoTransicion(true);
            screen.getGame().habilitarSiguiente(screen.getDatosRival().getIndice());

            Timer.schedule(new Timer.Task() {
                @Override public void run() {
                    screen.setEsperandoTransicion(false);
                    // Pasa el control a la tienda o finaliza la partida
                    screen.finalizarCombate(true);
                }
            }, 3.0f);
        } else if (estado == EstadoCombate.VICTORIA_RIVAL) {
            screen.setEsperandoTransicion(true);
            Timer.schedule(new Timer.Task() {
                @Override public void run() {
                    screen.setEsperandoTransicion(false);
                    screen.finalizarCombate(false);
                }
            }, 3.0f);
        }
    }

    public void comprobarGanadorRonda() {
        if (!juego.terminoLaMano()) return;
        juego.finalizarManoTruco();
        ResolucionPuntaje resolucion = juego.getUltimaResolucion();

        if (resolucion != null && !resolucion.getLog().isEmpty()) {
            screen.setEsperandoTransicion(true);
            screen.iniciarAnimacionResolucion(resolucion, true, () -> { // true = es truco
                screen.setEsperandoTransicion(false);
                continuarDespuesDeResolucion();
            });
        } else {
            continuarDespuesDeResolucion();
        }
    }

    private void continuarDespuesDeResolucion() {
        comprobarFinDelCombate();
        if (juego.verificarEstadoCombate() != EstadoCombate.EN_PROGRESO) return;

        screen.setEsperandoTransicion(true);
        screen.setIniciarNuevaRondaPendiente(true);
        screen.setTiempoNuevaRonda(2.5f);
    }

    public void comprobarRival() {
        if (!juego.getTurnoActual().equals(juego.getRival())) return;
        if (juego.hayCantoTrucoPendiente() || juego.hayCantoEnvidoPendiente()) {
            return;
        }
        if (screen.getCartasRival().isEmpty()) return;

        VistaCarta cartaRivalATirar = screen.getCartasRival().get(0);
        screen.jugarCartaRival(cartaRivalATirar);

        if (juego.getMesa().getMesaJugador().size() == juego.getMesa().getMesaRival().size()) {
            juego.jugarMano(screen.getTocoJugar());
            screen.incrementarTocoJugar();
        } else {
            juego.setTurnoActual(juego.getJugador());
        }
        comprobarGanadorRonda();
    }
}
