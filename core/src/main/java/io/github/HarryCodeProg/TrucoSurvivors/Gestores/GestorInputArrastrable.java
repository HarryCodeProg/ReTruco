package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Vista.Arrastrable;

import java.util.ArrayList;

public class GestorInputArrastrable<T extends Arrastrable> {
    private final ArrayList<T> objetos;
    private final ArrayList<T> seleccionados = new ArrayList<>();
    private T arrastrado;

    public GestorInputArrastrable(ArrayList<T> objetos) {
        this.objetos = objetos;
    }

    public void update(float mouseX, float mouseY, float delta, boolean puedeInteractuar) {
        if (!puedeInteractuar) {
            for (T obj : objetos) {
                obj.update(mouseX, mouseY, delta);
            }
            actualizarSeleccion();
            return;
        }
        // Si no hay ningún objeto siendo arrastrado, evaluamos de derecha a izquierda (arriba hacia abajo en z-index)
        if (arrastrado == null) {
            for (int i = objetos.size() - 1; i >= 0; i--) {
                T obj = objetos.get(i);
                obj.input(mouseX, mouseY);
                obj.update(mouseX, mouseY, delta);
                if (obj.isDragging()) {
                    arrastrado = obj;
                    break; // Solo un objeto puede ser capturado por clic
                }
            }
            // Actualizamos el resto de objetos que no fueron clickeados
            for (T obj : objetos) {
                if (obj != arrastrado) {
                    obj.update(mouseX, mouseY, delta);
                }
            }
        } else {
            // Si ya hay un objeto siendo arrastrado, le pasamos su input y update a él
            arrastrado.input(mouseX, mouseY);
            arrastrado.update(mouseX, mouseY, delta);
            // Si el usuario soltó el botón del mouse, liberamos la referencia
            if (!arrastrado.isDragging()) {
                soltar();
            }
            // Actualizamos la animación de los demás objetos
            for (T obj : objetos) {
                if (obj != arrastrado) {
                    obj.update(mouseX, mouseY, delta);
                }
            }
        }
        actualizarSeleccion();
    }

    private void actualizarSeleccion() {
        seleccionados.clear();
        for (T obj : objetos) {
            if (obj.isSeleccionada()) {
                seleccionados.add(obj);
            }
        }
    }

    public T getArrastrado() {
        return arrastrado;
    }

    public void setArrastrado(T arrastrado) {
        this.arrastrado = arrastrado;
    }

    public void soltar() {
        this.arrastrado = null;
    }

    public ArrayList<T> getSeleccionados() {
        return seleccionados;
    }

    public void limpiarSeleccion() {
        seleccionados.clear();
    }
}
