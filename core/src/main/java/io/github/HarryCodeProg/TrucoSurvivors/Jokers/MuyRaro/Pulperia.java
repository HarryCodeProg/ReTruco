package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;

import java.util.ArrayList;

public class Pulperia extends Joker {

    private boolean yaDescartoEstaRonda = false;

    public Pulperia() {
        super(109, "Pulperia", "Pulperia", "Consume todas las cartas de tu primer descarte. Otorga $2 por carta consumida",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.ALCOHOL);
    }

    @Override
    public Joker copiar() {
        Pulperia copia = new Pulperia();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.POST_REPARTO) {
            yaDescartoEstaRonda = false;
        }

        if (evento == EventoJuego.AL_DESCARTAR && !yaDescartoEstaRonda) {
            yaDescartoEstaRonda = true;

            Mazo mazo = ctx.getJugador().getMazo();
            ArrayList<Carta> descartadas = mazo.getCartasDescartadas();
            int cartasConsumidas = descartadas.size();

            if (cartasConsumidas > 0) {
                // Dar pesos
                ctx.getJugador().sumarPesos(cartasConsumidas * 2);

                // Remover permanentemente del tamaño del mazo y limpiar descartes para que no vuelvan
                mazo.restarTamañoMazo(cartasConsumidas);
                descartadas.clear();

                // Reproducir sonido para dar feedback
                GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
                if (sonidos != null) sonidos.reproducirSonidoGastarPeso();
            }
        }
    }
}
