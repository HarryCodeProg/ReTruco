package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.*;

public class VistaMazo {
    private final BitmapFont fontTitulo;
    private int totalInicialModal = 40;
    private static final float CARTA_W = 52f, CARTA_H = 74f, GAP_X = 8f, GAP_Y = 6f;
    private static final float SEPARACION_PALOS = 12f;
    private static final float ALTO_ETIQUETA_PALO = 22f;
    private static final float MARCO_W = 1040f;
    private static final float MARCO_H = 600f;
    private static final float MARCO_X = (1280 - MARCO_W) / 2f;
    private static final float MARCO_Y = (720 - MARCO_H) / 2f;
    private static final float ALTURA_TITULO = 56f;
    private static final float GROSOR_BORDE = 5f;
    private static final Palo[] PALOS = {Palo.ESPADA, Palo.BASTO, Palo.ORO, Palo.COPA};
    private static final String[] NOMBRES_PALO = {"ESPADA", "BASTO", "ORO", "COPA"};
    private final Boton botonAtras;
    private final Rectangle areaContenido;
    private final TextureRegion dorso;
    private final TextureAtlas atlasCartas;
    private final BitmapFont font;
    private final BitmapFont fontNumeros;
    private final Texture pixelBlanco;
    private boolean isHovered;
    private final float x, y, width, height;
    private final Rectangle boundsMazo;
    private boolean modalAbierto = false;
    private final OrthographicCamera camera;
    private List<Carta> cartasModalRestantes = new ArrayList<>();
    private static class GrupoCarta {
        final Carta representante;
        final int cantidad;
        GrupoCarta(Carta representante, int cantidad) {
            this.representante = representante;
            this.cantidad = cantidad;
        }
    }
    private final Map<Palo, ArrayList<GrupoCarta>> gruposPorPalo = new LinkedHashMap<>();
    private final Map<Palo, ArrayList<Rectangle>> slotsPorPalo = new LinkedHashMap<>();
    private final Map<String, VistaCarta> cacheVistaCartas = new HashMap<>();
    private final Map<Palo, ArrayList<VistaCarta>> vistaCartasPorPalo = new LinkedHashMap<>();
    private VistaCarta cartaConTooltipPendiente = null;
    private float lastMouseX = -1f, lastMouseY = -1f;
    private float scrollY = 0f;
    private float contenidoAltoTotal = 0f;

    public VistaMazo(float x, float y, float width, float height, TextureAtlas atlasCartas, BitmapFont font, Texture pixelBlanco, OrthographicCamera camera) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.atlasCartas = atlasCartas;
        this.dorso = atlasCartas.findRegion("back");
        this.font = font;
        this.fontNumeros = Main.getInstance() != null ? Main.getInstance().getFuenteNumeros() : font;
        this.fontTitulo = Main.getInstance() != null ? Main.getInstance().getFuenteTitulo() : font;
        this.pixelBlanco = pixelBlanco;
        this.boundsMazo = new Rectangle(x, y, width, height);
        this.camera = camera;
        float anchoBoton = 180f;
        float altoBoton = 48f;
        Rectangle boundsBotonAtras = new Rectangle(1280 / 2f - anchoBoton / 2f, MARCO_Y + 24f, anchoBoton, altoBoton);
        this.botonAtras = new Boton(boundsBotonAtras.x, boundsBotonAtras.y, boundsBotonAtras.width, boundsBotonAtras.height, "ATRÁS", Boton.TipoColor.BORDO, Accion.OPCIONES);

        float botonAbajoY = MARCO_Y + 24f + altoBoton + 16f;
        this.areaContenido = new Rectangle(
            MARCO_X + GROSOR_BORDE, botonAbajoY,
            MARCO_W - GROSOR_BORDE * 2, MARCO_H - ALTURA_TITULO - GROSOR_BORDE - (botonAbajoY - MARCO_Y)
        );
    }

    public void update(float mouseX, float mouseY) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        if (modalAbierto) {
            botonAtras.update(mouseX, mouseY);
            isHovered = false;
            if (botonAtras.consumirClick()) {
                modalAbierto = false;
                limpiarCacheVistas();
            }
        } else {
            isHovered = boundsMazo.contains(mouseX, mouseY);
        }
    }

    public boolean tocar(float mouseX, float mouseY) {
        if (modalAbierto) {
            return true;
        } else {
            if (boundsMazo.contains(mouseX, mouseY) && Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                modalAbierto = true;
                return true;
            }
        }
        return false;
    }

    private void limpiarCacheVistas() {
        cacheVistaCartas.clear();
        vistaCartasPorPalo.clear();
        cartaConTooltipPendiente = null;
    }

    private void recalcularLayout() {
        gruposPorPalo.clear();
        slotsPorPalo.clear();
        vistaCartasPorPalo.clear();
        for (Palo palo : PALOS) {
            LinkedHashMap<Integer, GrupoCarta> porNumero = new LinkedHashMap<>();
            for (Carta c : cartasModalRestantes) {
                if (c.getPalo() != palo) continue;
                int num = c.getNumero();
                GrupoCarta existente = porNumero.get(num);
                if (existente == null) {
                    porNumero.put(num, new GrupoCarta(c, 1));
                } else {
                    porNumero.put(num, new GrupoCarta(existente.representante, existente.cantidad + 1));
                }
            }
            ArrayList<GrupoCarta> lista = new ArrayList<>(porNumero.values());
            lista.sort(Comparator.comparingInt(g -> g.representante.getNumero()));
            gruposPorPalo.put(palo, lista);
        }
        float anchoDisponible = areaContenido.width - 20f;
        int porFila = Math.max(1, (int) ((anchoDisponible + GAP_X) / (CARTA_W + GAP_X)));
        Set<String> vistos = new HashSet<>();
        float cursorY = areaContenido.y + areaContenido.height - 10f; // Pequeño margen superior extra
        for (Palo palo : PALOS) {
            ArrayList<GrupoCarta> grupos = gruposPorPalo.get(palo);
            int filas = grupos.isEmpty() ? 1 : (int) Math.ceil(grupos.size() / (double) porFila);
            ArrayList<Rectangle> slots = new ArrayList<>();
            ArrayList<VistaCarta> vistasFila = new ArrayList<>();
            cursorY -= ALTO_ETIQUETA_PALO;
            int cantidadEnFila = Math.min(porFila, grupos.size());
            float anchoFila = cantidadEnFila * CARTA_W + (cantidadEnFila - 1) * GAP_X;
            float inicioFilaX = areaContenido.x + (areaContenido.width - anchoFila) / 2f;
            for (int i = 0; i < grupos.size(); i++) {
                GrupoCarta g = grupos.get(i);
                int fila = i / porFila;
                int col = i % porFila;
                float inicioX = fila == 0 ? inicioFilaX : areaContenido.x + (areaContenido.width - Math.min(porFila, grupos.size() - fila * porFila) * CARTA_W - (Math.min(porFila, grupos.size() - fila * porFila) - 1) * GAP_X) / 2f;
                float slotX = inicioX + col * (CARTA_W + GAP_X);
                float slotY = cursorY - CARTA_H - fila * (CARTA_H + GAP_Y);
                slots.add(new Rectangle(slotX, slotY, CARTA_W, CARTA_H));
                String key = palo.name() + "_" + g.representante.getNumero();
                VistaCarta vc = cacheVistaCartas.get(key);
                if (vc == null) {
                    vc = new VistaCarta(g.representante, false, atlasCartas);
                    vc.setTamaño(CARTA_W, CARTA_H);
                    vc.setEnModal(true);
                    cacheVistaCartas.put(key, vc);
                }
                vistasFila.add(vc);
                vistos.add(key);
            }
            cursorY -= filas * (CARTA_H + GAP_Y);
            cursorY -= SEPARACION_PALOS;
            slotsPorPalo.put(palo, slots);
            vistaCartasPorPalo.put(palo, vistasFila);
        }
        cacheVistaCartas.keySet().retainAll(vistos);
    }

    public void render(SpriteBatch batch, List<Carta> cartasRestantes, int totalInicial) {
        this.cartasModalRestantes = cartasRestantes;
        this.totalInicialModal = totalInicial;
        renderMazoFisico(batch, cartasRestantes, totalInicial);
    }

    private void renderMazoFisico(SpriteBatch batch, List<Carta> cartasRestantes, int totalInicial) {
        int cantidad = cartasRestantes.size();
        int cartasVisiblesEfecto = Math.min(cantidad, 4);
        if (isHovered) batch.setColor(0.7f, 0.7f, 0.7f, 1f); else batch.setColor(Color.WHITE);
        for (int i = 0; i < cartasVisiblesEfecto; i++) {
            float offsetY = i * 2f;
            float offsetX = i * 1f;
            batch.draw(dorso, x + offsetX, y + offsetY, width, height);
        }
        batch.setColor(Color.WHITE);
        String textoMazo = cantidad + "/" + totalInicial;
        fontNumeros.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(fontNumeros, textoMazo);
        fontNumeros.draw(batch, textoMazo, x + (width / 2f) - (layout.width / 2f), y - 10f);
    }

    public void renderModalSiCorresponde(SpriteBatch batch) {
        if (modalAbierto) {
            recalcularLayout();
            dibujarVentanaModal(batch, cartasModalRestantes);
        }
    }

    private void dibujarVentanaModal(SpriteBatch batch, List<Carta> cartasRestantes) {
        if (pixelBlanco == null) return;
        cartaConTooltipPendiente = null;
        float delta = Gdx.graphics.getDeltaTime();
        batch.setColor(0f, 0f, 0f, 0.78f);
        batch.draw(pixelBlanco, 0, 0, 1280, 720);
        batch.setColor(UITheme.SOMBRA);
        batch.draw(pixelBlanco, MARCO_X - 6f, MARCO_Y - 10f, MARCO_W + 12f, MARCO_H + 12f);
        batch.setColor(UITheme.PANEL_PRINCIPAL);
        batch.draw(pixelBlanco, MARCO_X, MARCO_Y, MARCO_W, MARCO_H);
        batch.setColor(UITheme.BORDE);
        batch.draw(pixelBlanco, MARCO_X, MARCO_Y, MARCO_W, GROSOR_BORDE);
        batch.draw(pixelBlanco, MARCO_X, MARCO_Y + MARCO_H - GROSOR_BORDE, MARCO_W, GROSOR_BORDE);
        batch.draw(pixelBlanco, MARCO_X, MARCO_Y, GROSOR_BORDE, MARCO_H);
        batch.draw(pixelBlanco, MARCO_X + MARCO_W - GROSOR_BORDE, MARCO_Y, GROSOR_BORDE, MARCO_H);
        batch.setColor(UITheme.PANEL_SECUNDARIO);
        batch.draw(pixelBlanco, MARCO_X + GROSOR_BORDE, MARCO_Y + MARCO_H - ALTURA_TITULO - GROSOR_BORDE, MARCO_W - GROSOR_BORDE * 2, ALTURA_TITULO);
        batch.setColor(UITheme.BORDE);
        batch.draw(pixelBlanco, MARCO_X + GROSOR_BORDE, MARCO_Y + MARCO_H - ALTURA_TITULO - GROSOR_BORDE - 3f, MARCO_W - GROSOR_BORDE * 2, 3f);
        String titulo = "MAZO";
        float escalaX = fontTitulo.getData().scaleX;
        float escalaY = fontTitulo.getData().scaleY;
        fontTitulo.getData().setScale(escalaX * 1.25f, escalaY * 1.25f);
        GlyphLayout layoutTitulo = new GlyphLayout(fontTitulo, titulo);
        float tituloX = MARCO_X + MARCO_W / 2f - layoutTitulo.width / 2f;
        float tituloY = MARCO_Y + MARCO_H - 20f;
        fontTitulo.setColor(Color.BLACK);
        fontTitulo.draw(batch, titulo, tituloX - 3f, tituloY);
        fontTitulo.draw(batch, titulo, tituloX + 3f, tituloY);
        fontTitulo.draw(batch, titulo, tituloX, tituloY - 3f);
        fontTitulo.draw(batch, titulo, tituloX, tituloY + 3f);
        fontTitulo.setColor(UITheme.DORADO);
        fontTitulo.draw(batch, titulo, tituloX, tituloY);
        fontTitulo.getData().setScale(escalaX, escalaY);
        fontNumeros.setColor(UITheme.TEXTO_PRINCIPAL);
        Rectangle clipBounds = new Rectangle(areaContenido);
        Rectangle scissors = new Rectangle();
        batch.flush();
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
        boolean pushed = ScissorStack.pushScissors(scissors);
        for (int fila = 0; fila < PALOS.length; fila++) {
            Palo palo = PALOS[fila];
            ArrayList<GrupoCarta> grupos = gruposPorPalo.get(palo);
            ArrayList<Rectangle> slots = slotsPorPalo.get(palo);
            ArrayList<VistaCarta> vistas = vistaCartasPorPalo.get(palo);
            if (slots.isEmpty()) continue;
            float etiquetaY = slots.get(0).y + CARTA_H + ALTO_ETIQUETA_PALO - 8f;
            font.setColor(UITheme.TEXTO_SECUNDARIO);
            font.draw(batch, NOMBRES_PALO[fila] + "  (" + contarCartas(grupos) + ")", areaContenido.x + 10f, etiquetaY);
            for (int i = 0; i < grupos.size(); i++) {
                GrupoCarta g = grupos.get(i);
                Rectangle r = slots.get(i);
                VistaCarta vc = vistas.get(i);
                float drawY = r.y;
                vc.setHandPosition(r.x, drawY);
                vc.setPosition(r.x, drawY);
                boolean mouseDentroDelArea = areaContenido.contains(lastMouseX, lastMouseY);
                vc.update(mouseDentroDelArea ? lastMouseX : -9999f, mouseDentroDelArea ? lastMouseY : -9999f, delta);
                vc.render(batch, Main.getInstance());
                if (vc.isHover()) {
                    cartaConTooltipPendiente = vc;
                }
                if (g.cantidad > 1) {
                    String txt = "x" + g.cantidad;
                    float badgeW = 26f, badgeH = 18f;
                    float badgeX = r.x + CARTA_W - badgeW + 4f;
                    float badgeY = drawY - 4f;
                    batch.setColor(UITheme.ROJO);
                    batch.draw(pixelBlanco, badgeX, badgeY, badgeW, badgeH);
                    batch.setColor(UITheme.BORDE);
                    batch.draw(pixelBlanco, badgeX, badgeY, badgeW, 2f);
                    batch.draw(pixelBlanco, badgeX, badgeY + badgeH - 2f, badgeW, 2f);
                    batch.draw(pixelBlanco, badgeX, badgeY, 2f, badgeH);
                    batch.draw(pixelBlanco, badgeX + badgeW - 2f, badgeY, 2f, badgeH);
                    fontNumeros.setColor(UITheme.TEXTO_PRINCIPAL);
                    GlyphLayout gl = new GlyphLayout(fontNumeros, txt);
                    fontNumeros.draw(batch, txt, badgeX + badgeW / 2f - gl.width / 2f, badgeY + badgeH / 2f + gl.height / 2f);
                }
            }
            if (fila < PALOS.length - 1 && !slots.isEmpty()) {
                float lineaY = slots.get(slots.size() - 1).y - 6f; // Ajustado al nuevo margen
                batch.setColor(UITheme.BORDE);
                batch.draw(pixelBlanco, areaContenido.x + 24f, lineaY, areaContenido.width - 48f, 1f);
            }
        }
        batch.setColor(Color.WHITE);
        batch.flush();
        if (pushed) ScissorStack.popScissors();
        if (cartaConTooltipPendiente != null) {
            cartaConTooltipPendiente.renderCartelStats(batch, Main.getInstance());
        }
        botonAtras.render(batch);
    }

    private int contarCartas(ArrayList<GrupoCarta> grupos) {
        int total = 0;
        for (GrupoCarta g : grupos) total += g.cantidad;
        return total;
    }

    public boolean isModalAbierto() {
        return modalAbierto;
    }
}
