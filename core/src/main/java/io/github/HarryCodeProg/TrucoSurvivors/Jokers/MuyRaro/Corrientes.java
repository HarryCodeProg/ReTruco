package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Corrientes extends Joker {

    public Corrientes() {
        super(117, "Corrientes", "Corrientes", "Acumula los puntos que sobran\nal superar la meta",
            Rareza.muyRaro,
            8,
            FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        // Sobrescribimos para mostrar el valor dinámico guardado en la variable nativa `acumulado`
        return getDescripcion() + "\n(actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
        // 1. ATRAPAR EL DESBORDE: Se ejecuta justo cuando ganás la ronda.
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            double puntosJugador = juego.getPuntosJugador();
            double meta = juego.getPuntajeMeta();
            if (puntosJugador > meta) {
                double exceso = puntosJugador - meta;
                this.sumarAcumulado(exceso);
            }
        }
        // 2. APLICAR EL ACUMULADO: Le da los puntos acumulados a tus manos jugadas.
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO || evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            if (getAcumulado() > 0 && contexto.getResolucionActual() != null) {
                // Sumamos el excedente como Puntos/Chips base a la mano
                contexto.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public Joker copiar() {
        Corrientes copia = new Corrientes();
        this.copiarEstado(copia);
        return copia;
    }
}
