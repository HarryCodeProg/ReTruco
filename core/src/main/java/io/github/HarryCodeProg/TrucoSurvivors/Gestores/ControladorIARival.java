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
    }

    /**
     * Actualiza las decisiones de la IA y procesa los cambios de estado en las apuestas.
     */
    public void update(boolean puedeInteractuar) {
        if (!puedeInteractuar) return;
        boolean huboRespuestaEnvido = juego.hayCantoEnvidoPendiente();
        boolean huboRespuestaTruco = juego.hayCantoTrucoPendiente();
        // 1. Ejecutar frame de la IA
        iaRival.actualizar();
        // 2. Verificar resolución de Envido
        if (huboRespuestaEnvido && !juego.hayCantoEnvidoPendiente()) {
            controladorCombate.comprobarFinDelCombate();
        }
        // 3. Verificar resolución de Truco
        if (huboRespuestaTruco && !juego.hayCantoTrucoPendiente()) {
            controladorCombate.comprobarFinDelCombate();
            // Si alguien dijo "No quiero" al Truco, programar inicio de nueva ronda
            if (juego.verificarEstadoCombate() == EstadoCombate.EN_PROGRESO
                && juego.isUltimaRespuestaTrucoFueNoQuiero()) {
                screen.setEsperandoTransicion(true);
                screen.setIniciarNuevaRondaPendiente(true);
                screen.setTiempoNuevaRonda(2.5f);
            }
        }
        // 4. Comprobar si le toca jugar carta al rival
        controladorCombate.comprobarRival();
    }

    public IARival getIaRival() {
        return iaRival;
    }
}
