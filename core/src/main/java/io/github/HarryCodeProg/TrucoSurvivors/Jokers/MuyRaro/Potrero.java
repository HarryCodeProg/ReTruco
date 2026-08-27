package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import java.util.ArrayList;

public class Potrero extends Joker {

    public Potrero() {
        super(111, "Potrero", "Potrero", "Reactiva todos los jokers con categoría 'DEPORTE'",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.DEPORTE);
    }

    @Override
    public Joker copiar() {
        Potrero copia = new Potrero();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (!ctx.marcarUsado(this, evento)) return;
        ArrayList<Joker> jokers = ctx.getJugador().getJokers();
        for (Joker j : jokers) {
            if (j == this) continue;
            if (j.tieneCategoria(CategoriaJoker.DEPORTE)) {
                ctx.reencolarActivacionJoker(j, evento);
            }
        }
    }
}
