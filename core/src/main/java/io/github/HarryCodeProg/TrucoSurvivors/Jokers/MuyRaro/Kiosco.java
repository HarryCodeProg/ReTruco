package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayList;

public class Kiosco extends Joker {

    public Kiosco() {
        super(105, "Kiosco", "Kiosco", "Reactiva todos los jokers con categoría 'DULCE'",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.DULCE);
    }

    @Override
    public Joker copiar() {
        Kiosco copia = new Kiosco();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (!ctx.marcarUsado(this, evento)) return; // evita reencolarse en bucle si el evento se repite en la misma resolución
        ArrayList<Joker> jokers = ctx.getJugador().getJokers();
        for (Joker j : jokers) {
            if (j == this) continue; // no se reactiva a sí mismo
            if (j.tieneCategoria(CategoriaJoker.DULCE)) {
                ctx.reencolarActivacionJoker(j, evento);
            }
        }
    }
}
