package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EstadoCombate;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.GameScreenV2;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;

import java.util.ArrayList;

public class GestorAccion {
    private final Juego juego;
    private final GameScreenV2 gScreen;
    private final ControladorCombate cCombate;
    private final GestorInputArrastrable<VistaCarta> gestorCartas;
    private final GestorAnimacionesMano gestorAnimaciones;

    public GestorAccion(Juego juego, GameScreenV2 gScreen, ControladorCombate cCombate,
                        GestorInputArrastrable<VistaCarta> gestorCartas,
                        GestorAnimacionesMano gestorAnimaciones) {
        this.juego = juego;
        this.gScreen = gScreen;
        this.cCombate = cCombate;
        this.gestorAnimaciones = gestorAnimaciones;
        this.gestorCartas = gestorCartas;
    }

    public void ejecutarAccion(Accion accion) {
        switch (accion) {
            case ENVIDO:
                juego.cantarEnvido(juego.getJugador(), 1);
                break;
            case REAL_ENVIDO:
                if (juego.hayCantoEnvidoPendiente()) juego.escalarEnvido(juego.getJugador());
                else juego.cantarEnvido(juego.getJugador(), 2);
                break;
            case FALTA_ENVIDO:
                if (juego.hayCantoEnvidoPendiente()) juego.escalarEnvido(juego.getJugador());
                else juego.cantarEnvido(juego.getJugador(), 3);
                break;
            case TRUCO:
                juego.cantarTruco(juego.getJugador(), 1);
                break;
            case RETRUCO:
            case VALE_CUATRO:
                juego.escalarTruco(juego.getJugador());
                break;
            case QUIERO:
                if (juego.hayCantoEnvidoPendiente()) {
                    juego.responderEnvido(true); // calcula resolución con jokers y cartas ya aplicados, no suma todavía
                    ResolucionPuntaje resolucionEnvido = juego.getUltimaResolucionEnvido();
                    if (resolucionEnvido != null && !resolucionEnvido.getLog().isEmpty()) {
                        gScreen.setEsperandoTransicion(true);
                        gScreen.iniciarAnimacionResolucion(resolucionEnvido, false, () -> {
                            gScreen.setEsperandoTransicion(false);
                            juego.aplicarResultadoEnvido();
                            cCombate.comprobarFinDelCombate();
                        });
                    } else {
                        juego.aplicarResultadoEnvido();
                        cCombate.comprobarFinDelCombate();
                    }
                } else if (juego.hayCantoTrucoPendiente()) {
                    juego.responderTruco(true);
                }
                break;
            case NO_QUIERO:
                if (juego.hayCantoEnvidoPendiente()) {
                    juego.responderEnvido(false); // arma resolución simple, no aplica todavía
                    ResolucionPuntaje resolucionEnvido = juego.getUltimaResolucionEnvido();
                    if (resolucionEnvido != null && !resolucionEnvido.getLog().isEmpty()) {
                        gScreen.setEsperandoTransicion(true);
                        gScreen.iniciarAnimacionResolucion(resolucionEnvido, false, () -> {
                            gScreen.setEsperandoTransicion(false);
                            juego.aplicarResultadoEnvido();
                            cCombate.comprobarFinDelCombate();
                        });
                    } else {
                        juego.aplicarResultadoEnvido();
                        cCombate.comprobarFinDelCombate();
                    }
                } else if (juego.hayCantoTrucoPendiente()) {
                    juego.responderTruco(false);
                    cCombate.comprobarFinDelCombate();
                    if (juego.verificarEstadoCombate() == EstadoCombate.EN_PROGRESO) {
                        gScreen.setEsperandoTransicion(true);
                        gScreen.setIniciarNuevaRondaPendiente(true);
                        gScreen.setTiempoNuevaRonda();
                    }
                }
                break;
            case IR_AL_MAZO:
                gScreen.iniciarNuevaRonda();
                break;
            case JUGAR_CARTA:
                if (!juego.getTurnoActual().equals(juego.getJugador())) {
                    break;
                }
                if (gestorCartas.getSeleccionados().size() != 1) break;
                VistaCarta cartaElegida = gestorCartas.getSeleccionados().get(0);
                gScreen.jugarCarta(cartaElegida);
                if (juego.getMesa().getMesaJugador().size() == juego.getMesa().getMesaRival().size()) {
                    juego.jugarMano(gScreen.getTocoJugar());
                    gScreen.incrementarTocoJugar();
                } else {
                    juego.setTurnoActual(juego.getRival());
                }
                cCombate.comprobarGanadorRonda();
                break;
            case DESCARTAR:
                if (gestorCartas.getSeleccionados().isEmpty()) break;
                ArrayList<VistaCarta> vistasADescartar = new ArrayList<>(gestorCartas.getSeleccionados());
                ArrayList<Carta> modelosViejos = new ArrayList<>();
                for (VistaCarta v : vistasADescartar) {
                    modelosViejos.add(v.getCarta());
                }
                ArrayList<Carta> cartasNuevas = juego.descartarCartas(modelosViejos);
                if (cartasNuevas.isEmpty()) break;
                gestorCartas.getSeleccionados().clear();
                gScreen.getCartasJugador().removeAll(vistasADescartar);
                gestorAnimaciones.iniciarDescarte(vistasADescartar, cartasNuevas);
                break;
        }
    }
}
