package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.JokerCopiaVecino;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;

public class Aconcagua extends JokerCopiaVecino {

    public Aconcagua() {
        super(144, "Aconcagua", "Aconcagua", "Copia el efecto del joker a la izquierda",
            Rareza.epico, 8, CategoriaJoker.NACIONAL, CategoriaJoker.NATURALEZA);
    }

    @Override
    protected Joker obtenerVecino(ContextoJuego ctx) {
        return ctx.obtenerJokerALaIzquierda(this);
    }

    @Override
    public Joker copiar() {
        Aconcagua copia = new Aconcagua();
        copiarEstado(copia);
        return copia;
    }
}
