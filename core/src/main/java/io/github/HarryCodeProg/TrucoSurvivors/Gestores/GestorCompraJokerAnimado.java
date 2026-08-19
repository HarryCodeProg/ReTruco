package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaJoker;

import java.util.ArrayList;

public class GestorCompraJokerAnimado {
    private static final float VELOCIDAD = 1400f;
    private VistaJoker animando;
    private float x, y, objetivoX, objetivoY;

    public void iniciar(Joker joker, TextureAtlas atlasJokers, float ancho, float alto,
                        float xInicial, float yInicial, ArrayList<VistaJoker> jokersActuales, AreaElementos<VistaJoker> area) {
        animando = new VistaJoker(joker, atlasJokers);
        animando.setTamaño(ancho, alto);
        x = xInicial; y = yInicial;
        animando.setPosition(x, y);
        ArrayList<VistaJoker> simulacion = new ArrayList<>(jokersActuales);
        simulacion.add(animando);
        area.distribuir(simulacion, null);
        objetivoX = animando.getHandTargetX();
        objetivoY = animando.getHandTargetY();
        area.distribuir(simulacion, animando); // reacomoda existentes dejando lugar
    }

    public Joker update(float delta, ArrayList<VistaJoker> jokersReales, AreaElementos<VistaJoker> area) {
        if (animando == null) return null;
        x = moverHacia(x, objetivoX, delta);
        y = moverHacia(y, objetivoY, delta);
        animando.setPosition(x, y);
        if (x == objetivoX && y == objetivoY) {
            jokersReales.add(animando);
            Joker modelo = animando.getJoker();
            animando = null;
            area.distribuir(jokersReales, null);
            return modelo;
        }
        return null;
    }

    private float moverHacia(float actual, float objetivo, float delta) {
        float d = objetivo - actual;
        if (Math.abs(d) <= VELOCIDAD * delta) return objetivo;
        return actual + Math.signum(d) * VELOCIDAD * delta;
    }

    public void render(SpriteBatch batch) { if (animando != null) animando.render(batch); }
    public boolean isAnimando() { return animando != null; }

    public void cancel() {
        animando = null;
    }
}
