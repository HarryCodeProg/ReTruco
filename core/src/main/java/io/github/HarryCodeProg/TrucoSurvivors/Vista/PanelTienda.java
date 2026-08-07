package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorInputArrastrable;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ConfiguracionEconomia;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.EstadoTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ItemTienda;

import java.util.ArrayList;

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
    private VistaItemTienda seleccionado;
    private final Boton botonComprar;
    private final Boton botonRerollCartas;
    private final Boton botonRerollJokers;
    private final Boton botonContinuar;
    private Texture iconoPeso;
    // Zona ocupada por el panel: deja libre la franja superior (donde estan los jokers)
    private static final float PANEL_Y = 40f;
    private static final float PANEL_ALTO = 460f;
    private static final float PANEL_X = 260f;
    private static final float PANEL_ANCHO = 1160f - PANEL_X;
    private static final float VELOCIDAD_SLIDE = 1800f; // px/seg
    private float offsetY;        // cuanto se desplaza el panel respecto a su posicion final (positivo = mas abajo)
    private float offsetYObjetivo; // 0 = posicion final (visible), -PANEL_ALTO-PANEL_Y = totalmente oculto abajo
    private boolean cerrando = false;
    private Runnable alCerrarCompletamente;
    private final ArrayList<VistaJoker> vistasJokersPropios = new ArrayList<>();
    private VistaJoker jokerPropioSeleccionado;

    public PanelTienda(Main game, Jugador jugador, Runnable alContinuar) {
        this.game = game;
        this.jugador = jugador;
        this.alContinuar = alContinuar;
        this.estadoTienda = new EstadoTienda(jugador);
        if (Gdx.files.internal("ui/peso.png").exists()) {
            iconoPeso = new Texture("ui/peso.png");
        }
        botonComprar = new Boton(PANEL_X + PANEL_ANCHO - 180, PANEL_Y + 20, 160, 50, Boton.TipoColor.VERDE, Accion.COMPRAR_ITEM_TIENDA);
        botonComprar.setHabilitado(false);
        botonRerollCartas = new Boton(PANEL_X + 20, PANEL_Y + PANEL_ALTO - 60, 160, 40, Boton.TipoColor.AZUL, Accion.REROLL_CARTAS);
        botonRerollJokers = new Boton(PANEL_X + 20, PANEL_Y + PANEL_ALTO - 210, 160, 40, Boton.TipoColor.AZUL, Accion.REROLL_JOKERS);
        botonContinuar = new Boton(PANEL_X + PANEL_ANCHO - 180, PANEL_Y + PANEL_ALTO - 60, 160, 50, Boton.TipoColor.DORADO, Accion.CONTINUAR_TIENDA);
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
        deseleccionarTodo();
        float xCartas = PANEL_X + 220;
        for (ItemTienda item : estadoTienda.getFilaCartas()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers());
            v.setPosition(xCartas, PANEL_Y + PANEL_ALTO - 200);
            vistasCartas.add(v);
            xCartas += 120;
        }
        float xJokers = PANEL_X + 220;
        for (ItemTienda item : estadoTienda.getFilaJokers()) {
            VistaItemTienda v = new VistaItemTienda(item, game.getAtlasCartas(), game.getAtlasJokers());
            v.setPosition(xJokers, PANEL_Y + 40);
            vistasJokers.add(v);
            xJokers += 120;
        }
    }

    private void deseleccionarTodo() {
        seleccionado = null;
        jokerPropioSeleccionado = null;
        for (VistaItemTienda v : vistasCartas) v.setSeleccionado(false);
        for (VistaItemTienda v : vistasJokers) v.setSeleccionado(false);
        for (VistaJoker vj : vistasJokersPropios) vj.setSeleccionada(false);
        botonComprar.setHabilitado(false);
    }

    /** Debe llamarse UNA vez por frame, con las coordenadas de mouse ya convertidas al mundo. */
    public void update(float mouseWorldX, float mouseWorldY, float delta) {
        if (isAnimando()) return;
        boolean justTouched = Gdx.input.justTouched();
        for (VistaItemTienda v : vistasCartas) v.update(mouseWorldX, mouseWorldY, delta);
        for (VistaItemTienda v : vistasJokers) v.update(mouseWorldX, mouseWorldY, delta);
        botonComprar.update(mouseWorldX, mouseWorldY);
        botonRerollCartas.update(mouseWorldX, mouseWorldY);
        botonRerollJokers.update(mouseWorldX, mouseWorldY);
        botonContinuar.update(mouseWorldX, mouseWorldY);
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
        } else if (botonRerollJokers.fueCliqueado(mouseWorldX, mouseWorldY)) {
            cliqueoAlgunElemento = true;
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
            if (itemClickeado != null) {
                if (seleccionado == itemClickeado) {
                    deseleccionarTodo();
                } else {
                    deseleccionarTodo();
                    seleccionado = itemClickeado;
                    seleccionado.setSeleccionado(true);
                    boolean dineroSuficiente = jugador.getPesos() >= seleccionado.getItem().getPrecio();
                    boolean espacioDisponible = seleccionado.getItem().getTipo() != ItemTienda.Tipo.JOKER
                        || jugador.getJokers().size() < jugador.getTamañoJokers();
                    botonComprar.setHabilitado(dineroSuficiente && espacioDisponible);
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
        if (item.getTipo() == ItemTienda.Tipo.JOKER && jugador.getJokers().size() >= jugador.getTamañoJokers()) return;
        if (!jugador.gastarPesos(item.getPrecio())) return;
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            jugador.getMazo().agregarCarta(item.getCarta());
        } else if (item.getTipo() == ItemTienda.Tipo.JOKER) {
            jugador.agregarJoker(item.getJoker());
        }
        estadoTienda.removerItemComprado(item);
        reconstruirVistas();
    }

    public void render(SpriteBatch batch) {
        batch.end(); // cerramos el batch actual para cambiar la matriz
        com.badlogic.gdx.math.Matrix4 matrizOriginal = batch.getProjectionMatrix().cpy();
        com.badlogic.gdx.math.Matrix4 matrizConOffset = matrizOriginal.cpy().translate(0, offsetY, 0);
        batch.setProjectionMatrix(matrizConOffset);
        batch.begin();
        Texture pixel = game.getPixelBlanco();
        batch.setColor(0.06f, 0.07f, 0.1f, 0.94f);
        batch.draw(pixel, PANEL_X, PANEL_Y, PANEL_ANCHO, PANEL_ALTO);
        batch.setColor(1, 1, 1, 1);
        for (VistaItemTienda v : vistasCartas) if (v != seleccionado) v.render(batch);
        for (VistaItemTienda v : vistasJokers) if (v != seleccionado) v.render(batch);
        if (seleccionado != null) seleccionado.render(batch);
        botonComprar.render(batch);
        botonRerollCartas.render(batch);
        botonRerollJokers.render(batch);
        botonContinuar.render(batch);
        String textoPesos = "$" + jugador.getPesos();
        if (iconoPeso != null) batch.draw(iconoPeso, PANEL_X + 20, PANEL_Y + PANEL_ALTO - 150, 32, 32);
        game.getFuentePrincipal().draw(batch, textoPesos, PANEL_X + 60, PANEL_Y + PANEL_ALTO - 125);
        if (seleccionado != null) {
            String textoPrecio = "Precio: $" + seleccionado.getItem().getPrecio();
            game.getFuentePrincipal().draw(batch, textoPrecio, PANEL_X + PANEL_ANCHO - 220, PANEL_Y + 90);
        } else if (jokerPropioSeleccionado != null) {
            int precioVenta = Math.max(1, jokerPropioSeleccionado.getJoker().getCoste() * ConfiguracionEconomia.PRECIO_VENTA_JOKER_PORCENTAJE / 100);
            String textoVenta = "Vender por: $" + precioVenta;
            game.getFuentePrincipal().draw(batch, textoVenta, PANEL_X + PANEL_ANCHO - 220, PANEL_Y + 90);
        }
        batch.end();
        batch.setProjectionMatrix(matrizOriginal);
        batch.begin(); // devolvemos el batch abierto, como estaba antes de entrar a este metodo
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
    }
}
