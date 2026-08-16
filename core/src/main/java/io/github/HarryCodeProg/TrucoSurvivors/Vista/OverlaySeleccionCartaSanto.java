package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        this.cartasDisponibles = new ArrayList<>(cartas);
        construirVistas();
        visible = true;
        actualizarEstadoBoton();
    }

    private void construirVistas() {
        int cantidad = Math.min(MAX_CARTAS_MOSTRADAS, cartasDisponibles.size());
        float anchoTotal = cantidad * CARTA_ANCHO + (cantidad - 1) * SEPARACION;
        float xInicial = (Gdx.graphics.getWidth() - anchoTotal) / 2f;
        for (int i = 0; i < cantidad; i++) {
            Carta carta = cartasDisponibles.get(i);
            VistaCarta vista = new VistaCarta(carta, false, atlasCartas);
            vista.setTamaño(CARTA_ANCHO, CARTA_ALTO);
            vista.setPosition(
                xInicial + i * (CARTA_ANCHO + SEPARACION),
                Y_CARTAS
            );
            vistasCartas.add(vista);
        }
    }

    public void update(float mouseWorldX, float mouseWorldY, float delta) {
        if (!visible) return;
        for (VistaCarta vista : vistasCartas) {
            vista.update(mouseWorldX, mouseWorldY, delta);
        }
        botonConfirmar.update(mouseWorldX, mouseWorldY);
        if (Gdx.input.justTouched()) {
            for (VistaCarta vista : vistasCartas) {
                if (!vista.contiene(mouseWorldX, mouseWorldY)) {
                    continue;
                }
                alternarSeleccion(vista);
                break;
            }
            if (botonConfirmar.fueCliqueado(mouseWorldX, mouseWorldY)) {
                confirmar();
            }
        }
    }

    private void alternarSeleccion(VistaCarta vista) {
        Carta carta = vista.getCarta();
        if (seleccionadas.contains(carta)) {
            seleccionadas.remove(carta);
            vista.setSeleccionada(false);
        } else {
            if (!puedeAgregarCarta()) {
                return;
            }
            seleccionadas.add(carta);
            vista.setSeleccionada(true);
        }
        actualizarEstadoBoton();
    }

    private boolean puedeAgregarCarta() {
        int maximo = obtenerMaximoSeleccionables();
        if (maximo < 0) {
            return true;
        }
        return seleccionadas.size() < maximo;
    }

    private int obtenerMaximoSeleccionables() {
        if (santo.cartasRequeridas() == -1) {
            return santo.maxCartasSeleccionables();
        }
        return santo.cartasRequeridas();
    }

    private boolean seleccionValida() {
        int requeridas = santo.cartasRequeridas();
        if (requeridas == 0) {
            return seleccionadas.isEmpty();
        }
        if (requeridas == -1) {
            return !seleccionadas.isEmpty();
        }
        return seleccionadas.size() == requeridas;
    }

    private void actualizarEstadoBoton() {
        botonConfirmar.setHabilitado(seleccionValida());
    }

    private void confirmar() {
        if (!seleccionValida()) {
            return;
        }
        ArrayList<Carta> resultado = new ArrayList<>(seleccionadas);
        visible = false;
        if (alConfirmar != null) {
            alConfirmar.accept(resultado);
        }
    }

    public void render(SpriteBatch batch, Main game) {
        if (!visible) return;
        batch.setColor(0f, 0f, 0f, 0.75f);
        batch.draw(game.getPixelBlanco(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
        // Título
        game.getFuentePrincipal().draw(batch, santo.getNombre(), 500f, 660f);
        game.getFuentePrincipal().draw(batch, santo.getDescripcion(), 400f, 620f);
        // Cartas
        for (VistaCarta vista : vistasCartas) {
            vista.render(batch, game);
        }
        // Contador
        String texto = seleccionadas.size() + "/" + obtenerTextoMaximo();
        game.getFuentePrincipal().draw(batch, texto, 600f, 190f);
        botonConfirmar.render(batch);
        batch.setColor(Color.WHITE);
    }

    private String obtenerTextoMaximo() {
        if (santo.cartasRequeridas() == -1) {
            return String.valueOf(santo.maxCartasSeleccionables());
        }
        return String.valueOf(santo.cartasRequeridas());
    }

    public boolean estaVisible() {
        return visible;
    }

    public void cerrar() {
        visible = false;
        seleccionadas.clear();
        vistasCartas.clear();
    }
}
