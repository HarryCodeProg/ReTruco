package io.github.HarryCodeProg.TrucoSurvivors.Gestores; // Ajustá el paquete según tu estructura

import java.util.Locale;

public class FormateadorPuntaje {

    // Umbral a partir del cual se usa notación científica (ej: 100 mil millones)
    private static final double UMBRAL_CIENTIFICO = 1e11;

    public static String formatear(double valor) {
        // 1. Manejar el desbordamiento total (Infinity / NaN) al estilo Balatro
        if (Double.isInfinite(valor) || Double.isNaN(valor)) {
            return "naneinf";
        }

        // 2. Si el número superó el umbral, usar notación científica
        if (valor >= UMBRAL_CIENTIFICO || valor <= -UMBRAL_CIENTIFICO) {
            // %.3e formatea con 3 decimales (ej: 9.346e+11)
            // Usamos Locale.US para asegurar que el decimal sea un punto
            String formateado = String.format(Locale.US, "%.3e", valor);
            // Reemplazamos el "e+" por "e" para que quede igual que Balatro (ej: 9.346e11)
            return formateado.replace("e+", "e");
        }

        // 3. Si es un número normal, lo mostramos entero sin decimales
        return String.format(Locale.US, "%.0f", valor);
    }
}
