package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Collections;

public abstract class JokerCopiaVecino extends Joker {

    public JokerCopiaVecino(int id, String nombre, String nombreArchivo, String descripcion, Rareza rareza,
                            int coste, CategoriaJoker... categorias) {
        super(id, nombre, nombreArchivo, descripcion, rareza, coste, FaseActivacion.INDEPENDIENTE, categorias);
    }

    protected abstract Joker obtenerVecino(ContextoJuego ctx);

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        Joker vecino = obtenerVecino(ctx);
        if (vecino == null || vecino == this) return;
        // Guardia anti-recursión: esta instancia YA está copiando este evento ahora mismo (en la pila actual).
        // Si el vecino es también una copia que intenta copiar de vuelta hacia acá, ctx.marcarUsado corta el ciclo.
        if (!ctx.marcarUsado(this, "copiando_ahora:" + evento)) return;
        vecino.aplicarEfecto(evento, ctx, juego);
    }
}
