package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.SignoZodiaco;

/** Guía consultable sin consumir ni volver a sortear un signo. */
public class GuiaZodiaco {
    private boolean abierta;
    private boolean hover;

    public boolean estaAbierta() { return abierta; }

    public boolean update(float x, float y, boolean click) {
        hover = x >= 1080 && x <= 1270 && y >= 38 && y <= 78;
        if (abierta) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                || (click && x >= 1050 && x <= 1210 && y >= 625 && y <= 680)) abierta = false;
            return true;
        }
        if (hover && click) { abierta = true; return true; }
        return false;
    }

    public void renderResumen(SpriteBatch batch, Main game, RuedaZodiaco.Estado estado, SignoZodiaco resultado) {
        texto(batch, game, "ZODÍACO", 1080, 236, 190, 0.75f, 1, true);
        String mensaje = estado == RuedaZodiaco.Estado.SIN_GIRAR ? "Tocá la rueda para girar"
            : estado == RuedaZodiaco.Estado.GIRANDO ? "Tu destino está girando..."
            : estado == RuedaZodiaco.Estado.CONSUMIENDO ? "Signo consumido" : "Tocá la rueda para consumir";
        if (resultado != null) {
            texto(batch, game, resultado.getNombre(), 1080, 210, 190, 0.85f, 1, true);
            texto(batch, game, resultado.getDescripcion(), 1080, 182, 190, 0.7f, 1, false);
        }
        texto(batch, game, mensaje, 1080, 100, 190, 0.54f, 1, false);
        texto(batch, game, "Ver los 12 signos", 1080, 64, 190, 0.65f, hover ? 1 : 0.8f, true);
    }

    public void render(SpriteBatch batch, Main game) {
        if (!abierta) return;
        float color = batch.getPackedColor();
        batch.setColor(0.025f, 0.035f, 0.07f, 0.97f);
        batch.draw(game.getPixelBlanco(), 0, 0, 1280, 720);
        texto(batch, game, "LOS DOCE SIGNOS", 80, 660, 920, 1.1f, 1, true);
        texto(batch, game, "Cada giro revela un signo. Leé su efecto y tocá la rueda para consumirlo.", 80, 620, 1120, 0.65f, 1, false);
        texto(batch, game, "Cerrar [Esc]", 1050, 660, 160, 0.65f, 1, true);
        SignoZodiaco[] signos = SignoZodiaco.values();
        for (int i = 0; i < signos.length; i++) {
            float x = 65 + (i % 3) * 392;
            float y = 555 - (i / 3) * 130;
            aro(batch, game, x + 32, y - 22, 36, 0, 0.28f);
            TextureRegion region = game.getAtlasZodiaco().findRegion(signos[i].getNombreRegion());
            batch.setColor(Color.WHITE);
            if (region != null) batch.draw(region, x + 3, y - 51, 58, 58);
            texto(batch, game, signos[i].getNombre(), x + 78, y + 8, 275, 0.8f, 1, true);
            texto(batch, game, signos[i].getDescripcion(), x + 78, y - 22, 275, 0.8f, 1, false);
        }
        batch.setPackedColor(color);
    }

    static void texto(SpriteBatch batch, Main game, String texto, float x, float y,
                      float ancho, float escala, float alpha, boolean dorado) {
        BitmapFont font = game.getFuentePrincipal();
        float sx = font.getData().scaleX, sy = font.getData().scaleY;
        float color = font.getColor().toFloatBits();
        font.getData().setScale(escala);
        font.setColor(dorado ? 0.96f : 0.87f, dorado ? 0.82f : 0.89f, dorado ? 0.48f : 0.95f, alpha);
        font.draw(batch, texto, x, y, ancho, Align.center, true);
        font.getData().setScale(sx, sy);
        Color.abgr8888ToColor(font.getColor(), color);
    }

    static void aro(SpriteBatch batch, Main game, float x, float y, float radio, float giro, float alpha) {
        batch.setColor(0.95f, 0.78f, 0.4f, alpha);
        for (int i = 0; i < 96; i++) {
            float a = giro + i * 360f / 96;
            float b = giro + (i + 1) * 360f / 96;
            float px = x + MathUtils.cosDeg(a) * radio, py = y + MathUtils.sinDeg(a) * radio;
            float dx = (MathUtils.cosDeg(b) - MathUtils.cosDeg(a)) * radio;
            float dy = (MathUtils.sinDeg(b) - MathUtils.sinDeg(a)) * radio;
            batch.draw(game.getPixelBlanco(), px, py, 0, 0.6f,
                (float)Math.sqrt(dx * dx + dy * dy), 1.2f, 1, 1,
                MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees, 0, 0, 1, 1, false, false);
        }
    }
}
