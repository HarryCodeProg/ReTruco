package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Tucuman extends Joker {

    public Tucuman() {
        super(77, "Tucumán", "Tucuman", "Espada y Oro cuentan como el mismo palo para el envido",
            Rareza.raro, 6, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.TV);
    }

    @Override
    public Joker copiar() {
        Tucuman copia = new Tucuman();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Efecto pasivo, sin lógica acá.
    }
}
