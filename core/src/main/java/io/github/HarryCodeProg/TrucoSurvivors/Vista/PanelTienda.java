package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorInputArrastrable;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.*;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Panel de tienda que se dibuja SOBRE la mesa de GameScreenV2, ocupando solo
 * la zona central/inferior, dejando visible la fila de jokers y el panel de
 * puntajes — igual que en Balatro, nunca hay pantallazo completo.
 */
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
    private final Boton botonReroll; // Unico boton REROLL (para jokers en esta implementación)
    private final Boton botonContinuar;
    private Texture iconoPeso;
    // Zona ocupada por el panel: deja libre la franja superior (donde estan los jokers)
    private static final float PANEL_Y = 40f;
    private static final float PANEL_ALTO = 540f; // aumentado para dar espacio a santos
    private static final float PANEL_X = 260f;
    private static final float PANEL_ANCHO = 1000f - PANEL_X;
    private static final float VELOCIDAD_SLIDE = 1800f; // px/seg
    private float offsetY;        // cuanto se desplaza el panel respecto a su posicion final (positivo = mas abajo)
    private float offsetYObjetivo; // 0 = posicion final (visible), -PANEL_ALTO-PANEL_Y = totalmente oculto abajo
    private boolean cerrando = false;
    private Runnable alCerrarCompletamente;
    private final ArrayList<VistaJoker> vistasJokersPropios = new ArrayList<>();
    private VistaJoker jokerPropioSeleccionado;
    private static final float RUEDA_X = 1250f;
    private static final float RUEDA_Y = 420f;
    private static final float RUEDA_RADIO = 170f;
    private RuedaZodiaco ruedaZodiaco;
    private OverlayConsumoZodiaco overlayConsumo = new OverlayConsumoZodiaco();
    private OverlaySeleccionCarta overlaySeleccion = new OverlaySeleccionCarta();
    private SignoZodiaco signoObtenido;
    private final Consumer<VistaItemTienda> alComprarJoker;
    private final Boton botonComprarYUsar;
    private final Consumer<Santo> alComprarYUsarSanto;


    public PanelTienda(Main game, Jugador jugador,
                       Runnable alContinuar, Consumer<VistaItemTienda> alComprarJoker, Consumer<Santo> alComprarYUsarSanto){
        this.game = game;
        this.jugador = jugador;
        this.alContinuar = alContinuar;
        this.estadoTienda = new EstadoTienda(jugador);
        this.alComprarJoker = alComprarJoker;
        this.alComprarYUsarSanto = alComprarYUsarSanto;
        if (Gdx.files.internal("ui/peso.png").exists()) {
            iconoPeso = new Texture("ui/peso.png");
        }
        botonComprar = new Boton(PANEL_X + PANEL_ANCHO - 180, PANEL_Y + 20, 160, 50, Boton.TipoColor.VERDE, Accion.COMPRAR_ITEM_TIENDA);
        botonComprar.setHabilitado(false);
        botonReroll = new Boton(PANEL_X + 20, PANEL_Y + PANEL_ALTO - 60, 160, 40, Boton.TipoColor.AZUL, Accion.REROLL_JOKERS);
        botonContinuar = new Boton(PANEL_X + PANEL_ANCHO - 180, PANEL_Y + PANEL_ALTO - 60, 160, 50, Boton.TipoColor.DORADO, Accion.CONTINUAR_TIENDA);
        botonComprarYUsar = new Boton(PANEL_X + PANEL_ANCHO - 180, PANEL_Y + 80, 160, 50, Boton.TipoColor.DORADO, Accion.COMPRAR_Y_USAR_SANTO);
        botonComprarYUsar.setVisible(false);
        ruedaZodiaco = new RuedaZodiaco(RUEDA_X, RUEDA_Y, RUEDA_RADIO, game.getTexturaRuletaFondo());
        reconstruirVistas();
        this.offsetY = -(PANEL_Y + PANEL_ALTO);
        this.offsetYObjetivo = 0f;
    }

    private void reconstruirJokersPropios() {
        vistasJokersPropios.clear();
        float xJokerPropio = PANEL_X + 220;
        for (Joker joker : jugador.getJokers()) {
            VistaJoker vj = new VistaJoker(joker, game.getAtlasJokers());
            vj.setTamaño(70, 95);
            vj.setPosition(xJokerPropio, PANEL_Y + PANEL_ALTO - 355); // una fila propia, entre cartas y jokers de venta
            vj.setHandPosition(xJokerPropio, PANEL_Y + PANEL_ALTO - 355);
            vistasJokersPropios.add(vj);
            xJokerPropio += 90;
        }
    }

    private void reconstruirVistas() {
        vistasCartas.clear();
        vistasJokers.clear();
        vistasSantos.clear();
        deseleccionarTodo();
        float xCartas = PANEL_X + 220;
        for (ItemTienda item : estadoTienda.getFilaCartas()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers());
            v.setPosition(xCartas, PANEL_Y + PANEL_ALTO - 200);
            vistasCartas.add(v);
            xCartas += 120;
        }
        float xJokers = PANEL_X + 220;
        // Dibujamos los jokers de venta un poco arriba de la zona inferior
        for (ItemTienda item : estadoTienda.getFilaJokers()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers());
            v.setPosition(xJokers, PANEL_Y + 120);
            vistasJokers.add(v);
            xJokers += 120;
        }
        float xSantos = PANEL_X + 220;
        // Santos aparecen abajo de los jokers
        for (ItemTienda item : estadoTienda.getFilaSantos()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers());
            v.setPosition(xSantos, PANEL_Y + 20);
            vistasSantos.add(v);
            xSantos += 120;
        }
    }

    private void deseleccionarTodo() {
        seleccionado = null;
        jokerPropioSeleccionado = null;
        for (VistaItemTienda v : vistasCartas) v.setSeleccionado(false);
        for (VistaItemTienda v : vistasJokers) v.setSeleccionado(false);
        for (VistaItemTienda v : vistasSantos) v.setSeleccionado(false);
        for (VistaJoker vj : vistasJokersPropios) vj.setSeleccionada(false);
        botonComprar.setHabilitado(false);
    }

    /** Debe llamarse UNA vez por frame, con las coordenadas de mouse ya convertidas al mundo. */
    public void update(float mouseWorldX, float mouseWorldY, float delta) {
        if (isAnimando()) return;
        boolean justTouched = Gdx.input.justTouched();
        for (VistaItemTienda v : vistasCartas) v.update(mouseWorldX, mouseWorldY, delta);
        for (VistaItemTienda v : vistasJokers) v.update(mouseWorldX, mouseWorldY, delta);
        for (VistaItemTienda v : vistasSantos) v.update(mouseWorldX, mouseWorldY, delta);
        for (VistaJoker vj : vistasJokersPropios) vj.update(mouseWorldX, mouseWorldY, delta);
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
            ruedaZodiaco.click(mouseWorldX, mouseWorldY, signo -> {
                overlayConsumo.abrir(signo, game.getAtlasZodiaco().findRegion(signo.getNombreRegion()), () -> {});
            });
            overlaySeleccion.click(mouseWorldX, mouseWorldY, jugador, null);
        }
        if (justTouched) {
            for (VistaJoker vj : vistasJokersPropios) {
                if (vj.contiene(mouseWorldX, mouseWorldY)) {
                    deseleccionarTodo();
                    jokerPropioSeleccionado = vj;
                    vj.setSeleccionada(true);
                    botonComprar.setHabilitado(true); // reusamos el mismo boton, ver mas abajo
                    break;
                }
            }
        }
        boolean cliqueoAlgunElemento = false;
        if (botonComprarYUsar.fueCliqueado(mouseWorldX, mouseWorldY)
            && seleccionado != null
            && seleccionado.getItem().getTipo() == ItemTienda.Tipo.SANTO) {
            comprarYUsarSanto(seleccionado);
            return;
        }
        if (botonContinuar.fueCliqueado(mouseWorldX, mouseWorldY)) {
            alContinuar.run();
            return;
        } else if (botonComprar.fueCliqueado(mouseWorldX, mouseWorldY) && seleccionado != null) {
            cliqueoAlgunElemento = true;
            comprar(seleccionado);
        } else if (botonComprar.fueCliqueado(mouseWorldX, mouseWorldY)) {
            cliqueoAlgunElemento = true;
            if (jokerPropioSeleccionado != null) {
                vender(jokerPropioSeleccionado);
            } else if (seleccionado != null) {
                comprar(seleccionado);
            }
        } else if (botonReroll.fueCliqueado(mouseWorldX, mouseWorldY)) {
            cliqueoAlgunElemento = true;
            // Unico reroll: rerollear jokers
            estadoTienda.rerollearJokers(jugador);
            reconstruirVistas();
        }
        if (justTouched && !cliqueoAlgunElemento) {
            VistaItemTienda itemClickeado = null;
            for (VistaItemTienda v : vistasCartas) {
                if (v.contiene(mouseWorldX, mouseWorldY)) { itemClickeado = v; break; }
            }
            if (itemClickeado == null) {
                for (VistaItemTienda v : vistasJokers) {
                    if (v.contiene(mouseWorldX, mouseWorldY)) { itemClickeado = v; break; }
                }
            }
            if (itemClickeado == null) {
                for (VistaItemTienda v : vistasSantos) {
                    if (v.contiene(mouseWorldX, mouseWorldY)) { itemClickeado = v; break; }
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
                    } else if (seleccionado.getItem().getTipo() == ItemTienda.Tipo.SANTO) {
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

    private void vender(VistaJoker vista) {
        Joker joker = vista.getJoker();
        int precioVenta = Math.max(1, joker.getCoste() * ConfiguracionEconomia.PRECIO_VENTA_JOKER_PORCENTAJE / 100);
        jugador.eliminarJoker(joker);
        jugador.sumarPesos(precioVenta);
        reconstruirJokersPropios();
        deseleccionarTodo();
    }

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
        } else if (item.getTipo() == ItemTienda.Tipo.JOKER) {
            jugador.agregarJoker(item.getJoker());
            if (alComprarJoker != null) {
                alComprarJoker.accept(vista);
            }
        } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
            jugador.agregarSanto(item.getSanto());
        }
        estadoTienda.removerItemComprado(item);
        reconstruirVistas();
    }

    private void comprarYUsarSanto(VistaItemTienda vista) {
        ItemTienda item = vista.getItem();
        Santo santo = item.getSanto();
        if (santo == null) return;
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
        // 1. Guardamos la matriz original y aplicamos la del slide/offset de la tienda
        com.badlogic.gdx.math.Matrix4 matrizOriginal = batch.getProjectionMatrix().cpy();
        com.badlogic.gdx.math.Matrix4 matrizConOffset = matrizOriginal.cpy().translate(0, offsetY, 0);
        batch.setProjectionMatrix(matrizConOffset);
        // --- Fondo de la tienda ---
        Texture pixel = game.getPixelBlanco();
        batch.setColor(0.06f, 0.07f, 0.1f, 0.94f);
        batch.draw(pixel, PANEL_X, PANEL_Y, PANEL_ANCHO, PANEL_ALTO);
        batch.setColor(1, 1, 1, 1);
        // --- Elementos ---
        for (VistaItemTienda v : vistasCartas) if (v != seleccionado) v.render(batch, game);
        for (VistaItemTienda v : vistasJokers) if (v != seleccionado) v.render(batch, game);
        for (VistaItemTienda v : vistasSantos) if (v != seleccionado) v.render(batch, game);
        for (VistaJoker vj : vistasJokersPropios) vj.render(batch);
        if (seleccionado != null) seleccionado.render(batch, game);
        // --- Botones y textos ---
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
        } else if (jokerPropioSeleccionado != null) {
            Joker j = jokerPropioSeleccionado.getJoker();
            game.getFuentePrincipal().draw(batch, "Nombre: " + j.getNombre(), infoX, infoY);
            game.getFuentePrincipal().draw(batch, "Rareza: " + j.getRareza().name(), infoX, infoY - 30);
            game.getFuentePrincipal().draw(batch, j.getDescripcion(), infoX, infoY - 70, maxAnchoTexto, com.badlogic.gdx.utils.Align.left, true);
            int precioVenta = Math.max(1, j.getCoste() * ConfiguracionEconomia.PRECIO_VENTA_JOKER_PORCENTAJE / 100);
            String textoVenta = "Vender por: $" + precioVenta;
            game.getFuentePrincipal().draw(batch, textoVenta, PANEL_X + PANEL_ANCHO - 220, PANEL_Y + 90);
        }
        // --- Carteles de stats (hovers) ---
        for (VistaItemTienda v : vistasCartas) v.renderCartelStats(batch, game);
        for (VistaItemTienda v : vistasJokers) v.renderCartelStats(batch, game);
        for (VistaItemTienda v : vistasSantos) v.renderCartelStats(batch, game);
        for (VistaJoker vj : vistasJokersPropios) {
            if (vj.isHover()) {
                vj.renderCartelStats(batch, game);
            }
        }
        ruedaZodiaco.render(batch);
        overlayConsumo.render(batch, game);
        overlaySeleccion.render(batch, game);
        // 3. Restauramos la matriz original para devolver el batch como venía
        batch.setProjectionMatrix(matrizOriginal);
    }

    /** Debe llamarse una vez por frame, ANTES de update() de input, para que la animacion avance siempre. */
    public void updateAnimacion(float delta) {
        float diferencia = offsetYObjetivo - offsetY;
        if (Math.abs(diferencia) <= VELOCIDAD_SLIDE * delta) {
            offsetY = offsetYObjetivo;
            if (cerrando && offsetY == offsetYObjetivo) {
                if (alCerrarCompletamente != null) alCerrarCompletamente.run();
            }
        } else {
            offsetY += Math.signum(diferencia) * VELOCIDAD_SLIDE * delta;
        }
    }

    public boolean isAnimando() {
        return offsetY != offsetYObjetivo;
    }

    /** Inicia el slide down. Cuando termine de salir de pantalla, ejecuta el callback (recien ahi cerrar la tienda de verdad). */
    public void cerrar(Runnable alCerrarCompletamente) {
        this.cerrando = true;
        this.alCerrarCompletamente = alCerrarCompletamente;
        this.offsetYObjetivo = -(PANEL_Y + PANEL_ALTO);
    }

    public void dispose() {
        if (iconoPeso != null) iconoPeso.dispose();
        if (ruedaZodiaco != null) ruedaZodiaco.dispose();
    }

    public void consumir(SignoZodiaco signo, Jugador jugador, Juego juego, EstadoTienda tienda, OverlaySeleccionCarta overlaySeleccion, Runnable alTerminarTodo) {
        signo.aplicarEfecto(jugador, juego, tienda, null);
        alTerminarTodo.run();
    }

    public float getOffsetY() {
        return offsetY;
    }
}
