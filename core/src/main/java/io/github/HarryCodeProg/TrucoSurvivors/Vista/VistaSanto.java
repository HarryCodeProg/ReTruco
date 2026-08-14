package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

public class VistaSanto implements Arrastrable {
    private Santo santo;
    private TextureRegion region;
    private float x, y, width = 70, height = 95;
    private boolean hover, dragging, draggingAnterior, seleccionada;
    private float handX, handY, targetX, targetY;
    private float visualOffsetY, targetOffsetY;
    private float rotation, targetRotation;
    private float scale = 1f, targetScale = 1f;
    private static boolean ALGUN_DRAG_ACTIVO = false;
    private static final float UMBRAL_CLICK = 6f;
    private static final float OFFSET_SELECCIONADA = 25f;
    private static final float OFFSET_HOVER = 8f;
    private static final float ESCALA_HOVER = 1.04f;
    private static final float VELOCIDAD_POSICION = 900f;
    private static final float VELOCIDAD_OFFSET = 250f;
    private static final float VELOCIDAD_ESCALA = 4f;
    private static final float VELOCIDAD_ROTACION = 360f;
    private float pressX, pressY, dragOffsetX, dragOffsetY;
    private boolean huboMovimientoSignificativo;

    public VistaSanto(Santo santo, TextureAtlas atlasSantos) {
        this.santo = santo;
        this.region = atlasSantos.findRegion(santo.getNombreRegion());
    }

    public Santo getSanto() { return santo; }
    public void setTamaño(float w, float h) { width = w; height = h; }
    public boolean isHover() { return hover; }
    public boolean isSeleccionada() { return seleccionada; }
    public void setSeleccionada(boolean s) { seleccionada = s; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public void setHandPosition(float x, float y) { handX = x; handY = y; targetX = x; targetY = y; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHandTargetX() { return targetX; }

    @Override
    public float getAncho() {
        return 0;
    }

    public float getCentroX() { return x + (width * scale) / 2f; }

    public boolean contiene(float mx, float my) {
        float w = width * scale, h = height * scale;
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override public boolean isDragging() { return dragging; }

    @Override
    public void update(float mouseX, float mouseY, float delta) {
        hover = !ALGUN_DRAG_ACTIVO && contiene(mouseX, mouseY);
        float offsetHover = hover ? OFFSET_HOVER : 0f;
        float offsetSel = seleccionada ? OFFSET_SELECCIONADA : 0f;
        targetScale = hover ? ESCALA_HOVER : 1f;
        targetOffsetY = offsetHover + offsetSel;
        if (hover) targetRotation = 0f;
        if (!dragging) {
            x = moverHacia(x, targetX, VELOCIDAD_POSICION * delta);
            y = moverHacia(y, targetY, VELOCIDAD_POSICION * delta);
            scale = moverHacia(scale, targetScale, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, targetOffsetY, VELOCIDAD_OFFSET * delta);
            rotation = moverHacia(rotation, targetRotation, VELOCIDAD_ROTACION * delta);
        } else {
            x = targetX; y = targetY;
            scale = moverHacia(scale, 1.2f, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, 0f, VELOCIDAD_OFFSET * delta);
        }
    }

    private float moverHacia(float v, float t, float max) {
        float d = t - v;
        if (Math.abs(d) <= max) return t;
        return v + Math.signum(d) * max;
    }

    @Override
    public void input(float mouseX, float mouseY) {
        draggingAnterior = dragging;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && contiene(mouseX, mouseY)) {
            dragging = true; pressX = mouseX; pressY = mouseY;
            huboMovimientoSignificativo = false;
            dragOffsetX = mouseX - x; dragOffsetY = mouseY - y;
            ALGUN_DRAG_ACTIVO = true;
        }
        if (dragging) {
            float dx = mouseX - pressX, dy = mouseY - pressY;
            if (!huboMovimientoSignificativo && (Math.abs(dx) > UMBRAL_CLICK || Math.abs(dy) > UMBRAL_CLICK))
                huboMovimientoSignificativo = true;
        }
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (dragging && !huboMovimientoSignificativo) {
                seleccionada = !seleccionada;
                GestorSonidos s = Main.getInstance().getGestorSonidos();
                if (s != null) s.reproducirConVariacion(seleccionada ? "seleccionar" : "deseleccionar");
            }
            if (dragging) ALGUN_DRAG_ACTIVO = false;
            dragging = false;
        }
        if (dragging) { targetX = mouseX - dragOffsetX; targetY = mouseY - dragOffsetY; }
    }

    public void render(SpriteBatch batch) {
        float drawY = y + visualOffsetY;
        batch.draw(region, x, drawY, width / 2f, height / 2f, width, height, scale, scale, rotation);
    }

    public void dispose() {}
}
