package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaSanto;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class GestorUsoSanto {
    private final Boton botonUsar;

    public GestorUsoSanto() {
        this.botonUsar = new Boton(0, 0, 100, GameLayout.ALTO_BOTON, Boton.TipoColor.VERDE, Accion.USAR_SANTO);
        this.botonUsar.setVisible(false);
    }

    /** alUsar recibe (VistaSanto seleccionado, Jugador) — quien llama decide como abrir el flujo de consumo. */
    public void update(float mouseX, float mouseY, ArrayList<VistaSanto> santos, Jugador jugador, BiConsumer<VistaSanto, Jugador> alUsar) {
        VistaSanto seleccionado = null;
        for (VistaSanto v : santos) {
            if (v.isSeleccionada()) { seleccionado = v; break; }
        }
        if (seleccionado == null) {
            botonUsar.setVisible(false);
            botonUsar.update(-1000, -1000);
            return;
        }
        Santo santo = seleccionado.getSanto();
        boolean puedeUsarse = puedeUsarseAhora(santo, jugador);

        botonUsar.setTexto("USAR");
        float xBoton = seleccionado.getX() + (seleccionado.getWidth() - botonUsar.getAncho()) / 2f;
        float yBoton = seleccionado.getY() + seleccionado.getHeight() + 10f;
        botonUsar.setPosition(xBoton, yBoton);
        botonUsar.setHabilitado(puedeUsarse);
        botonUsar.setVisible(true);
        botonUsar.update(mouseX, mouseY);

        if (puedeUsarse && botonUsar.fueCliqueado(mouseX, mouseY)) {
            alUsar.accept(seleccionado, jugador);
        }
    }

    /** Ajustar segun reglas reales: por ahora, siempre usable si el santo no requiere cartas o si hay mano con cartas suficientes. */
    private boolean puedeUsarseAhora(Santo santo, Jugador jugador) {
        if (santo.cartasRequeridas() == 0) return true;
        return jugador.getMano() != null && !jugador.getMano().isEmpty();
    }

    public void render(SpriteBatch batch) {
        if (botonUsar.isVisible()) botonUsar.render(batch);
    }
}
