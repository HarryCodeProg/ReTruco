package io.github.HarryCodeProg.TrucoSurvivors.Vista;

public class FisicaTiltArrastre {

    private float angulo = 0f;
    private float velocidadAngular = 0f;
    private float ultimoMouseX = 0f;

    private static final float FUERZA_VELOCIDAD = 0.035f;
    private static final float FUERZA_RESORTE = 18f;
    private static final float AMORTIGUACION = 7f;
    private static final float MAX_TILT = 24f;

    private static final float UMBRAL_QUIETO = 0.05f;

    public void iniciarArrastre(float mouseX) {
        ultimoMouseX = mouseX;
        velocidadAngular *= 0.15f;
    }

    public void actualizarArrastre(float mouseX, float delta) {
        if (delta <= 0f) return;
        float velocidadMouse = (mouseX - ultimoMouseX) / delta;
        ultimoMouseX = mouseX;
        velocidadMouse = Math.max(-900f, Math.min(900f, velocidadMouse));
        float objetivo = -velocidadMouse * FUERZA_VELOCIDAD;
        objetivo = Math.max(-MAX_TILT, Math.min(MAX_TILT, objetivo));
        actualizarResorte(objetivo, delta);
    }

    public void actualizarSoltada(float delta) {
        actualizarResorte(0f, delta);
    }

    private void actualizarResorte(float objetivo, float delta) {
        float aceleracion = (objetivo - angulo) * FUERZA_RESORTE;
        velocidadAngular += aceleracion * delta;
        velocidadAngular *= (float) Math.exp(-AMORTIGUACION * delta);
        angulo += velocidadAngular * delta;
        if (Math.abs(angulo) < UMBRAL_QUIETO && Math.abs(velocidadAngular) < UMBRAL_QUIETO) {
            angulo = 0f;
            velocidadAngular = 0f;
        }
    }

    public float getAngulo() {
        return angulo;
    }

    public boolean estaActiva() {
        return Math.abs(angulo) > UMBRAL_QUIETO || Math.abs(velocidadAngular) > UMBRAL_QUIETO;
    }

    public void reset() {
        angulo = 0f;
        velocidadAngular = 0f;
        ultimoMouseX = 0f;
    }
}
