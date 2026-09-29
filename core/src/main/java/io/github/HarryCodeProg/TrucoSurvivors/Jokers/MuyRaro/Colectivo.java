package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Colectivo extends Joker {

    private static final double MULT_INICIAL = 4;

    public Colectivo() {
        super(
            115,
            "Colectivo",
            "Colectivo",
            "X4 multiplicador truco, pierde X1 por mano jugada. Se resetea en cada rival (Actual: X4)",
            Rareza.muyRaro,
            8, // placeholder: ajustá el coste real
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        // Se ejecuta al comprarlo/agregarlo, cubre el caso de agregarlo a mitad de combate
        setAcumulado(MULT_INICIAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.INICIO_COMBATE) {
            setAcumulado(MULT_INICIAL);
            return;
        }
        if (evento == EventoJuego.TERMINO_MANO) {
            double nuevo = getAcumulado() - 1;
            setAcumulado(Math.max(0, nuevo));
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        if (ctx.getResolucionActual() == null) return;
        if (getAcumulado() <= 0) return;
        ctx.getResolucionActual().multiplicarMult(getAcumulado(), getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "X" + (int) MULT_INICIAL + " multiplicador truco, pierde X1 por mano jugada. Se resetea en cada rival (Actual: X"
            + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Colectivo copia = new Colectivo();
        copiarEstado(copia);
        return copia;
    }
}
