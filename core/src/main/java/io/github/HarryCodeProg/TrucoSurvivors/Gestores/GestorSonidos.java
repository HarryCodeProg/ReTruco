package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;

public class GestorSonidos implements Disposable {
    private final HashMap<String, Sound> sonidos;
    private float volumenGlobal = 0.2f;
    private boolean silenciado = false;
    private boolean alternarReparto = false;

    public GestorSonidos() {
        sonidos = new HashMap<>();
        cargarSonidos();
    }

    private void cargarSonidos() {
        // agregar todos los archivos .ogg
        cargar("seleccionar", "sonidos/seleccionar.ogg");
        cargar("deseleccionar", "sonidos/deseleccionar.ogg");
        cargar("reparto1", "sonidos/reparto1.ogg");
        cargar("reparto2", "sonidos/reparto2.ogg");
        cargar("activar_carta", "sonidos/activar_carta.ogg");   // NUEVO
        cargar("activar_joker", "sonidos/activar_joker.ogg");
        cargar("ganar-peso1", "sonidos/gano-peso-1.ogg");
        cargar("ganar-peso2", "sonidos/gano-peso-2.ogg");
        cargar("ganar-peso3", "sonidos/gano-peso-3.ogg");
        cargar("gastar-peso1", "sonidos/peso-gastado-1.ogg");
        cargar("gastar-peso2", "sonidos/peso-gastado-2.ogg");
        cargar("gano-mas-1", "sonidos/gano-mas-1.ogg");
        cargar("gano-mas-20", "sonidos/gano-mas-20.ogg");
        cargar("gano-mas-50", "sonidos/gano-mas-50.ogg");
    }

    private void cargar(String clave, String ruta) {
        try {
            Sound s = Gdx.audio.newSound(Gdx.files.internal(ruta));
            sonidos.put(clave, s);
        } catch (Exception e) {
            Gdx.app.error("GestorSonidos", "Error al cargar el sonido: " + ruta, e);
        }
    }

    /** Reproduce un sonido básico por su nombre clave */
    public void reproducir(String clave) {
        reproducir(clave, 1.0f, 1.0f);
    }

    /**
     * Reproduce un sonido con pitch aleatorio variable.
     * Le da un efecto orgánico/jugoso (estilo Balatro) para evitar que suene repetitivo.
     */
    public void reproducirConVariacion(String clave) {
        float pitchAleatorio = MathUtils.random(0.92f, 1.08f);
        reproducir(clave, 1.0f, pitchAleatorio);
    }

    public void reproducir(String clave, float volumenRelativo, float pitch) {
        if (silenciado) return;
        Sound s = sonidos.get(clave);
        if (s != null) {
            s.play(volumenGlobal * volumenRelativo, pitch, 0f);
        }
    }

    public void reproducirSonidoReparto() {
        String clave = alternarReparto ? "reparto1" : "reparto2";
        alternarReparto = !alternarReparto; // Alterna para la próxima carta
        // Reproducir con pitch ligeramente dinámico
        reproducirConVariacion(clave);
    }

    public void setVolumenGlobal(float volumen) {
        this.volumenGlobal = MathUtils.clamp(volumen, 0f, 1f);
    }

    public void toggleMute() {
        this.silenciado = !this.silenciado;
    }

    public void reproducirSonidoGanarPeso() {
        int idx = MathUtils.random(1, 3);
        reproducirConVariacion("ganar-peso" + idx); // ya trae pitch aleatorio, mismo patrón que reparto
    }

    public void reproducirSonidoFinalGanancia(int totalPesos) {
        if (totalPesos >= 50) reproducir("gano-mas-50");
        else if (totalPesos >= 20) reproducir("gano-mas-20");
        else if (totalPesos >= 1) reproducir("gano-mas-1");
    }

    public void reproducirSonidoGastarPeso() {
        int idx = MathUtils.random(1, 2);
        reproducirConVariacion("gastar-peso" + idx);
    }

    @Override
    public void dispose() {
        for (Sound s : sonidos.values()) {
            if (s != null) s.dispose();
        }
        sonidos.clear();
    }
}
