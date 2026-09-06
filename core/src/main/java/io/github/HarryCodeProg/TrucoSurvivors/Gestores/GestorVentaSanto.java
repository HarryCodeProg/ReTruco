package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaSanto;

import java.util.ArrayList;
import java.util.function.Consumer;

public class GestorVentaSanto {
    private final Boton botonVender;

    public GestorVentaSanto() {
        this.botonVender = new Boton(0, 0, 100, GameLayout.ALTO_BOTON, Boton.TipoColor.BORDO, Accion.VENDER_SANTO);
        this.botonVender.setVisible(false);
    }

    public void update(float mouseX, float mouseY, ArrayList<VistaSanto> santos, Jugador jugador, Consumer<Runnable> alOrganizar) {
        VistaSanto santoSeleccionado = null;
        for (VistaSanto v : santos) {
            if (v.isSeleccionada()) {
                santoSeleccionado = v;
                break;
            }
        }
        if (santoSeleccionado != null) {
            int precioVenta = Math.max(1, santoSeleccionado.getSanto().getCoste() / 2);
            botonVender.setTexto("VENDER $" + precioVenta);
            float xBoton = santoSeleccionado.getX() + santoSeleccionado.getWidth() + 10f; // a la derecha, no arriba
            float yBoton = santoSeleccionado.getY() + (santoSeleccionado.getHeight() - botonVender.getAlto()) / 2f;
            botonVender.setPosition(xBoton, yBoton);
            botonVender.setVisible(true);
            botonVender.update(mouseX, mouseY);
            if (botonVender.fueCliqueado(mouseX, mouseY)) {
                venderSanto(santoSeleccionado, santos, jugador, alOrganizar);
            }
        } else {
            botonVender.setVisible(false);
            botonVender.update(-1000, -1000);
        }
    }

    private void venderSanto(VistaSanto santoAVender, ArrayList<VistaSanto> santos, Jugador jugador, Consumer<Runnable> alOrganizar) {
        Santo santo = santoAVender.getSanto();
        int precioVenta = Math.max(1, santo.getCoste() / 2);
        jugador.sumarPesos(precioVenta);
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        if (sonidos != null) sonidos.reproducirSonidoGanarPeso();
        jugador.eliminarSanto(santo); // dispara el listener que ya remueve la VistaSanto en GestorSantos
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
}
