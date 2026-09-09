package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class CuentaCorriente extends Joker {

    private static final int DEUDA_OTORGADA = 20;
    private boolean deudaAplicada = false; // aplicarEfectoInstantaneo puede dispararse una sola vez por instancia

    public CuentaCorriente() {
        super(81, "Cuenta Corriente", "CuentaCorriente",
            "Obtiene hasta -$20 de deuda. +4 multiplicador envido por cada mano jugada en deuda",
            Rareza.raro, 5, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.TV);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Obtiene hasta -$20 de deuda. +4 multiplicador envido por cada mano jugada en deuda (actual: +"
            + (int) getAcumulado() + ")";
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        if (!deudaAplicada) {
            jugador.sumarDeudaMaxima(DEUDA_OTORGADA);
            deudaAplicada = true;
        }
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        if (deudaAplicada) {
            jugador.sumarDeudaMaxima(-DEUDA_OTORGADA); // si se vende, se retira el permiso de deuda otorgado
            deudaAplicada = false;
        }
    }

    @Override
    public Joker copiar() {
        CuentaCorriente copia = new CuentaCorriente();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.TERMINO_MANO) {
            if (ctx.getJugador().getPesos() < 0) {
                sumarAcumulado(4);
            }
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }
}
