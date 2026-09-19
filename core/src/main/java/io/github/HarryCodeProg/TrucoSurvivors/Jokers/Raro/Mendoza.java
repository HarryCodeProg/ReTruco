package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Mendoza extends Joker {

    public Mendoza() {
        super(92, "Mendoza", "Mendoza", "Los jokers pueden aparecer duplicados", Rareza.raro, 6,
            FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
    }

    @Override
    public Joker copiar() {
        Mendoza copia = new Mendoza();
        copiarEstado(copia);
        return copia;
    }
}
