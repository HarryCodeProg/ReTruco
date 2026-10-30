package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.GestorJokers;
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
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        ArrayList<Carta> mano = ctx.getJugador().getMano();
        ArrayList<Carta> contribuyentes = ctx.getCartasContribuyentesEnvido();
        if (mano == null || mano.isEmpty() || contribuyentes == null) return;
        for (Carta carta : new ArrayList<>(mano)) {
            if (contribuyentes.contains(carta)) {
                ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA_ENVIDO);
            } else {
                ctx.reencolarActivacionCartaSoloActivar(carta, EventoJuego.AL_PUNTUAR_CARTA_ENVIDO);
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
