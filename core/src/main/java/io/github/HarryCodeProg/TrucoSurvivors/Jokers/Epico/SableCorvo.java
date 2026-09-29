package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import com.badlogic.gdx.math.MathUtils;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class SableCorvo extends Joker {

    public enum EfectoSable { PUNTOS, MULT_SUMA, MULT_X, DINERO }
    private EfectoSable efectoActual;

    public SableCorvo() {
        super(
            143,
            "SableCorvo",
            "SableCorvo",
            "Obtiene aleatoriamente uno de estos efectos:\n+125 puntos, +35 mult, X4 mult o +$10.\n(Cambia en cada mano)",
            Rareza.epico,
            8,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        cambiarEfecto();
    }

    private void cambiarEfecto() {
        EfectoSable[] efectos = EfectoSable.values();
        efectoActual = efectos[MathUtils.random(efectos.length - 1)];
    }

    public void setEfectoActual(EfectoSable efecto) {
        this.efectoActual = efecto;
    }

    @Override
    public Joker copiar() {
        SableCorvo copia = new SableCorvo();
        copiarEstado(copia);
        copia.setEfectoActual(this.efectoActual);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // 1. Efectos de Puntuación (Se aplican tanto al Truco como al Envido)
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO || evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            switch (efectoActual) {
                case PUNTOS:
                    ctx.getResolucionActual().sumarChips(125, this.getNombre(), this);
                    break;
                case MULT_SUMA:
                    ctx.getResolucionActual().sumarMult(35, this.getNombre(), this);
                    break;
                case MULT_X:
                    ctx.getResolucionActual().multiplicarMult(4, this.getNombre(), this);
                    break;
                default:
                    break;
            }
        }
        // 2. Efecto de Dinero y Rotación (Se activa al terminar CADA mano)
        if (evento == EventoJuego.TERMINO_MANO) {
            // Si el efecto actual es DINERO, lo entregamos al finalizar la mano
            if (efectoActual == EfectoSable.DINERO) {
                juego.getJugador().sumarPesos(10);
            }
            // Sorteamos el efecto para la próxima mano jugada
            cambiarEfecto();
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        String textoEfecto = "";
        switch (efectoActual) {
            case PUNTOS:
                textoEfecto = "+125 puntos";
                break;
            case MULT_SUMA:
                textoEfecto = "+35 mult";
                break;
            case MULT_X:
                textoEfecto = "X4 mult";
                break;
            case DINERO:
                textoEfecto = "+$10";
                break;
        }
        return "Obtiene aleatoriamente uno de estos efectos:\n+125 puntos, +35 mult, X4 mult o +$10.\n(Cambia en cada mano) ";
    }
}
