package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class NoMeMidas extends Joker {

    public NoMeMidas(){
        super(48, "No Me Midas", "NoMeMidas", "Cada espada que mata vuelve a activarse",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.TV, CategoriaJoker.SECUENCIA);
    }

    @Override
    public Joker copiar() {
        NoMeMidas copia = new NoMeMidas();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getPalo() != Palo.ESPADA) return;
        if (!ctx.cartaMato(c)) return;
        if (!ctx.marcarUsado(this, c)) return;
        ctx.reencolarActivacionCarta(c, EventoJuego.AL_PUNTUAR_CARTA);
    }
}
