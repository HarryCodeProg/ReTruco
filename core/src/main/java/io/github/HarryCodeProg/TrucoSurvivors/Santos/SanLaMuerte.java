package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolJokersTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;

import java.util.ArrayList;

public class SanLaMuerte extends Santo {

    public SanLaMuerte() {
        super(
            19,
            "San La Muerte",
            "SanLaMuerte",
            "Selecciona 2 cartas, estas se eliminan y se genera un joker epico aleatorio (si hay espacio)",
            8
        );
    }

    @Override
    public int cartasRequeridas() {
        return 2;
    }

    @Override
    public int maxCartasSeleccionables() {
        return 2;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        if (seleccionadas == null || seleccionadas.size() != 2) return;
        if (jugador.getJokers().size() >= jugador.getTamañoJokers()) return;
        for (Carta carta : seleccionadas) {
            jugador.eliminarCarta(carta);
        }
        PoolJokersTienda pool = new PoolJokersTienda();
        Joker joker = pool.tomarAleatorioDeRareza(Rareza.epico, jugador);
        if (joker != null) {
            jugador.agregarJoker(joker);
        }
    }
}
