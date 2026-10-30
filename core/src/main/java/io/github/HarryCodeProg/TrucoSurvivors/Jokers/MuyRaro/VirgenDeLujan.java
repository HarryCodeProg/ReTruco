package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.ColorMecanica;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class VirgenDeLujan extends Joker {

    public VirgenDeLujan() {
        super(126, "Virgen de Luján", "VirgenDeLujan",
            "En cada ronda consume los descartes hasta dejarlos en 0, obtiene el multiplicador envido igual a lo consumido (actual: +0)",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.ENVIDO, CategoriaJoker.CONSUMIBLES, CategoriaJoker.TERRITORIO);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.POST_REPARTO) {
            int descartesAConsumir;

            if (ctx.getPuntosBaseCalculo() > 0) {
                // Otra copia ya leyó y consumió los descartes de esta ronda → compartir el valor
                descartesAConsumir = (int) ctx.getPuntosBaseCalculo();
            } else {
                // Primera copia en ejecutarse: leer, guardar en ctx y consumir de verdad
                descartesAConsumir = juego.getDescartesActuales();
                if (descartesAConsumir <= 0) return;
                ctx.setPuntosBaseCalculo(descartesAConsumir);
                for (int i = 0; i < descartesAConsumir; i++) {
                    juego.restarUnDescarte();
                }
            }

            sumarAcumulado(descartesAConsumir);
            return;
        }

        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            if (getAcumulado() > 0) {
                ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "En cada ronda, consume los descartes hasta dejarlos en 0, obtiene el multiplicador envido igual a lo consumido (actual: "
            + ColorMecanica.NUMERO.colorHex() + "+" + (int) getAcumulado() + "[])";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        VirgenDeLujan copia = new VirgenDeLujan();
        copiarEstado(copia);
        return copia;
    }
}
