package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import java.util.Random;

public class Ardilla extends Joker {

    // -1 = Ninguno, 0 = Manos, 1 = Tamaño Mano, 2 = Descartes
    private int buffActivo = -1;
    private String textoBuffActual = "";

    public Ardilla() {
        super(63, "Ardilla", "Ardilla",
            "Al seleccionar rival obtienes\naleatoriamente uno de estos efectos:\n+1 mano, +2 tamaño mano o +3 descartes",
            Rareza.raro,
            6,
            FaseActivacion.INDEPENDIENTE);
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        String desc = getDescripcion();
        // Le mostramos al jugador qué buff le tocó en este combate
        if (buffActivo != -1) {
            desc += "\n(Activo: [CYAN]" + textoBuffActual + "[])";
        }
        return desc;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
        // 1. APLICAR EL BUFF ALEATORIO AL EMPEZAR
        if (evento == EventoJuego.INICIO_COMBATE) {
            // Por seguridad, si había un buff residual de la pelea anterior, lo limpiamos
            revertirBuff(contexto.getJugador());
            Random rand = new Random();
            buffActivo = rand.nextInt(3);
            switch (buffActivo) {
                case 0: // +1 Mano
                    contexto.getJugador().aumentarManosMaximas(1);
                    textoBuffActual = "+1 Mano";
                    break;
                case 1: // +2 Tamaño Mano
                    contexto.getJugador().aumentarTamañoMano(2);
                    textoBuffActual = "+2 Tamaño Mano";
                    // Como INICIO_COMBATE ocurre después del primer reparto,
                    // forzamos al juego a rellenar esos 2 espacios nuevos inmediatamente.
                    juego.repartir();
                    break;
                case 2: // +3 Descartes
                    contexto.getJugador().sumarDescartesExtra(3);
                    // Actualizamos los descartes de la partida actual para que reflejen el nuevo máximo
                    juego.restaurarDescartes();
                    textoBuffActual = "+3 Descartes";
                    break;
            }
        }
        // 2. REVERTIR EL BUFF AL TERMINAR
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            revertirBuff(contexto.getJugador());
        }
    }

    // 3. REVERTIR EL BUFF SI SE VENDE EN MEDIO DEL COMBATE
    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        revertirBuff(jugador);
    }

    private void revertirBuff(Jugador jugador) {
        if (buffActivo == -1) return;
        switch (buffActivo) {
            case 0:
                jugador.aumentarManosMaximas(-1);
                break;
            case 1:
                jugador.aumentarTamañoMano(-2);
                break;
            case 2:
                jugador.sumarDescartesExtra(-3);
                break;
        }
        buffActivo = -1;
        textoBuffActual = "";
    }

    @Override
    public Joker copiar() {
        Ardilla copia = new Ardilla();
        this.copiarEstado(copia);
        copia.buffActivo = this.buffActivo;
        copia.textoBuffActual = this.textoBuffActual;
        return copia;
    }
}
