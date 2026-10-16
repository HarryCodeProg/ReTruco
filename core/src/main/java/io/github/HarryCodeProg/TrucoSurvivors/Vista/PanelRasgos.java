package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorRasgos;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Main;

import java.util.List;

public class PanelRasgos {
    private static final float ANCHO_PANEL   = 250f;
    private static final float ALTO_FILA     = 42f;
    private static final float GAP_FILAS     = 6f;
    private static final float BARRA_ANCHO   = 4f;
    private static final int   MAX_VISIBLES  = 5;
    private static final float ALTO_FLECHA   = 22f;
    private static final float ANCHO_FLECHA  = 40f;
    private static final float GAP_FLECHA    = 4f;
    private static final float CHAFLAN       = 8f;
    // Escalas del progreso
    private static final float ESCALA_NUM_NORMAL    = 0.90f;
    private static final float ESCALA_NUM_ACTUAL    = 1.05f;
    private static final float ESCALA_SEPARADOR     = 0.70f;
    private static final float GAP_ENTRE_NUMEROS    = 5f;
    private static final float MARGEN_DERECHO       = 10f;
    private static final float MARGEN_ENTRE_BADGE   = 8f;
    private int scrollIndex = 0;
    private boolean hoverArriba = false;
    private boolean hoverAbajo  = false;
    private float tiempoAnimacion = 0f;
    private final GlyphLayout layout = new GlyphLayout();
    private static final float RADIO_FILA = 12f;

    public PanelRasgos() { }

    public void update(float mx, float my, float delta, boolean click,
                       GestorRasgos gestor, float x, float yTop) {
        tiempoAnimacion += delta;
        List<CategoriaJoker> activas = gestor.getCategoriasActivas();
        int total = activas.size();
        if (total <= MAX_VISIBLES) {
            scrollIndex = 0; hoverArriba = false; hoverAbajo = false;
            return;
        }
        int maxScroll = total - MAX_VISIBLES;
        scrollIndex = Math.max(0, Math.min(scrollIndex, maxScroll));
        float panelAlto = MAX_VISIBLES * ALTO_FILA + (MAX_VISIBLES - 1) * GAP_FILAS;
        float flechaArribaY = yTop - ALTO_FLECHA;
        float flechaAbajoY  = yTop - (ALTO_FLECHA + GAP_FLECHA) - panelAlto - ALTO_FLECHA;
        float flechaX = x + (ANCHO_PANEL - ANCHO_FLECHA) / 2f;
        hoverArriba = dentroDeFlecha(mx, my, flechaX, flechaArribaY, ANCHO_FLECHA, ALTO_FLECHA);
        hoverAbajo  = dentroDeFlecha(mx, my, flechaX, flechaAbajoY, ANCHO_FLECHA, ALTO_FLECHA);
        if (click) {
            if (hoverArriba && scrollIndex > 0) scrollIndex--;
            if (hoverAbajo && scrollIndex < maxScroll) scrollIndex++;
        }
    }

    public void render(SpriteBatch batch, GestorRasgos gestor, Main game, float x, float yTop) {
        List<CategoriaJoker> activas = gestor.getCategoriasActivas();
        if (activas.isEmpty()) return;
        int total = activas.size();
        boolean hayPaginacion = total > MAX_VISIBLES;
        int inicio = scrollIndex;
        int fin = Math.min(total, inicio + MAX_VISIBLES);
        BitmapFont fNombre = game.getFuentePrincipal();
        Texture pixel = game.getPixelBlanco();
        float offsetArriba = hayPaginacion ? (ALTO_FLECHA + GAP_FLECHA) : 0f;
        float filaYTopBase = yTop - offsetArriba;
        if (hayPaginacion) {
            int maxScroll = total - MAX_VISIBLES;
            Color colorArriba = (scrollIndex > 0) ? new Color(0.85f, 0.85f, 0.90f, 1f)
                : new Color(0.35f, 0.35f, 0.40f, 0.7f);
            if (hoverArriba && scrollIndex > 0) colorArriba = UITheme.DORADO_BRILLANTE;
            dibujarFlecha(batch, pixel, x + (ANCHO_PANEL - ANCHO_FLECHA) / 2f, yTop - ALTO_FLECHA,
                ANCHO_FLECHA, ALTO_FLECHA, true, colorArriba);
            float panelAlto = MAX_VISIBLES * ALTO_FILA + (MAX_VISIBLES - 1) * GAP_FILAS;
            float flechaAbajoY = yTop - (ALTO_FLECHA + GAP_FLECHA) - panelAlto - ALTO_FLECHA;
            Color colorAbajo = (scrollIndex < maxScroll) ? new Color(0.85f, 0.85f, 0.90f, 1f)
                : new Color(0.35f, 0.35f, 0.40f, 0.7f);
            if (hoverAbajo && scrollIndex < maxScroll) colorAbajo = UITheme.DORADO_BRILLANTE;
            dibujarFlecha(batch, pixel, x + (ANCHO_PANEL - ANCHO_FLECHA) / 2f, flechaAbajoY,
                ANCHO_FLECHA, ALTO_FLECHA, false, colorAbajo);
        }
        for (int i = inicio; i < fin; i++) {
            CategoriaJoker cat = activas.get(i);
            int cantidad = gestor.getCantidad(cat);
            int tier     = gestor.getTier(cat);
            int idx = i - inicio;
            float filaY = filaYTopBase - (idx + 1) * ALTO_FILA - idx * GAP_FILAS;
            dibujarFila(batch, pixel, fNombre, cat, cantidad, tier, x, filaY);
        }
    }

    private void dibujarFila(SpriteBatch batch, Texture pixel,
                             BitmapFont fNombre,
                             CategoriaJoker cat, int cantidad, int tier,
                             float x, float y) {
        Color colorCat = cat.getColor();
        boolean maxeado = tier >= cat.getMaxTier();
        int cantidadSiguiente = (tier < cat.getMaxTier()) ? cat.getCantidadParaTier(tier + 1) : -1;
        boolean casiActivo = !maxeado && cantidadSiguiente > 0 && (cantidadSiguiente - cantidad) <= 1;
        // ─── Fondo con esquinas redondeadas ────────────────────
        Color fondo = getFondoTier(tier);
        dibujarRectRedondeado(batch, pixel, x, y, ANCHO_PANEL, ALTO_FILA, RADIO_FILA, fondo);
        // ─── Barra lateral (inset para no salirse del redondeo) ─
        batch.setColor(colorCat.r, colorCat.g, colorCat.b, (tier == 0) ? 0.35f : 1f);
        batch.draw(pixel, x + 2f, y + 5f, BARRA_ANCHO, ALTO_FILA - 10f);
        batch.setColor(Color.WHITE);
        // ─── Borde superior pulsante si está "casi activo" ──────
        if (casiActivo) {
            float pulso = 0.55f + 0.35f * (float)Math.sin(tiempoAnimacion * 4f);
            batch.setColor(colorCat.r, colorCat.g, colorCat.b, pulso * 0.55f);
            batch.draw(pixel, x + RADIO_FILA, y + ALTO_FILA - 1f,
                ANCHO_PANEL - RADIO_FILA * 2f, 1f);
            batch.setColor(Color.WHITE);
        }
        // ─── Glow dorado si está maxeado ────────────────────────
        if (maxeado) {
            float pulso = 0.55f + 0.45f * (float)Math.sin(tiempoAnimacion * 3f);
            Color glow = new Color(UITheme.DORADO_BRILLANTE.r, UITheme.DORADO_BRILLANTE.g,
                UITheme.DORADO_BRILLANTE.b, pulso * 0.22f);
            dibujarRectRedondeado(batch, pixel, x, y, ANCHO_PANEL, ALTO_FILA, RADIO_FILA, glow);
        }
        float centroY = y + ALTO_FILA / 2f;
        // ─── Badge del nombre ──────────────────────────────────
        float xBadge = x + BARRA_ANCHO + 10f;
        float anchoBadge = dibujarBadgeNombre(batch, fNombre, pixel, cat.getTexto(), colorCat, xBadge, centroY);
        // ─── Progreso a la derecha ─────────────────────────────
        float xInicioProgreso = xBadge + anchoBadge + MARGEN_ENTRE_BADGE;
        float xFinProgreso = x + ANCHO_PANEL - MARGEN_DERECHO;
        float anchoDisponible = xFinProgreso - xInicioProgreso;
        if (anchoDisponible < 40f) anchoDisponible = 40f;
        dibujarProgreso(batch, fNombre, pixel, cat, cantidad, tier, xFinProgreso, centroY, anchoDisponible);
    }

    /**
     * Fondo del panel de la fila según el tier.
     * Inactivo: negro semi-transparente.
     * Activo: color del tier (bronce/plata/oro/prismático) extremadamente transparente.
     */
    private Color getFondoTier(int tier) {
        switch (tier) {
            case 0: return new Color(0.06f, 0.07f, 0.10f, 0.30f);  // inactivo, oscuro
            case 1: return new Color(0.72f, 0.45f, 0.20f, 0.28f);  // bronce
            case 2: return new Color(0.78f, 0.82f, 0.88f, 0.28f);  // plata
            case 3: return new Color(0.98f, 0.80f, 0.30f, 0.28f);  // oro
            case 4: return new Color(0.85f, 0.70f, 1.00f, 0.29f);  // prismático (violeta claro)
            default: return new Color(0.06f, 0.07f, 0.10f, 0.30f);
        }
    }

    /**
     * Dibuja el progreso del rasgo.
     * Si está inactivo (tier == 0): muestra "1/2" (cantidad actual / primer umbral).
     * Si está activo: muestra la secuencia completa "2 > 4 > 6" con el tier
     *   actual resaltado en dorado brillante, los alcanzados con el color de la
     *   categoría, y los no alcanzados en gris tenue.
     */
    private void dibujarProgreso(SpriteBatch batch, BitmapFont font, Texture pixel,
                                 CategoriaJoker cat, int cantidad, int tier,
                                 float xDer, float centroY, float anchoDisponible)  {
        int[] umbrales = cat.getUmbrales();
        int maxTier = umbrales.length;
        // ─── Inactivo: cantidad/umbral ─────────────────────────
        if (tier == 0) {
            String texto = cantidad + "/" + umbrales[0];
            float escala = 0.90f;
            font.getData().setScale(escala);
            layout.setText(font, texto);
            // Ajustar escala si no cabe
            if (layout.width > anchoDisponible) {
                escala *= anchoDisponible / layout.width;
                font.getData().setScale(escala);
                layout.setText(font, texto);
            }
            float x = xDer - layout.width;
            // Sombra
            font.setColor(0.10f, 0.10f, 0.12f, 0.85f);
            font.draw(batch, texto, x, centroY + 5f - 1.5f);
            // Color gris tenue
            font.setColor(0.75f, 0.75f, 0.80f, 1f);
            font.draw(batch, texto, x, centroY + 5f);
            font.getData().setScale(1f);
            font.setColor(Color.WHITE);
            return;
        }
        // ─── Activo: secuencia "2 > 4 > 6" ─────────────────────
        // Primero: medir el ancho total para alinear a la derecha
        float anchoTotal = medirAnchoSecuencia(font, umbrales, tier);
        // Si no cabe, reducir todas las escalas proporcionalmente
        float factorAjuste = 1f;
        if (anchoTotal > anchoDisponible) {
            factorAjuste = anchoDisponible / anchoTotal;
        }
        float escalaNormal = ESCALA_NUM_NORMAL * factorAjuste;
        float escalaActual = ESCALA_NUM_ACTUAL * factorAjuste;
        float escalaSep    = ESCALA_SEPARADOR * factorAjuste;
        // Redibujar el ancho con las escalas ajustadas
        float anchoTotalAjustado = medirAnchoSecuenciaConEscalas(font, umbrales, tier, escalaNormal, escalaActual, escalaSep);
        float cursorX = xDer - anchoTotalAjustado;
        for (int i = 0; i < maxTier; i++) {
            boolean esActual = (i == tier - 1);
            boolean alcanzado = (i < tier);
            Color colorNum;
            float escala;
            if (esActual) {
                colorNum = UITheme.DORADO_BRILLANTE;
                escala = escalaActual;
            } else if (alcanzado) {
                colorNum = cat.getColor();
                escala = escalaNormal;
            } else {
                colorNum = new Color(0.55f, 0.55f, 0.60f, 0.75f);
                escala = escalaNormal;
            }
            String texto = String.valueOf(umbrales[i]);
            font.getData().setScale(escala);
            layout.setText(font, texto);
            // Sombra dura
            font.setColor(0.10f, 0.10f, 0.12f, 0.85f);
            font.draw(batch, texto, cursorX, centroY + 5f - 1.5f);
            // Color real
            font.setColor(colorNum);
            font.draw(batch, texto, cursorX, centroY + 5f);
            // Pequeño glow dorado pulsante alrededor del número actual
            // Pequeño glow dorado pulsante debajo del número actual
            if (esActual) {
                float pulso = 0.55f + 0.35f * (float)Math.sin(tiempoAnimacion * 5f);
                batch.setColor(UITheme.DORADO_BRILLANTE.r, UITheme.DORADO_BRILLANTE.g,
                    UITheme.DORADO_BRILLANTE.b, pulso * 0.25f);
                batch.draw(pixel, cursorX, centroY - layout.height / 2f - 1f, layout.width, 2f);
                batch.setColor(Color.WHITE);
            }
            cursorX += layout.width;
            // Separador ">"
            if (i < maxTier - 1) {
                font.getData().setScale(escalaSep);
                String sep = ">";
                layout.setText(font, sep);
                cursorX += GAP_ENTRE_NUMEROS;
                font.setColor(0.55f, 0.55f, 0.60f, 0.85f);
                font.draw(batch, sep, cursorX, centroY + 5f - 1f);
                cursorX += layout.width + GAP_ENTRE_NUMEROS;
            }
        }
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }

    private float medirAnchoSecuencia(BitmapFont font, int[] umbrales, int tier) {
        return medirAnchoSecuenciaConEscalas(font, umbrales, tier,
            ESCALA_NUM_NORMAL, ESCALA_NUM_ACTUAL, ESCALA_SEPARADOR);
    }

    private float medirAnchoSecuenciaConEscalas(BitmapFont font, int[] umbrales, int tier,
                                                float escalaNormal, float escalaActual, float escalaSep) {
        float total = 0f;
        int maxTier = umbrales.length;
        for (int i = 0; i < maxTier; i++) {
            boolean esActual = (i == tier - 1);
            float escala = esActual ? escalaActual : escalaNormal;
            font.getData().setScale(escala);
            layout.setText(font, String.valueOf(umbrales[i]));
            total += layout.width;

            if (i < maxTier - 1) {
                font.getData().setScale(escalaSep);
                layout.setText(font, ">");
                total += layout.width + GAP_ENTRE_NUMEROS * 2f;
            }
        }
        font.getData().setScale(1f);
        return total;
    }

    /**
     * Dibuja el nombre en formato píldora. Devuelve el ancho total del badge,
     * para que el llamador pueda calcular el espacio que queda para el progreso.
     */
    private float dibujarBadgeNombre(SpriteBatch batch, BitmapFont font, Texture pixel,
                                     String texto, Color color,
                                     float xIzq, float centroY) {
        float escalaOriginal = font.getScaleX();
        font.getData().setScale(escalaOriginal * 0.72f);
        GlyphLayout badgeLayout = new GlyphLayout(font, texto);
        float padX = 8f;
        float padY = 3f;
        float badgeW = padX + badgeLayout.width + padX;
        float badgeH = font.getCapHeight() + padY * 2f + 4f;
        float badgeX = xIzq;
        float badgeY = centroY - badgeH / 2f;
        float radio = badgeH / 2f;
        // Sombra
        dibujarRectRedondeado(batch, pixel, badgeX + 2f, badgeY - 2f, badgeW, badgeH, radio, new Color(0f, 0f, 0f, 0.40f));
        // Borde negro
        dibujarRectRedondeado(batch, pixel, badgeX, badgeY, badgeW, badgeH, radio, new Color(0.10f, 0.10f, 0.12f, 1f));
        // Fondo color
        float grosor = 2.5f;
        dibujarRectRedondeado(batch, pixel, badgeX + grosor, badgeY + grosor, badgeW - grosor * 2f, badgeH - grosor * 2f, radio - 1.2f, color);
        // Highlight superior
        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(pixel, badgeX + radio, badgeY + badgeH - grosor - 2f, badgeW - radio * 2f, 2f);
        // Sombra inferior
        batch.setColor(0f, 0f, 0f, 0.15f);
        batch.draw(pixel, badgeX + radio, badgeY + grosor, badgeW - radio * 2f, 2f);
        batch.setColor(Color.WHITE);
        // Texto con sombra dura
        float textX = badgeX + padX;
        float textY = badgeY + badgeH - padY - 1f;
        font.setColor(0.10f, 0.10f, 0.12f, 0.85f);
        font.draw(batch, texto, textX, textY - 1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, texto, textX, textY);
        font.getData().setScale(escalaOriginal);
        font.setColor(Color.WHITE);
        return badgeW;
    }

    private void dibujarRectRedondeado(SpriteBatch batch, Texture pixel,
                                       float x, float y, float w, float h,
                                       float radio, Color color) {
        if (w <= 0f || h <= 0f) return;
        radio = Math.min(radio, Math.min(w, h) / 2f);
        batch.setColor(color);

        // Cuerpo central (solo la zona que no toca las esquinas)
        batch.draw(pixel, x + radio, y, w - radio * 2f, h);
        // Franjas laterales (altura media, sin las esquinas)
        batch.draw(pixel, x, y + radio, radio, h - radio * 2f);
        batch.draw(pixel, x + w - radio, y + radio, radio, h - radio * 2f);

        // Esquinas: cada fila solo cubre su propia zona (izq o der), sin solapar el centro
        int pasos = Math.max(2, (int) radio);
        for (int i = 0; i < pasos; i++) {
            float dy = i + 0.5f;
            float distancia = radio - dy;
            float raiz = (float) Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));
            float inset = radio - raiz;
            float rowWidth = raiz;
            // Arriba-izquierda
            batch.draw(pixel, x + inset, y + h - i - 1f, rowWidth, 1f);
            // Arriba-derecha
            batch.draw(pixel, x + w - radio, y + h - i - 1f, rowWidth, 1f);
            // Abajo-izquierda
            batch.draw(pixel, x + inset, y + i, rowWidth, 1f);
            // Abajo-derecha
            batch.draw(pixel, x + w - radio, y + i, rowWidth, 1f);
        }
        batch.setColor(Color.WHITE);
    }

    private void dibujarFlecha(SpriteBatch batch, Texture pixel,
                               float x, float y, float w, float h,
                               boolean apuntaArriba, Color color) {
        batch.setColor(color);
        int pasos = Math.max(2, (int) h);
        for (int i = 0; i < pasos; i++) {
            float t = i / (float) (pasos - 1);
            float ancho = w * (apuntaArriba ? (1f - t) : t);
            float offsetX = (w - ancho) / 2f;
            float fy = y + i;
            batch.draw(pixel, x + offsetX, fy, ancho, 1f);
        }
        batch.setColor(Color.WHITE);
    }

    private boolean dentroDeFlecha(float mx, float my, float x, float y,
                                   float w, float h) {
        float margenLateral = 15f;
        float margenAbajo = 10f;
        float margenArriba = 20f;
        float hitX = x - margenLateral;
        float hitW = w + (margenLateral * 2f);
        float hitY = y - margenAbajo;
        float hitH = h + margenAbajo + margenArriba;
        return mx >= hitX && mx <= hitX + hitW && my >= hitY && my <= hitY + hitH;
    }
}
