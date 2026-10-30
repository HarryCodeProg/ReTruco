package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.ColorMecanica;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.HashSet;
import java.util.Set;

public class TierraDelFuego extends Joker {

    private static final double PUNTOS_POR_ACTIVACION = 5;

    /** IDs de jokers que ya aportaron en la mano actual (para no contar 2 veces al mismo). */
    private final Set<Integer> jokersContadosEstaMano = new HashSet<>();

    public TierraDelFuego() {
        super(
            99,
            "Tierra Del Fuego",
            "TierraDelFuego",
            ColorMecanica.PUNTOS.colorHex()+"+5[] puntos cada vez que se active otro joker (actual: +0)",
            Rareza.raro,
            6,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TRUCO,
            CategoriaJoker.ENVIDO,
            CategoriaJoker.TERRITORIO
        );
    }

    @Override
    public void onOtroJokerActivado(Joker otro, EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Ignorarse a sí mismo (por si el caller notifica a todos, incluido este).
        if (otro == null || otro == this || otro.getId() == getId()) return;
        // Al terminar la mano, reseteamos la ventana de dedup para la próxima.
        if (evento == EventoJuego.TERMINO_MANO) {
            jokersContadosEstaMano.clear();
            return;
        }
        // Solo contar una vez por joker activado: usamos el evento de truco como
        // "disparador canónico" para no duplicar por envido/otros eventos.
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        // Si este joker ya aportó esta mano, no sumar de nuevo.
        if (!jokersContadosEstaMano.add(otro.getId())) return;

        sumarAcumulado(PUNTOS_POR_ACTIVACION);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO &&
            evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            return;
        }
        if (ctx.getResolucionActual() == null) return;
        if (getAcumulado() <= 0) return;
        ctx.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada() {
        String hex = ColorMecanica.PUNTOS.colorHex();
        return hex + "+5[] puntos cada vez que se active otro joker "
            + "(actual: " + hex + "+" + (int) getAcumulado() + "[])";
    }

    @Override
    public Joker copiar() {
        TierraDelFuego copia = new TierraDelFuego();
        copiarEstado(copia);
        return copia;
    }
}
