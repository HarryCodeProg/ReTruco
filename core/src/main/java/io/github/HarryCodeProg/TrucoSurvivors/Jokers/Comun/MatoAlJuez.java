package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolSantosTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

public class MatoAlJuez extends Joker {

    public MatoAlJuez() {
        super(
            55,
            "Mato Al Juez",
            "MatoAlJuez",
            "Matar con 8 o 9 genera un santo aleatorio",
            Rareza.comun,
            5,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TV, CategoriaJoker.NACIONAL
        );
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Matar con 8 o 9 genera un santo aleatorio";
    }

    @Override
    public Joker copiar() {
        MatoAlJuez copia = new MatoAlJuez();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Se activa exclusivamente cuando tu carta le gana a la del rival
        if (evento != EventoJuego.AL_MATAR_CARTA) return;
        Carta cartaQueMato = ctx.getCartaEnResolucion();
        if (cartaQueMato == null) return;
        // Verificamos si la carta que ganó es un 8 o un 9
        int numero = cartaQueMato.getNumero();
        if (numero == 8 || numero == 9) {
            // 1. Verificar si hay espacio para un Santo nuevo
            if (ctx.getJugador().getSantos().size() >= ctx.getJugador().getTamañoSantos()) {
                return; // Si el inventario está lleno, no hacemos nada
            }
            // 2. Generar y agregar el Santo aleatorio usando PoolSantosTienda
            PoolSantosTienda pool = new PoolSantosTienda();
            Santo santoNuevo = pool.tomarAleatorio();
            if (santoNuevo != null) {
                ctx.getJugador().agregarSanto(santoNuevo);
            }
        }
    }
}
