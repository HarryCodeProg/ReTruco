package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayList;

public class NoMeMidas extends Joker {

    public NoMeMidas(){
        super(48, "No Me Midas", "NoMeMidas", "Reactiva las cartas Espada",
            Rareza.comun, 4, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.ACTIVACION, CategoriaJoker.TRUCO, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        NoMeMidas copia = new NoMeMidas();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        boolean esTruco  = (evento == EventoJuego.AL_PUNTUAR_CARTA);
        boolean esEnvido = (evento == EventoJuego.AL_PUNTUAR_CARTA_ENVIDO);
        if (!esTruco && !esEnvido) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getPalo() != Palo.ESPADA) return;
        if (esTruco) {
            if (!ctx.cartaMato(c)) return;
        } else {
            ArrayList<Carta> contribuyentes = ctx.getCartasContribuyentesEnvido();
            if (contribuyentes == null || !contribuyentes.contains(c)) return;
        }
        if (!ctx.marcarUsado(this, c)) return;
        ctx.reencolarActivacionCarta(c, evento);
    }
}
