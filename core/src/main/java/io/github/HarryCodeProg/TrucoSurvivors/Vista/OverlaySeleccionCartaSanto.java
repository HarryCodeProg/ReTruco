package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.AreaElementos;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorInputArrastrable;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorReordenamiento;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class OverlaySeleccionCartaSanto {
    private boolean visible = false;
    private Santo santo;
    private ArrayList<Carta> cartasDisponibles = new ArrayList<>();
    private ArrayList<Carta> seleccionadas = new ArrayList<>();
    private final ArrayList<VistaCarta> vistasCartas = new ArrayList<>();
    private TextureAtlas atlasCartas;
    private Consumer<ArrayList<Carta>> alConfirmar;
    private final Random random = new Random();
    private static final int MAX_CARTAS_MOSTRADAS = 10;
    private static final float CARTA_ANCHO = 95f;
    private static final float CARTA_ALTO = 135f;
    private static final float SEPARACION = 15f;
    private static final float Y_CARTAS = 250f;
    private static final float ANCHO_BOTON = 180f;
    private static final float ALTO_BOTON = 50f;
    private final Boton botonConfirmar;

    // --- sistema de arrastre/reordenamiento, mismo patrón que la mano del jugador ---
    private static final float MARGEN_AREA = 220f;
    private final AreaElementos<VistaCarta> area =
        new AreaElementos<>(MARGEN_AREA, Y_CARTAS, 1280f - MARGEN_AREA * 2, CARTA_ALTO, CARTA_ANCHO, CARTA_ALTO, SEPARACION);
    private final GestorInputArrastrable<VistaCarta> gestorInput = new GestorInputArrastrable<>(vistasCartas);
    private final GestorReordenamiento gestorReordenamiento = new GestorReordenamiento();

    // destino "mazo" al cerrar
    private static final float DECK_X = 1195f;
    private static final float DECK_Y = 130f;
    private boolean cerrando = false;
    private int animacionesPendientes = 0;
    private ArrayList<Carta> resultadoPendiente;

    public OverlaySeleccionCartaSanto() {
        botonConfirmar = new Boton(550f, 120f, ANCHO_BOTON, ALTO_BOTON,
            Boton.TipoColor.VERDE, io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion.CONFIRMAR_SELECCION_SANTO
        );
        botonConfirmar.setHabilitado(false);
    }

    public void abrir(Santo santo, ArrayList<Carta> cartas, TextureAtlas atlasCartas,
                      Consumer<ArrayList<Carta>> alConfirmar) {
        this.santo = santo;
        this.atlasCartas = atlasCartas;
        this.alConfirmar = alConfirmar;
        this.seleccionadas.clear();
        this.vistasCartas.clear();
        this.cerrando = false;
        this.cartasDisponibles = new ArrayList<>(cartas);
        construirVistas();
        visible = true;
        actualizarEstadoBoton();
    }

    private void construirVistas() {
        int cantidad = Math.min(MAX_CARTAS_MOSTRADAS, cartasDisponibles.size());
        if (cantidad == 0) return;
        for (int i = 0; i < cantidad; i++) {
            Carta carta = cartasDisponibles.get(i);
            VistaCarta vista = new VistaCarta(carta, false, atlasCartas);
            vista.setTamaño(CARTA_ANCHO, CARTA_ALTO);
            vistasCartas.add(vista);
        }
        // area.distribuir posiciona Y fija el target (setHandPosition internamente) -> nada de esquina
        area.distribuir(vistasCartas, null);
        for (VistaCarta v : vistasCartas) {
            v.setPosition(v.getHandTargetX(), Y_CARTAS); // instantáneo al abrir, sin animación de entrada
        }
    }

    public void update(float mouseWorldX, float mouseWorldY, float delta) {
        if (!visible) return;

        if (cerrando) {
            for (VistaCarta vista : new ArrayList<>(vistasCartas)) {
                vista.update(mouseWorldX, mouseWorldY, delta);
            }
            return;
        }

        VistaCarta arrastradoAntes = gestorInput.getArrastrado();
        gestorInput.update(mouseWorldX, mouseWorldY, delta, true);

        for (VistaCarta v : vistasCartas) v.update(mouseWorldX, mouseWorldY, delta);

        // reordenamiento en vivo (mismo mecanismo que la mano)
        boolean cambio = gestorReordenamiento.previsualizarReordenamiento(gestorInput, vistasCartas);
        if (cambio) reorganizar();
        // al soltar, recalcular distribución final
        if (arrastradoAntes != null && gestorInput.getArrastrado() == null) reorganizar();

        botonConfirmar.update(mouseWorldX, mouseWorldY);

        // click simple (sin arrastre real) togglea selección — lo maneja VistaCarta.input() vía gestorInput
        for (VistaCarta vista : vistasCartas) {
            boolean quiereSeleccionar = vista.isSeleccionada();
            boolean yaEstaba = seleccionadas.contains(vista.getCarta());
            if (quiereSeleccionar != yaEstaba) sincronizarSeleccion(vista, quiereSeleccionar);
        }

        if (Gdx.input.justTouched() && botonConfirmar.fueCliqueado(mouseWorldX, mouseWorldY)) {
            confirmar();
        }
    }

    private void reorganizar() {
        area.distribuir(vistasCartas, gestorInput.getArrastrado());
    }

    private void sincronizarSeleccion(VistaCarta vista, boolean quiereSeleccionar) {
        Carta carta = vista.getCarta();
        if (quiereSeleccionar) {
            if (!puedeAgregarCarta()) {
                vista.setSeleccionada(false);
                return;
            }
            seleccionadas.add(carta);
        } else {
            seleccionadas.remove(carta);
        }
        actualizarEstadoBoton();
    }

    private boolean puedeAgregarCarta() {
        int maximo = obtenerMaximoSeleccionables();
        if (maximo < 0) return true;
        return seleccionadas.size() < maximo;
    }

    private int obtenerMaximoSeleccionables() {
        if (santo.cartasRequeridas() == -1) return santo.maxCartasSeleccionables();
        return santo.cartasRequeridas();
    }

    private boolean seleccionValida() {
        int requeridas = santo.cartasRequeridas();
        if (requeridas == 0) return seleccionadas.isEmpty();
        if (requeridas == -1) return !seleccionadas.isEmpty();
        return seleccionadas.size() == requeridas;
    }

    private void actualizarEstadoBoton() {
        botonConfirmar.setHabilitado(seleccionValida());
    }

    private void confirmar() {
        if (!seleccionValida() || cerrando) return;
        resultadoPendiente = new ArrayList<>(seleccionadas);
        iniciarCierre();
    }

    private void iniciarCierre() {
        cerrando = true;
        animacionesPendientes = vistasCartas.size();
        if (animacionesPendientes == 0) {
            finalizarCierre();
            return;
        }
        for (VistaCarta vista : vistasCartas) {
            vista.animarHacia(DECK_X, DECK_Y, this::onCartaTerminoAnimacion);
        }
    }

    private void onCartaTerminoAnimacion() {
        animacionesPendientes--;
        if (animacionesPendientes <= 0) finalizarCierre();
    }

    private void finalizarCierre() {
        visible = false;
        cerrando = false;
        vistasCartas.clear();
        if (alConfirmar != null && resultadoPendiente != null) {
            Consumer<ArrayList<Carta>> callback = alConfirmar;
            ArrayList<Carta> resultado = resultadoPendiente;
            alConfirmar = null;
            resultadoPendiente = null;
            callback.accept(resultado);
        }
    }

    public void render(SpriteBatch batch, Main game) {
        if (!visible) return;
        batch.setColor(0f, 0f, 0f, 0.75f);
        batch.draw(game.getPixelBlanco(), 0, 0, 1280f, 720f);
        batch.setColor(Color.WHITE);
        game.getFuentePrincipal().draw(batch, santo.getNombre(), 500f, 660f);
        game.getFuentePrincipal().draw(batch, santo.getDescripcion(), 400f, 620f);

        VistaCarta arrastrado = gestorInput.getArrastrado();
        for (VistaCarta vista : vistasCartas) {
            if (vista != arrastrado) vista.render(batch, game);
        }
        if (arrastrado != null) arrastrado.render(batch, game); // arrastrada al final: queda por encima

        String texto = seleccionadas.size() + "/" + obtenerTextoMaximo();
        game.getFuentePrincipal().draw(batch, texto, 600f, 190f);
        botonConfirmar.render(batch);
        batch.setColor(Color.WHITE);
    }

    private String obtenerTextoMaximo() {
        if (santo.cartasRequeridas() == -1) return String.valueOf(santo.maxCartasSeleccionables());
        return String.valueOf(santo.cartasRequeridas());
    }

    public boolean estaVisible() { return visible; }

    public void cerrar() {
        visible = false;
        cerrando = false;
        seleccionadas.clear();
        vistasCartas.clear();
    }
}
