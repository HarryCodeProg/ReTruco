package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayList;

/**
 * Obelisco: Al final de cada mano, obtienes +1 multiplicador envido por cada joker
 * que contenga alguna de estas categorías: Alcohol, Deporte, Campo, Transporte, Ciudad.
 * (actual: X)
 */
public class Obelisco extends Joker {

    private static final CategoriaJoker[] CATEGORIAS_OBJETIVO = {
            CategoriaJoker.ALCOHOL,
            CategoriaJoker.DEPORTE,
            CategoriaJoker.CAMPO,
            CategoriaJoker.TRANSPORTE,
            CategoriaJoker.CIUDAD
    };

    public Obelisco() {
        super(128, "Obelisco", "Obelisco",
                "Al final de cada mano, obtienes +1 multiplicador envido por cada joker " +
                "que contenga alguna de estas categorías: Alcohol, Deporte, Campo, Transporte, Ciudad. (actual: 0)",
                Rareza.muyRaro, 8, Joker.FaseActivacion.AL_MANTENER_CARTA,
                CategoriaJoker.CIUDAD); // Categoría principal: Ciudad
    }

    @Override
    public Joker copiar() {
        Obelisco copia = new Obelisco();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Solo aplicar al final de la mano
        if (evento != EventoJuego.TERMINO_MANO) {
            return;
        }

        if (juego == null) {
            return;
        }

        Jugador jugador = juego.getTurnoActual();
        if (jugador == null) {
            return;
        }

        ArrayList<Joker> jokers = jugador.getJokers();

        // Contar jokers que tengan alguna de las categorías objetivo
        int count = 0;
        for (Joker joker : jokers) {
            for (CategoriaJoker categoriaObjetivo : CATEGORIAS_OBJETIVO) {
                if (joker.tieneCategoria(categoriaObjetivo)) {
                    count++;
                    break; // Evitar contar doble si tiene múltiples categorías objetivo
                }
            }
        }

        // El acumulado almacena el último bonus aplicado
        int bonusAplicado = (int) getAcumulado();
        int delta = count - bonusAplicado; // Solo aplicar la diferencia

        if (delta != 0) {
            jugador.aumentarMultiplicadorEnvido(delta);
            setAcumulado(count);
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        if (juego != null) {
            Jugador jugador = juego.getTurnoActual();
            if (jugador != null) {
                ArrayList<Joker> jokers = jugador.getJokers();

                int count = 0;
                for (Joker joker : jokers) {
                    for (CategoriaJoker categoriaObjetivo : CATEGORIAS_OBJETIVO) {
                        if (joker.tieneCategoria(categoriaObjetivo)) {
                            count++;
                            break;
                        }
                    }
                }

                String baseDesc = getDescripcion();
                return baseDesc.replace("(actual: 0)", "(actual: " + count + ")");
            }
        }
        return getDescripcion();
    }
}