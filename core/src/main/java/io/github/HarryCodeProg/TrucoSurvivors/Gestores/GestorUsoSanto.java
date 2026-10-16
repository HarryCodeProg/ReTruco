package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaSanto;

import java.util.ArrayList;
import java.util.function.Supplier;

public class GestorUsoSanto {

    @FunctionalInterface
    public interface AlUsarSanto {
        void usar(VistaSanto vista, Jugador jugador, ArrayList<Carta> seleccion);
    }

    // Mismos valores que GestorVentaSanto para que los botones queden alineados
    private static final float MARGEN_DERECHO = 10f;
    private static final float GAP_ENTRE_BOTONES = 4f;

    private final Boton botonUsar;
    private Supplier<ArrayList<VistaCarta>> supplierCartasJugador;

    public GestorUsoSanto() {
        this.botonUsar = new Boton(0, 0, 100, GameLayout.ALTO_BOTON, Boton.TipoColor.VERDE, Accion.USAR_SANTO);
        this.botonUsar.setVisible(false);
    }

    public void setSupplierCartasJugador(Supplier<ArrayList<VistaCarta>> s) {
        this.supplierCartasJugador = s;
    }

    public void update(float mouseX, float mouseY, ArrayList<VistaSanto> santos,
                       Jugador jugador, AlUsarSanto alUsar) {
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
        ArrayList<Carta> seleccion = obtenerCartasSeleccionadas();
        boolean puedeUsarse = seleccionValida(santo, seleccion);

        botonUsar.setTexto("USAR");

        // Misma fórmula de posición que GestorVentaSanto, pero un poco más arriba
        float xBoton = seleccionado.getX() + seleccionado.getWidth() + MARGEN_DERECHO;
        float yVender = seleccionado.getY() + (seleccionado.getHeight() - GameLayout.ALTO_BOTON) / 2f;
        float yBoton  = yVender + GameLayout.ALTO_BOTON + GAP_ENTRE_BOTONES;

        botonUsar.setPosition(xBoton, yBoton);
        botonUsar.setHabilitado(puedeUsarse);
        botonUsar.setVisible(true);
        botonUsar.update(mouseX, mouseY);

        if (puedeUsarse && botonUsar.fueCliqueado(mouseX, mouseY)) {
            alUsar.usar(seleccionado, jugador, seleccion);
        }
    }

    private ArrayList<Carta> obtenerCartasSeleccionadas() {
        ArrayList<Carta> resultado = new ArrayList<>();
        if (supplierCartasJugador == null) return resultado;
        ArrayList<VistaCarta> cartas = supplierCartasJugador.get();
        if (cartas == null) return resultado;
        for (VistaCarta v : cartas) {
            if (v.isSeleccionada()) resultado.add(v.getCarta());
        }
        return resultado;
    }

    private boolean seleccionValida(Santo santo, ArrayList<Carta> seleccion) {
        int req = santo.cartasRequeridas();
        if (req == 0) return seleccion.isEmpty();
        if (req == -1) {
            int max = santo.maxCartasSeleccionables();
            return seleccion.size() >= 1 && (max <= 0 || seleccion.size() <= max);
        }
        return seleccion.size() == req;
    }

    public void render(SpriteBatch batch) {
        if (botonUsar.isVisible()) botonUsar.render(batch);
    }
}
