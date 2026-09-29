package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Gallina extends Joker {

    public Gallina() {
        super(
            73,
            "Gallina",
            "Gallina",
            "Evita aumentos o reducciones de acumulaciones del joker de la derecha",
            Rareza.raro,
            6, // placeholder: ajustá el coste real
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // No hace nada por sí misma: el congelamiento se resuelve en Joker.sumarAcumulado/setAcumulado
        // consultando la posición en tiempo real. Gallina no necesita reaccionar a ningún evento.
    }

    @Override
    public Joker copiar() {
        Gallina copia = new Gallina();
        copiarEstado(copia);
        return copia;
    }
}
