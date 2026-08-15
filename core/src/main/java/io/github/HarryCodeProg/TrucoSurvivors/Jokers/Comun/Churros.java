package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Churros: la primera carta que NO mate, +1 valor truco (poder/jerarquia)
public class Churros extends Joker {

    public Churros(){
        super(15, "Churros", "Churros", "La primer carta que no mate recibe +1 valor truco",
            Rareza.comun, 1, FaseActivacion.AL_PUNTUAR_CARTA, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        Churros copia = new Churros();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_SER_MATADO) return;
        if (juego.isPrimeraCartaQueNoMataAplicada()) return;
        Carta c = ctx.getCartaEnResolucion(); // la del jugador, que perdio
        if (c == null) return;
        c.modificarValorTrucoPermanente(1);
        juego.marcarPrimeraCartaQueNoMataAplicada();
    }
}
