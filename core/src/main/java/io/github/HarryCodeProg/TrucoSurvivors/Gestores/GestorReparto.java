package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;

import java.util.ArrayList;

public class GestorReparto {

    /**
     * Acomoda una mano en su posición final.
     * No inicia animaciones; solamente cambia el destino de las cartas.
     */
    public void organizarMano(ArrayList<VistaCarta> cartas, float y, float anchoCarta, float separacion) {
        int cantidad = cartas.size();
        float inicioX = calcularInicioX(cantidad, anchoCarta, separacion);
        float paso = calcularPaso(cantidad, anchoCarta, separacion);
        for (int i = 0; i < cantidad; i++) {
            VistaCarta carta = cartas.get(i);
            float x = inicioX + i * paso;
            carta.setPosition(x, y);
            carta.setHandPosition(x, y);
            carta.setTargetRotation(0f);
        }
    }

    public float calcularPaso(int cantidad, float anchoCarta, float separacion) {
        float margenLateral = 220f;
        float anchoMaximo = Gdx.graphics.getWidth() - margenLateral * 2;
        return GestorReordenamiento.calcularPaso(cantidad, anchoCarta, separacion, anchoMaximo);
    }

    public float calcularInicioX(int cantidad, float anchoCarta, float separacion) {
        float margenLateral = 220f;
        float anchoMaximo = Gdx.graphics.getWidth() - margenLateral * 2;
        float paso = GestorReordenamiento.calcularPaso(cantidad, anchoCarta, separacion, anchoMaximo);
        float anchoTotal = anchoCarta + (cantidad - 1) * paso;
        return margenLateral + (anchoMaximo - anchoTotal) / 2f;
    }

    private DatosAbanico calcularDatosAbanico(int indice, float inicioX, float yBase, float anchoCarta, float separacion) {
        float margenLateral = 220f;
        float anchoMaximo = Gdx.graphics.getWidth() - margenLateral * 2;
        int cantidadTotal = 3;
        float paso = GestorReordenamiento.calcularPaso(cantidadTotal, anchoCarta, separacion, anchoMaximo);
        float angulo = 0f;
        float x = inicioX + indice * paso;
        float y = yBase;
        return new DatosAbanico(x, y, angulo);
    }

    private static class DatosAbanico {
        final float x;
        final float y;
        final float angulo;
        DatosAbanico(float x, float y, float angulo) {
            this.x = x;
            this.y = y;
            this.angulo = angulo;
        }
    }
}
