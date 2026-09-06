package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Pastelitos extends Joker {

    public Pastelitos(){
        super(17, "Pastelitos", "Pastelitos", "Las cartas de Copa que maten reciben +15 puntos truco",
            Rareza.comun, 1, FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA, CategoriaJoker.DULCE);
    }

    @Override
    public Joker copiar() {
        Pastelitos copia = new Pastelitos();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_MATAR_CARTA) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getPalo() != Palo.COPA) return;
        c.modificarPuntosTrucoAportePermanente(15);
    }
}
