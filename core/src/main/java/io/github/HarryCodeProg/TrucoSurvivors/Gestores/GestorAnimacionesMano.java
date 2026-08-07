package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;

import java.util.ArrayList;

public class GestorAnimacionesMano {
    private static final float MAZO_VISUAL_X = 1150f;
    private static final float MAZO_VISUAL_Y = 50f;
    private static final float DELAY_REPARTO = 0.08f;
    private final ArrayList<VistaCarta> cartasJugador;
    private final ArrayList<VistaCarta> cartasRival;
    private final ArrayList<VistaCarta> cartasMesaJugador;
    private final ArrayList<VistaCarta> cartasMesaRival;
    private final Runnable callbackOrganizarCartas;
    private final ArrayList<VistaCarta> colaJugador = new ArrayList<>();
    private final ArrayList<VistaCarta> colaRival = new ArrayList<>();
    private boolean esperandoTransicion;
    private boolean turnoJugador = true;
    private float cronometro;

    public GestorAnimacionesMano(ArrayList<VistaCarta> cartasJugador, ArrayList<VistaCarta> cartasRival,
        ArrayList<VistaCarta> cartasMesaJugador, ArrayList<VistaCarta> cartasMesaRival,
                                 Runnable callbackOrganizarCartas) {
        this.cartasJugador = cartasJugador;
        this.cartasRival = cartasRival;
        this.cartasMesaJugador = cartasMesaJugador;
        this.cartasMesaRival = cartasMesaRival;
        this.callbackOrganizarCartas = callbackOrganizarCartas;
    }

    public void iniciarTransicion(
        ArrayList<Carta> nuevasJugador,
        ArrayList<Carta> nuevasRival) {
        if (esperandoTransicion)
            return;
        esperandoTransicion = true;
        System.out.println("[ANIM] iniciarTransicion: esperandoTransicion=true, cartasViejas jugador=" + cartasJugador.size() + " rival=" + cartasRival.size() + " mesaJ=" + cartasMesaJugador.size() + " mesaR=" + cartasMesaRival.size());
        ArrayList<VistaCarta> viejas = new ArrayList<>();
        viejas.addAll(cartasJugador);
        viejas.addAll(cartasMesaJugador);
        viejas.addAll(cartasMesaRival);
        viejas.addAll(cartasRival);
        if (viejas.isEmpty()) {
            limpiarVista();
            prepararColas(nuevasJugador, nuevasRival);
            return;
        }
        final int[] pendientes = {viejas.size()};
        for (VistaCarta carta : viejas) {
            carta.animarHacia(MAZO_VISUAL_X, MAZO_VISUAL_Y, () -> {
                pendientes[0]--;
                if (pendientes[0] == 0) {
                    limpiarVista();
                    prepararColas(nuevasJugador, nuevasRival);
                }
            });

        }
    }

    public void iniciarDescarte(
        ArrayList<VistaCarta> cartasViejas,
        ArrayList<Carta> cartasNuevas) {
        if (esperandoTransicion)
            return;
        esperandoTransicion = true;
        colaJugador.clear();
        colaRival.clear();
        for (Carta carta : cartasNuevas) {
            VistaCarta vista = new VistaCarta(carta, false,Main.getInstance().getAtlasCartas());
            vista.setPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            colaJugador.add(vista);
        }
        if (cartasViejas.isEmpty()) {
            cronometro = 0;
            turnoJugador = true;
            return;
        }
        final int[] pendientes = {cartasViejas.size()};
        for (VistaCarta vieja : cartasViejas) {
            vieja.animarHacia(MAZO_VISUAL_X, MAZO_VISUAL_Y, () -> {
                cartasJugador.remove(vieja);
                vieja.dispose();
                pendientes[0]--;
                if (pendientes[0] == 0) {
                    cronometro = 0;
                    turnoJugador = true;
                }
            });
        }
    }

    private void limpiarVista() {
        for (VistaCarta c : cartasJugador)
            c.dispose();
        for (VistaCarta c : cartasRival)
            c.dispose();
        for (VistaCarta c : cartasMesaJugador)
            c.dispose();
        for (VistaCarta c : cartasMesaRival)
            c.dispose();
        cartasJugador.clear();
        cartasRival.clear();
        cartasMesaJugador.clear();
        cartasMesaRival.clear();
    }

    private void prepararColas(ArrayList<Carta> nuevasJugador, ArrayList<Carta> nuevasRival) {
        colaJugador.clear();
        colaRival.clear();
        for (Carta carta : nuevasJugador) {
            VistaCarta vista = new VistaCarta(carta, false,Main.getInstance().getAtlasCartas());
            vista.setPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            // Setea también su hand position inicial para evitar tirones visuales
            vista.setHandPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            colaJugador.add(vista);
        }
        for (Carta carta : nuevasRival) {
            VistaCarta vista = new VistaCarta(carta, true,Main.getInstance().getAtlasCartas());
            vista.setTamaño(70f, 80f);
            vista.setPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            vista.setHandPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            colaRival.add(vista);
        }
        cronometro = 0;
        turnoJugador = true;
    }

    public void update(float delta) {
        if (colaJugador.isEmpty() && colaRival.isEmpty()) {
            if (esperandoTransicion
                && todasLlegaron(cartasJugador)
                && todasLlegaron(cartasRival)
                && todasLlegaron(cartasMesaJugador)
                && todasLlegaron(cartasMesaRival)) {
                esperandoTransicion = false;
                System.out.println("[ANIM] esperandoTransicion -> false (todas llegaron)");
            }
            return;
        }
        cronometro += delta;
        if (cronometro < DELAY_REPARTO)
            return;
        cronometro = 0;
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        if (turnoJugador) {
            if (!colaJugador.isEmpty()) {
                cartasJugador.add(colaJugador.remove(0));
                sonidos.reproducirSonidoReparto();
                callbackOrganizarCartas.run();
            } else if (!colaRival.isEmpty()) {
                cartasRival.add(colaRival.remove(0));
                sonidos.reproducirSonidoReparto();
                callbackOrganizarCartas.run();
            }
        } else {
            if (!colaRival.isEmpty()) {
                cartasRival.add(colaRival.remove(0));
                sonidos.reproducirSonidoReparto();
                callbackOrganizarCartas.run();
            } else if (!colaJugador.isEmpty()) {
                cartasJugador.add(colaJugador.remove(0));
                sonidos.reproducirSonidoReparto();
                callbackOrganizarCartas.run();
            }

        }
        turnoJugador = !turnoJugador;
    }

    public boolean isEsperandoTransicion() {
        return esperandoTransicion;
    }

    private boolean todasLlegaron(ArrayList<VistaCarta> cartas) {
        for (VistaCarta carta : cartas) {
            if (carta.isAnimando()) return false; // todavia volando al mazo
            if (!carta.llegoATarget()) return false;
        }
        return true;
    }
}
