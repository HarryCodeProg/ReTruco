package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Agua?: la primera carta figura que puntua en la resolucion otorga x2 mult truco
public class Agua extends Joker {

    public Agua(){
        super(39, "Agua?", "Agua", "La primer carta figura que active otorga x2 multiplicador truco",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.TV, CategoriaJoker.SECUENCIA);
    }

    @Override
    public Joker copiar() {
        Agua copia = new Agua();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        if (ctx.isPrimerFiguraPuntuadaAplicada(this)) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getNumero() < 10) return;
        ctx.marcarPrimerFiguraPuntuadaAplicada(this);
        ctx.getResolucionActual().multiplicarMult(2, getNombre(), this);
    }
}
