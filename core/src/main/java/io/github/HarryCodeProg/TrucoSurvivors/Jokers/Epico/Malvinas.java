package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Malvinas extends Joker {

    public Malvinas() {
        super(133, "Malvinas", "Malvinas",
            "Obtiene multiplicador truco igual al Valor Truco de la primer carta que mate (actual: x1)",
            Rareza.epico, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL);
        setAcumulado(1.0);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_MATAR_CARTA) {
            // Se activa solo una vez por mano cuando el jugador mata la primer carta
            if (!juego.isPrimeraCartaQueMataAplicada()) {
                juego.marcarPrimeraCartaQueMataAplicada();
                if (ctx.getCartaEnResolucion() != null) {
                    double valorTruco = ctx.getCartaEnResolucion().getValorTrucoActual();
                    setAcumulado(1.0 + valorTruco);
                }
            }
            return;
        }

        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            if (getAcumulado() > 1.0) {
                ctx.getResolucionActual().multiplicarMult(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Obtiene multiplicador truco igual al Valor Truco de la primer carta que mate (actual: x"
            + String.format("%.2f", getAcumulado()) + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        Malvinas copia = new Malvinas();
        copiarEstado(copia);
        return copia;
    }
}

