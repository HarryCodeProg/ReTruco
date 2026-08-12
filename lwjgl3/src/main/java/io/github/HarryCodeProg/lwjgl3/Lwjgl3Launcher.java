package io.github.HarryCodeProg.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import io.github.HarryCodeProg.TrucoSurvivors.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.stripWhitespaceX = false;
        settings.stripWhitespaceY = false;
        TexturePacker.process(settings, "assets/imagenesCartas", "assets/atlas", "cartas");
        TexturePacker.process(settings, "assets/jokers", "assets/atlas", "jokers");
        TexturePacker.process(settings, "assets/zodiaco_src", "assets/atlas", "zodiaco");
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Truco Survivors");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        // --- Modo ventana (comentar este bloque y descomentar el de pantalla
        // completa mas abajo para alternar entre los dos modos) ---
        //configuration.setWindowedMode(1280, 720);
        configuration.setMaximized(true);
        // --- Modo pantalla completa real (descomentar para usar, y comentar
        // el bloque de "Modo ventana" de arriba) ---
        configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
