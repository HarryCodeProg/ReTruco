package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import java.util.ArrayList;

public class Revisionista extends Joker {

    public Revisionista() {
        super(112, "Revisionista", "Revisionista", "Reactiva todos los jokers con categoría 'HISTORIA'",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.HISTORIA);
    }

    @Override
    public Joker copiar() {
        Revisionista copia = new Revisionista();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (!ctx.marcarUsado(this, evento)) return;
        ArrayList<Joker> jokers = ctx.getJugador().getJokers();
        for (Joker j : jokers) {
            if (j == this) continue;
            if (j.tieneCategoria(CategoriaJoker.HISTORIA)) {
                ctx.reencolarActivacionJoker(j, evento);
            }
        }
    }
}
