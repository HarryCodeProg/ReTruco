package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Dogo extends Joker {

    public Dogo() {
        super(
            71,
            "Dogo",
            "Dogo",
            "+25 puntos envido por cada carta con numero par en tu mano",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.MASCOTA, CategoriaJoker.ANIMAL
        );
    }

    @Override
    public Joker copiar() {
        Dogo copia = new Dogo();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        int cantidadPares = 0;
        for (Carta carta : juego.getJugador().getMano()) {
            if (carta.getNumero() % 2 == 0) cantidadPares++;
        }
        if (cantidadPares > 0) {
            double bonus = cantidadPares * 25;
            ctx.getResolucionActual().sumarChips(bonus, this.getNombre(), this);
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        if (juego == null || juego.getJugador() == null) return getDescripcion();
        int cantidadPares = 0;
        for (Carta carta : juego.getJugador().getMano()) {
            if (carta.getNumero() % 2 == 0) cantidadPares++;
        }
        return getDescripcion() + "\n(Actual: [+#CAFE]+" + (cantidadPares * 25) + "[] puntos)";
    }
}
