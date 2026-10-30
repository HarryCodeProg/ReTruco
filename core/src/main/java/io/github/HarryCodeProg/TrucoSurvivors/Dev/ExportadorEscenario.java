package io.github.HarryCodeProg.TrucoSurvivors.Dev;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.DatosRival;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

import java.util.ArrayList;

/**
 * Convierte el estado de una partida real en un script del entorno de desarrollo.
 * Se pega en DevScreen (Ctrl+V) para reproducir lo que vio un jugador.
 */
public final class ExportadorEscenario {
    private ExportadorEscenario() {}

    public static String desdeEstado(Jugador jugador, Juego juego, DatosRival rival, String pantalla) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ReTruco - escenario exportado\n");
        sb.append("# pantalla: ").append(pantalla);
        if (rival != null) sb.append("   rival: ").append(rival.getNombre()).append(" (meta ").append(EntornoDev.fmt(rival.getPuntosMeta())).append(")");
        sb.append("\nreiniciar\n");
        sb.append("pesos ").append(jugador.getPesos()).append('\n');

        ArrayList<Joker> jokers = jugador.getJokers();
        if (!jokers.isEmpty()) {
            sb.append("joker ");
            for (int i = 0; i < jokers.size(); i++) sb.append(i > 0 ? ", " : "").append(jokers.get(i).getId());
            sb.append("   # ");
            for (int i = 0; i < jokers.size(); i++) sb.append(i > 0 ? ", " : "").append(jokers.get(i).getNombre());
            sb.append('\n');
            for (int i = 0; i < jokers.size(); i++) {
                Joker j = jokers.get(i);
                if (j.getAcumulado() != 0) sb.append("acum ").append(i + 1).append(' ').append(EntornoDev.fmt(j.getAcumulado())).append('\n');
                String extra = j.getEstadoExtra();
                if (extra != null && !extra.isEmpty()) sb.append("extra ").append(i + 1).append(' ').append(extra).append('\n');
            }
        }
        ArrayList<Santo> santos = jugador.getSantos();
        if (!santos.isEmpty()) {
            sb.append("santo ");
            for (int i = 0; i < santos.size(); i++) sb.append(i > 0 ? ", " : "").append(santos.get(i).getId());
            sb.append('\n');
        }

        Mazo mazo = jugador.getMazo();
        ArrayList<Carta> todas = new ArrayList<>();
        for (Carta c : mazo.getMazo()) todas.add(c);
        for (Carta c : mazo.getCartasTomadas()) todas.add(c);
        for (Carta c : mazo.getCartasDescartadas()) todas.add(c);
        if (!todas.isEmpty()) sb.append("mazo ").append(EntornoDev.textoCartas(todas)).append('\n');

        if (juego != null) {
            sb.append("meta ").append(EntornoDev.fmt(juego.getPuntajeMeta())).append('\n');
            sb.append("manos ").append(jugador.getManosActuales()).append('\n');
            sb.append("descartes ").append(juego.getDescartesActuales()).append('\n');
            sb.append("esmano ").append(juego.isJugadorEsMano() ? "si" : "no").append('\n');
            sb.append("mano ").append(EntornoDev.textoCartas(jugador.getMano())).append('\n');
            sb.append("rival ").append(EntornoDev.textoCartas(juego.getRival().getMano())).append('\n');
            sb.append("# mesa jugador: ").append(EntornoDev.textoCartas(juego.getMesa().getMesaJugador())).append('\n');
            sb.append("# mesa rival:   ").append(EntornoDev.textoCartas(juego.getMesa().getMesaRival())).append('\n');
            sb.append("# puntos jugador ").append(EntornoDev.fmt(juego.getPuntosJugador()))
                .append("  rival ").append(EntornoDev.fmt(juego.getPuntosRival())).append('\n');
            StringBuilder rasgos = new StringBuilder();
            for (CategoriaJoker c : juego.getGestorRasgos().getCategoriasActivas()) {
                rasgos.append(c.name()).append(" t").append(juego.getGestorRasgos().getTier(c)).append("  ");
            }
            sb.append("# rasgos activos: ").append(rasgos.length() > 0 ? rasgos.toString() : "ninguno").append('\n');
        }
        return sb.toString();
    }
}
