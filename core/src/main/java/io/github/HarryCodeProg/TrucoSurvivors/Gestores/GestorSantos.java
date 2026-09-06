package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.SantaRita;
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
    private final GestorVentaSanto gestorVenta = new GestorVentaSanto(); // FIX
    private Function<Carta, VistaCarta> buscadorVistaCarta;
    private Jugador jugadorActual;

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

    /** FIX: overload que además actualiza la venta — llamar este desde GameScreenV2 en vez del de arriba,
     * pasando el jugador. Si preferís no tocar las llamadas existentes, ver alternativa abajo. */
    public void update(float mouseX, float mouseY, float delta, Jugador jugador) {
        update(mouseX, mouseY, delta);
        if (!hayOverlayActivo()) { // no vender mientras hay un overlay de santo abierto encima
            gestorVenta.update(mouseX, mouseY, santos, jugador, r -> area.distribuir(santos, gestorInput.getArrastrado()));
        }
    }

    public void render(SpriteBatch batch, Main game) {
        for (VistaSanto v : santos) v.render(batch);
        overlaySeleccion.render(batch, game);
        overlayConsumo.render(batch, game);
        gestorVenta.render(batch); // FIX
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


    public void comprarYUsar(Santo santo, Jugador jugador) {
        if (santo == null) return;
        TextureRegion region = game.getAtlasSantos().findRegion(santo.getNombreRegion());
        if (region == null) return;
        Santo santoParaRequisitos = resolverSantoEfectivo(santo, jugador);
        int requeridas = (santoParaRequisitos != null) ? santoParaRequisitos.cartasRequeridas() : 0;
        if (requeridas == 0) {
            overlayConsumo.abrir(santo, region, () -> {
                santo.aplicarEfecto(jugador, new ArrayList<>(), null);
                registrarUsoParaTracking(santo, jugador);
                aplicarCambiosDiferidosConFlipYEsperar(santo, overlayConsumo::confirmarCierre);
            });
            return;
        }
        ArrayList<Carta> cartas = jugador.getMazo().getCartasAleatoriasParaSanto(10);
        overlaySeleccion.abrir(santo, cartas, game.getAtlasCartas(), seleccion ->
            overlayConsumo.abrir(santo, region, () -> {
                santo.aplicarEfecto(jugador, seleccion, null);
                registrarUsoParaTracking(santo, jugador);
                aplicarCambiosDiferidosConFlipYEsperar(santo, () -> {
                    overlayConsumo.confirmarCierre();
                    overlaySeleccion.cerrarConVuelta();
                });
            })
        );
    }

    public void usarSeleccionado(VistaSanto vista, Jugador jugador) {
        if (vista == null || jugador == null) return;
        Santo santo = vista.getSanto();
        TextureRegion region = game.getAtlasSantos().findRegion(santo.getNombreRegion());
        if (region == null) return;
        Santo santoParaRequisitos = resolverSantoEfectivo(santo, jugador);
        int requeridas = (santoParaRequisitos != null) ? santoParaRequisitos.cartasRequeridas() : 0;
        if (requeridas == 0) {
            overlayConsumo.abrir(santo, region, () -> {
                santo.aplicarEfecto(jugador, new ArrayList<>(), null);
                registrarUsoParaTracking(santo, jugador);
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
                    registrarUsoParaTracking(santo, jugador);
                    aplicarCambiosDiferidosConFlipYEsperar(santo, () -> {
                        jugador.eliminarSanto(santo);
                        overlayConsumo.confirmarCierre();
                        overlaySeleccion.cerrarConVuelta();
                    });
                })
        );
    }

    private Santo resolverSantoEfectivo(Santo santo, Jugador jugador) {
        if (santo instanceof SantaRita) {
            return jugador.getUltimoSantoUsado();
        }
        return santo;
    }

    private void registrarUsoParaTracking(Santo santo, Jugador jugador) {
        if (santo instanceof SantaRita) return;
        jugador.setUltimoSantoUsado(santo);
    }
}
