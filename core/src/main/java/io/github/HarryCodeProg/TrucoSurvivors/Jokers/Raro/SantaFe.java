package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class SantaFe extends Joker {

    public SantaFe() {
        super(
            88,
            "SantaFe",
            "SantaFe",
            "X0.1 multiplicador envido por cada santo usado.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        setAcumulado(1.0);
    }

    @Override
    public Joker copiar() {
        SantaFe copia = new SantaFe();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // 1. Escalar el multiplicador cada vez que el jugador consume un Santo
        if (evento == EventoJuego.AL_CONSUMIR_SANTO) {
            setAcumulado(getAcumulado() + 0.1);
        }
        // 2. Aplicar el multiplicador exponencial (X-Mult) al Envido
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            double multActual = getAcumulado();
            if (multActual > 1.0) { // Filtramos para no mostrar "X1" inútilmente en la primera vez
                ctx.getResolucionActual().multiplicarMult(multActual, this.getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        // Redondeamos a un decimal para evitar errores de coma flotante (ej: 1.200000001)
        double multActualRedondeado = Math.round(getAcumulado() * 10.0) / 10.0;
        // Coloreamos de rojo al estilo clásico de los X-Mult
        return getDescripcion() + "\n(Actual: X" + multActualRedondeado + ")";
    }
}
