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
            "En envido activa todas las cartas en la mano, las cartas que contribuyen se reactivan",
            Rareza.epico, 7, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.ACTIVACION, CategoriaJoker.ENVIDO, CategoriaJoker.PERSONA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            // Obtenemos todas las cartas en la mano del jugador
            ArrayList<Carta> mano = ctx.getJugador().getMano();

            if (mano != null && !mano.isEmpty()) {
                // Recorremos una copia para evitar problemas si la lista se modifica
                for (Carta carta : new ArrayList<>(mano)) {
                    // Esto reactivará las contribuyentes y activará el resto por primera vez
                    ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA_ENVIDO);
                }
            }
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "En envido activa todas las cartas en la mano, las cartas que contribuyen se reactivan";
    }

    @Override
    public Joker copiar() {
        Cerati copia = new Cerati();
        copiarEstado(copia);
        return copia;
    }
}
