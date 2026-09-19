package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Chubut extends Joker {

    public Chubut() {
        super(
            97,
            "Chubut",
            "Chubut",
            "X0.25 multiplicador truco cada vez que terminas una mano con 23 o menos de valor de Envido.\n(Se reinicia si tienes 24 o más)",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        // Arranca en X1.0 (sin efecto extra)
        setAcumulado(1.0);
    }

    @Override
    public Joker copiar() {
        Chubut copia = new Chubut();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {

        // 1. Aplicar el multiplicador al Truco (solo si la racha ya empezó y es mayor a 1)
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            double multActual = getAcumulado();
            if (multActual > 1.0) {
                ctx.getResolucionActual().multiplicarMult(multActual, this.getNombre(), this);
            }
        }

        // 2. Al terminar la mano, evaluamos el puntaje de Envido del jugador
        if (evento == EventoJuego.TERMINO_MANO) {
            // Obtenemos el valor de envido que tuvo el jugador en esta mano
            double puntosEnvidoJugador = juego.getJugador().getPuntosEnvido();
            if (puntosEnvidoJugador <= 23) {
                // Racha mantenida: Sube X0.25
                setAcumulado(getAcumulado() + 0.25);
            } else {
                // Racha rota: Se reinicia a X1.0
                setAcumulado(1.0);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        // Formateamos para que muestre, por ejemplo, "X1.25" o "X2.5"
        // Math.round con *100.0 / 100.0 asegura que queden máximo dos decimales prolijos
        double multActual = Math.round(getAcumulado() * 100.0) / 100.0;
        return getDescripcion() + "\n(Actual: X" + multActual + "[])";
    }
}
