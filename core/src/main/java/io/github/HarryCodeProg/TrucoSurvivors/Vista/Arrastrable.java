package io.github.HarryCodeProg.TrucoSurvivors.Vista;

public interface Arrastrable {
    float getCentroX();
    float getHandTargetX(); // <- Para saber a dónde se está desplazando
    float getAncho();       // <- Para calcular el centro del slot de destino
    void setHandPosition(float x, float y);
    void input(float mouseX, float mouseY);
    void update(float mouseX, float mouseY, float delta);
    boolean isDragging();
    boolean isSeleccionada();
}
