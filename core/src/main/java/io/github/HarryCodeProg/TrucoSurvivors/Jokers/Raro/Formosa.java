package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import com.badlogic.gdx.math.MathUtils;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Formosa extends Joker {

    public Formosa() {
        super(
            84,
            "Formosa",
            "Formosa",
            "",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        setAcumulado(1); // Arranca dando $1
    }

    @Override
    public Joker copiar() {
        Formosa copia = new Formosa();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public String getDescripcion() {
        return "($" + (int)getAcumulado() + ") al derrotar un rival. 1/2 chances de recibir el doble. Al recibir el doble, aumenta en +$1.";
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_GANAR_COMBATE) return;
        int recompensaBase = (int) getAcumulado();
        int recompensaFinal = recompensaBase;
        // Tiramos la moneda (50% de probabilidad)
        boolean suerte = MathUtils.randomBoolean(0.5f);
        if (suerte) {
            recompensaFinal *= 2;
            setAcumulado(recompensaBase + 1); // Aumenta su valor permanentemente
        }
        // Sumamos el dinero directo al jugador
        juego.getJugador().sumarPesos(recompensaFinal);
        // Creamos una animación de chips visual en la pantalla
        if (ctx.getResolucionActual() != null) {
            ctx.getResolucionActual().sumarChips(recompensaFinal, "+$" + recompensaFinal, this);
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "($" + (int)getAcumulado() + ")[] al derrotar un rival, 1/2 chances de recibir el doble.\nAl recibir el doble, el valor aumenta en +$1[].";
    }
}
