package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

/**
 * Un efecto asociado a un tier específico de una categoría.
 * El ciclo de vida es:
 *   - onActivar() cuando el tier pasa a estar activo.
 *   - onDesactivar() cuando el tier deja de estar activo.
 *   - onEvento() cada vez que ocurre un evento relevante mientras está activo.
 */
public interface EfectoRasgo {
    /** Se llama UNA vez cuando el rasgo pasa a estar activo con este tier. */
    void onActivar(Juego juego);

    /** Se llama UNA vez cuando el rasgo deja de estar activo con este tier. */
    void onDesactivar(Juego juego);

    /** Opcional: se llama cada vez que ocurre un evento de juego mientras está activo. */
    default void onEvento(EventoJuego evento, ContextoJuego ctx, Juego juego) {}
}
