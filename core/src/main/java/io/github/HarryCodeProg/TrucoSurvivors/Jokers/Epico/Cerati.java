package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayList;

public class Cerati extends Joker {

    public Cerati() {
        super(134, "Cerati", "Cerati",
            "Reactiva las cartas jugadas en envido",
            Rareza.epico, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.MUSICA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            // Reactiva todas las cartas que contribuyeron al envido
            ArrayList<Carta> cartasEnvido = ctx.getCartasContribuyentesEnvido();
            if (cartasEnvido != null && !cartasEnvido.isEmpty()) {
                for (Carta carta : cartasEnvido) {
                    ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA_ENVIDO);
                }
            }
        }
    }

    @Override
    public Joker copiar() {
        Cerati copia = new Cerati();
        copiarEstado(copia);
        return copia;
    }
}
