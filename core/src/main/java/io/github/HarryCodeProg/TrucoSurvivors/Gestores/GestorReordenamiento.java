package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Vista.Arrastrable;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaJoker;

import java.util.ArrayList;

public class GestorReordenamiento {

    public <T extends Arrastrable> boolean previsualizarReordenamiento(GestorInputArrastrable<T> gestor,
                                                                       ArrayList<T> lista) {
        T arrastrando = gestor.getArrastrado();
        if (arrastrando == null) return false;
        int indiceActual = lista.indexOf(arrastrando);
        if (indiceActual == -1 || lista.size() <= 1) return false;
        float xCentroArrastrado = arrastrando.getCentroX();
        for (int i = 0; i < lista.size(); i++) {
            if (i == indiceActual) continue;
            T otro = lista.get(i);
            // Comparamos contra el centro teórico de la posición de destino (target)
            // para evitar parpadeos si el otro elemento se está moviendo.
            float xCentroOtro = otro.getHandTargetX() + (otro.getAncho() / 2f);
            // Si se mueve hacia la derecha y supera el centro del elemento vecino
            if (indiceActual < i && xCentroArrastrado > xCentroOtro) {
                lista.remove(indiceActual);
                lista.add(i, arrastrando);
                return true;
            }
            // Si se mueve hacia la izquierda y supera el centro del elemento vecino
            else if (indiceActual > i && xCentroArrastrado < xCentroOtro) {
                lista.remove(indiceActual);
                lista.add(i, arrastrando);
                return true;
            }
        }
        return false;
    }

    // Sobrecargas para mantener compatibilidad con llamadas explícitas si las tienes
    public boolean previsualizarReordenamientoCartas(GestorInputArrastrable<VistaCarta> gestorCartas, ArrayList<VistaCarta> cartasJugador) {
        return previsualizarReordenamiento(gestorCartas, cartasJugador);
    }

    public boolean previsualizarReordenamientoJokers(GestorInputArrastrable<VistaJoker> gestorJokers, ArrayList<VistaJoker> jokers) {
        return previsualizarReordenamiento(gestorJokers, jokers);
    }

    public static float calcularPaso(int cantidad, float anchoCarta, float separacionDeseada, float anchoMaximo) {
        if (cantidad <= 1) return anchoCarta + separacionDeseada;
        float pasoIdeal = anchoCarta + separacionDeseada;
        float anchoTotalIdeal = anchoCarta + (cantidad - 1) * pasoIdeal;
        if (anchoTotalIdeal <= anchoMaximo) {
            return pasoIdeal;
        }
        float pasoAjustado = (anchoMaximo - anchoCarta) / (cantidad - 1);
        return Math.max(pasoAjustado, anchoCarta * 0.25f);
    }
}
