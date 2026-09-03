package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class VirgenItati extends Joker {

    public VirgenItati() {
        super(125, "Virgen Itatí", "VirgenItati",
            "En cada ronda, consume los descartes hasta dejarlos en 0, obtiene el multiplicador truco igual a lo consumido (actual: +0)",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.HISTORIA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.POST_REPARTO) {
            // Al inicio de la ronda, consume todos los descartes disponibles y los acumula
            int descartesAConsumir = juego.getDescartesActuales();
            if (descartesAConsumir <= 0) return;
            for (int i = 0; i < descartesAConsumir; i++) {
                juego.restarUnDescarte();
            }
            sumarAcumulado(descartesAConsumir);
            return;
        }

        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            if (getAcumulado() > 0) {
                ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "En cada ronda, consume los descartes hasta dejarlos en 0, obtiene el multiplicador truco igual a lo consumido (actual: +"
            + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        VirgenItati copia = new VirgenItati();
        copiarEstado(copia);
        return copia;
    }
}
