package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.SantaRita;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

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
    private Runnable alConsumirSantoCallback;
    private final GestorReordenamiento gestorReordenamiento = new GestorReordenamiento();
    private final ModoSeleccionSanto modoSeleccion = new ModoSeleccionSanto();
    private java.util.function.Supplier<ArrayList<VistaCarta>> supplierCartasJugador;

    public GestorSantos(Main game, float areaX, float areaY, float areaAncho, float altoSanto) {
        this.game = game;
        this.gestorInput = new GestorInputArrastrable<>(santos);
        this.area = new AreaElementos<>(areaX, areaY, areaAncho, altoSanto, 70f, 95f, 5f);
    }

    public void setBuscadorVistaCarta(Function<Carta, VistaCarta> buscador) {
        this.buscadorVistaCarta = buscador;
    }

    private void aplicarCambiosDiferidosConFlipYEsperar(Santo santo, Runnable alTerminarTodo) {
        if (!santo.tieneCambiosDiferidos()) {
            terminarSeleccionCuandoCorresponda(alTerminarTodo);
            return;
        }
        ArrayList<Carta> cartas = santo.getCartasDiferidas();
        ArrayList<Runnable> acciones = santo.getAccionesDiferidas();
        TextureRegion dorso = game.getAtlasCartas().findRegion("back");
        Map<VistaCarta, ArrayList<Runnable>> cambiosPorVista = new LinkedHashMap<>();
        for (int i = 0; i < cartas.size(); i++) {
            Carta carta = cartas.get(i);
            Runnable accion = acciones.get(i);
            VistaCarta vista = null;
            if (buscadorVistaCarta != null) {
                vista = buscadorVistaCarta.apply(carta);
            }
            if (vista == null && buscadorVistaCarta != null) {
                vista = buscadorVistaCarta.apply(carta);
            }
            if (vista != null && dorso != null) {
                cambiosPorVista.computeIfAbsent(vista, ignorada -> new ArrayList<>()).add(accion);
            } else {
                accion.run();
            }
        }
        santo.limpiarDiferidos();
        if (cambiosPorVista.isEmpty()) {
            terminarSeleccionCuandoCorresponda(alTerminarTodo);
            return;
        }
        // Todos arrancan a la vez, esperamos a que terminen todos
        final int[] pendientes = { cambiosPorVista.size() };
        for (Map.Entry<VistaCarta, ArrayList<Runnable>> entrada : cambiosPorVista.entrySet()) {
            VistaCarta vista = entrada.getKey();
            ArrayList<Runnable> cambios = entrada.getValue();
            vista.iniciarFlip(dorso, () -> {
                for (Runnable cambio : cambios) cambio.run();
                vista.actualizarRegionDesdeCarta(game.getAtlasCartas());
            }, () -> {
                pendientes[0]--;
                if (pendientes[0] == 0) {
                    if (overlaySeleccion.estaVisible()) {
                        overlaySeleccion.esperarFlipsYLuegoVolver(alTerminarTodo);
                    } else if (alTerminarTodo != null) {
                        alTerminarTodo.run();
                    }
                }
            });
        }
        if (alTerminarTodo != null) alTerminarTodo.run();
    }

    private void terminarSeleccionCuandoCorresponda(Runnable alTerminarTodo) {
        if (overlaySeleccion.estaVisible()) {
            overlaySeleccion.esperarFlipsYLuegoVolver(alTerminarTodo);
        } else if (alTerminarTodo != null) {
            alTerminarTodo.run();
        }
    }

    public void update(float mouseX, float mouseY, float delta, Jugador jugador) {
        this.jugadorActual = jugador; // Guardamos la referencia para sincronizar el orden
        update(mouseX, mouseY, delta);
        if (!hayOverlayActivo()) {
            gestorVenta.update(mouseX, mouseY, santos, jugador, r -> area.distribuir(santos, gestorInput.getArrastrado()));
        }
        modoSeleccion.update(mouseX, mouseY, delta, jugador != null ? supplyCartasJugador() : new ArrayList<>());
    }

    public void update(float mouseX, float mouseY, float delta) {
        // Guardamos quién estaba siendo arrastrado antes del update
        VistaSanto arrastradoAntes = gestorInput.getArrastrado();
        gestorInput.update(mouseX, mouseY, delta, true);
        // Vemos quién está siendo arrastrado después
        VistaSanto arrastradoDespues = gestorInput.getArrastrado();
        // 1. Evaluar si cruzamos el centro de otro Santo para intercambiar lugares
        boolean reordenado = gestorReordenamiento.previsualizarReordenamiento(gestorInput, santos);
        // 2. Si se reordenó la lista O si soltamos el santo que veníamos arrastrando
        if (reordenado || (arrastradoAntes != null && arrastradoDespues == null)) {
            // Sincronizar el modelo real del jugador con el nuevo orden visual
            if (reordenado && jugadorActual != null) {
                java.util.ArrayList<Santo> modelo = jugadorActual.getSantos();
                modelo.clear();
                for (VistaSanto v : santos) {
                    modelo.add(v.getSanto());
                }
            }
            // 3. Acá ocurre la magia: si soltaste el santo (arrastradoDespues es null),
            // el AreaElementos le asignará de nuevo su 'targetX/Y' y el santo volverá a su lugar.
            area.distribuir(santos, arrastradoDespues);
        }
        resolverHoverExclusivoSantos();
        overlayConsumo.update(delta);
        overlaySeleccion.update(mouseX, mouseY, delta);
    }

    private void resolverHoverExclusivoSantos() {
        VistaSanto ganador = null;
        for (VistaSanto v : santos) {
            if (v.isHover()) ganador = v;
        }
        for (VistaSanto v : santos) {
            if (v != ganador) v.limpiarHover();
        }
    }

    public void render(SpriteBatch batch, Main game) {
        for (VistaSanto v : santos) v.render(batch);
        overlaySeleccion.render(batch, game);
        overlayConsumo.render(batch, game);
        modoSeleccion.render(batch, game);
        gestorVenta.render(batch);
    }

    public boolean hayOverlayActivo() {
        return overlayConsumo.estaActivo() || overlaySeleccion.estaVisible() || modoSeleccion.estaActivo();
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

    public void comprarYUsar(Santo santo, Jugador jugador) {
        if (santo == null) return;
        TextureRegion region = game.getAtlasSantos().findRegion(santo.getNombreRegion());
        if (region == null) return;
        // Posición "de aparición" para la quema cuando se compra desde la tienda
        float anchoSanto = 110f;
        float altoSanto  = 150f;
        float xSanto = 1280f / 2f - anchoSanto / 2f;
        float ySanto = 720f / 2f - altoSanto / 2f;
        Santo santoParaRequisitos = resolverSantoEfectivo(santo, jugador);
        int requeridas = (santoParaRequisitos != null) ? santoParaRequisitos.cartasRequeridas() : 0;
        if (requeridas == 0) {
            overlayConsumo.abrir(santo, region, xSanto, ySanto, anchoSanto, altoSanto, () -> {
                santo.aplicarEfecto(jugador, new ArrayList<>(), null);
                registrarUsoParaTracking(santo, jugador);
                aplicarCambiosDiferidosConFlipYEsperar(santo, () -> {
                    overlayConsumo.confirmarCierre();
                    if (alConsumirSantoCallback != null) alConsumirSantoCallback.run();
                });
            });
            return;
        }
        // Requiere selección: se elige en la tienda, se quema desde el centro
        ArrayList<Carta> cartas = jugador.getMazo().getCartasAleatoriasParaSanto(10);
        modoSeleccion.abrir(santo,
            seleccion -> {
                overlayConsumo.abrir(santo, region, xSanto, ySanto, anchoSanto, altoSanto, () -> {
                    santo.aplicarEfecto(jugador, seleccion, null);
                    registrarUsoParaTracking(santo, jugador);
                    aplicarCambiosDiferidosConFlipYEsperar(santo, () -> {
                        overlayConsumo.confirmarCierre();
                        if (alConsumirSantoCallback != null) alConsumirSantoCallback.run();
                    });
                });
            },
            () -> { /* cancelado */ }
        );
    }

    public void usarSeleccionado(VistaSanto vista, Jugador jugador, ArrayList<Carta> seleccion) {
        if (vista == null || jugador == null) return;
        Santo santo = vista.getSanto();
        TextureRegion region = game.getAtlasSantos().findRegion(santo.getNombreRegion());
        if (region == null) return;
        if (!seleccionValidaParaUso(santo, seleccion)) return;
        final ArrayList<Carta> seleccionFinal = new ArrayList<>(seleccion);
        // Ocultamos el VistaSanto real mientras la disolución lo reemplaza visualmente
        vista.setOculto(true);
        overlayConsumo.abrir(santo, region,
            vista.getX(), vista.getY(), vista.getWidth(), vista.getHeight(),
            () -> {
                santo.aplicarEfecto(jugador, seleccionFinal, null);
                registrarUsoParaTracking(santo, jugador);
                aplicarCambiosDiferidosConFlipYEsperar(santo, () -> {
                    jugador.eliminarSanto(santo);
                    overlayConsumo.confirmarCierre();
                    if (alConsumirSantoCallback != null) alConsumirSantoCallback.run();
                });
            });
    }

    private boolean seleccionValidaParaUso(Santo santo, ArrayList<Carta> seleccion) {
        int req = santo.cartasRequeridas();
        if (req == 0) return seleccion.isEmpty();
        if (req == -1) {
            int max = santo.maxCartasSeleccionables();
            return seleccion.size() >= 1 && (max <= 0 || seleccion.size() <= max);
        }
        return seleccion.size() == req;
    }

    public void setSupplierCartasJugador(java.util.function.Supplier<ArrayList<VistaCarta>> s) {
        this.supplierCartasJugador = s;
    }

    private ArrayList<VistaCarta> supplyCartasJugador() {
        return supplierCartasJugador != null ? supplierCartasJugador.get() : new ArrayList<>();
    }

    public ModoSeleccionSanto getModoSeleccion() { return modoSeleccion; }

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

    public void setAlConsumirSanto(Runnable callback) {
        this.alConsumirSantoCallback = callback;
    }

    public boolean isMouseOverButton() {
        return gestorVenta.isMouseOverButton();
    }
}
