package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolSantosTienda;

import java.util.ArrayList;

public class LuzMala extends Santo {

    public LuzMala() {
        super(25, "Luz Mala", "LuzMala", "Genera 2 santos aleatorios (si hay espacio)", 5);
    }

    @Override public int cartasRequeridas() { return 0; }

    @Override
    public int maxCartasSeleccionables() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        PoolSantosTienda pool = new PoolSantosTienda();
        int espacioDisponible = jugador.getTamañoSantos() - jugador.getSantos().size();
        int cantidad = Math.min(2, espacioDisponible);
        for (int i = 0; i < cantidad; i++) {
            Santo santo = pool.tomarAleatorio();
            if (santo != null) {
                jugador.agregarSanto(santo);
            }
        }
    }
}
