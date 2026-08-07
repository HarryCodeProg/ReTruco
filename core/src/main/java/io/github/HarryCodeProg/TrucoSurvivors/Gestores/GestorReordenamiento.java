package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Vista.Arrastrable;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaJoker;

import java.util.ArrayList;

public class GestorReordenamiento {

    public void actualizarPreviews(GestorInputArrastrable<VistaCarta> gestorCartas,
                                   ArrayList<VistaCarta> cartasJugador,
                                   GestorInputArrastrable<VistaJoker> gestorJokers,
                                   ArrayList<VistaJoker> jokers,
                                   Runnable alOrganizarCartas,
                                   Runnable alOrganizarJokers) {
        boolean cambioCartas = previsualizarReordenamiento(gestorCartas, cartasJugador);
        if (cambioCartas && alOrganizarCartas != null) {
            alOrganizarCartas.run();
        }
        boolean cambioJokers = previsualizarReordenamiento(gestorJokers, jokers);
        if (cambioJokers && alOrganizarJokers != null) {
            alOrganizarJokers.run();
        }
    }

    /**
     * Reordena genéricamente cualquier lista de Arrastrables (Cartas o Jokers)
     * en tiempo real mientras se arrastra un elemento.
     */
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

    public <T extends Arrastrable> void actualizarSlots(
        ArrayList<Float> slots,
        int cantidad,
        float ancho,
        float separacion,
        float anchoPantalla) {
        slots.clear();
        float margenLateral = 220f;
        float anchoMaximo = anchoPantalla - margenLateral * 2;
        float paso = calcularPaso(cantidad, ancho, separacion, anchoMaximo);
        float anchoTotal = ancho + (cantidad - 1) * paso;
        float inicioX = margenLateral + (anchoMaximo - anchoTotal) / 2f;
        for (int i = 0; i < cantidad; i++) {
            slots.add(inicioX + i * paso);
        }
    }

    public <T extends Arrastrable> int calcularIndicePreview(
        T arrastrado,
        ArrayList<Float> slots,
        float ancho) {
        if (arrastrado == null)
            return -1;
        float centroX = arrastrado.getCentroX();
        int mejorIndice = 0;
        float mejorDistancia = Float.MAX_VALUE;
        for (int i = 0; i < slots.size(); i++) {
            float centroSlot = slots.get(i) + ancho / 2f;
            float distancia = Math.abs(centroX - centroSlot);

            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                mejorIndice = i;
            }
        }
        return mejorIndice;
    }

    public <T extends Arrastrable> void organizarPreview(
        ArrayList<T> lista,
        T arrastrado,
        int indicePreview,
        float ancho,
        float separacion,
        float y,
        float anchoPantalla) {
        if (arrastrado == null)
            return;
        ArrayList<T> visual = new ArrayList<>(lista);
        visual.remove(arrastrado);
        if (indicePreview < 0 || indicePreview > visual.size()) {
            indicePreview = visual.size();
        }
        visual.add(indicePreview, arrastrado);
        float anchoTotal = (visual.size() * ancho) + ((visual.size() - 1) * separacion);
        float inicioX = (anchoPantalla - anchoTotal) / 2f;
        for (int i = 0; i < visual.size(); i++) {
            visual.get(i).setHandPosition(
                inicioX + i * (ancho + separacion),
                y
            );
        }
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
