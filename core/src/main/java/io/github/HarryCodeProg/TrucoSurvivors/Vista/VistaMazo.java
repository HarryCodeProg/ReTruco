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

    // Actualiza el estado (detecta hover en botón atrás)
    public void update(float mouseX, float mouseY) {
        if (modalAbierto) {
            hoverAtras = boundsBotonAtras.contains(mouseX, mouseY);
            for (VistaCarta vc : cartasModal) {
                // Desactivar la lógica global de drag en el modal
                vc.update(mouseX, mouseY, Gdx.graphics.getDeltaTime());
                vc.input(mouseX, mouseY);
                vc.setSeleccionada(false);
            }
            isHovered = false;
        } else {
            isHovered = boundsMazo.contains(mouseX, mouseY);
            hoverAtras = false;
        }
    }

    public boolean tocar(float mouseX, float mouseY) {
        if (modalAbierto) {
            // Si hace click en "Atrás" cerramos la ventana
            if (boundsBotonAtras.contains(mouseX, mouseY)) {
                modalAbierto = false;
                return true;
            }
            return true; // bloquea clicks al fondo mientras está abierto
        } else {
            // Si hace click en la pila del mazo, abrimos la ventana y creamos las cartas
            if (boundsMazo.contains(mouseX, mouseY)) {
                modalAbierto = true;
                cartasModal.clear();
                Palo[] palos = {Palo.ESPADA, Palo.BASTO, Palo.ORO, Palo.COPA};
                float startX = (1280 - 1000f) / 2f + 80f;
                float startY = (720 - 580f) / 2f + 580f - 160f;
                float cartaW = 54f, cartaH = 78f, gapX = 14f, gapY = 40f;
                int maxCols = 15;
                for (int fila = 0; fila < palos.length; fila++) {
                    Palo paloActual = palos[fila];
                    float currentY = startY - (fila * (cartaH + gapY));
                    int col = 0, rowExtra = 0;
                    for (Carta c : cartasModalRestantes) {
                        if (c.getPalo() == paloActual) {
                            float currentX = startX + (col * (cartaW + gapX));
                            float yCarta = currentY - (rowExtra * (cartaH + gapY));
                            VistaCarta vc = new VistaCarta(c, false, atlasCartas);
                            vc.setTamaño(cartaW, cartaH);
                            vc.setPosition(currentX, yCarta);
                            vc.setHandPosition(currentX, yCarta);
                            vc.volverAMano();
                            vc.setSeleccionada(false);
                            cartasModal.add(vc);

                            col++;
                            if (col >= maxCols) { col = 0; rowExtra++; }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }


    // Dibuja el mazo en la mesa + la ventana modal si está abierta
    public void render(SpriteBatch batch, List<Carta> cartasRestantes, int totalInicial) {
        this.cartasModalRestantes = cartasRestantes;
        int cantidad = cartasRestantes.size();
        int cartasVisiblesEfecto = Math.min(cantidad, 4);
        //  1. Si está en hover, le aplicamos un tinte más oscuro al batch
        if (isHovered) {
            batch.setColor(0.7f, 0.7f, 0.7f, 1f); // Oscurece al 70% de brillo
        } else {
            batch.setColor(Color.WHITE); // Normal
        }
        // 2. Dibujamos las cartas encimadas del mazo
        for (int i = 0; i < cartasVisiblesEfecto; i++) {
            float offsetY = i * 2f;
            float offsetX = i * 1f;
            batch.draw(dorso, x + offsetX, y + offsetY, width, height);
        }
        //  3. RESTAURAR el color normal del batch para no oscurecer el texto ni otros elementos
        batch.setColor(Color.WHITE);
        // 4. Texto con las cartas restantes (Ej: "36/52")
        String textoMazo = cantidad + "/" + totalInicial;
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, textoMazo);
        // Volvemos a posicionarlo ABAJO del mazo (y - 8f o -12f según el margen que quieras)
        font.draw(batch, textoMazo, x + (width / 2f) - (layout.width / 2f), y - 10f);
        // 5. Dibujar ventana modal si está abierta
        if (modalAbierto) {
            dibujarVentanaModal(batch, cartasRestantes);
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
