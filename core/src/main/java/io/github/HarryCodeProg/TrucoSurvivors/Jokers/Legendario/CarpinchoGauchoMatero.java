package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Legendario;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class CarpinchoGauchoMatero extends Joker {

    public CarpinchoGauchoMatero() {
        super(151, "CarpinchoGauchoMatero", "CarpinchoGauchoMatero",
            "X10 multiplicador truco",
            Rareza.legendario,
            20,
            Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "X10 multiplicador truco";
    }

    @Override
    public Joker copiar() {
        CarpinchoGauchoMatero copia = new CarpinchoGauchoMatero();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        ctx.getResolucionActual().multiplicarMult(10, getNombre(), this);

    }
}
