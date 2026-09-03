package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;

import java.util.EnumSet;
import java.util.Set;

public abstract class JokerCopiaVecino extends Joker {

    private static final Set<EventoJuego> EVENTOS_COPIABLES = EnumSet.of(
        EventoJuego.ANTES_DE_SUMAR_TRUCO,
        EventoJuego.ANTES_DE_SUMAR_ENVIDO,
        EventoJuego.AL_PUNTUAR_CARTA,
        EventoJuego.AL_PUNTUAR_CARTA_ENVIDO
    );

    public JokerCopiaVecino(int id, String nombre, String nombreArchivo, String descripcion, Rareza rareza,
                            int coste, CategoriaJoker... categorias) {
        super(id, nombre, nombreArchivo, descripcion, rareza, coste, FaseActivacion.INDEPENDIENTE, categorias);
    }

    protected abstract Joker obtenerVecino(ContextoJuego ctx);

    public Joker getVecinoActual(ContextoJuego ctx) {
        return obtenerVecino(ctx);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (!EVENTOS_COPIABLES.contains(evento)) return;
        Joker vecino = obtenerVecino(ctx);
        if (vecino == null || vecino == this) return;
        if (!ctx.iniciarCopia(this)) return;
        if (!ctx.puedeEntrarACopia()) {
            ctx.terminarCopia(this);
            return;
        }
        try {
            ResolucionPuntaje resolucion = ctx.getResolucionActual();
            int pasosAntes = (resolucion != null) ? resolucion.getLog().size() : 0;
            vecino.aplicarEfecto(evento, ctx, juego);
            if (resolucion != null) {
                int pasosDespues = resolucion.getLog().size();
                for (int i = pasosAntes; i < pasosDespues; i++) {
                    ResolucionPuntaje.PasoResolucion paso = resolucion.getLog().get(i);
                    resolucion.getLog().set(i, new ResolucionPuntaje.PasoResolucion(
                        this.getNombre(), paso.tipo, paso.valor, paso.chipsActual, paso.multActual, this
                    ));
                }
            }
        } finally {
            ctx.salirDeCopia();
            ctx.terminarCopia(this);
        }
    }
}
