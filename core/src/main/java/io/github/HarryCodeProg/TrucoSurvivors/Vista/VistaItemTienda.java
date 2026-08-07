package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ItemTienda;

public class VistaItemTienda {
    private final ItemTienda item;
    private final TextureRegion region;
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

    public VistaItemTienda(ItemTienda item, TextureAtlas atlasCartas, TextureAtlas atlasJokers) {
        this.item = item;
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            this.region = atlasCartas.findRegion(item.getCarta().getNombreRegion());
        } else {
            this.region = atlasJokers.findRegion(item.getJoker().getNombreRegion());
        }
    }

    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public ItemTienda getItem() { return item; }
    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }

    public boolean contiene(float mx, float my) {
        float w = width * scale;
        float h = height * scale;
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    /** Ahora recibe delta para poder animar como VistaCarta/VistaJoker. */
    public void update(float mouseX, float mouseY, float delta) {
        hover = contiene(mouseX, mouseY);
        float offsetHover = hover ? OFFSET_HOVER : 0f;
        float offsetSeleccion = seleccionado ? OFFSET_SELECCIONADO : 0f;
        targetScale = hover ? ESCALA_HOVER : 1f;
        targetOffsetY = offsetHover + offsetSeleccion;

        scale = moverHacia(scale, targetScale, VELOCIDAD_ESCALA * delta);
        visualOffsetY = moverHacia(visualOffsetY, targetOffsetY, VELOCIDAD_OFFSET * delta);
    }

    private float moverHacia(float value, float target, float maxDelta) {
        float diferencia = target - value;
        if (Math.abs(diferencia) <= maxDelta) return target;
        return value + Math.signum(diferencia) * maxDelta;
    }

    public void render(SpriteBatch batch) {
        float drawY = y + visualOffsetY;
        float w = width * scale;
        float h = height * scale;
        batch.draw(region, x, drawY, w, h);
    }
}
