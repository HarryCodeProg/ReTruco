package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
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
    private static final float ANCHO_ITEM = 85f;
    // Filas compactas para reservar aire vertical incluso cuando un ítem se eleva al seleccionarse.
    private static final float ALTO_ITEM = 105f;
    private static final float ESPACIO_ITEM = 20f;
    private static final float ANCHO_BOTON = 170f;
    private static final float ALTO_BOTON = 48f;
    private static final float COLUMNA_ACCIONES_X = PANEL_X + PANEL_ANCHO - ANCHO_BOTON - 28f;
    private static final float GALERIA_X = PANEL_X + 190f;
    private static final float GALERIA_ANCHO_SUPERIOR = PANEL_ANCHO - 220f;
    private static final float GALERIA_ANCHO_SANTOS = COLUMNA_ACCIONES_X - GALERIA_X - 28f;
    private static final float ESPACIO_ITEM_MINIMO = 10f;
    private static final float Y_FILA_CARTAS = PANEL_Y + 335f;
    private static final float Y_FILA_JOKERS = PANEL_Y + 175f;
    private static final float Y_FILA_SANTOS = PANEL_Y + 15f;
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
        botonComprar = new Boton(COLUMNA_ACCIONES_X, PANEL_Y + 24f, ANCHO_BOTON, ALTO_BOTON, Boton.TipoColor.VERDE, Accion.COMPRAR_ITEM_TIENDA);
        botonComprar.setHabilitado(false);
        botonReroll = new Boton(PANEL_X + 22f, PANEL_Y + PANEL_ALTO - 62f, ANCHO_BOTON, ALTO_BOTON, Boton.TipoColor.AZUL, Accion.REROLL_JOKERS);
        botonReroll.setTexto("Reroll $" + estadoTienda.costoRerollTienda());
        botonContinuar = new Boton(COLUMNA_ACCIONES_X, PANEL_Y + PANEL_ALTO - 62f, ANCHO_BOTON, ALTO_BOTON, Boton.TipoColor.DORADO, Accion.CONTINUAR_TIENDA);
        botonComprarYUsar = new Boton(COLUMNA_ACCIONES_X, PANEL_Y + 88f, ANCHO_BOTON, ALTO_BOTON, Boton.TipoColor.DORADO, Accion.COMPRAR_Y_USAR_SANTO);
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
        int cantidadCartas = estadoTienda.getFilaCartas().size();
        float anchoCartas = calcularAnchoItem(cantidadCartas, GALERIA_ANCHO_SUPERIOR);
        float altoCartas = calcularAltoItem(anchoCartas);
        float espacioCartas = calcularEspacioItem(cantidadCartas, anchoCartas, GALERIA_ANCHO_SUPERIOR);
        float xCartas = calcularInicioFila(cantidadCartas, anchoCartas, espacioCartas, GALERIA_ANCHO_SUPERIOR);
        for (ItemTienda item : estadoTienda.getFilaCartas()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(anchoCartas, altoCartas);
            v.setPosition(xCartas, Y_FILA_CARTAS);
            vistasCartas.add(v);
            xCartas += anchoCartas + espacioCartas;
        }
        // JOKERS DE LA TIENDA
        int cantidadJokers = estadoTienda.getFilaJokers().size();
        float anchoJokers = calcularAnchoItem(cantidadJokers, GALERIA_ANCHO_SUPERIOR);
        float altoJokers = calcularAltoItem(anchoJokers);
        float espacioJokers = calcularEspacioItem(cantidadJokers, anchoJokers, GALERIA_ANCHO_SUPERIOR);
        float xJokers = calcularInicioFila(cantidadJokers, anchoJokers, espacioJokers, GALERIA_ANCHO_SUPERIOR);
        for (ItemTienda item : estadoTienda.getFilaJokers()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(anchoJokers, altoJokers);
            v.setPosition(xJokers, Y_FILA_JOKERS);
            vistasJokers.add(v);
            xJokers += anchoJokers + espacioJokers;
        }
        // SANTOS DE LA TIENDA
        int cantidadSantos = estadoTienda.getFilaSantos().size();
        float anchoSantos = calcularAnchoItem(cantidadSantos, GALERIA_ANCHO_SANTOS);
        float altoSantos = calcularAltoItem(anchoSantos);
        float espacioSantos = calcularEspacioItem(cantidadSantos, anchoSantos, GALERIA_ANCHO_SANTOS);
        float xSantos = calcularInicioFila(cantidadSantos, anchoSantos, espacioSantos, GALERIA_ANCHO_SANTOS);
        for (ItemTienda item : estadoTienda.getFilaSantos()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(anchoSantos, altoSantos);
            v.setPosition(xSantos, Y_FILA_SANTOS);
            vistasSantos.add(v);
            xSantos += anchoSantos + espacioSantos;
        }
    }

    private float calcularAnchoItem(int cantidad, float anchoGaleria) {
        return ANCHO_ITEM;
    }

    private float calcularAltoItem(float anchoItem) {
        return ALTO_ITEM;
    }

    private float calcularEspacioItem(int cantidad, float anchoItem, float anchoGaleria) {
        if (cantidad <= 1) return 0f;
        float separacion = (anchoGaleria - anchoItem) / (cantidad - 1);
        return Math.min(ESPACIO_ITEM, separacion - anchoItem);
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
                        () -> {}
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
            if (exito) {
                GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
                if (sonidos != null) sonidos.reproducirSonidoGastarPeso();
                reconstruirVistas();
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
                    GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
                    if (sonidos != null) {
                        sonidos.reproducirConVariacion("deseleccionar");
                    }
                } else {
                    deseleccionarTodo();
                    seleccionado = itemClickeado;
                    seleccionado.setSeleccionado(true);
                    GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
                    if (sonidos != null) {
                        sonidos.reproducirConVariacion("seleccionar");
                    }
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
                if (seleccionado != null) { // FIX: solo suena si realmente había algo seleccionado antes
                    GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
                    if (sonidos != null) sonidos.reproducirConVariacion("deseleccionar");
                }
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
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos(); // FIX
        if (sonidos != null) sonidos.reproducirSonidoGastarPeso();     // FIX
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            jugador.getMazo().agregarCarta(item.getCarta());
            estadoTienda.removerItemComprado(item);
        } else if (item.getTipo() == ItemTienda.Tipo.JOKER) {
            if (alComprarJoker != null) {
                estadoTienda.removerItemComprado(item);
                reconstruirVistas();
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
        if (santo == null) return;
        if (jugador.getSantos().size() >= jugador.getTamañoSantos()) return;
        if (!jugador.gastarPesos(item.getPrecio())) return;
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos(); // FIX
        if (sonidos != null) sonidos.reproducirSonidoGastarPeso();     // FIX
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
        batch.setColor(0.015f, 0.02f, 0.04f, 0.60f);
        batch.draw(pixel, PANEL_X + 6f, PANEL_Y - 7f, PANEL_ANCHO, PANEL_ALTO);
        batch.setColor(0.055f, 0.065f, 0.10f, 0.97f);
        batch.draw(pixel, PANEL_X, PANEL_Y, PANEL_ANCHO, PANEL_ALTO);
        batch.setColor(0.78f, 0.62f, 0.22f, 0.90f);
        batch.draw(pixel, PANEL_X, PANEL_Y + PANEL_ALTO - 4f, PANEL_ANCHO, 4f);
        batch.setColor(0.25f, 0.32f, 0.43f, 0.9f);
        batch.draw(pixel, PANEL_X, PANEL_Y, 2f, PANEL_ALTO);
        batch.draw(pixel, PANEL_X + PANEL_ANCHO - 2f, PANEL_Y, 2f, PANEL_ALTO);
        dibujarEncabezado(batch, "TIENDA DEL CAMINO", PANEL_Y + PANEL_ALTO - 32f, PANEL_ANCHO);
        dibujarSeccion(batch, "CARTAS", Y_FILA_CARTAS + ALTO_ITEM + 14f);
        dibujarSeccion(batch, "JOKERS", Y_FILA_JOKERS + ALTO_ITEM + 14f);
        dibujarSeccion(batch, "SANTOS", Y_FILA_SANTOS + ALTO_ITEM + 14f);
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
        if (seleccionado != null) {
            ItemTienda item = seleccionado.getItem();
            String textoPrecio = "Precio: $" + item.getPrecio();
            batch.setColor(0.09f, 0.12f, 0.18f, 0.96f);
            batch.draw(pixel, COLUMNA_ACCIONES_X, PANEL_Y + 150f, ANCHO_BOTON, 30f);
            game.getFuenteNumeros().setColor(0.95f, 0.78f, 0.28f, 1f);
            game.getFuenteNumeros().draw(batch, textoPrecio, COLUMNA_ACCIONES_X, PANEL_Y + 172f,
                ANCHO_BOTON, com.badlogic.gdx.utils.Align.center, false);
            game.getFuenteNumeros().setColor(1f, 1f, 1f, 1f);
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

    private void dibujarEncabezado(SpriteBatch batch, String texto, float y, float ancho) {
        com.badlogic.gdx.graphics.g2d.BitmapFont font = game.getFuentePrincipal();
        float escalaOriginal = font.getScaleX();
        font.getData().setScale(escalaOriginal * 1.18f);
        font.setColor(0.95f, 0.78f, 0.28f, 1f);
        font.draw(batch, texto, PANEL_X, y, ancho, com.badlogic.gdx.utils.Align.center, false);
        font.getData().setScale(escalaOriginal);
        font.setColor(1f, 1f, 1f, 1f);
    }

    private void dibujarSeccion(SpriteBatch batch, String texto, float y) {
        Texture pixel = game.getPixelBlanco();
        batch.setColor(0.22f, 0.30f, 0.40f, 0.75f);
        batch.draw(pixel, PANEL_X + 22f, y - 7f, PANEL_ANCHO - 44f, 1f);
        game.getFuentePrincipal().setColor(0.62f, 0.76f, 0.84f, 1f);
        game.getFuentePrincipal().draw(batch, texto, PANEL_X + 32f, y + 8f);
        game.getFuentePrincipal().setColor(1f, 1f, 1f, 1f);
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

    public float getOffsetY() {return offsetY;}

    private float calcularInicioFila(int cantidad, float anchoItem, float espacioItem, float anchoGaleria) {
        if (cantidad <= 0) return GALERIA_X;
        return GALERIA_X;
    }
}
