package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaJoker;

import java.util.ArrayList;
import java.util.function.Consumer;

public class GestorVentaJoker {
    private final Boton botonVender;

    public GestorVentaJoker() {
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
            // FIX: posicionado a la derecha del joker seleccionado, en vez de arriba (se salía de pantalla)
            float xBoton = jokerSeleccionado.getX() + jokerSeleccionado.getWidth() + 10f;
            float yBoton = jokerSeleccionado.getY() + (jokerSeleccionado.getHeight() - botonVender.getAlto()) / 2f;
            botonVender.setPosition(xBoton, yBoton);
            botonVender.setVisible(true);
            botonVender.update(mouseX, mouseY);
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
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos(); // FIX
        if (sonidos != null) sonidos.reproducirSonidoGanarPeso();      // FIX
        Joker jokerVendido = jokerAVender.getJoker();
        for (Joker j : new ArrayList<>(jugador.getJokers())) {
            j.onVendido(jokerVendido, jugador);
        }
        jugador.eliminarJoker(jokerVendido);
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
