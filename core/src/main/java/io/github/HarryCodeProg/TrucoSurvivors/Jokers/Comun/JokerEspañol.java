package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.CartaDrop;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Probabilidad;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;


public class JokerEspañol extends Joker {

    public JokerEspañol() {
        super(
            1,
            "Joker Español",
            "JokerEspañol",
            "Al final de cada ronda, agrega una carta aleatoria al mazo",
            Rareza.comun,
            1,
            INDEPENDIENTE,
            CategoriaJoker.INTERNACIONAL
        );
    }

    @Override
    public Joker copiar() {
        JokerEspañol copia = new JokerEspañol();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    private static Carta[] todas(int numero) {
        return new Carta[]{
            new Carta(numero, Palo.BASTO),
            new Carta(numero, Palo.ESPADA),
            new Carta(numero, Palo.COPA),
            new Carta(numero, Palo.ORO)
        };
    }

    private static Carta[] cartas(Object... datos) {
        Carta[] resultado = new Carta[datos.length / 2];
        for (int i = 0, j = 0; i < datos.length; i += 2, j++) {
            resultado[j] = new Carta((Integer) datos[i], (Palo) datos[i + 1]);
        }
        return resultado;
    }

    private static final CartaDrop[] DROPS = {
        new CartaDrop(1, cartas(
            8, Palo.BASTO, 8, Palo.ESPADA, 8, Palo.COPA, 8, Palo.ORO,
            9, Palo.BASTO, 9, Palo.ESPADA, 9, Palo.COPA, 9, Palo.ORO
        )),
        new CartaDrop(10, todas(4)),
        new CartaDrop(10, todas(5)),
        new CartaDrop(10, todas(6)),
        new CartaDrop(10, todas(10)),
        new CartaDrop(10, todas(11)),
        new CartaDrop(10, todas(12)),
        new CartaDrop(10, cartas(1, Palo.COPA, 1, Palo.ORO)),
        new CartaDrop(10, cartas(
            2, Palo.BASTO, 2, Palo.ESPADA, 2, Palo.COPA, 2, Palo.ORO,
            3, Palo.BASTO, 3, Palo.ESPADA, 3, Palo.COPA, 3, Palo.ORO
        )),
        new CartaDrop(5, cartas(7, Palo.ESPADA, 7, Palo.ORO)),
        new CartaDrop(3, cartas(1, Palo.ESPADA, 1, Palo.BASTO))
    };

    private Carta obtenerDrop() {
        int total = 0;
        for (CartaDrop drop : DROPS) {
            total += drop.getPeso();
        }
        int roll = Probabilidad.random().nextInt(total);
        for (CartaDrop drop : DROPS) {
            if (roll < drop.getPeso()) {
                return drop.obtenerCarta(Probabilidad.random());
            }
            roll -= drop.getPeso();
        }
        throw new IllegalStateException("No se pudo obtener un drop.");
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.TERMINO_MANO) return;
        Carta carta = obtenerDrop();
        if (carta != null) {
            juego.getJugador().getMazo().agregarCarta(carta);
        }
    }
}
