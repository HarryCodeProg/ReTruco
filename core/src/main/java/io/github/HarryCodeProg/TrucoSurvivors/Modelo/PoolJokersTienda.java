package io.github.HarryCodeProg.TrucoSurvivors.Modelo;


import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun.BotellaCortada;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class PoolJokersTienda {
    private final ArrayList<Supplier<Joker>> fabricas = new ArrayList<>();

    public PoolJokersTienda() {
        fabricas.add(JokerEspañol::new);
        fabricas.add(Mate::new);
        fabricas.add(Gaseosa::new);
        fabricas.add(BotellaCortada::new);
        fabricas.add(Fernet::new);
        fabricas.add(VinoCaja::new);
        fabricas.add(BolasDeFraile::new);
        fabricas.add(Cañoncito::new);
        fabricas.add(Cerveza::new);
        fabricas.add(CervezaRota::new);
        fabricas.add(Churros::new);
        fabricas.add(DulceDeLeche::new);
        fabricas.add(Galletitas::new);
        fabricas.add(MediaLuna::new);
        fabricas.add(Milanesa::new);
        fabricas.add(Moñito::new);
        fabricas.add(Pastafrola::new);
        fabricas.add(Pastelitos::new);
        fabricas.add(Sacramento::new);
        fabricas.add(Termo::new);
        fabricas.add(TortaNegra::new);
        fabricas.add(Vigilante::new);
    }

    /** Devuelve un joker nuevo al azar, evitando (si es posible) los que el jugador ya tiene por clase. */
    public Joker tomarAleatorio(Random random, Jugador jugador) {
        ArrayList<Supplier<Joker>> disponibles = new ArrayList<>();
        for (Supplier<Joker> f : fabricas) {
            Joker candidato = f.get();
            boolean yaLoTiene = jugador.getJokers().stream()
                .anyMatch(j -> j.getClass().equals(candidato.getClass()));
            if (!yaLoTiene) disponibles.add(f);
        }
        if (disponibles.isEmpty()) disponibles = fabricas; // si ya tiene todos, permite repetidos
        return disponibles.get(random.nextInt(disponibles.size())).get();
    }
}
