package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaJoker;

import java.util.ArrayList;
import java.util.function.Consumer;

public class GestorVentaJoker {
    private final Boton botonVender;

    public GestorVentaJoker() {
        // Aprovecha directamente tu nuevo enum TipoColor.BORDO o ROJO
        this.botonVender = new Boton(0, 0, 100, GameLayout.ALTO_BOTON, Boton.TipoColor.BORDO, Accion.VENDER_JOKER);
        this.botonVender.setVisible(false);
    }

    public void update(float mouseX, float mouseY, ArrayList<VistaJoker> jokers, Jugador jugador, Consumer<Runnable> alOrganizar) {
        VistaJoker jokerSeleccionado = null;
        for (VistaJoker j : jokers) {
            if (j.isSeleccionada()) {
                jokerSeleccionado = j;
                break;
            }
        }
        if (jokerSeleccionado != null) {
            int precioVenta = Math.max(1, jokerSeleccionado.getJoker().getCoste() / 2);
            botonVender.setTexto("VENDER $" + precioVenta);
            // Centrar sobre el Joker usando las dimensiones de VistaJoker y Boton
            float xBoton = jokerSeleccionado.getX() + (jokerSeleccionado.getWidth() - botonVender.getAncho()) / 2f;
            float yBoton = jokerSeleccionado.getY() + jokerSeleccionado.getHeight() + 10f;
            // Usa el nuevo setPosition que agregaste a Boton
            botonVender.setPosition(xBoton, yBoton);
            botonVender.setVisible(true);
            botonVender.update(mouseX, mouseY);
            // Verifica click utilizando la colisión directa del botón
            if (botonVender.fueCliqueado(mouseX, mouseY)) {
                venderJoker(jokerSeleccionado, jokers, jugador, alOrganizar);
            }
        } else {
            botonVender.setVisible(false);
            botonVender.update(-1000, -1000);
        }
    }

    private void venderJoker(VistaJoker jokerAVender, ArrayList<VistaJoker> jokers, Jugador jugador, Consumer<Runnable> alOrganizar) {
        int precioVenta = Math.max(1, jokerAVender.getJoker().getCoste() / 2);
        jugador.sumarPesos(precioVenta);
        jugador.eliminarJoker(jokerAVender.getJoker());
        jokers.remove(jokerAVender);
        botonVender.setVisible(false);
        if (alOrganizar != null) {
            alOrganizar.accept(null);
        }
    }

    public void render(SpriteBatch batch) {
        if (botonVender.isVisible()) {
            botonVender.render(batch);
        }
    }

    public Boton getBotonVender() {
        return botonVender;
    }
}
