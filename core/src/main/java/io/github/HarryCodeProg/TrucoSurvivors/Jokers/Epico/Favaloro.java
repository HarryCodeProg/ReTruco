package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Favaloro extends Joker {

    public Favaloro() {
        super(132, "Favaloro", "Favaloro",
            "Si al acabar las manos no llegas al puntaje meta, pasas igual al rival, este joker se consume y recibes 100 pesos",
            Rareza.epico, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.TERMINO_MANO) return;
        Jugador jugador = ctx.getJugador();
        double puntajeMeta = juego.getPuntajeMeta();
        // Solo se activa si el jugador no alcanzó el puntaje meta y se quedó sin manos
        if (juego.getPuntosJugador() >= puntajeMeta || jugador.getManosActuales() > 0) {
            return;
        }
        // El joker se consume: le gana al rival sumando los puntos necesarios para alcanzar la meta
        double puntosNecesarios = puntajeMeta - juego.getPuntosJugador();
        juego.sumarPuntosJugador(puntosNecesarios);
        // Otorgamos 100 pesos al jugador
        jugador.sumarPesos(100);
        // Se elimina automáticamente el joker
        jugador.eliminarJoker(this);
    }

    @Override
    public Joker copiar() {
        Favaloro copia = new Favaloro();
        copiarEstado(copia);
        return copia;
    }
}
