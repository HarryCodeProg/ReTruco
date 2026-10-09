package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class RevolucionDeMayo extends Joker {

    public RevolucionDeMayo() {
        super(
            120,
            "RevolucionDeMayo",
            "RevolucionDeMayo",
            "Todos los santos en la tienda son gratis",
            Rareza.muyRaro,
            8, // placeholder: ajustá el coste real
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // No hace nada: la tienda consulta la presencia de este joker directamente vía PanelTienda.tieneRevolucionDeMayo()
    }

    @Override
    public Joker copiar() {
        RevolucionDeMayo copia = new RevolucionDeMayo();
        copiarEstado(copia);
        return copia;
    }
}
