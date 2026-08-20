package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Flan extends Joker {

    public Flan() {
        super(25, "Flan", "Flan", "+50 puntos truco y +50 puntos envido por cada otro joker con categoria POSTRE",
            Rareza.comun, 4, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.POSTRE, CategoriaJoker.COMIDA, CategoriaJoker.DULCE);
    }

    @Override
    public Joker copiar() {
        Flan copia = new Flan();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.TERMINO_MANO) return;
        int cantidadPostres = ctx.contarJokersConCategoria(CategoriaJoker.POSTRE, this);
        if (cantidadPostres > 0) {
            double bonus = cantidadPostres * 50.0;
            ctx.sumarPuntosBaseCalculo(bonus); // Ajustar según los setters de tu ResolucionPuntaje
        }
    }
}
