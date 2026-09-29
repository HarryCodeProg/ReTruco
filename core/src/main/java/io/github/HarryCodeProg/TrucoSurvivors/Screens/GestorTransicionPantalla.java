package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GestorTransicionPantalla {
    private enum Fase { INACTIVA, CUBRIENDO, EJECUTANDO, DESCUBRIENDO }
    private Fase fase = Fase.INACTIVA;
    private float progreso = 0f;
    private static final float DURACION = 0.3f;
    private Runnable accionEnMedio;
    private boolean accionEjecutada;

    public void iniciar(Runnable accionEnMedio) {
        this.accionEnMedio = accionEnMedio;
        this.accionEjecutada = false;
        this.progreso = 0f;
        this.fase = Fase.CUBRIENDO;
    }

    public boolean estaActiva() {
        return fase != Fase.INACTIVA;
    }

    public void update(float delta) {
        switch (fase) {
            case CUBRIENDO:
                progreso += delta / DURACION;
                if (progreso >= 1f) {
                    progreso = 1f;
                    fase = Fase.EJECUTANDO;
                }
                break;
            case EJECUTANDO:
                if (!accionEjecutada) {
                    if (accionEnMedio != null) accionEnMedio.run();
                    accionEjecutada = true;
                }
                fase = Fase.DESCUBRIENDO;
                break;
            case DESCUBRIENDO:
                progreso -= delta / DURACION;
                if (progreso <= 0f) {
                    progreso = 0f;
                    fase = Fase.INACTIVA;
                }
                break;
            default:
                break;
        }
    }

    public void render(SpriteBatch batch, Texture pixelBlanco) {
        if (fase == Fase.INACTIVA || pixelBlanco == null) return;
        float alturaCubierta = 720f * progreso;
        batch.setColor(0.05f, 0.06f, 0.09f, 1f);
        batch.draw(pixelBlanco, 0f, 720f - alturaCubierta, 1280f, alturaCubierta);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
