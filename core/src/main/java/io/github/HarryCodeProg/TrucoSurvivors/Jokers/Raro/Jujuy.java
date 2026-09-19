package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Jujuy extends Joker {

    public Jujuy() {
        super(
            62,
            "Jujuy",
            "Jujuy",
            "+7 multiplicador envido, se reactiva por cada carta en tu mano.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public Joker copiar() {
        Jujuy copia = new Jujuy();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Solo nos interesa el momento de sumar los puntos del Envido
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            // Obtenemos cuántas cartas tiene actualmente el jugador en la mano
            int cartasEnMano = juego.getJugador().getMano().size();
            // Hacemos un bucle para que el Joker se "reactive" visual y matemáticamente
            for (int i = 0; i < cartasEnMano; i++) {
                ctx.getResolucionActual().sumarMult(7, this.getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        if (juego == null || juego.getJugador() == null) {
            return getDescripcion();
        }
        int cartasEnMano = juego.getJugador().getMano().size();
        int totalMult = cartasEnMano * 7;
        return getDescripcion() + "\n(Actual: [+#ROJO]+" + totalMult + "[] mult)";
    }
}
