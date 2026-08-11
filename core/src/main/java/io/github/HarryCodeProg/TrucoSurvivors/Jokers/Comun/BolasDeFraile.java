package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// BolasDeFraile: la primera carta que mate, +1 valor truco (poder/jerarquia)
public class BolasDeFraile extends Joker {

    public BolasDeFraile(){
        super(16, "Bolas de Fraile", "BolasDeFraile", "La primer carta que mate recibe +1 valor truco",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_MATAR_CARTA) return;
        if (juego.isPrimeraCartaQueMataAplicada()) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null) return;
        c.modificarValorTrucoPermanente(1);
        juego.marcarPrimeraCartaQueMataAplicada();
    }
}
