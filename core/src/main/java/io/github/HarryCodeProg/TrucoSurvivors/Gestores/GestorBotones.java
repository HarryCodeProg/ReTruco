package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import java.util.ArrayList;
import java.util.List;

public class GestorBotones {
    private final List<Boton> botones = new ArrayList<>();
    private float ultimoMouseX;
    private float ultimoMouseY;

    public void agregar(Boton boton) {
        botones.add(boton);
    }

    public void update(float mouseX, float mouseY) {
        this.ultimoMouseX = mouseX;
        this.ultimoMouseY = mouseY;
        for (Boton boton : botones) {
            boton.update(mouseX, mouseY);
        }
    }

    public Boton obtenerBotonCliqueado() {
        for (Boton boton : botones) {
            if (boton.fueCliqueado(ultimoMouseX, ultimoMouseY)) {
                return boton;
            }
        }
        return null;
    }

    public void render(SpriteBatch batch) {
        for (Boton boton : botones) {
            boton.render(batch);
        }
    }

    public void setHabilitado(Accion accion, boolean habilitado) {
        Boton boton = buscar(accion);
        if (boton != null) {
            boton.setHabilitado(habilitado);
        }
    }

    public Boton buscar(Accion accion) {
        for (Boton boton : botones) {
            if (boton.getAccion() == accion) {
                return boton;
            }
        }
        return null;
    }
}
