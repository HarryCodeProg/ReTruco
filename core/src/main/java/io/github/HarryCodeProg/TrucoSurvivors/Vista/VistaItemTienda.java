package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ItemTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;

public class VistaItemTienda {
    private final ItemTienda item;
    private TextureRegion region;
    private float x, y;
    private float width = 100, height = 150;
    private boolean seleccionado = false;
    private boolean hover = false;
    private float visualOffsetY = 0f;
    private float targetOffsetY = 0f;
    private float scale = 1f;
    private float targetScale = 1f;
    private static final float OFFSET_SELECCIONADO = 25f;
    private static final float OFFSET_HOVER = 8f;
    private static final float ESCALA_HOVER = 1.04f;
    private static final float VELOCIDAD_OFFSET = 250f;
    private static final float VELOCIDAD_ESCALA = 4f;
    // =========================
    // ESTILO VISUAL
    // =========================
    private static final float RADIO = 9f;
    private static final float SOMBRA_X = 5f;
    private static final float SOMBRA_Y = 7f;
    private final Color colorAcento = new Color();
    private final Color colorGlow = new Color();
    private VistaJoker vistaJokerInterna;
    private VistaCarta vistaCartaInterna;
    private VistaSanto vistaSantoInterna;

    private Juego juego;

    public VistaItemTienda(ItemTienda item, TextureAtlas atlasCartas, TextureAtlas atlasJokers, Juego juego) {
        this.item = item;
        this.juego = juego;
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            this.vistaCartaInterna = new VistaCarta(item.getCarta(), false, atlasCartas);
            this.vistaCartaInterna.setEnModal(true);
        } else if (item.getTipo() == ItemTienda.Tipo.JOKER) {
            if (item.getJoker() != null) {
                this.vistaJokerInterna = new VistaJoker(item.getJoker(), atlasJokers);
            }
        } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
            if (item.getSanto() != null) {
                TextureAtlas atlasSantos = (Main.getInstance() != null) ? Main.getInstance().getAtlasSantos() : null;
                this.vistaSantoInterna = new VistaSanto(item.getSanto(), atlasSantos);
            }
        } else {
            this.vistaJokerInterna = null;
        }
    } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
        // Crear VistaSanto usando atlas de Main (si está disponible)
        if (item.getSanto() != null) {
            TextureAtlas atlasSantos = (Main.getInstance() != null) ? Main.getInstance().getAtlasSantos() : null;
            this.vistaSantoInterna = new VistaSanto(item.getSanto(), atlasSantos);
        }
    } else {
        // otros tipos (ZODIACO, etc) — inicializar region si hace falta
        this.region = null;
    }
}

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        if (vistaJokerInterna != null) {
            vistaJokerInterna.setPosition(x, y);
            vistaJokerInterna.setHandPosition(x, y);
            vistaJokerInterna.setTamaño(width, height);
        }
        if (vistaCartaInterna != null) {
            vistaCartaInterna.setPosition(x, y);
            vistaCartaInterna.setHandPosition(x, y);
            vistaCartaInterna.setTamaño(width, height);
        }
        if (vistaSantoInterna != null) {
            vistaSantoInterna.setPosition(x, y);
            vistaSantoInterna.setHandPosition(x, y);
            vistaSantoInterna.setTamaño(width, height);
        }
    }

    public ItemTienda getItem() {
        return item;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        if (vistaJokerInterna != null) vistaJokerInterna.setSeleccionada(seleccionado);
        if (vistaCartaInterna != null) vistaCartaInterna.setSeleccionada(seleccionado);
        if (vistaSantoInterna != null) vistaSantoInterna.setSeleccionada(seleccionado);
    }

    public boolean contiene(float mx, float my) {
        float w = width * scale;
        float h = height * scale;
        float hitboxX = x + (width - w) / 2f;
        float hitboxY = y + (height - h) / 2f;
        return mx >= hitboxX && mx <= hitboxX + w && my >= hitboxY && my <= hitboxY + h;
    }
    /*
    public boolean contiene(float mx, float my) {
        float w = width * scale;
        float h = height * scale;
        return mx >= x && mx <= x + w && my >= y + visualOffsetY && my <= y + visualOffsetY + h;
    }*/

    private float moverHacia(float value, float target, float maxDelta) {
        float diferencia = target - value;
        if (Math.abs(diferencia) <= maxDelta) return target;
        return value + Math.signum(diferencia) * maxDelta;
    }

    public void render(SpriteBatch batch, Main game) {
        float drawY = y + visualOffsetY;
        float drawW = width * scale;
        float drawH = height * scale;
        Color acento = obtenerColorAcento();
        dibujarRectRedondeado(batch, x + SOMBRA_X, drawY - SOMBRA_Y, drawW, drawH, RADIO, new Color(0f, 0f, 0f, 0.50f));
        if (hover || seleccionado) {
            float intensidad = seleccionado ? 0.18f : 0.10f;
            colorGlow.set(acento.r, acento.g, acento.b, intensidad);
            dibujarRectRedondeado(batch, x - 7f, drawY - 7f, drawW + 14f, drawH + 14f, RADIO + 3f, colorGlow);
            colorGlow.set(acento.r, acento.g, acento.b, intensidad * 0.55f);
            dibujarRectRedondeado(batch, x - 12f, drawY - 12f, drawW + 24f, drawH + 24f, RADIO + 5f, colorGlow);
        }
        dibujarRectRedondeado(batch, x - 2f, drawY - 2f, drawW + 4f, drawH + 4f, RADIO, new Color(0.025f, 0.035f, 0.055f, 0.65f));
        if (vistaJokerInterna != null) {
            vistaJokerInterna.setPosition(x, y);
            vistaJokerInterna.render(batch);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.setPosition(x, y);
            vistaCartaInterna.render(batch, game);
        } else if (vistaSantoInterna != null) {
            vistaSantoInterna.setPosition(x, y);
            vistaSantoInterna.render(batch);
        } else if (region != null) {
            batch.draw(region, x, drawY, drawW, drawH);
        }
        // -----------------------------------------------------------------------------------
        if (seleccionado) {
            float lineaX = x + 8f;
            float lineaW = drawW - 16f;
            float lineaY = drawY - 6f;
            // sombra
            batch.setColor(0f, 0f, 0f, 0.45f);
            batch.draw(game.getPixelBlanco(), lineaX + 2f, lineaY - 2f, lineaW, 4f);
            // línea dorada
            batch.setColor(UITheme.DORADO_BRILLANTE);
            batch.draw(game.getPixelBlanco(), lineaX, lineaY, lineaW, 3f);
            batch.setColor(Color.WHITE);
        }
        if (hover && !seleccionado) {
            batch.setColor(1f, 1f, 1f, 0.16f);
            batch.draw(game.getPixelBlanco(), x + 8f, drawY + drawH - 3f, drawW - 16f, 2f);
            batch.setColor(Color.WHITE);
        }
    }

    public boolean isHover() {
        return hover;
    }

    public void update(float mouseX, float mouseY, float delta) {
        hover = contiene(mouseX, mouseY);

        // --- AHORA SE CALCULA EL OFFSET SIEMPRE PARA TODOS ---
        float offsetHover = hover ? OFFSET_HOVER : 0f;
        float offsetSeleccion = seleccionado ? OFFSET_SELECCIONADO : 0f;
        targetScale = hover ? ESCALA_HOVER : 1f;
        targetOffsetY = offsetHover + offsetSeleccion;
        scale = moverHacia(scale, targetScale, VELOCIDAD_ESCALA * delta);
        visualOffsetY = moverHacia(visualOffsetY, targetOffsetY, VELOCIDAD_OFFSET * delta);
        // -----------------------------------------------------

        if (vistaJokerInterna != null) {
            vistaJokerInterna.update(mouseX, mouseY, delta);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.update(mouseX, mouseY, delta);
        }
        if (vistaSantoInterna != null) {
            vistaSantoInterna.update(mouseX, mouseY, delta);
        }
    }

    public void renderCartelStats(SpriteBatch batch, Main game) {
        if (!hover) return;
        if (vistaSantoInterna != null) {
            vistaSantoInterna.renderCartelStats(batch, game);
        } else if (vistaJokerInterna != null) {
            vistaJokerInterna.renderCartelStats(batch, game, juego);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.renderCartelStats(batch, game);
        } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
            // Si queremos mostrar info del santo al hover, podríamos dibujar un texto simple
            // Por ahora no hacemos nada extra para evitar NPEs.
        }
    }

    private Color obtenerColorAcento() {
        if (item.getTipo() == ItemTienda.Tipo.JOKER) {
            Joker joker = item.getJoker();
            if (joker != null) {
                Rareza rareza = joker.getRareza();
                return UITheme.porNombreRareza(rareza != null ? rareza.name() : null);
            }
            return UITheme.RAREZA_COMUN;
        }
        if (item.getTipo() == ItemTienda.Tipo.SANTO) {
            return UITheme.RAREZA_EPICO;
        }
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            return UITheme.RAREZA_RARO;
        }
        return UITheme.BORDE;
    }

    private void dibujarRectRedondeado(SpriteBatch batch, float x, float y, float width, float height, float radio, Color color) {
        if (width <= 0f || height <= 0f) return;
        radio = Math.min(radio, Math.min(width, height) / 2f);
        batch.setColor(color);
        // Centro
        batch.draw(gamePixel(batch), x + radio, y, width - radio * 2f, height);
        // Laterales
        batch.draw(gamePixel(batch), x, y + radio, radio, height - radio * 2f);
        batch.draw(gamePixel(batch), x + width - radio, y + radio, radio, height - radio * 2f);
        // Curvas
        int pasos = Math.max(2, (int) radio);
        for (int i = 0; i < pasos; i++) {
            float dy = i + 0.5f;
            float distancia = radio - dy;
            float raiz = (float) Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));
            float inset = radio - raiz;
            batch.draw(gamePixel(batch), x + inset, y + i, width - inset * 2f, 1f);
            batch.draw(gamePixel(batch), x + inset, y + height - i - 1f, width - inset * 2f, 1f);
        }
        batch.setColor(Color.WHITE);
    }

    private com.badlogic.gdx.graphics.Texture gamePixel(SpriteBatch batch) {
        return Main.getInstance().getPixelBlanco();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setTamaño(float width, float height) {
        this.width = width;
        this.height = height;
        if (vistaJokerInterna != null) {
            vistaJokerInterna.setTamaño(width, height);
        }
        if (vistaCartaInterna != null) {
            vistaCartaInterna.setTamaño(width, height);
        }
    }

    public float getYConOffset() {
        return y + visualOffsetY;
    }

    public float getTopeY() {
        return y + visualOffsetY + (height * scale);
    }

    public void limpiarHover() { this.hover = false; }

    public void setTooltipLateral(boolean lateral) {
        if (vistaJokerInterna != null) {
            vistaJokerInterna.setTooltipLateral(lateral);
        } else if (vistaSantoInterna != null) {
            vistaSantoInterna.setTooltipLateral(lateral);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.setTooltipLateral(lateral);
        }
    }
}
