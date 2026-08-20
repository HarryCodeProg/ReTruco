package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Milanesa: cartas que maten reciben +10 PUNTOS TRUCO (aporte, no poder)
public class Milanesa extends Joker {
    public Milanesa(){
        super(13, "Milanesa", "Milanesa", "Las cartas que maten reciben +10 puntos truco",
            Rareza.comun, 1, FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.INTERNACIONAL, CategoriaJoker.COMIDA);
    }

    @Override
    public Joker copiar() {
        Milanesa copia = new Milanesa();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_MATAR_CARTA) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null) return;
        c.modificarPuntosTrucoAportePermanente(10);
    }
}
