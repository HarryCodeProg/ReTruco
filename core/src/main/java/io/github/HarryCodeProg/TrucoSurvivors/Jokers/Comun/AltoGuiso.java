package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Alto Guiso: la primera carta no figura que puntua en la resolucion otorga x1.5 mult truco
public class AltoGuiso extends Joker {

    public AltoGuiso(){
        super(40, "Alto Guiso", "AltoGuiso", "La primer carta no figura que active otorga x1.5 multiplicador truco",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        if (ctx.isPrimerNoFiguraPuntuadaAplicada()) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getNumero() >= 10) return;
        ctx.marcarPrimerNoFiguraPuntuadaAplicada();
        ctx.getResolucionActual().multiplicarMult(1.5, getNombre());
    }
}
