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
    private static final float ALTO_ITEM = 145f;
    private static final float PANEL_X = 240f;
    private static final float PANEL_ANCHO = 820f;
    private static final float ANCHO_ITEM = 100f;
    private static final float ESPACIO_ITEM = 18f;
    private static final float ANCHO_BOTON = 170f;
    private static final float ALTO_BOTON = 48f;
    private static final float COLUMNA_ACCIONES_X = PANEL_X + PANEL_ANCHO - ANCHO_BOTON - 28f;
    private static final float GALERIA_X = PANEL_X + 25f;
    private static final float GALERIA_ANCHO_COMPLETO = PANEL_ANCHO - 50f;
    private static final float GALERIA_ANCHO_MITAD = (GALERIA_ANCHO_COMPLETO / 2f) - 15f;
    private static final float GALERIA_X_MITAD_DER = GALERIA_X + GALERIA_ANCHO_MITAD + 30f;
    private static final float GALERIA_ANCHO_SUPERIOR = PANEL_ANCHO - 50f;
    private static final float GALERIA_ANCHO_SANTOS = COLUMNA_ACCIONES_X - GALERIA_X - 28f;
    private static final float ESPACIO_ITEM_MINIMO = 10f;
    private static final float Y_FILA_SUPERIOR = PANEL_Y + 315f;
    private static final float Y_FILA_INFERIOR = PANEL_Y + 85f;
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
        // JOKERS DE LA TIENDA (Fila Superior, Ancho Completo)
        int cantidadJokers = estadoTienda.getFilaJokers().size();
        float anchoJokers = calcularAnchoItem(cantidadJokers, GALERIA_ANCHO_COMPLETO);
        float altoJokers = calcularAltoItem(anchoJokers);
        float espacioJokers = calcularEspacioItem(cantidadJokers, anchoJokers, GALERIA_ANCHO_COMPLETO);
        float xJokers = calcularInicioFila(cantidadJokers, anchoJokers, espacioJokers, GALERIA_X, GALERIA_ANCHO_COMPLETO);
        for (ItemTienda item : estadoTienda.getFilaJokers()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(anchoJokers, altoJokers);
            v.setPosition(xJokers, Y_FILA_SUPERIOR);
            // <--- NUEVO: Activamos el tooltip lateral SOLO para los Jokers
            v.setTooltipLateral(true);
            vistasJokers.add(v);
            xJokers += anchoJokers + espacioJokers;
        }
        // SANTOS DE LA TIENDA (Fila Inferior, Mitad Izquierda)
        int cantidadSantos = estadoTienda.getFilaSantos().size();
        float anchoSantos = calcularAnchoItem(cantidadSantos, GALERIA_ANCHO_MITAD);
        float altoSantos = calcularAltoItem(anchoSantos);
        float espacioSantos = calcularEspacioItem(cantidadSantos, anchoSantos, GALERIA_ANCHO_MITAD);
        float xSantos = calcularInicioFila(cantidadSantos, anchoSantos, espacioSantos, GALERIA_X, GALERIA_ANCHO_MITAD);
        for (ItemTienda item : estadoTienda.getFilaSantos()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(anchoSantos, altoSantos);
            v.setPosition(xSantos, Y_FILA_INFERIOR);
            vistasSantos.add(v);
            xSantos += anchoSantos + espacioSantos;
        }
        // CARTAS (Fila Inferior, Mitad Derecha)
        int cantidadCartas = estadoTienda.getFilaCartas().size();
        float anchoCartas = calcularAnchoItem(cantidadCartas, GALERIA_ANCHO_MITAD);
        float altoCartas = calcularAltoItem(anchoCartas);
        float espacioCartas = calcularEspacioItem(cantidadCartas, anchoCartas, GALERIA_ANCHO_MITAD);
        float xCartas = calcularInicioFila(cantidadCartas, anchoCartas, espacioCartas, GALERIA_X_MITAD_DER, GALERIA_ANCHO_MITAD);
        for (ItemTienda item : estadoTienda.getFilaCartas()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers(), juego);
            v.setTamaño(anchoCartas, altoCartas);
            v.setPosition(xCartas, Y_FILA_INFERIOR);
            vistasCartas.add(v);
            xCartas += anchoCartas + espacioCartas;
        }
    }

    private float calcularAnchoItem(int cantidad, float anchoGaleria) {
        return ANCHO_ITEM;
    }

    private float calcularEspacioItem(int cantidad, float anchoItem, float anchoGaleria) {
        if (cantidad <= 1) return 0f;
        float separacion = (anchoGaleria - (anchoItem * cantidad)) / (cantidad - 1);
        return Math.min(ESPACIO_ITEM, separacion);
    }

    private float calcularAltoItem(float anchoItem) {
        return ALTO_ITEM;
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
        // 1. DETERMINAR HOVER EXCLUSIVO GLOBAL
        // Evaluamos en el mismo orden en el que se dibujan, así el último evaluado es el que queda arriba de todo.
        VistaItemTienda ganadorHover = null;
        for (VistaItemTienda v : vistasCartas) { if (v.contiene(mouseWorldX, mouseWorldY)) ganadorHover = v; }
        for (VistaItemTienda v : vistasJokers) { if (v.contiene(mouseWorldX, mouseWorldY)) ganadorHover = v; }
        for (VistaItemTienda v : vistasSantos) { if (v.contiene(mouseWorldX, mouseWorldY)) ganadorHover = v; }
        // 2. ACTUALIZAR ITEMS (Solo el ganador recibe el mouse, el resto recibe coordenadas falsas para apagarse)
        for (VistaItemTienda v : vistasCartas) {
            if (v == ganadorHover) v.update(mouseWorldX, mouseWorldY, delta);
            else v.update(-1000f, -1000f, delta);
        }
        for (VistaItemTienda v : vistasJokers) {
            if (v == ganadorHover) v.update(mouseWorldX, mouseWorldY, delta);
            else v.update(-1000f, -1000f, delta);
        }
        for (VistaItemTienda v : vistasSantos) {
            if (v == ganadorHover) v.update(mouseWorldX, mouseWorldY, delta);
            else v.update(-1000f, -1000f, delta);
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
        if (justTouched && !cliqueoAlgunElemento) {
            // Utilizamos directamente al ganador del Hover como el ítem clickeado
            VistaItemTienda itemClickeado = ganadorHover;
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
                if (seleccionado != null) {
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
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        if (sonidos != null) sonidos.reproducirSonidoGastarPeso();
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            juego.agregarCartaAlMazoJugador(item.getCarta());
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
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        if (sonidos != null) sonidos.reproducirSonidoGastarPeso();
        estadoTienda.removerItemComprado(item);
        reconstruirVistas();
        alComprarYUsarSanto.accept(santo);
    }

    public void render(SpriteBatch batch) {
        com.badlogic.gdx.math.Matrix4 matrizOriginal = batch.getProjectionMatrix().cpy();
        com.badlogic.gdx.math.Matrix4 matrizConOffset = matrizOriginal.cpy().translate(0, offsetY, 0);
        batch.setProjectionMatrix(matrizConOffset);
        Texture pixel = game.getPixelBlanco();
        // FONDO DEL PANEL
        float panelX = PANEL_X;
        float panelY = PANEL_Y; // No sumamos offsetY porque la matrizConOffset ya mueve TODO junto.
        PanelUI.dibujarFondoPanel(batch, pixel, panelX, panelY, PANEL_ANCHO, PANEL_ALTO);
        dibujarCabecera(batch);
        // FONDOS DE FILAS
        PanelUI.dibujarFondoFila(batch, pixel, GALERIA_X - 12f, Y_FILA_SUPERIOR - 15f, GALERIA_ANCHO_COMPLETO + 24f, ALTO_ITEM + 30f, UITheme.ROJO_SECCION);
        dibujarSeccionTitulo(batch, "JOKERS", GALERIA_X, Y_FILA_SUPERIOR + ALTO_ITEM + 15f);
        // SANTOS (Abajo, mitad izquierda)
        PanelUI.dibujarFondoFila(batch, pixel, GALERIA_X - 12f, Y_FILA_INFERIOR - 15f, GALERIA_ANCHO_MITAD + 24f, ALTO_ITEM + 30f, UITheme.RAREZA_EPICO);
        dibujarSeccionTitulo(batch, "SANTOS", GALERIA_X, Y_FILA_INFERIOR + ALTO_ITEM + 15f);
        // CARTAS (Abajo, mitad derecha)
        PanelUI.dibujarFondoFila(batch, pixel, GALERIA_X_MITAD_DER - 12f, Y_FILA_INFERIOR - 15f, GALERIA_ANCHO_MITAD + 24f, ALTO_ITEM + 30f, UITheme.RAREZA_RARO);
        dibujarSeccionTitulo(batch, "CARTAS", GALERIA_X_MITAD_DER, Y_FILA_INFERIOR + ALTO_ITEM + 15f);
        batch.setColor(1, 1, 1, 1);
        // RENDERIZAR ITEMS NO SELECCIONADOS
        for (VistaItemTienda v : vistasCartas) {
            if (v != seleccionado) v.render(batch, game);
            dibujarEtiquetaPrecio(batch, v);
        }
        for (VistaItemTienda v : vistasJokers) {
            if (v != seleccionado) {
                v.render(batch, game);
            }
            dibujarEtiquetaPrecio(batch, v);
        }
        for (VistaItemTienda v : vistasSantos) {
            if (v != seleccionado) v.render(batch, game);
            dibujarEtiquetaPrecio(batch, v);
        }
        // ITEM SELECCIONADO AL FRENTE
        if (seleccionado != null) {
            seleccionado.render(batch, game);
            dibujarEtiquetaPrecio(batch, seleccionado);
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

    private void dibujarSeccionTitulo(SpriteBatch batch, String texto, float x, float y) {
        com.badlogic.gdx.graphics.g2d.BitmapFont font = game.getFuentePrincipal();
        float anchoTexto = 110f;
        float alto = 24f;
        float etiquetaX = x;
        float etiquetaY = y + 2f;
        // Pequeña placa oscura detrás del título
        PanelUI.dibujarFondoFila(batch, game.getPixelBlanco(), etiquetaX, etiquetaY, anchoTexto, alto, UITheme.BORDE);
        // Título
        font.setColor(UITheme.TEXTO_PRINCIPAL);
        font.getData().setScale(0.72f);
        font.draw(batch, texto, etiquetaX + 12f, etiquetaY + 17f);
        font.getData().setScale(1f);
        font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        // Línea decorativa
        batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.75f);
        batch.draw(game.getPixelBlanco(), etiquetaX + 8f, etiquetaY, anchoTexto - 16f, 2f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void dibujarEtiquetaPrecio(SpriteBatch batch, VistaItemTienda v) {
        EtiquetaPrecio.dibujar(batch, game.getPixelBlanco(), game.getFuenteNumeros(), v.getX() + ANCHO_ITEM / 2f, v.getTopeY(), v.getItem().getPrecio());
    }

    public void updateAnimacion(float delta) {
        float diferencia = offsetYObjetivo - offsetY;
        if (Math.abs(diferencia) <= VELOCIDAD_SLIDE * delta) {
            offsetY = offsetYObjetivo;
            if (cerrando && offsetY == offsetYObjetivo) {
                if (alCerrarCompletamente != null) {
                    alCerrarCompletamente.run();
                }
            }
        } else {
            offsetY += Math.signum(diferencia) * VELOCIDAD_SLIDE * delta;
        }
    }

    private void dibujarCabecera(SpriteBatch batch) {
        float centroX = PANEL_X + PANEL_ANCHO / 2f;
        float y = PANEL_Y + PANEL_ALTO - 47f;
        // Línea decorativa debajo de la cabecera
        batch.setColor(UITheme.BORDE.r, UITheme.BORDE.g, UITheme.BORDE.b, 0.65f);
        batch.draw(game.getPixelBlanco(), PANEL_X + 22f, PANEL_Y + PANEL_ALTO - 82f, PANEL_ANCHO - 44f, 1f);
        // Pequeño brillo dorado central
        batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.65f);
        batch.draw(game.getPixelBlanco(), centroX - 125f, PANEL_Y + PANEL_ALTO - 83f, 250f, 2f);
        // TÍTULO
        com.badlogic.gdx.graphics.g2d.BitmapFont font = game.getFuentePrincipal();
        String titulo = "LA TIENDA DEL CAMINO";
        font.getData().setScale(1.05f);
        font.setColor(UITheme.DORADO_BRILLANTE);
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, titulo);
        float tituloX = centroX - layout.width / 2f;
        font.draw(batch, titulo, tituloX, y);
        font.getData().setScale(1f);
        font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        batch.setColor(1f, 1f, 1f, 1f);
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

    private float calcularInicioFila(int cantidad, float anchoItem, float espacioItem, float inicioAreaX, float anchoArea) {
        if (cantidad <= 0) return inicioAreaX;
        float anchoTotalOcupado = (cantidad * anchoItem) + ((cantidad - 1) * espacioItem);
        float espacioSobrante = anchoArea - anchoTotalOcupado;
        return inicioAreaX + (espacioSobrante / 2f);
    }
}
