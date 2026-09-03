package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.Random;

public class SosInumputable extends Joker {

    private final Random random = new Random();

    public SosInumputable() {
        super(
            54,
            "Sos Inimputable",
            "SosInimputable",
            "Genera un multiplicador truco aleatorio entre +1 y +20 al final de la mano",
            Rareza.comun,
            1,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TV, CategoriaJoker.SECUENCIA
        );
    }

    @Override
    public Joker copiar() {
        SosInumputable copia = new SosInumputable();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.TERMINO_MANO) {
            int mult = random.nextInt(20) + 1;
            // Guardamos el multiplicador generado
            sumarAcumulado(mult);
            return;
        }
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            if (getAcumulado() <= 0) return;
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(),this);
            // Se consume el valor generado.
            double generado = getAcumulado();
            sumarAcumulado(-generado);
        }
    }
}
