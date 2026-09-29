package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Chaco extends Joker {

    public Chaco() {
        super(
            83,
            "Chaco",
            "Chaco",
            "Gana X0.4 de multiplicador truco por cada mano jugada.\n(Se reinicia al final de la ronda)",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        // Inicializamos el X-Mult en X1 (no hace nada en la primera mano)
        setAcumulado(1.0);
    }

    @Override
    public Joker copiar() {
        Chaco copia = new Chaco();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // 1. Aplicamos el X-Mult al Truco (solo si es mayor a 1 para no ensuciar las animaciones visuales)
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            double multActual = getAcumulado();
            if (multActual > 1.0) {
                // Usamos el método de multiplicarMult de tu ResolucionPuntaje
                ctx.getResolucionActual().multiplicarMult(multActual, this.getNombre(), this);
            }
        }
        // 2. Aumentamos el multiplicador porque se jugó una mano
        if (evento == EventoJuego.TERMINO_MANO) {
            setAcumulado(getAcumulado() + 0.4);
        }
        // 3. Reseteamos el multiplicador cuando ganamos y pasamos al siguiente rival/tienda
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            setAcumulado(1.0);
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        double multActualRedondeado = Math.round(getAcumulado() * 10.0) / 10.0;
        return getDescripcion() + "\n(Actual: X" + multActualRedondeado + "[])";
    }
}
