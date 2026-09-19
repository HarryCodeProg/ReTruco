package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Guanaco extends Joker {

    public Guanaco() {
        super(
            67,
            "Guanaco",
            "Guanaco",
            "+1 descarte.\n+8 y X0.3 multiplicador truco por cada descarte restante al derrotar a un rival.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.ANIMAL
        );
        // El acumulado va a guardar el total histórico de descartes ahorrados
        setAcumulado(0);
    }

    @Override
    public Joker copiar() {
        Guanaco copia = new Guanaco();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarDescartesExtra(1);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarDescartesExtra(-1);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // 1. Aplicamos los multiplicadores al Truco
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            double totalDescartesAhorrados = getAcumulado();
            if (totalDescartesAhorrados > 0) {
                double sumaMult = totalDescartesAhorrados * 8.0;
                double xMult = 1.0 + (totalDescartesAhorrados * 0.3);
                // Primero aplicamos la suma (es importante el orden para que la matemática rinda más)
                ctx.getResolucionActual().sumarMult(sumaMult, this.getNombre(), this);
                // Luego aplicamos el multiplicador X
                ctx.getResolucionActual().multiplicarMult(xMult, this.getNombre(), this);
            }
        }

        // 2. Acumulamos los descartes restantes al ganar el nivel/combate
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            int descartesRestantes = juego.getDescartesActuales();
            if (descartesRestantes > 0) {
                setAcumulado(getAcumulado() + descartesRestantes);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        double ahorrados = getAcumulado();
        int sumaActual = (int) (ahorrados * 8);
        double xActual = Math.round((1.0 + (ahorrados * 0.3)) * 100.0) / 100.0;
        return getDescripcion() + "\n\n(Actual: +" + sumaActual + " mult y X" + xActual + " mult)";
    }
}
