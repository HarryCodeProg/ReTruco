package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Bariloche extends Joker {

    public Bariloche() {
        super(100, "Bariloche", "Bariloche",
            "+$10 al derrotar al rival.\nEstablece en $0 tus pesos al ser vendido.",
            Rareza.raro,
            5,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        Bariloche copia = new Bariloche();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            ctx.getJugador().sumarPesos(10);
            // Nota: Si tenés acceso al GestorVisualVictoria desde tu "Juego" o "ContextoJuego",
            // podrías registrarlo acá para que salga en la pantallita final.
            // Ej: juego.getGestorVisualVictoria().agregarRecompensaJoker(getNombre(), 10);
        }
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        int pesosActuales = jugador.getPesos();
        jugador.sumarPesos(-pesosActuales);
    }
}
