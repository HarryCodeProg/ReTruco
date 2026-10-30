package io.github.HarryCodeProg.TrucoSurvivors.Dev;

import com.badlogic.gdx.Gdx;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.GameScreen;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Reportes de error para los testers: guardan en la carpeta "logs" el estado de la partida
 * (jokers, mazo, manos, puntos) como un script que se pega en el entorno de desarrollo.
 */
public final class ReporteDev {
    private ReporteDev() {}

    private static String sello() { return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()); }

    private static String escenarioActual(Main game) {
        try {
            if (game.getScreen() instanceof GameScreen) return ((GameScreen) game.getScreen()).reporteEscenario();
            return "(el juego no estaba en una partida: " + game.getScreen() + ")";
        } catch (Throwable t) {
            return "(no se pudo exportar el estado: " + t + ")";
        }
    }

    private static String encabezado(String motivo) {
        return "ReTruco - reporte (" + motivo + ")\n"
            + "fecha: " + new Date() + "\n"
            + "sistema: " + System.getProperty("os.name") + " / java " + System.getProperty("java.version") + "\n\n";
    }

    /** Lo que se dispara con F8 en la partida. Devuelve la ruta del archivo (o null si no pudo escribirlo). */
    public static String guardarReporte(Main game, String motivo) {
        try {
            String texto = encabezado(motivo) + escenarioActual(game);
            String ruta = "logs/reporte-" + sello() + ".txt";
            Gdx.files.local(ruta).writeString(texto, false);
            return ruta;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    /** Se llama desde Main.render cuando algo revienta: guarda el stack trace y el estado de la partida. */
    public static void registrarCrash(Main game, Throwable error) {
        try {
            StringWriter sw = new StringWriter();
            error.printStackTrace(new PrintWriter(sw));
            String texto = encabezado("CRASH") + sw + "\n--- ESTADO DE LA PARTIDA ---\n" + escenarioActual(game);
            Gdx.files.local("logs/crash-" + sello() + ".txt").writeString(texto, false);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
