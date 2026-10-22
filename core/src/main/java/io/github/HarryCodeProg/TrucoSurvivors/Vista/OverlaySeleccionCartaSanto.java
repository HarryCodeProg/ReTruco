package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.AreaElementos;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorInputArrastrable;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorReordenamiento;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import java.util.ArrayList;
import java.util.function.Consumer;

public class OverlaySeleccionCartaSanto {
    private boolean visible = false;
    private Santo santo;
    private ArrayList<Carta> cartasDisponibles = new ArrayList<>();
    private ArrayList<Carta> seleccionadas = new ArrayList<>();
    private final ArrayList<VistaCarta> vistasCartas = new ArrayList<>();
    private TextureAtlas atlasCartas;
    private Consumer<ArrayList<Carta>> alConfirmar;
    private static final int MAX_CARTAS_MOSTRADAS = 10;
    private static final float CARTA_ANCHO = 95f;
    private static final float CARTA_ALTO = 135f;
    private static final float SEPARACION = 15f;
    private static final float Y_CARTAS = 250f;

    private static final float ANCHO_BOTON = 180f;
    private static final float ALTO_BOTON = 50f;
    private final Boton botonConfirmar;
    private final Boton botonOmitir; // NUEVO

    private static final float MARGEN_AREA = 220f;
    private final AreaElementos<VistaCarta> area =
        new AreaElementos<>(MARGEN_AREA, Y_CARTAS, 1280f - MARGEN_AREA * 2, CARTA_ALTO, CARTA_ANCHO, CARTA_ALTO, SEPARACION);
    private final GestorInputArrastrable<VistaCarta> gestorInput = new GestorInputArrastrable<>(vistasCartas);
    private final GestorReordenamiento gestorReordenamiento = new GestorReordenamiento();

    private static final float DECK_X = 1195f;
    private static final float DECK_Y = 130f;
    private boolean cerrando = false;
    private int animacionesPendientes = 0;
    private boolean esperandoFlips = false;
    private Runnable alTerminarFlipsYCerrar;

    // NUEVO: Variables para la animación de reparto inicial
    private boolean repartiendo = false;
    private float tiempoReparto = 0f;
    private int indiceReparto = 0;
    private static final float DELAY_REPARTO = 0.08f;
    private ArrayList<float[]> posicionesObjetivo = new ArrayList<>();

    private VistaCarta cartaHover = null;

    public OverlaySeleccionCartaSanto() {
        // Botón confirmar movido a la izquierda
        botonConfirmar = new Boton(450f, 120f, ANCHO_BOTON, ALTO_BOTON,
            Boton.TipoColor.VERDE, io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion.CONFIRMAR_SELECCION_SANTO
        );
        botonConfirmar.setHabilitado(false);

        // NUEVO: Botón Omitir a la derecha
        botonOmitir = new Boton(650f, 120f, ANCHO_BOTON, ALTO_BOTON,
            Boton.TipoColor.BORDO, io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion.OPCIONES
        );
        botonOmitir.setTexto("OMITIR");
        botonOmitir.setHabilitado(true);
    }

    public void abrir(Santo santo, ArrayList<Carta> cartas, TextureAtlas atlasCartas,
                      Consumer<ArrayList<Carta>> alConfirmar) {
        this.santo = santo;
        this.atlasCartas = atlasCartas;
        this.alConfirmar = alConfirmar;
        this.seleccionadas.clear();
        this.vistasCartas.clear();
        this.cerrando = false;
        this.esperandoFlips = false;
        this.cartasDisponibles = new ArrayList<>(cartas);
        botonOmitir.setHabilitado(true);
        botonConfirmar.setHabilitado(false);
        construirVistas();
        this.repartiendo = true;
        this.tiempoReparto = 0f;
        this.indiceReparto = 0;
        visible = true;
        actualizarEstadoBotones();
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

        area.distribuir(vistasCartas, null);

        // NUEVO: Guardamos las posiciones finales y mandamos todas las cartas al mazo
        posicionesObjetivo.clear();
        for (VistaCarta v : vistasCartas) {
            posicionesObjetivo.add(new float[]{v.getHandTargetX(), Y_CARTAS});
            v.setPosition(DECK_X, DECK_Y);
            v.setHandPosition(DECK_X, DECK_Y); // El target también va al mazo
        }
    }

    public void update(float mouseWorldX, float mouseWorldY, float delta) {
        if (!visible) return;

        if (repartiendo) {
            tiempoReparto += delta;
            if (tiempoReparto >= DELAY_REPARTO) {
                tiempoReparto = 0f;
                if (indiceReparto < vistasCartas.size()) {
                    float[] pos = posicionesObjetivo.get(indiceReparto);
                    vistasCartas.get(indiceReparto).setHandPosition(pos[0], pos[1]);
                    GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
                    if (sonidos != null) sonidos.reproducirSonidoReparto();
                    indiceReparto++;
                } else {
                    repartiendo = false;
                }
            }
            for (VistaCarta vista : vistasCartas) {
                vista.update(mouseWorldX, mouseWorldY, delta);
            }
        }
        else if (cerrando) {
            for (VistaCarta vista : new ArrayList<>(vistasCartas)) {
                vista.update(mouseWorldX, mouseWorldY, delta);
            }
            return;
        }
        else if (esperandoFlips) {
            for (VistaCarta vista : new ArrayList<>(vistasCartas)) {
                vista.update(mouseWorldX, mouseWorldY, delta);
            }
            if (!hayAlgunaCartaFlipeando()) {
                esperandoFlips = false;
                iniciarCierre();
            }
            return;
        }
        else {
            // Update normal
            VistaCarta arrastradoAntes = gestorInput.getArrastrado();
            gestorInput.update(mouseWorldX, mouseWorldY, delta, true);
            for (VistaCarta v : vistasCartas) v.update(mouseWorldX, mouseWorldY, delta);
            boolean cambio = gestorReordenamiento.previsualizarReordenamiento(gestorInput, vistasCartas);
            if (cambio) reorganizar();
            if (arrastradoAntes != null && gestorInput.getArrastrado() == null) reorganizar();
            for (VistaCarta vista : vistasCartas) {
                boolean quiereSeleccionar = vista.isSeleccionada();
                boolean yaEstaba = seleccionadas.contains(vista.getCarta());
                if (quiereSeleccionar != yaEstaba) sincronizarSeleccion(vista, quiereSeleccionar);
            }
        }

        // UI Updates (siempre corren)
        botonConfirmar.update(mouseWorldX, mouseWorldY);
        botonOmitir.update(mouseWorldX, mouseWorldY);
        actualizarEstadoBotones();
        if (botonConfirmar.fueCliqueado(mouseWorldX, mouseWorldY)) {
            confirmar();
        }

        if (botonOmitir.fueCliqueado(mouseWorldX, mouseWorldY)) {
            omitirAnimacionYConfirmar();
        }
        cartaHover = null;
        for (VistaCarta vista : vistasCartas) {
            if (vista.isHover()) {
                cartaHover = vista;
                break;
            }
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
        actualizarEstadoBotones();
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

    private void actualizarEstadoBotones() {
        boolean valida = seleccionValida();
        botonConfirmar.setHabilitado(valida);
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
        if (arrastrado != null) arrastrado.render(batch, game);
        String texto = seleccionadas.size() + "/" + obtenerTextoMaximo();
        game.getFuentePrincipal().draw(batch, texto, 600f, 190f);
        botonConfirmar.render(batch);
        botonOmitir.render(batch);
        if (cartaHover != null) {
            cartaHover.renderCartelStats(batch, game);
        }
        batch.setColor(Color.WHITE);
    }

    private String obtenerTextoMaximo() {
        if (santo.cartasRequeridas() == -1) return String.valueOf(santo.maxCartasSeleccionables());
        return String.valueOf(santo.cartasRequeridas());
    }

    public boolean estaVisible() { return visible; }

    public VistaCarta buscarVistaPorCarta(Carta carta) {
        for (VistaCarta v : vistasCartas) {
            if (v.getCarta() == carta) return v;
        }
        return null;
    }

    private void confirmar() {
        if (!seleccionValida() || cerrando || esperandoFlips) return;
        ArrayList<Carta> resultado = new ArrayList<>(seleccionadas);
        botonConfirmar.setHabilitado(false);
        botonOmitir.setHabilitado(false);
        esperandoFlips = true;
        if (alConfirmar != null) {
            alConfirmar.accept(resultado);
        }
    }

    private void omitirAnimacionYConfirmar() {
        if (cerrando || esperandoFlips) return;
        // Si no es válida, cancela el uso del santo
        if (!seleccionValida()) {
            visible = false;
            cerrando = false;
            esperandoFlips = false;
            repartiendo = false;
            return;
        }
        // Si es válida, saltea la animación y usa el santo
        ArrayList<Carta> resultado = new ArrayList<>(seleccionadas);
        repartiendo = false;
        cerrando = false;
        esperandoFlips = false;
        visible = false;
        if (alConfirmar != null) {
            alConfirmar.accept(resultado);
        }
    }

    public void esperarFlipsYLuegoVolver(Runnable alCerrarFinal) {
        this.alTerminarFlipsYCerrar = alCerrarFinal;
        this.esperandoFlips = true;
    }

    private boolean hayAlgunaCartaFlipeando() {
        for (VistaCarta v : vistasCartas) {
            if (v.isFlipeando()) return true;
        }
        return false;
    }

    private void iniciarCierre() {
        cerrando = true;
        animacionesPendientes = vistasCartas.size();
        if (animacionesPendientes == 0) {
            finalizarCierre();
            return;
        }
        // Sonido al momento de cerrar
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        if (sonidos != null) sonidos.reproducirSonidoReparto();
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
        esperandoFlips = false;
        vistasCartas.clear();
        alConfirmar = null;
        Runnable callback = alTerminarFlipsYCerrar;
        alTerminarFlipsYCerrar = null;
        if (callback != null) callback.run();
    }
}
