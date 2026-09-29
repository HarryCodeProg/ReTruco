package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Perro extends Joker {

    private static final int META_GASTO = 50;
    private static final double MULT = 2;

    public Perro() {
        super(
            131,
            "Perro",
            "Perro",
            "X2 multiplicador, después de gastar ($50) este joker no consume espacio",
            Rareza.epico,
            8,
            Joker.FaseActivacion.INDEPENDIENTE
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO &&
            evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            return;
        }
        if (ctx.getResolucionActual() == null) return;
        ctx.getResolucionActual().multiplicarMult(MULT, getNombre(), this);
    }

    @Override
    public void onPesosGastados(int cantidad, Jugador jugador) {
        if (getAcumulado() < META_GASTO) {
            sumarAcumulado(cantidad);
        }
    }

    @Override
    public boolean ocupaEspacio() {
        return getAcumulado() < META_GASTO;
    }

    @Override
    public String getDescripcionRenderizada() {
        int gastado = (int) Math.min(getAcumulado(), META_GASTO);
        if (gastado >= META_GASTO) {
            return "X2 multiplicador. No consume espacio (gastado $" + META_GASTO + ")";
        }
        return getDescripcion() + " ($" + gastado + "/" + META_GASTO + ")";
    }

    @Override
    public Joker copiar() {
        Perro copia = new Perro();
        copiarEstado(copia);
        return copia;
    }
}
