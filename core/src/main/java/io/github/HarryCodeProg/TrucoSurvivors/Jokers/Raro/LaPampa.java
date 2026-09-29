package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class LaPampa extends Joker {

    public LaPampa() {
        super(
            93,
            "LaPampa",
            "LaPampa",
            "X4 multiplicador envido.\nPierde X0.25 por cada joker vendido o comprado.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        setAcumulado(4.0);
    }

    @Override
    public Joker copiar() {
        LaPampa copia = new LaPampa();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado()); // Importante para que no se resetee al guardarlo
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // 1. Degradación al interactuar con la tienda
        if (evento == EventoJuego.AL_COMPRAR_JOKER || evento == EventoJuego.AL_VENDER_JOKER) {
            if (getAcumulado() > 1.0) {
                // Le restamos 0.25 pero le ponemos un tope mínimo de 1.0 (para que no te reste puntos)
                setAcumulado(Math.max(1.0, getAcumulado() - 0.25));
            }
        }

        // 2. Aplicación del Multiplicador al Envido
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            double multActual = getAcumulado();
            // Solo lo aplicamos si es mayor a 1 (x1 no hace nada matemáticamente)
            if (multActual > 1.0) {
                ctx.getResolucionActual().multiplicarMult(multActual, getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        // Formateamos para que se vea lindo (ej: X3.75 o X4.0)
        String multTexto = String.valueOf(getAcumulado());
        if (multTexto.endsWith(".0")) {
            multTexto = multTexto.substring(0, multTexto.length() - 2); // Quita el ".0" si es un número entero
        }
        return "X4 multiplicador envido.\nPierde X0.25 por cada Joker vendido o comprado.\n\n(Actual: X" + multTexto + ")";
    }
}
