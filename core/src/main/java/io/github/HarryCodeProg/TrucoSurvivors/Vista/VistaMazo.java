package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.AreaElementos;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorInputArrastrable;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorReordenamiento;
import io.github.HarryCodeProg.TrucoSurvivors.Main;

import java.util.ArrayList;
import java.util.List;

public class VistaMazo {
    private final TextureRegion dorso;
    private final TextureAtlas atlasCartas;
    private final BitmapFont font;
    private final Texture pixelBlanco;
    private boolean isHovered; //  Guardamos el estado del hover
    // Posición y tamaño del mazo interactivo (Abajo a la derecha)
    private final float x, y, width, height;
    private final Rectangle boundsMazo;
    // Estado de la ventana emergente
    private boolean modalAbierto = false;
    // Botón "Atrás" dentro de la ventana
    private final Rectangle boundsBotonAtras;
    private boolean hoverAtras = false;
    private List<VistaCarta> cartasModal = new ArrayList<>();
    private List<Carta> cartasModalRestantes = new ArrayList<>();
    private final java.util.Map<Palo, ArrayList<VistaCarta>> filasModal = new java.util.EnumMap<>(Palo.class);
    private final java.util.Map<Palo, GestorInputArrastrable<VistaCarta>> gestoresFilas = new java.util.EnumMap<>(Palo.class);
    private final java.util.Map<Palo, AreaElementos<VistaCarta>> areasFilas = new java.util.EnumMap<>(Palo.class);
    private final GestorReordenamiento gestorReordenamiento = new GestorReordenamiento();
    private static final float MARCO_W = 1000f;
    private static final float MARCO_H = 580f;
    private static final float MARCO_X = (1280 - MARCO_W) / 2f;
    private static final float MARCO_Y = (720 - MARCO_H) / 2f;


    public VistaMazo(float x, float y, float width, float height, TextureAtlas atlasCartas, BitmapFont font, Texture pixelBlanco) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.atlasCartas = atlasCartas;
        this.dorso = atlasCartas.findRegion("back");
        this.font = font;
        this.pixelBlanco = pixelBlanco;
        this.boundsMazo = new Rectangle(x, y, width, height);
        // Botón Atrás centrado en la parte inferior de la ventana
        float anchoBoton = 140f;
        float altoBoton = 40f;
        this.boundsBotonAtras = new Rectangle(1280 / 2f - anchoBoton / 2f, 80f, anchoBoton, altoBoton);
    }

    public void update(float mouseX, float mouseY) {
        if (modalAbierto) {
            hoverAtras = boundsBotonAtras.contains(mouseX, mouseY);
            float delta = Gdx.graphics.getDeltaTime();
            for (Palo palo : filasModal.keySet()) {
                ArrayList<VistaCarta> fila = filasModal.get(palo);
                io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorInputArrastrable<VistaCarta> gestor = gestoresFilas.get(palo);
                io.github.HarryCodeProg.TrucoSurvivors.Gestores.AreaElementos<VistaCarta> area = areasFilas.get(palo);
                VistaCarta arrastradoAntes = gestor.getArrastrado();
                gestor.update(mouseX, mouseY, delta, true);
                for (VistaCarta vc : fila) {
                    vc.update(mouseX, mouseY, delta);
                    vc.setSeleccionada(false); // en este modal no hay selección, solo visualización
                }
                boolean cambio = gestorReordenamiento.previsualizarReordenamiento(gestor, fila);
                if (cambio) area.distribuir(fila, gestor.getArrastrado());
                if (arrastradoAntes != null && gestor.getArrastrado() == null) area.distribuir(fila, null);
            }
            isHovered = false;
        } else {
            isHovered = boundsMazo.contains(mouseX, mouseY);
            hoverAtras = false;
        }
    }

    public boolean tocar(float mouseX, float mouseY) {
        if (modalAbierto) {
            if (boundsBotonAtras.contains(mouseX, mouseY)) {
                modalAbierto = false;
                return true;
            }
            return true;
        } else {
            if (boundsMazo.contains(mouseX, mouseY)) {
                modalAbierto = true;
                cartasModal.clear();
                filasModal.clear();
                gestoresFilas.clear();
                areasFilas.clear();
                Palo[] palos = {Palo.ESPADA, Palo.BASTO, Palo.ORO, Palo.COPA};
                float startX = MARCO_X + 80f;
                float startY = MARCO_Y + MARCO_H - 160f;
                float cartaW = 54f, cartaH = 78f, gapX = 14f, gapY = 40f;
                float anchoDisponible = (MARCO_X + MARCO_W - 20f) - startX; // hasta cerca del borde derecho del marco
                for (int fila = 0; fila < palos.length; fila++) {
                    Palo paloActual = palos[fila];
                    float currentY = startY - (fila * (cartaH + gapY));
                    ArrayList<VistaCarta> filaLista = new ArrayList<>();
                    for (Carta c : cartasModalRestantes) {
                        if (c.getPalo() != paloActual) continue;
                        VistaCarta vc = new VistaCarta(c, false, atlasCartas);
                        vc.setTamaño(cartaW, cartaH);
                        vc.setEnModal(true); // <-- FIX: hover individual correcto dentro del modal
                        filaLista.add(vc);
                        cartasModal.add(vc);
                    }
                    AreaElementos<VistaCarta> area = new AreaElementos<>(startX, currentY, anchoDisponible, cartaH, cartaW, cartaH, gapX);
                    area.distribuir(filaLista, null); // posiciona target correctamente (no esquina 0,0)
                    for (VistaCarta vc : filaLista) {
                        vc.setPosition(vc.getHandTargetX(), currentY); // instantáneo al abrir
                        vc.setSeleccionada(false);
                    }
                    filasModal.put(paloActual, filaLista);
                    areasFilas.put(paloActual, area);
                    gestoresFilas.put(paloActual, new GestorInputArrastrable<>(filaLista));
                }
                return true;
            }
        }
        return false;
    }

    public void render(SpriteBatch batch, List<Carta> cartasRestantes, int totalInicial) {
        this.cartasModalRestantes = cartasRestantes;
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
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, textoMazo);
        font.draw(batch, textoMazo, x + (width / 2f) - (layout.width / 2f), y - 10f);
    }

    public void renderModalSiCorresponde(SpriteBatch batch) {
        if (modalAbierto) {
            dibujarVentanaModal(batch, cartasModalRestantes);
        }
    }

    private void dibujarVentanaModal(SpriteBatch batch, List<Carta> cartasRestantes) {
        if (pixelBlanco == null) return;
        // Overlay oscuro
        batch.setColor(0f, 0f, 0f, 0.75f);
        batch.draw(pixelBlanco, 0, 0, 1280, 720);
        // Marco central
        float marcoW = 1000f;
        float marcoH = 580f;
        float marcoX = (1280 - marcoW) / 2f;
        float marcoY = (720 - marcoH) / 2f;
        batch.setColor(0.08f, 0.09f, 0.12f, 0.95f);
        batch.draw(pixelBlanco, marcoX, marcoY, marcoW, marcoH);
        // Borde
        batch.setColor(0.35f, 0.4f, 0.55f, 0.9f);
        float grosor = 2f;
        batch.draw(pixelBlanco, marcoX, marcoY, marcoW, grosor);
        batch.draw(pixelBlanco, marcoX, marcoY + marcoH - grosor, marcoW, grosor);
        batch.draw(pixelBlanco, marcoX, marcoY, grosor, marcoH);
        batch.draw(pixelBlanco, marcoX + marcoW - grosor, marcoY, grosor, marcoH);
        // Título
        font.setColor(Color.GOLD);
        font.draw(batch, "CARTAS RESTANTES EN EL MAZO", marcoX, marcoY + marcoH - 20f, marcoW, Align.center, false);
        // -------------------------------------------------------------
        // DIBUJAR CARTAS INTERACTIVAS YA CREADAS (VistaCarta)
        // -------------------------------------------------------------
        for (VistaCarta vc : cartasModal) {
            vc.render(batch, Main.getInstance());
        }
        // -------------------------------------------------------------
        // BOTÓN ATRÁS
        // -------------------------------------------------------------
        if (hoverAtras) {
            batch.setColor(0.8f, 0.2f, 0.2f, 0.9f);
        } else {
            batch.setColor(0.5f, 0.15f, 0.15f, 0.9f);
        }
        batch.draw(pixelBlanco, boundsBotonAtras.x, boundsBotonAtras.y,
            boundsBotonAtras.width, boundsBotonAtras.height);
        GlyphLayout layout = new GlyphLayout(font, "ATRÁS");
        font.setColor(Color.WHITE);
        font.draw(batch, "ATRÁS",
            boundsBotonAtras.x,
            boundsBotonAtras.y + boundsBotonAtras.height / 2f + layout.height / 2f,
            boundsBotonAtras.width, Align.center, false);
        batch.setColor(1f, 1f, 1f, 1f);
    }



    public boolean isModalAbierto() {
        return modalAbierto;
    }
}
