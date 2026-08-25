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

    /**
     * Como iniciarTransicion, pero solo anima "ida al mazo y vuelta" para las cartas del jugador
     * que efectivamente son nuevas (robadas). Las cartas que ya estaban en mano se mantienen en su
     * VistaCarta existente, sin recrearla ni animarla — simplemente permanecen donde están.
     */
    public void iniciarTransicionConservandoMano(
        ArrayList<Carta> manoJugadorNueva, // jugador.getMano() después de repartir(): conservadas + robadas
        ArrayList<Carta> nuevasRival) {
        if (esperandoTransicion) return;
        esperandoTransicion = true;

        // 1. Separar: qué VistaCarta de cartasJugador sigue representando una carta que continúa en mano
        ArrayList<VistaCarta> vistasConservadas = new ArrayList<>();
        ArrayList<Carta> cartasRealmenteNuevas = new ArrayList<>();
        for (Carta c : manoJugadorNueva) {
            VistaCarta existente = buscarVistaPorCarta(cartasJugador, c);
            if (existente != null) {
                vistasConservadas.add(existente);
            } else {
                cartasRealmenteNuevas.add(c); // no había VistaCarta para esta carta: es robada nueva
            }
        }

        // 2. Todo lo demás (mesa jugador, mesa rival, mano rival, y cualquier VistaCarta de cartasJugador
        // que ya no está en la mano nueva) sí vuela al mazo y se destruye, como antes.
        ArrayList<VistaCarta> viejasAVolar = new ArrayList<>();
        for (VistaCarta v : cartasJugador) {
            if (!vistasConservadas.contains(v)) viejasAVolar.add(v);
        }
        viejasAVolar.addAll(cartasMesaJugador);
        viejasAVolar.addAll(cartasMesaRival);
        viejasAVolar.addAll(cartasRival);

        if (viejasAVolar.isEmpty()) {
            limpiarVistaExcepto(vistasConservadas);
            prepararColasParciales(cartasRealmenteNuevas, nuevasRival, vistasConservadas);
            return;
        }
        final int[] pendientes = {viejasAVolar.size()};
        for (VistaCarta carta : viejasAVolar) {
            carta.animarHacia(MAZO_VISUAL_X, MAZO_VISUAL_Y, () -> {
                pendientes[0]--;
                if (pendientes[0] == 0) {
                    limpiarVistaExcepto(vistasConservadas);
                    prepararColasParciales(cartasRealmenteNuevas, nuevasRival, vistasConservadas);
                }
            });
        }
    }

    private VistaCarta buscarVistaPorCarta(ArrayList<VistaCarta> lista, Carta carta) {
        for (VistaCarta v : lista) {
            if (v.getCarta() == carta) return v;
        }
        return null;
    }

    private void limpiarVistaExcepto(ArrayList<VistaCarta> conservar) {
        for (VistaCarta c : cartasJugador) {
            if (!conservar.contains(c)) c.dispose();
        }
        for (VistaCarta c : cartasRival) c.dispose();
        for (VistaCarta c : cartasMesaJugador) c.dispose();
        for (VistaCarta c : cartasMesaRival) c.dispose();
        cartasJugador.clear();
        cartasJugador.addAll(conservar); // las conservadas vuelven a la lista tal cual, sin recrear
        cartasRival.clear();
        cartasMesaJugador.clear();
        cartasMesaRival.clear();
    }

    private void prepararColasParciales(ArrayList<Carta> nuevasJugador, ArrayList<Carta> nuevasRival, ArrayList<VistaCarta> yaConservadas) {
        colaJugador.clear();
        colaRival.clear();
        for (Carta carta : nuevasJugador) { // solo las robadas nuevas entran en la cola de reparto animado
            VistaCarta vista = new VistaCarta(carta, false, Main.getInstance().getAtlasCartas());
            vista.setPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            vista.setHandPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            colaJugador.add(vista);
        }
        for (Carta carta : nuevasRival) {
            VistaCarta vista = new VistaCarta(carta, true, Main.getInstance().getAtlasCartas());
            vista.setTamaño(70f, 80f);
            vista.setPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            vista.setHandPosition(MAZO_VISUAL_X, MAZO_VISUAL_Y);
            colaRival.add(vista);
        }
        cronometro = 0;
        turnoJugador = true;
        callbackOrganizarCartas.run(); // reposiciona ya mismo las conservadas, mientras entran las nuevas de a poco
    }
}
