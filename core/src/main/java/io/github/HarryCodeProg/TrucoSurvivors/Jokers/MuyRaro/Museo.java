package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Museo extends Joker {

    public Museo() {
        super(
            105,
            "Museo",
            "Museo",
            "Todos los jokers Muy Raro, Raro y Comun, son gratis en la tienda",
            Rareza.muyRaro,
            8, // placeholder: ajustá coste real
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // sin efecto activo: PanelTienda consulta tieneMuseo() directo
    }

    @Override
    public Joker copiar() {
        Museo copia = new Museo();
        copiarEstado(copia);
        return copia;
    }
}
