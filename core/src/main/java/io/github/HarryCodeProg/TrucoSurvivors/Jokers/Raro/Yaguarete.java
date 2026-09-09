package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro; // Ajustá al paquete correcto

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Yaguarete extends Joker {

    public Yaguarete() {
        super(
            68,
            "Yaguareté",
            "Yaguarete",
            "Todas las cartas son consideradas figura.",
            Rareza.raro,
            8,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL,
            CategoriaJoker.ANIMAL
        );
    }

    @Override
    public Joker copiar() {
        Yaguarete copia = new Yaguarete();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
    }
}
