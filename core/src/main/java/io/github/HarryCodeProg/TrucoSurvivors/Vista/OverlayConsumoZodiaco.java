package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.SignoZodiaco;

public class OverlayConsumoZodiaco {
    public enum Estado { OCULTO, ENTRANDO, QUEMANDO, APLICANDO_EFECTO, CERRADO }
    private Estado estado = Estado.OCULTO;
    private SignoZodiaco signo;
    private TextureRegion region;
    private float tiempo;
    private Runnable alTerminar;
    private static final float ENTRADA = 0.65f;
    private static final float LECTURA = 1.9f;
    private static final float SALIDA = 0.8f;
    private static final float TOTAL = ENTRADA + LECTURA + SALIDA;

    public void abrir(SignoZodiaco signo, TextureRegion region, Runnable alTerminar) {
        this.signo = signo;
        this.region = region;
        this.alTerminar = alTerminar;
        estado = Estado.ENTRANDO;
        tiempo = 0;
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        if (sonidos != null) sonidos.reproducirConVariacion("consumir_zodiaco");
    }

    public void update(float delta) {
        if (!estaActivo() || estado == Estado.APLICANDO_EFECTO) return;
        tiempo += Math.max(0, delta);
        if (tiempo >= TOTAL) estado = Estado.APLICANDO_EFECTO;
        else if (tiempo >= ENTRADA) estado = Estado.QUEMANDO;
    }

    public boolean estaActivo() { return estado != Estado.OCULTO && estado != Estado.CERRADO; }
    public boolean debeAplicarEfectoAhora() { return estado == Estado.APLICANDO_EFECTO; }
    public boolean estaCerrado() { return estado == Estado.CERRADO; }

    public void confirmarCierre() {
        if (estado != Estado.APLICANDO_EFECTO) return;
        estado = Estado.OCULTO;
        Runnable callback = alTerminar;
        alTerminar = null;
        if (callback != null) callback.run();
    }

    public void render(SpriteBatch batch, Main game) {
        if (!estaActivo()) return;
        float color = batch.getPackedColor();
        float entrada = MathUtils.clamp(tiempo / ENTRADA, 0, 1);
        float salida = MathUtils.clamp((tiempo - ENTRADA - LECTURA) / SALIDA, 0, 1);
        float alpha = Interpolation.smooth.apply(entrada) * (1 - Interpolation.smooth.apply(salida));
        Texture pixel = game.getPixelBlanco();
        batch.setColor(0.025f, 0.035f, 0.075f, 0.88f * alpha);
        batch.draw(pixel, 0, 0, 1280, 720);
        float radio = 125 + 18 * Interpolation.smooth.apply(entrada) + salida * 95;
        GuiaZodiaco.aro(batch, game, 640, 410, radio, tiempo * 12, alpha * 0.7f);
        GuiaZodiaco.aro(batch, game, 640, 410, radio + 18, -tiempo * 9, alpha * 0.28f);
        // Órbitas que se abren al liberar el efecto; sin texturas ni partículas asignadas por frame.
        for (int i = 0; i < 36; i++) {
            float angulo = i * 137.5f + tiempo * (i % 2 == 0 ? 16 : -12);
            float distancia = 115 + (i % 5) * 15 + salida * (60 + i * 3);
            float x = 640 + MathUtils.cosDeg(angulo) * distancia;
            float y = 410 + MathUtils.sinDeg(angulo) * distancia;
            float brillo = alpha * (0.5f + 0.5f * MathUtils.sin(tiempo * 3 + i));
            batch.setColor(1, 0.83f, 0.5f, brillo);
            float size = i % 3 == 0 ? 7 : 3;
            batch.draw(pixel, x - size / 2, y - 0.7f, size, 1.4f);
            batch.draw(pixel, x - 0.7f, y - size / 2, 1.4f, size);
        }
        float escala = Interpolation.swingOut.apply(entrada) * (1 - salida * 0.55f);
        float size = 205 * escala * (1 + 0.025f * MathUtils.sin(tiempo * 3));
        batch.setColor(1, 1, 1, alpha);
        if (region != null) batch.draw(region, 640 - size / 2, 410 - size / 2 + MathUtils.sin(tiempo * 2) * 5, size, size);
        GuiaZodiaco.texto(batch, game, "EL DESTINO TE CONCEDE", 290, 640, 700, 0.7f, alpha, true);
        GuiaZodiaco.texto(batch, game, signo.getNombre(), 290, 235, 700, 1.4f, alpha, true);
        GuiaZodiaco.texto(batch, game, signo.getDescripcion(), 360, 180, 560, 0.85f, alpha, false);
        batch.setPackedColor(color);
    }
}
