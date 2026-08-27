package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;

import java.util.EnumSet;
import java.util.Set;

public abstract class JokerCopiaVecino extends Joker {

    // FIX: Agregamos AL_MATAR_CARTA (y deberías agregar cualquier otro evento que genere
    // consumibles o dinero instantáneo, como AL_DESCARTAR si tenés jokers de descarte).
    private static final Set<EventoJuego> EVENTOS_COPIABLES = EnumSet.of(
        EventoJuego.ANTES_DE_SUMAR_TRUCO,
        EventoJuego.ANTES_DE_SUMAR_ENVIDO,
        EventoJuego.AL_PUNTUAR_CARTA,
        EventoJuego.AL_PUNTUAR_CARTA_ENVIDO,
        EventoJuego.AL_MATAR_CARTA
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
        if (!ctx.marcarUsado(this, evento)) return;
        ResolucionPuntaje resolucion = ctx.getResolucionActual();
        int pasosAntes = (resolucion != null) ? resolucion.getLog().size() : 0;
        // Ejecutamos el efecto del vecino
        vecino.aplicarEfecto(evento, ctx, juego);
        // --- MAGIA VISUAL: Secuestro de identidad ---
        // Si el vecino generó pasos de puntuación, les cambiamos la firma para que
        // la UI haga brillar a Andes y muestre su nombre, igual que Blueprint.
        // --- MAGIA VISUAL: Secuestro de identidad ---
        if (resolucion != null) {
            int pasosDespues = resolucion.getLog().size();
            for (int i = pasosAntes; i < pasosDespues; i++) {
                ResolucionPuntaje.PasoResolucion paso = resolucion.getLog().get(i);

                // Si el paso fue generado por el vecino, lo reemplazamos por uno idéntico
                // pero a nombre de este Joker (la copia)
                if (paso.origenRef == vecino) {
                    resolucion.getLog().set(i, new ResolucionPuntaje.PasoResolucion(
                        this.getNombre(),
                        paso.tipo,
                        paso.valor,
                        paso.chipsActual,
                        paso.multActual,
                        this
                    ));
                }
            }
        }
    }

}
