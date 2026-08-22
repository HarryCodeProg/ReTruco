package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.*;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PanelTienda {
    private final Main game;
    private final Jugador jugador;
    private final EstadoTienda estadoTienda;
    private final Runnable alContinuar;
    private final ArrayList<VistaItemTienda> vistasCartas = new ArrayList<>();
    private final ArrayList<VistaItemTienda> vistasJokers = new ArrayList<>();
    private final ArrayList<VistaItemTienda> vistasSantos = new ArrayList<>();
    private VistaItemTienda seleccionado;
    private final Boton botonComprar;
    private final Boton botonReroll;
    private final Boton botonContinuar;
    private final Boton botonComprarYUsar;
    private Texture iconoPeso;
    private static final float PANEL_Y = 40f;
    private static final float PANEL_ALTO = 540f;
    private static final float PANEL_X = 260f;
    private static final float PANEL_ANCHO = 1000f - PANEL_X;
    private static final float VELOCIDAD_SLIDE = 1800f;
    private float offsetY;
    private float offsetYObjetivo;
    private boolean cerrando = false;
    private Runnable alCerrarCompletamente;
    private static final float RUEDA_X = 1250f;
    private static final float RUEDA_Y = 420f;
    private static final float RUEDA_RADIO = 170f;
    private RuedaZodiaco ruedaZodiaco;
    private final OverlayConsumoZodiaco overlayConsumo = new OverlayConsumoZodiaco();
    private final OverlaySeleccionCarta overlaySeleccion = new OverlaySeleccionCarta();
    private SignoZodiaco signoObtenido;
    private final Consumer<VistaItemTienda> alComprarJoker;
    private final Consumer<Santo> alComprarYUsarSanto;
    private final Runnable onBeforeReroll;
    private Juego juego;
    private boolean bloqueadoPorModalExterno = false;

    public PanelTienda(Main game, Jugador jugador, Runnable alContinuar, Consumer<VistaItemTienda> alComprarJoker,
        Consumer<Santo> alComprarYUsarSanto, Runnable onBeforeReroll, Juego juego) {
        this.game = game;
        this.juego = juego;
        this.jugador = jugador;
        this.alContinuar = alContinuar;
        this.alComprarJoker = alComprarJoker;
        this.alComprarYUsarSanto = alComprarYUsarSanto;
        this.onBeforeReroll = onBeforeReroll;
        this.estadoTienda = new EstadoTienda(jugador);
        if (Gdx.files.internal("ui/peso.png").exists()) {
            iconoPeso = new Texture("ui/peso.png");
        }
        botonComprar = new Boton(PANEL_X + PANEL_ANCHO - 180, PANEL_Y + 20, 160, 50, Boton.TipoColor.VERDE, Accion.COMPRAR_ITEM_TIENDA);
        botonComprar.setHabilitado(false);
        botonReroll = new Boton(PANEL_X + 20, PANEL_Y + PANEL_ALTO - 60, 160, 40, Boton.TipoColor.AZUL, Accion.REROLL_JOKERS);
        botonReroll.setTexto("Reroll $" + estadoTienda.costoRerollTienda());
        botonContinuar = new Boton(PANEL_X + PANEL_ANCHO - 180, PANEL_Y + PANEL_ALTO - 60, 160, 50, Boton.TipoColor.DORADO, Accion.CONTINUAR_TIENDA);
        botonComprarYUsar = new Boton(PANEL_X + PANEL_ANCHO - 180, PANEL_Y + 80, 160, 50, Boton.TipoColor.DORADO, Accion.COMPRAR_Y_USAR_SANTO);
        botonComprarYUsar.setVisible(false);
        ruedaZodiaco = new RuedaZodiaco(RUEDA_X, RUEDA_Y, RUEDA_RADIO, game.getTexturaRuletaFondo());
        reconstruirVistas();
        this.offsetY = -(PANEL_Y + PANEL_ALTO);
        this.offsetYObjetivo = 0f;
    }

    private void reconstruirVistas() {
        vistasCartas.clear();
        vistasJokers.clear();
        vistasSantos.clear();
        deseleccionarTodo();
        // CARTAS
        float xCartas = PANEL_X + 220;
        for (ItemTienda item : estadoTienda.getFilaCartas()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(85f, 125f);
            v.setPosition(xCartas, PANEL_Y + PANEL_ALTO - 200);
            vistasCartas.add(v);
            xCartas += 105;
        }
        // JOKERS DE LA TIENDA
        float xJokers = PANEL_X + 220;
        for (ItemTienda item : estadoTienda.getFilaJokers()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(85f, 125f);
            v.setPosition(xJokers, PANEL_Y + 200);
            vistasJokers.add(v);
            xJokers += 105;
        }
        // SANTOS DE LA TIENDA
        float xSantos = PANEL_X + 220;
        for (ItemTienda item : estadoTienda.getFilaSantos()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(85f, 125f);
            v.setPosition(xSantos, PANEL_Y + 20);
            vistasSantos.add(v);
            xSantos += 105;
        }
    }

    private void deseleccionarTodo() {
        seleccionado = null;
        for (VistaItemTienda v : vistasCartas) {
            v.setSeleccionado(false);
        }
        for (VistaItemTienda v : vistasJokers) {
            v.setSeleccionado(false);
        }
        for (VistaItemTienda v : vistasSantos) {
            v.setSeleccionado(false);
        }
        botonComprar.setHabilitado(false);
        botonComprarYUsar.setVisible(false);
        botonComprarYUsar.setHabilitado(false);
    }

    public void update(float mouseWorldX, float mouseWorldY, float delta) {
        if (isAnimando() || bloqueadoPorModalExterno) return;
        boolean justTouched = Gdx.input.justTouched();
        for (VistaItemTienda v : vistasCartas) {
            v.update(mouseWorldX, mouseWorldY, delta);
        }
        for (VistaItemTienda v : vistasJokers) {
            v.update(mouseWorldX, mouseWorldY, delta);
        }
        for (VistaItemTienda v : vistasSantos) {
            v.update(mouseWorldX, mouseWorldY, delta);
        }
        botonComprar.update(mouseWorldX, mouseWorldY);
        botonComprarYUsar.update(mouseWorldX, mouseWorldY);
        botonReroll.update(mouseWorldX, mouseWorldY);
        botonContinuar.update(mouseWorldX, mouseWorldY);
        ruedaZodiaco.update(delta);
        overlayConsumo.update(delta);
        overlaySeleccion.update(mouseWorldX, mouseWorldY, delta);
        if (overlayConsumo.debeAplicarEfectoAhora()) {
            SignoZodiaco s = ruedaZodiaco.getUltimoSignoConsumido();
            s.aplicarEfecto(jugador, null, estadoTienda, null);
            overlayConsumo.confirmarCierre();
        }
        if (Gdx.input.justTouched()) {
            ruedaZodiaco.click(mouseWorldX, mouseWorldY,
                signo -> {
                    overlayConsumo.abrir(signo, game.getAtlasZodiaco().findRegion(signo.getNombreRegion()),
                        () -> {
                        }
                    );
                }
            );
            overlaySeleccion.click(mouseWorldX, mouseWorldY, jugador, null);
        }
        boolean cliqueoAlgunElemento = false;
        // COMPRAR Y USAR SANTO
        if (botonComprarYUsar.fueCliqueado(mouseWorldX, mouseWorldY) && seleccionado != null && seleccionado.getItem().getTipo() == ItemTienda.Tipo.SANTO) {
            comprarYUsarSanto(seleccionado);
            return;
        }
        // CONTINUAR
        if (botonContinuar.fueCliqueado(mouseWorldX, mouseWorldY)) {
            alContinuar.run();
            return;
        }
        // COMPRAR
        if (botonComprar.fueCliqueado(mouseWorldX, mouseWorldY) && seleccionado != null) {
            cliqueoAlgunElemento = true;
            comprar(seleccionado);
        } else if (botonReroll.fueCliqueado(mouseWorldX, mouseWorldY)) {
            cliqueoAlgunElemento = true;
            if (onBeforeReroll != null) {
                onBeforeReroll.run();
            }
            boolean exito = estadoTienda.rerollearTienda(jugador);
            if (exito) {reconstruirVistas();
                botonReroll.setTexto("Reroll $" + estadoTienda.costoRerollTienda());
            }
        }
        // SELECCIONAR ITEM
        if (justTouched && !cliqueoAlgunElemento) {
            VistaItemTienda itemClickeado = null;
            for (VistaItemTienda v : vistasCartas) {
                if (v.contiene(mouseWorldX, mouseWorldY)) {
                    itemClickeado = v;
                    break;
                }
            }
            if (itemClickeado == null) {
                for (VistaItemTienda v : vistasJokers) {
                    if (v.contiene(mouseWorldX, mouseWorldY)) {
                        itemClickeado = v;
                        break;
                    }
                }
            }
            if (itemClickeado == null) {
                for (VistaItemTienda v : vistasSantos) {
                    if (v.contiene(mouseWorldX, mouseWorldY)) {
                        itemClickeado = v;
                        break;
                    }
                }
            }
            if (itemClickeado != null) {
                if (seleccionado == itemClickeado) {
                    deseleccionarTodo();
                } else {
                    deseleccionarTodo();
                    seleccionado = itemClickeado;
                    seleccionado.setSeleccionado(true);
                    boolean dineroSuficiente = jugador.getPesos() >= seleccionado.getItem().getPrecio();
                    boolean esSanto = seleccionado.getItem().getTipo() == ItemTienda.Tipo.SANTO;
                    boolean espacioDisponible;
                    if (seleccionado.getItem().getTipo() == ItemTienda.Tipo.JOKER) {
                        espacioDisponible = jugador.getJokers().size() < jugador.getTamañoJokers();
                    } else if (esSanto) {
                        espacioDisponible = jugador.getSantos().size() < jugador.getTamañoSantos();
                    } else {
                        espacioDisponible = true;
                    }
                    botonComprar.setHabilitado(dineroSuficiente && espacioDisponible);
                    botonComprarYUsar.setVisible(esSanto);
                    botonComprarYUsar.setHabilitado(esSanto && dineroSuficiente && espacioDisponible);
                }
            } else {
                deseleccionarTodo();
            }
        }
    }

    public void setBloqueadoPorModalExterno(boolean b) { this.bloqueadoPorModalExterno = b; }

    private void comprar(VistaItemTienda vista) {
        ItemTienda item = vista.getItem();
        if (item.getTipo() == ItemTienda.Tipo.JOKER && jugador.getJokers().size() >= jugador.getTamañoJokers()) {
            return;
        }
        if (item.getTipo() == ItemTienda.Tipo.SANTO && jugador.getSantos().size() >= jugador.getTamañoSantos()) {
            return;
        }
        if (!jugador.gastarPesos(item.getPrecio())) {
            return;
        }
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            jugador.getMazo().agregarCarta(item.getCarta());
            estadoTienda.removerItemComprado(item);
        } else if (item.getTipo() == ItemTienda.Tipo.JOKER) {
            if (alComprarJoker != null) {
                estadoTienda.removerItemComprado(item);
                reconstruirVistas();
                // La animación agregará el Joker al modelo
                // al terminar.
                alComprarJoker.accept(vista);
            } else {
                jugador.agregarJoker(item.getJoker());
                estadoTienda.removerItemComprado(item);
            }
        } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
            Santo santo = item.getSanto();
            if (!jugador.agregarSanto(santo)) {
                jugador.sumarPesos(item.getPrecio());
                return;
            }
            estadoTienda.removerItemComprado(item);
        }
        reconstruirVistas();
    }

    private void comprarYUsarSanto(VistaItemTienda vista) {
        ItemTienda item = vista.getItem();
        Santo santo = item.getSanto();
        if (santo == null) {
            return;
        }
        if (jugador.getSantos().size() >= jugador.getTamañoSantos()) {
            return;
        }
        if (!jugador.gastarPesos(item.getPrecio())) {
            return;
        }
        estadoTienda.removerItemComprado(item);
        reconstruirVistas();
        alComprarYUsarSanto.accept(santo);
    }

    public void render(SpriteBatch batch) {
        com.badlogic.gdx.math.Matrix4 matrizOriginal = batch.getProjectionMatrix().cpy();
        com.badlogic.gdx.math.Matrix4 matrizConOffset = matrizOriginal.cpy().translate(0, offsetY, 0);
        batch.setProjectionMatrix(matrizConOffset);
        // Fondo
        Texture pixel = game.getPixelBlanco();
        batch.setColor(0.06f, 0.07f, 0.1f, 0.94f);
        batch.draw(pixel, PANEL_X, PANEL_Y, PANEL_ANCHO, PANEL_ALTO);
        batch.setColor(1, 1, 1, 1);
        // CARTAS
        for (VistaItemTienda v : vistasCartas) {
            if (v != seleccionado) {
                v.render(batch, game);
            }
        }
        // JOKERS DE LA TIENDA
        for (VistaItemTienda v : vistasJokers) {
            if (v != seleccionado) {
                v.render(batch, game);
            }
        }
        // SANTOS DE LA TIENDA
        for (VistaItemTienda v : vistasSantos) {
            if (v != seleccionado) {
                v.render(batch, game);
            }
        }
        // ITEM SELECCIONADO
        if (seleccionado != null) {
            seleccionado.render(batch, game);
        }
        // Botones
        botonComprar.render(batch);
        botonReroll.render(batch);
        botonContinuar.render(batch);
        botonComprarYUsar.render(batch);
        float infoX = PANEL_X + PANEL_ANCHO - 350;
        float infoY = PANEL_Y + PANEL_ALTO - 60;
        float maxAnchoTexto = 320f;
        if (seleccionado != null) {
            ItemTienda item = seleccionado.getItem();
            String textoPrecio = "Precio: $" + item.getPrecio();
            game.getFuentePrincipal().draw(batch, textoPrecio, PANEL_X + PANEL_ANCHO - 220, PANEL_Y + 90);
        }
        // Carteles de stats
        for (VistaItemTienda v : vistasCartas) {
            v.renderCartelStats(batch, game);
        }
        for (VistaItemTienda v : vistasJokers) {
            v.renderCartelStats(batch, game);
        }
        for (VistaItemTienda v : vistasSantos) {
            v.renderCartelStats(batch, game);
        }
        ruedaZodiaco.render(batch);
        overlayConsumo.render(batch, game);
        overlaySeleccion.render(batch, game);
        batch.setProjectionMatrix(matrizOriginal);
    }

    public void updateAnimacion(float delta) {
        float diferencia = offsetYObjetivo - offsetY;
        if (Math.abs(diferencia)
            <= VELOCIDAD_SLIDE * delta
        ) {
            offsetY = offsetYObjetivo;
            if (
                cerrando && offsetY == offsetYObjetivo
            ) {
                if (alCerrarCompletamente != null) {
                    alCerrarCompletamente.run();
                }
            }
        } else {
            offsetY += Math.signum(diferencia) * VELOCIDAD_SLIDE * delta;
        }
    }

    public boolean isAnimando() {
        return offsetY != offsetYObjetivo;
    }

    public void cerrar(Runnable alCerrarCompletamente) {
        this.cerrando = true;
        this.alCerrarCompletamente = alCerrarCompletamente;
        this.offsetYObjetivo = -(PANEL_Y + PANEL_ALTO);
    }

    public void dispose() {
        if (iconoPeso != null) {
            iconoPeso.dispose();
        }
        if (ruedaZodiaco != null) {
            ruedaZodiaco.dispose();
        }
    }

    public void consumir(SignoZodiaco signo, Jugador jugador, Juego juego,
                         EstadoTienda tienda, OverlaySeleccionCarta overlaySeleccion, Runnable alTerminarTodo) {
        signo.aplicarEfecto(jugador, juego, tienda, null);
        alTerminarTodo.run();
    }

    public float getOffsetY() {
        return offsetY;
    }
}
