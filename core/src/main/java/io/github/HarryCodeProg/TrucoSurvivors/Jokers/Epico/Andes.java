package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.JokerCopiaVecino;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;

public class Andes extends JokerCopiaVecino {

    public Andes() {
        super(145, "Andes", "Andes", "Copia el efecto del joker a la derecha",
            Rareza.epico, 10, CategoriaJoker.NACIONAL, CategoriaJoker.NATURALEZA);
    }

    @Override
    protected Joker obtenerVecino(ContextoJuego ctx) {
        return ctx.obtenerJokerALaDerecha(this);
    }

    @Override
    public Joker copiar() {
        Andes copia = new Andes();
        copiarEstado(copia);
        return copia;
    }
}
