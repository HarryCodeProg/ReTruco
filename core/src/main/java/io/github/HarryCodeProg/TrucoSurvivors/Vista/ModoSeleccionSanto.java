package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Modo de selección de cartas para consumir un santo.
 * Reutiliza las VistaCarta reales de la mano, no crea cartas nuevas
 * ni oscurece la pantalla. Solo agrega confirmar/cancelar.
 */
public class ModoSeleccionSanto {

    private boolean activo = false;
    private Santo santo;
    private Consumer<ArrayList<Carta>> alConfirmar;
    private Runnable alCancelar;
    private final ArrayList<Carta> seleccionadas = new ArrayList<>();
    private final Boton botonConfirmar;
    private final Boton botonCancelar;

    private static final float ANCHO_BOTON = 180f;
    private static final float ALTO_BOTON = 50f;
    private static final float Y_BOTONES = 40f;
    private static final float GAP_BOTONES = 20f;

    public ModoSeleccionSanto() {
        float cx = 1280f / 2f;
        botonCancelar = new Boton(cx - ANCHO_BOTON - GAP_BOTONES / 2f, Y_BOTONES,
            ANCHO_BOTON, ALTO_BOTON, Boton.TipoColor.BORDO,
            io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion.OPCIONES);
        botonConfirmar = new Boton(cx + GAP_BOTONES / 2f, Y_BOTONES,
            ANCHO_BOTON, ALTO_BOTON, Boton.TipoColor.VERDE,
            io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion.CONFIRMAR_SELECCION_SANTO);
        botonConfirmar.setHabilitado(false);
    }

    public void abrir(Santo santo, Consumer<ArrayList<Carta>> alConfirmar, Runnable alCancelar) {
        this.santo = santo;
        this.alConfirmar = alConfirmar;
        this.alCancelar = alCancelar;
        this.seleccionadas.clear();
        this.activo = true;
        actualizarEstadoBoton();
    }

    public boolean estaActivo() { return activo; }
    public Santo getSanto() { return santo; }

    public void update(float mx, float my, float delta, ArrayList<VistaCarta> cartasJugador) {
        if (!activo) return;

        // Sincronizar la lista de seleccionadas con el estado real de las VistaCarta
        seleccionadas.clear();
        for (VistaCarta v : cartasJugador) {
            if (v.isSeleccionada()) seleccionadas.add(v.getCarta());
        }

        // Si se pasó del máximo, deseleccionar las últimas
        int maximo = obtenerMaximoSeleccionables();
        if (maximo > 0 && seleccionadas.size() > maximo) {
            int exceso = seleccionadas.size() - maximo;
            for (int i = cartasJugador.size() - 1; i >= 0 && exceso > 0; i--) {
                VistaCarta v = cartasJugador.get(i);
                if (v.isSeleccionada()) {
                    v.setSeleccionada(false);
                    seleccionadas.remove(v.getCarta());
                    exceso--;
                }
            }
        }

        botonConfirmar.update(mx, my);
        botonCancelar.update(mx, my);
        actualizarEstadoBoton();

        if (botonConfirmar.fueCliqueado(mx, my) && seleccionValida()) {
            confirmar();
        }
        if (botonCancelar.fueCliqueado(mx, my)) {
            cancelar();
        }
    }

    private void actualizarEstadoBoton() {
        botonConfirmar.setHabilitado(seleccionValida());
    }

    private boolean seleccionValida() {
        int requeridas = santo.cartasRequeridas();
        if (requeridas == 0) return true;
        if (requeridas == -1) return !seleccionadas.isEmpty();
        return seleccionadas.size() == requeridas;
    }

    private int obtenerMaximoSeleccionables() {
        if (santo.cartasRequeridas() == -1) return santo.maxCartasSeleccionables();
        return santo.cartasRequeridas();
    }

    private void confirmar() {
        Consumer<ArrayList<Carta>> cb = alConfirmar;
        ArrayList<Carta> resultado = new ArrayList<>(seleccionadas);
        // limpiar selección visual de las cartas
        cerrarInterno();
        if (cb != null) cb.accept(resultado);
    }

    private void cancelar() {
        Runnable cb = alCancelar;
        cerrarInterno();
        if (cb != null) cb.run();
    }

    /** Cancela sin disparar el callback de cancelación (para cierres forzados). */
    public void cerrarSilencioso() {
        cerrarInterno();
    }

    private void cerrarInterno() {
        activo = false;
        alConfirmar = null;
        alCancelar = null;
        seleccionadas.clear();
    }

    public void render(SpriteBatch batch, Main game) {
        if (!activo) return;

        String nombre = santo.getNombre() != null ? santo.getNombre() : "";
        String instruccion = santo.getDescripcion() != null
            ? santo.getDescripcion()
            : "Seleccioná cartas para consumir";
        String contador = seleccionadas.size() + " / " + obtenerMaximoSeleccionables();

        BitmapFont font = game.getFuentePrincipal();
        font.setColor(Color.WHITE);
        font.draw(batch, nombre, 0, 700f, 1280f, Align.center, false);
        font.draw(batch, instruccion, 0, 670f, 1280f, Align.center, false);
        font.draw(batch, contador, 0, 640f, 1280f, Align.center, false);
        font.setColor(Color.WHITE);

        botonCancelar.render(batch);
        botonConfirmar.render(batch);
    }
}
