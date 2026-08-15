package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class VinoCaja extends Joker {

    public VinoCaja(){
        super(
            7,
            "Vino en caja",
            "VinoCaja",
            "+300 puntos de truco. Por cada otro joker con la categoria 'Alcohol' -50 puntos",
            Rareza.comun,
            1,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.BEBIDA, CategoriaJoker.ALCOHOL
        );
    }

    @Override
    public Joker copiar() {
        VinoCaja copia = new VinoCaja();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        double bonus = 300;
        int otrosConAlcohol = ctx.contarJokersConCategoria(CategoriaJoker.ALCOHOL, this);
        bonus -= 50 * otrosConAlcohol;
        ctx.getResolucionActual().sumarChips(bonus, this.getNombre());
    }

}
