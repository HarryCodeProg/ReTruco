package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Estados.EstadoCombate;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Rival.IARival;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.GameScreenV2;

public class ControladorIARival {
    private final Juego juego;
    private final IARival iaRival;
    private final ControladorCombate controladorCombate;
    private final GameScreenV2 screen;

    public ControladorIARival(Juego juego, ControladorCombate controladorCombate, GameScreenV2 screen) {
        this.juego = juego;
        this.iaRival = new IARival(juego);
        this.controladorCombate = controladorCombate;
        this.screen = screen;

        // FIX: cuando la IA gana un envido, animar igual que si lo hubiera ganado el jugador,
        // y bloquear input (como el truco) mientras dura.
        this.iaRival.setSolicitarAnimacionEnvido((resolucion, alTerminar) -> {
            screen.setEsperandoTransicion(true); // bloquea botones y reordenamiento, permite mover cartas libremente
            screen.iniciarAnimacionResolucion(resolucion, false, () -> {
                screen.setEsperandoTransicion(false);
                alTerminar.run();
                controladorCombate.comprobarFinDelCombate();
            });
        });
    }

    public void update(boolean puedeInteractuar) {
        if (!puedeInteractuar) return;
        if (iaRival.isAnimandoEnvido()) return; // FIX: no seguir procesando IA mientras se anima su propio envido

        boolean huboRespuestaEnvido = juego.hayCantoEnvidoPendiente();
        boolean huboRespuestaTruco = juego.hayCantoTrucoPendiente();

        iaRival.actualizar();

        if (huboRespuestaEnvido && !juego.hayCantoEnvidoPendiente() && !iaRival.isAnimandoEnvido()) {
            // solo comprobar acá si NO quedó una animación en curso — si quedó, el propio callback ya lo hace
            controladorCombate.comprobarFinDelCombate();
        }
        if (huboRespuestaTruco && !juego.hayCantoTrucoPendiente()) {
            controladorCombate.comprobarFinDelCombate();
            if (juego.verificarEstadoCombate() == EstadoCombate.EN_PROGRESO
                && juego.isUltimaRespuestaTrucoFueNoQuiero()) {
                screen.setEsperandoTransicion(true);
                screen.setIniciarNuevaRondaPendiente(true);
                screen.setTiempoNuevaRonda(2.5f);
            }
        }
        controladorCombate.comprobarRival();
    }

    public IARival getIaRival() {
        return iaRival;
    }
}
