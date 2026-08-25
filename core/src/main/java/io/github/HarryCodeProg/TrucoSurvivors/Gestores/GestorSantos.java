package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.OverlayConsumoSanto;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.OverlaySeleccionCartaSanto;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaSanto;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class GestorSantos {
    private final Main game;
    private final ArrayList<VistaSanto> santos = new ArrayList<>();
    private final GestorInputArrastrable<VistaSanto> gestorInput;
    private final AreaElementos<VistaSanto> area;
    private final OverlayConsumoSanto overlayConsumo = new OverlayConsumoSanto();
    private final OverlaySeleccionCartaSanto overlaySeleccion = new OverlaySeleccionCartaSanto();
    private Function<Carta, VistaCarta> buscadorVistaCarta;

    public GestorSantos(Main game, float areaX, float areaY, float areaAncho, float altoSanto) {
        this.game = game;
        this.gestorInput = new GestorInputArrastrable<>(santos);
        this.area = new AreaElementos<>(areaX, areaY, areaAncho, altoSanto, 70f, 95f, 5f);
    }

    public void setBuscadorVistaCarta(Function<Carta, VistaCarta> buscador) {
        this.buscadorVistaCarta = buscador;
    }

    public boolean agregarComprado(Santo santo, Jugador jugador) {
        return agregarVistaDesdeModelo(santo, jugador);
    }

    public void comprarYUsar(Santo santo, Jugador jugador) {
        if (santo == null) return;
        TextureRegion region = game.getAtlasSantos().findRegion(santo.getNombreRegion());
        if (region == null) return;
        if (santo.cartasRequeridas() == 0) {
            overlayConsumo.abrir(santo, region, () -> {
                santo.aplicarEfecto(jugador, new ArrayList<>(), null);
                aplicarCambiosDiferidosConFlipYEsperar(santo, overlayConsumo::confirmarCierre);
            });
            return;
        }
        ArrayList<Carta> cartas = jugador.getMazo().getCartasAleatoriasParaSanto(10);
        overlaySeleccion.abrir(santo, cartas, game.getAtlasCartas(), seleccion ->
            overlayConsumo.abrir(santo, region, () -> {
                santo.aplicarEfecto(jugador, seleccion, null);
                aplicarCambiosDiferidosConFlipYEsperar(santo, overlayConsumo::confirmarCierre);
            })
        );
    }

    public void usarSeleccionado(VistaSanto vista, Jugador jugador) {
        if (vista == null || jugador == null) return;
        Santo santo = vista.getSanto();
        TextureRegion region = game.getAtlasSantos().findRegion(santo.getNombreRegion());
        if (region == null) return;
        if (santo.cartasRequeridas() == 0) {
            overlayConsumo.abrir(santo, region, () -> {
                santo.aplicarEfecto(jugador, new ArrayList<>(), null);
                aplicarCambiosDiferidosConFlipYEsperar(santo, () -> {
                    jugador.eliminarSanto(santo);
                    overlayConsumo.confirmarCierre();
                });
            });
            return;
        }
        ArrayList<Carta> cartasParaElegir = new ArrayList<>(jugador.getMano());
        overlaySeleccion.abrir(santo, cartasParaElegir, game.getAtlasCartas(),
            seleccion ->
                overlayConsumo.abrir(santo, region, () -> {
                    santo.aplicarEfecto(jugador, seleccion, null);
                    aplicarCambiosDiferidosConFlipYEsperar(santo, () -> {
                        jugador.eliminarSanto(santo);
                        overlayConsumo.confirmarCierre();
                    });
                })
        );
    }

    private void aplicarCambiosDiferidosConFlipYEsperar(Santo santo, Runnable alTerminarTodo) {
        if (!santo.tieneCambiosDiferidos()) {
            if (alTerminarTodo != null) alTerminarTodo.run();
            return;
        }
        ArrayList<Carta> cartas = santo.getCartasDiferidas();
        ArrayList<Runnable> acciones = santo.getAccionesDiferidas();
        TextureRegion dorso = game.getAtlasCartas().findRegion("back");
        for (int i = 0; i < cartas.size(); i++) {
            Carta carta = cartas.get(i);
            Runnable accion = acciones.get(i);
            VistaCarta vista = overlaySeleccion.buscarVistaPorCarta(carta);
            if (vista == null && buscadorVistaCarta != null) {
                vista = buscadorVistaCarta.apply(carta);
            }
            if (vista != null && dorso != null) {
                final VistaCarta vistaFinal = vista;
                vistaFinal.iniciarFlip(dorso, () -> {
                    accion.run();
                    vistaFinal.actualizarRegionDesdeCarta(game.getAtlasCartas());
                    // ya no hace falta avisar nada acá — el overlay chequea isFlipeando() solo
                });
            } else {
                accion.run(); // sin vista: aplicar directo
            }
        }
        santo.limpiarDiferidos();
        // el overlay espera hasta que TODAS las cartas dejen de estar flipeando (chequeo real, no contado)
        overlaySeleccion.esperarFlipsYLuegoVolver(alTerminarTodo);
    }

    public void update(float mouseX, float mouseY, float delta) {
        gestorInput.update(mouseX, mouseY, delta, true);
        overlayConsumo.update(delta);
        overlaySeleccion.update(mouseX, mouseY, delta);
    }

    public void render(SpriteBatch batch, Main game) {
        for (VistaSanto v : santos) v.render(batch);
        overlaySeleccion.render(batch, game);
        overlayConsumo.render(batch, game);
    }

    public ArrayList<VistaSanto> getSantos() { return santos; }
    public AreaElementos<VistaSanto> getArea() { return area; }
    public int getMaximo(Jugador jugador) { return jugador.getTamañoSantos(); }

    public boolean agregarVistaDesdeModelo(Santo santo, Jugador jugador) {
        if (santo == null) return false;
        if (santos.size() >= jugador.getTamañoSantos()) return false;
        VistaSanto vista = new VistaSanto(santo, game.getAtlasSantos());
        vista.setTamaño(70, 95);
        santos.add(vista);
        area.distribuir(santos, null);
        return true;
    }

    public void eliminarVistaDesdeModelo(Santo santo) {
        if (santo == null) return;
        VistaSanto vistaAEliminar = null;
        for (VistaSanto vista : santos) {
            if (vista.getSanto() == santo) { vistaAEliminar = vista; break; }
        }
        if (vistaAEliminar != null) {
            santos.remove(vistaAEliminar);
            vistaAEliminar.dispose();
            area.distribuir(santos, null);
        }
    }

    public boolean hayOverlayActivo() {
        return overlayConsumo.estaActivo() || overlaySeleccion.estaVisible();
    }
}
