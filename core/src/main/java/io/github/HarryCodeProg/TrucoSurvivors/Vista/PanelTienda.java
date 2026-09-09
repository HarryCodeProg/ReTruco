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
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;

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
    private static final float PANEL_Y = -30f;
    private static final float PANEL_ALTO = 590f;
    private static final float ALTO_ITEM = 120f;
    private static final float PANEL_X = 260f;
    private static final float PANEL_ANCHO = 1000f - PANEL_X;
    private static final float ANCHO_ITEM = 85f;
    // Filas compactas para reservar aire vertical incluso cuando un ítem se eleva al seleccionarse.
    private static final float ESPACIO_ITEM = 20f;
    private static final float ANCHO_BOTON = 170f;
    private static final float ALTO_BOTON = 48f;
    private static final float COLUMNA_ACCIONES_X = PANEL_X + PANEL_ANCHO - ANCHO_BOTON - 28f;
    private static final float GALERIA_X = PANEL_X + 190f;
    private static final float GALERIA_ANCHO_SUPERIOR = PANEL_ANCHO - 220f;
    private static final float GALERIA_ANCHO_SANTOS = COLUMNA_ACCIONES_X - GALERIA_X - 28f;
    private static final float ESPACIO_ITEM_MINIMO = 10f;
    private static final float Y_FILA_CARTAS = PANEL_Y + 380f;
    private static final float Y_FILA_JOKERS = PANEL_Y + 220f;
    private static final float Y_FILA_SANTOS = PANEL_Y + 60f;
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
    private static final com.badlogic.gdx.graphics.Color COLOR_COMUN = new com.badlogic.gdx.graphics.Color(0.45f, 0.48f, 0.55f, 1f);
    private static final com.badlogic.gdx.graphics.Color COLOR_RARO = new com.badlogic.gdx.graphics.Color(0.24f, 0.49f, 0.78f, 1f);
    private static final com.badlogic.gdx.graphics.Color COLOR_EPICO = new com.badlogic.gdx.graphics.Color(0.63f, 0.37f, 0.88f, 1f);
    private static final com.badlogic.gdx.graphics.Color COLOR_LEGENDARIO = new com.badlogic.gdx.graphics.Color(0.91f, 0.77f, 0.42f, 1f);

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
        botonComprar = new Boton(0, 0, 100f, 35f, Boton.TipoColor.VERDE, Accion.COMPRAR_ITEM_TIENDA);
        botonComprar.setHabilitado(false);
        botonComprar.setVisible(false);
        botonReroll = new Boton(PANEL_X + 22f, PANEL_Y + PANEL_ALTO - 62f, ANCHO_BOTON, ALTO_BOTON, Boton.TipoColor.AZUL, Accion.REROLL_JOKERS);
        botonReroll.setTexto("Reroll $" + estadoTienda.costoRerollTienda());
        botonContinuar = new Boton(COLUMNA_ACCIONES_X, PANEL_Y + PANEL_ALTO - 62f, ANCHO_BOTON, ALTO_BOTON, Boton.TipoColor.DORADO, Accion.CONTINUAR_TIENDA);
        botonComprarYUsar = new Boton(0, 0, 120f, 35f, Boton.TipoColor.DORADO, Accion.COMPRAR_Y_USAR_SANTO);
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
        botonComprar.setVisible(false);
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
                    botonComprar.setVisible(true);
                    botonComprar.setHabilitado(dineroSuficiente && espacioDisponible);
                    float botX = seleccionado.getX() + (ANCHO_ITEM / 2f) - (botonComprar.getWidth() / 2f);
                    // Solapar apenas abajo de la carta
                    float botY = seleccionado.getY() - (botonComprar.getHeight() / 2f);
                    botonComprar.setPosition(botX, botY);
                    botonComprarYUsar.setVisible(esSanto);
                    botonComprarYUsar.setHabilitado(esSanto && dineroSuficiente && espacioDisponible);
                    if (esSanto) {
                        float botYUsarX = seleccionado.getX() + (ANCHO_ITEM / 2f) - (botonComprarYUsar.getWidth() / 2f);
                        botonComprarYUsar.setPosition(botYUsarX, botY - botonComprarYUsar.getHeight() - 5f);
                    }
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
            juego.agregarCartaAlMazoJugador(item.getCarta()); // FIX: antes era jugador.getMazo().agregarCarta(item.getCarta())
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
        Texture pixel = game.getPixelBlanco();
        // FONDO CON PROFUNDIDAD
        dibujarFondoProfundo(batch, pixel);
        // MARCO DEL PANEL
        dibujarMarcoPanelProfundo(batch, pixel);
        // ENCABEZADO
        dibujarEncabezadoAvanzado(batch, "TIENDA", PANEL_Y + PANEL_ALTO - 30f, PANEL_ANCHO);
        // SECCIONES CON FILAS VISUALES
        dibujarFilaProfunda(batch, pixel, GALERIA_X - 12f, Y_FILA_CARTAS - 15f, GALERIA_ANCHO_SUPERIOR + 24f, ALTO_ITEM + 30f, tema.acento_raro);
        dibujarSeccionTitulo(batch, "CARTAS", Y_FILA_CARTAS + ALTO_ITEM - 45f);
        dibujarFilaProfunda(batch, pixel, GALERIA_X - 12f, Y_FILA_JOKERS - 15f, GALERIA_ANCHO_SUPERIOR + 24f, ALTO_ITEM + 30f, tema.acento_epico);
        dibujarSeccionTitulo(batch, "JOKERS", Y_FILA_JOKERS + ALTO_ITEM - 45f);
        dibujarFilaProfunda(batch, pixel, GALERIA_X - 12f, Y_FILA_SANTOS - 15f, GALERIA_ANCHO_SANTOS + 24f, ALTO_ITEM + 30f, tema.acento_legendario);
        dibujarSeccionTitulo(batch, "SANTOS", Y_FILA_SANTOS + ALTO_ITEM - 45f);
        batch.setColor(1, 1, 1, 1);
        // RENDERIZAR ITEMS NO SELECCIONADOS
        for (VistaItemTienda v : vistasCartas) {
            if (v != seleccionado) v.render(batch, game);
            dibujarPrecioAvanzado(batch, v, pixel);
        }
        for (VistaItemTienda v : vistasJokers) {
            if (v != seleccionado) {
                dibujarMarcoRarezaAvanzado(batch, v, pixel);
                v.render(batch, game);
            }
            dibujarPrecioAvanzado(batch, v, pixel);
        }
        for (VistaItemTienda v : vistasSantos) {
            if (v != seleccionado) v.render(batch, game);
            dibujarPrecioAvanzado(batch, v, pixel);
        }
        // ITEM SELECCIONADO AL FRENTE
        if (seleccionado != null) {
            if (seleccionado.getItem().getTipo() == ItemTienda.Tipo.JOKER) {
                dibujarMarcoRarezaAvanzado(batch, seleccionado, pixel);
            }
            seleccionado.render(batch, game);
            dibujarPrecioAvanzado(batch, seleccionado, pixel);
        }
        // BOTONES
        botonComprar.render(batch);
        botonReroll.render(batch);
        botonContinuar.render(batch);
        botonComprarYUsar.render(batch);
        // CARTELES DE STATS
        for (VistaItemTienda v : vistasCartas) v.renderCartelStats(batch, game);
        for (VistaItemTienda v : vistasJokers) v.renderCartelStats(batch, game);
        for (VistaItemTienda v : vistasSantos) v.renderCartelStats(batch, game);
        // OVERLAYS
        ruedaZodiaco.render(batch);
        overlayConsumo.render(batch, game);
        overlaySeleccion.render(batch, game);
        batch.setProjectionMatrix(matrizOriginal);
    }

    private void dibujarEncabezadoAvanzado(SpriteBatch batch, String texto, float y, float ancho) {
        com.badlogic.gdx.graphics.g2d.BitmapFont font = game.getFuentePrincipal();
        Texture pixel = game.getPixelBlanco();
        // Fondo del encabezado
        batch.setColor(tema.sombra_media);
        batch.draw(pixel, PANEL_X, y - 30f, ancho, 45f);
        // Línea decorativa inferior del encabezado
        batch.setColor(tema.borde_dorado);
        batch.draw(pixel, PANEL_X, y - 5f, ancho, 2f);
        // Texto GRANDE del título
        float escalaOriginal = font.getScaleX();
        font.getData().setScale(escalaOriginal * 1.35f);
        font.setColor(tema.borde_dorado);
        font.draw(batch, texto, PANEL_X + 20f, y + 8f);
        font.getData().setScale(escalaOriginal);
        font.setColor(1f, 1f, 1f, 1f);
    }

    private void dibujarSeccionTitulo(SpriteBatch batch, String texto, float y) {
        com.badlogic.gdx.graphics.g2d.BitmapFont font = game.getFuentePrincipal();
        font.setColor(tema.txt_secundario);
        font.getData().setScale(0.9f);
        font.draw(batch, texto, PANEL_X + 35f, y + 8f);
        font.getData().setScale(1f);
        font.setColor(1f, 1f, 1f, 1f);
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

    public float getOffsetY() {return offsetY;}

    private float calcularInicioFila(int cantidad, float anchoItem, float espacioItem, float anchoGaleria) {
        if (cantidad <= 0) return GALERIA_X;
        return GALERIA_X;
    }

    // === TEMA DE COLORES PROFESIONAL ===
    private static final class TemaUI {
        // Fondos principales
        final com.badlogic.gdx.graphics.Color bgPrincipal = new com.badlogic.gdx.graphics.Color(0.06f, 0.08f, 0.10f, 1f);
        final com.badlogic.gdx.graphics.Color bgSecundario = new com.badlogic.gdx.graphics.Color(0.10f, 0.12f, 0.15f, 1f);
        final com.badlogic.gdx.graphics.Color bgPanel = new com.badlogic.gdx.graphics.Color(0.09f, 0.11f, 0.14f, 0.97f);
        // Acentos por rareza
        final com.badlogic.gdx.graphics.Color acento_comun = new com.badlogic.gdx.graphics.Color(0.45f, 0.48f, 0.55f, 1f);
        final com.badlogic.gdx.graphics.Color acento_raro = new com.badlogic.gdx.graphics.Color(0.24f, 0.49f, 0.78f, 1f);
        final com.badlogic.gdx.graphics.Color acento_epico = new com.badlogic.gdx.graphics.Color(0.70f, 0.35f, 0.95f, 1f);
        final com.badlogic.gdx.graphics.Color acento_legendario = new com.badlogic.gdx.graphics.Color(1.00f, 0.80f, 0.20f, 1f);
        // Textos
        final com.badlogic.gdx.graphics.Color txt_principal = new com.badlogic.gdx.graphics.Color(0.96f, 0.97f, 0.98f, 1f);
        final com.badlogic.gdx.graphics.Color txt_secundario = new com.badlogic.gdx.graphics.Color(0.72f, 0.78f, 0.84f, 1f);
        final com.badlogic.gdx.graphics.Color txt_deshabilitado = new com.badlogic.gdx.graphics.Color(0.45f, 0.48f, 0.52f, 0.6f);
        // Dinero y valor
        final com.badlogic.gdx.graphics.Color dinero = new com.badlogic.gdx.graphics.Color(0.80f, 0.95f, 0.40f, 1f);
        final com.badlogic.gdx.graphics.Color dinero_glow = new com.badlogic.gdx.graphics.Color(0.80f, 0.95f, 0.40f, 0.35f);
        // Botones
        final com.badlogic.gdx.graphics.Color btn_comprar = new com.badlogic.gdx.graphics.Color(0.28f, 0.75f, 0.38f, 1f);
        final com.badlogic.gdx.graphics.Color btn_comprar_hover = new com.badlogic.gdx.graphics.Color(0.35f, 0.85f, 0.45f, 1f);
        final com.badlogic.gdx.graphics.Color btn_comprar_press = new com.badlogic.gdx.graphics.Color(0.22f, 0.60f, 0.32f, 1f);
        final com.badlogic.gdx.graphics.Color btn_reroll = new com.badlogic.gdx.graphics.Color(0.32f, 0.55f, 0.80f, 1f);
        final com.badlogic.gdx.graphics.Color btn_reroll_hover = new com.badlogic.gdx.graphics.Color(0.40f, 0.62f, 0.92f, 1f);
        final com.badlogic.gdx.graphics.Color btn_continuar = new com.badlogic.gdx.graphics.Color(1.00f, 0.80f, 0.20f, 1f);
        final com.badlogic.gdx.graphics.Color btn_continuar_hover = new com.badlogic.gdx.graphics.Color(1.00f, 0.90f, 0.35f, 1f);
        // Sombras y profundidad
        final com.badlogic.gdx.graphics.Color sombra_fuerte = new com.badlogic.gdx.graphics.Color(0.01f, 0.01f, 0.02f, 0.65f);
        final com.badlogic.gdx.graphics.Color sombra_media = new com.badlogic.gdx.graphics.Color(0.05f, 0.06f, 0.10f, 0.45f);
        final com.badlogic.gdx.graphics.Color sombra_suave = new com.badlogic.gdx.graphics.Color(0.10f, 0.12f, 0.18f, 0.25f);
        // Bordes y separadores
        final com.badlogic.gdx.graphics.Color borde_dorado = new com.badlogic.gdx.graphics.Color(1.00f, 0.82f, 0.25f, 1f);
        final com.badlogic.gdx.graphics.Color borde_sutil = new com.badlogic.gdx.graphics.Color(0.25f, 0.35f, 0.45f, 0.5f);
    }

    private final TemaUI tema = new TemaUI();

    // Métodos de rendering mejorado
    private void dibujarFondoProfundo(SpriteBatch batch, Texture pixel) {
        // Capa 1: Sombra lejana (efecto de profundidad)
        batch.setColor(tema.sombra_fuerte);
        batch.draw(pixel, PANEL_X + 12f, PANEL_Y - 12f, PANEL_ANCHO - 12f, PANEL_ALTO - 12f);
        // Capa 2: Sombra media
        batch.setColor(tema.sombra_media);
        batch.draw(pixel, PANEL_X + 6f, PANEL_Y - 6f, PANEL_ANCHO - 6f, PANEL_ALTO - 6f);
        // Capa 3: Fondo principal con degradé (arriba oscuro, abajo claro)
        batch.setColor(tema.bgPanel);
        batch.draw(pixel, PANEL_X, PANEL_Y, PANEL_ANCHO, PANEL_ALTO * 0.5f);
        batch.setColor(0.11f, 0.13f, 0.16f, 0.97f);
        batch.draw(pixel, PANEL_X, PANEL_Y + PANEL_ALTO * 0.5f, PANEL_ANCHO, PANEL_ALTO * 0.5f);
    }

    private void dibujarMarcoPanelProfundo(SpriteBatch batch, Texture pixel) {
        // Borde dorado superior (grosor: 5px, con highlight interno)
        batch.setColor(tema.borde_dorado);
        batch.draw(pixel, PANEL_X - 2f, PANEL_Y + PANEL_ALTO - 5f, PANEL_ANCHO + 4f, 5f);
        batch.setColor(1.0f, 0.90f, 0.35f, 0.6f);
        batch.draw(pixel, PANEL_X - 2f, PANEL_Y + PANEL_ALTO - 2f, PANEL_ANCHO + 4f, 1f);
        // Bordes laterales sutiles
        batch.setColor(tema.borde_sutil);
        batch.draw(pixel, PANEL_X, PANEL_Y, 2f, PANEL_ALTO);
        batch.draw(pixel, PANEL_X + PANEL_ANCHO - 2f, PANEL_Y, 2f, PANEL_ALTO);
        // Línea inferior muy sutil
        batch.setColor(tema.borde_sutil);
        batch.draw(pixel, PANEL_X, PANEL_Y, PANEL_ANCHO, 1f);
    }

    private void dibujarFilaProfunda(SpriteBatch batch, Texture pixel, float x, float y, float ancho, float alto,
                                     com.badlogic.gdx.graphics.Color acento) {
        // Fondo de la fila con esquinas visuales
        batch.setColor(0.08f, 0.10f, 0.13f, 0.8f);
        batch.draw(pixel, x - 10f, y - 12f, ancho + 20f, alto + 20f);
        // Borde superior con color de acento
        batch.setColor(acento.r, acento.g, acento.b, 0.7f);
        batch.draw(pixel, x - 10f, y + alto + 5f, ancho + 20f, 2f);
        // Línea de resaltado
        batch.setColor(acento.r, acento.g, acento.b, 0.3f);
        batch.draw(pixel, x - 10f, y, 2f, alto + 2f);
    }

    private void dibujarPrecioAvanzado(SpriteBatch batch, VistaItemTienda v, Texture pixel) {
        String texto = "$" + v.getItem().getPrecio();
        float etiqW = 50f;
        float etiqH = 26f;
        float etiqX = v.getX() + (ANCHO_ITEM / 2f) - (etiqW / 2f);
        // Ahora usa getYConOffset() para incluir el offset visual
        float etiqY = v.getYConOffset() + ALTO_ITEM - 18f;
        // Sombra de la etiqueta
        batch.setColor(tema.sombra_fuerte);
        batch.draw(pixel, etiqX + 2f, etiqY - 2f, etiqW, etiqH);
        // Fondo de la etiqueta (oscuro con transparencia)
        batch.setColor(0.06f, 0.08f, 0.12f, 0.95f);
        batch.draw(pixel, etiqX, etiqY, etiqW, etiqH);
        // Borde dorado superior
        batch.setColor(tema.borde_dorado);
        batch.draw(pixel, etiqX, etiqY + etiqH - 2f, etiqW, 2f);
        // Borde dorado inferior sutil
        batch.setColor(tema.dinero_glow);
        batch.draw(pixel, etiqX, etiqY, etiqW, 1f);
        // Texto con color dinero
        game.getFuenteNumeros().setColor(tema.dinero);
        game.getFuenteNumeros().getData().setScale(0.90f);
        game.getFuenteNumeros().draw(batch, texto, etiqX, etiqY + etiqH - 6f, etiqW, com.badlogic.gdx.utils.Align.center, false);
        game.getFuenteNumeros().getData().setScale(1f);
        game.getFuenteNumeros().setColor(1f, 1f, 1f, 1f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void dibujarMarcoRarezaAvanzado(SpriteBatch batch, VistaItemTienda v, Texture pixel) {
        if (v.getItem().getTipo() != ItemTienda.Tipo.JOKER) return;
        Joker joker = v.getItem().getJoker();
        if (joker == null) return;
        com.badlogic.gdx.graphics.Color color = colorPorRareza(joker.getRareza());
        boolean esRara = !esComun(joker.getRareza());
        float margen = 5f;
        float mx = v.getX() - margen;
        float my = v.getY() - margen;
        float mw = ANCHO_ITEM + margen * 2f;
        float mh = ALTO_ITEM + margen * 2f;
        if (esRara) {
            // Glow multicapa para rareza alta
            batch.setColor(color.r, color.g, color.b, 0.08f);
            batch.draw(pixel, mx - 15f, my - 15f, mw + 30f, mh + 30f);
            batch.setColor(color.r, color.g, color.b, 0.12f);
            batch.draw(pixel, mx - 8f, my - 8f, mw + 16f, mh + 16f);
        }
        // Marco principal
        batch.setColor(color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, 0.9f);
        batch.draw(pixel, mx, my, mw, mh);
        // Highlight del marco (esquina superior izquierda)
        batch.setColor(color.r, color.g, color.b, 0.4f);
        batch.draw(pixel, mx, my + mh - 3f, mw, 3f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private com.badlogic.gdx.graphics.Color colorPorRareza(Rareza rareza) {
        if (rareza == null) return tema.acento_comun;
        String nombre = rareza.name().toLowerCase().replace("_", "");
        if (nombre.contains("legendario")) return tema.acento_legendario;
        if (nombre.contains("epico")) return tema.acento_epico;
        if (nombre.contains("raro")) return tema.acento_raro;
        return tema.acento_comun;
    }

    private boolean esComun(Rareza rareza) {
        if (rareza == null) return true;
        return rareza.name().toLowerCase().contains("comun");
    }

}
