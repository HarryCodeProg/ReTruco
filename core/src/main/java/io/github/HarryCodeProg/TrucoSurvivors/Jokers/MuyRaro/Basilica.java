package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Basilica extends Joker {

    public Basilica() {
        super(130, "Basílica", "Basilica", "x0.25 de multiplicador por cada carta consumida",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        Basilica copia = new Basilica();
        copiarEstado(copia);
        return copia;
    }

    private double calcularMultiplicador(Juego juego) {
        if (juego == null || juego.getJugador() == null || juego.getJugador().getMazo() == null) return 1.0;
        int tamañoMazo = juego.getJugador().getMazo().getTamañoMazo();
        int cartasConsumidas = Math.max(0, 40 - tamañoMazo);
        return 1.0 + (cartasConsumidas * 0.25);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO || evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            double mult = calcularMultiplicador(juego);
            if (mult > 1.0) {
                ctx.getResolucionActual().multiplicarMult(mult, getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        double mult = calcularMultiplicador(juego);
        String formattedMult = String.format(java.util.Locale.US, "%.2f", mult);
        if (formattedMult.endsWith(".00")) formattedMult = formattedMult.substring(0, formattedMult.length() - 3);
        else if (formattedMult.endsWith("0")) formattedMult = formattedMult.substring(0, formattedMult.length() - 1);

        return "x0.25 de multiplicador por cada carta consumida (actual: x" + formattedMult + ")";
    }
}
