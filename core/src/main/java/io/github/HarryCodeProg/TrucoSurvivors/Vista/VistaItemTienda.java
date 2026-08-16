package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ItemTienda;

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
    private VistaJoker vistaJokerInterna;
    private VistaCarta vistaCartaInterna;

    public VistaItemTienda(ItemTienda item, TextureAtlas atlasCartas, TextureAtlas atlasJokers) {
        this.item = item;
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            this.vistaCartaInterna = new VistaCarta(item.getCarta(), false, atlasCartas);
            this.vistaCartaInterna.setEnModal(true); // Permite hover individual dentro de paneles/tienda
        } else if (item.getTipo() == ItemTienda.Tipo.JOKER) {
            if (item.getJoker() != null) {
                this.vistaJokerInterna = new VistaJoker(item.getJoker(), atlasJokers);
            } else {
                this.vistaJokerInterna = null;
            }
        } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
            if (Main.getInstance() != null && Main.getInstance().getAtlasSantos() != null) {
                String regionName = item.getSanto() != null ? item.getSanto().getNombreRegion() : null;
                if (regionName != null) {
                    this.region = Main.getInstance().getAtlasSantos().findRegion(regionName);
                }
            }
        } else {
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
    }

    public ItemTienda getItem() { return item; }
    public boolean isSeleccionado() { return seleccionado; }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        if (vistaJokerInterna != null) vistaJokerInterna.setSeleccionada(seleccionado);
        if (vistaCartaInterna != null) vistaCartaInterna.setSeleccionada(seleccionado);
    }

    public boolean contiene(float mx, float my) {
        float w = width * scale;
        float h = height * scale;
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private float moverHacia(float value, float target, float maxDelta) {
        float diferencia = target - value;
        if (Math.abs(diferencia) <= maxDelta) return target;
        return value + Math.signum(diferencia) * maxDelta;
    }

    public void render(SpriteBatch batch, Main game) {
        if (vistaJokerInterna != null) {
            vistaJokerInterna.render(batch);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.render(batch, game);
        } else if (region != null) {
            float drawY = y + visualOffsetY;
            float w = width * scale;
            float h = height * scale;
            batch.draw(region, x, drawY, w, h);
        }
    }

    public boolean isHover() {return hover;}

    public void update(float mouseX, float mouseY, float delta) {
        hover = contiene(mouseX, mouseY);
        if (vistaJokerInterna != null) {
            vistaJokerInterna.update(mouseX, mouseY, delta);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.update(mouseX, mouseY, delta);
        } else {
            float offsetHover = hover ? OFFSET_HOVER : 0f;
            float offsetSeleccion = seleccionado ? OFFSET_SELECCIONADO : 0f;
            targetScale = hover ? ESCALA_HOVER : 1f;
            targetOffsetY = offsetHover + offsetSeleccion;
            scale = moverHacia(scale, targetScale, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, targetOffsetY, VELOCIDAD_OFFSET * delta);
        }
    }

    public void renderCartelStats(SpriteBatch batch, Main game) {
        if (!hover) return;
        if (vistaJokerInterna != null) {
            vistaJokerInterna.renderCartelStats(batch, game);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.renderCartelStats(batch, game);
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
