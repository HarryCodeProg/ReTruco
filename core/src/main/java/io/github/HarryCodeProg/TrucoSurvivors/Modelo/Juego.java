package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.*;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.GestorJokers;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class Juego {
    private Jugador jugador;
    private Jugador rival;
    private Mesa mesa;
    private Jugador turnoActual;
    private boolean jugadorEsMano;
    private double puntosJugador;
    private double puntosRival;
    private int faseActual;
    private int rondaActual;
    private double puntajeMeta;
    private ArrayList<CantoEnvido> cantosEnvido;
    private ArrayList<CantoTruco> cantosTruco = new ArrayList<>();
    private ArrayList<ResultadoMano> resultados;
    private int descartesActuales;
    private GestorJokers gestorJokers;
    private boolean manoFinalizada;
    private Jugador cantorEnvidoPendiente;
    private Integer nivelEnvidoPendiente;
    private Jugador cantorTrucoPendiente;
    private Integer nivelTrucoPendiente;
    private int nivelTrucoActual = 0;
    private Jugador ultimoCantorTruco;
    private int nivelEnvidoActual = 0;
    private Jugador ultimoCantorEnvido;
    private boolean ultimaRespuestaTrucoFueNoQuiero = false;
    private ResolucionPuntaje ultimaResolucion;
    private ResolucionPuntaje ultimaResolucionEnvido;
    private ResolutorSecuencia resolutorSecuencia;
    private boolean primeraCartaQueMataAplicada = false;
    private boolean primeraCartaQueNoMataAplicada = false;
    private int manosGanadasConsecutivas = 0;
    private boolean recompensaFinDeRondaAplicada = false;
    private boolean ultimoGanadorEnvidoFueJugador = false;

    public Juego(Jugador jugador, Jugador rival, Mazo mazoRival) {
        this.jugador = jugador;
        this.rival = rival;
        this.jugadorEsMano = true;
        this.turnoActual = jugador;
        this.mesa = new Mesa();
        this.cantosEnvido = new ArrayList<>();
        this.resultados = new ArrayList<>();
        this.rival.setMazo(mazoRival);
        this.gestorJokers = new GestorJokers(jugador);
        this.resolutorSecuencia = new ResolutorSecuencia(gestorJokers, this);
        this.descartesActuales = jugador.getDescartesMaximos();

        limpiarEstadoResidualDeCombateAnterior();
        jugador.multTrucoOriginal();
        jugador.multEnvidoOriginal();
        rival.multTrucoOriginal();
        rival.multEnvidoOriginal();
        jugador.reiniciarManos();
        repartir();
    }

    private ContextoJuego crearContexto() {
        return new ContextoJuego(jugador, rival, jugador.getMazo(), mesa, this);
    }

    public void repartir() {
        this.jugador.getMazo().barajar();
        this.rival.getMazo().barajar();
        int faltanJugador = jugador.getTamañoMano() - jugador.getMano().size();
        if (faltanJugador > 0) {
            jugador.robar(jugador.getMazo(), faltanJugador); // roba lo que falte; si el mazo tiene menos, robar() ya corta solo (tomarCarta() devuelve null y el loop no agrega)
        }
        jugador.ordenarMano();
        int faltanRival = rival.getTamañoMano() - rival.getMano().size();
        if (faltanRival > 0) {
            rival.robar(rival.getMazo(), faltanRival);
        }
        gestorJokers.disparar(EventoJuego.POST_REPARTO, crearContexto(), this);
    }

    private void limpiarEstadoResidualDeCombateAnterior() {
        jugador.getMazo().reciclarCartasTomadas();
        jugador.limpiarMano();
    }

    public static Mazo crearMazoRival(int nivelDificultad) {
        Mazo mazo = Mazo.crearMazoBase();
        for (int i = 0; i < nivelDificultad && i < mazo.getMazo().size(); i++) {
            mazo.getMazo().get(i).modificarValorTrucoPermanente(2); // ajustar nombre real del metodo en Carta
        }
        return mazo;
    }

    private double puntosPendientesEnvidoRival = 0;

    public void ganadorEnvido() {
        double puntosEJ = jugador.getPuntosEnvido();
        double puntosER = rival.getPuntosEnvido();
        double sumMult = calcularPuntosEnvido();
        jugador.aumentarMultiplicadorEnvidoTemporal(sumMult);
        rival.aumentarMultiplicadorEnvidoTemporal(sumMult);
        boolean ganaJugador = (puntosEJ == puntosER) ? jugadorEsMano : (puntosEJ > puntosER);
        this.ultimoGanadorEnvidoFueJugador = ganaJugador; // Guardamos quién ganó para saber a quién darle los puntos después
        if (ganaJugador) {
            ArrayList<Carta> cartasEnvido = jugador.getCartasEnvidoGanador();
            double chipsBase = (cartasEnvido.size() == 2) ? 20 : 0;
            ResolucionPuntaje resolucion = new ResolucionPuntaje(chipsBase, jugador.getMultiplicadorEnvido());
            ContextoJuego ctx = crearContexto();
            ctx.setResolucionActual(resolucion);
            ctx.setCartasContribuyentesEnvido(cartasEnvido);
            gestorJokers.disparar(EventoJuego.AL_GANAR_ENVIDO_CANTO, ctx, this);
            resolutorSecuencia.resolverEnvido(cartasEnvido, ctx, EventoJuego.AL_PUNTUAR_CARTA_ENVIDO, EventoJuego.ANTES_DE_SUMAR_ENVIDO);
            this.ultimaResolucionEnvido = resolucion;
        } else {
            // FIX: Ahora el rival también crea una resolución para disparar la animación visual
            ResolucionPuntaje resolucion = new ResolucionPuntaje(0, rival.getMultiplicadorEnvido());
            resolucion.sumarChips(puntosER, "Cartas Rival", null); // Esto hace que aparezca "+33 (Cartas Rival)" en pantalla
            this.ultimaResolucionEnvido = resolucion;
            gestorJokers.disparar(EventoJuego.AL_PERDER_ENVIDO, crearContexto(), this);
        }
    }

    public double calcularPuntosEnvido() {
        double total = 0;
        for (CantoEnvido canto : cantosEnvido) {
            total += canto.getValorBase();
        }
        return total;
    }

    public void irAlMazo() {
        vaciarTruco();
        vaciarCantos();
        devolverCartas();
        avanzarMano();
        this.turnoActual = jugadorEsMano ? jugador : rival;
        repartir();
    }

    public void recargarDescartes() {
        this.descartesActuales = jugador.getDescartesMaximos();
    }

    public void setManoFinalizada(boolean b) {
        this.manoFinalizada = b;
    }

    private void resetearEstadoTemporalDeMano() {
        vaciarTruco();
        vaciarCantos();
    }

    public void responderEnvidoSinAnimacion(boolean quiero) {
        responderEnvido(quiero);
        if (quiero) {
            aplicarResultadoEnvido(); // aplica inmediato, sin esperar animación
        }
    }

    public ArrayList<Carta> descartarCartas(ArrayList<Carta> cartas) {
        ArrayList<Carta> cartasNuevas = new ArrayList<>();
        if (cartas.isEmpty()) return cartasNuevas;
        if (!hayDescartes()) return cartasNuevas;
        if (cartas.size() > jugador.getMazo().getCantidadDisponibles()) return cartasNuevas;
        for (Carta carta : cartas) {
            jugador.eliminarCarta(carta);
            jugador.getMazo().descartarCarta(carta); // va a "descartadas", vuelve al mazo real en limpiarDescartadas()
            jugador.robar(jugador.getMazo(), 1);
            ArrayList<Carta> mano = jugador.getMano();
            cartasNuevas.add(mano.get(mano.size() - 1));
        }
        restarUnDescarte();
        gestorJokers.disparar(EventoJuego.AL_DESCARTAR, crearContexto(), this);
        return cartasNuevas;
    }

    public void setTurnoActual(Jugador turnoActual) {
        this.turnoActual = turnoActual;
    }

    public boolean hayDescartes() {
        return this.descartesActuales > 0;
    }

    public void restarUnDescarte() {
        this.descartesActuales -= 1;
    }

    public int getMazoDisponible() {
        return jugador.getMazo().getCantidadDisponibles();
    }

    public Mazo getMazoJugador() {
        return this.jugador.getMazo();
    }

    public ResolucionPuntaje getUltimaResolucion() {
        return ultimaResolucion;
    }

    public ResolucionPuntaje getUltimaResolucionEnvido() {
        return ultimaResolucionEnvido;
    }

    public void devolverCartas() {
        for (Carta c : mesa.getMesaJugador()) {
            c.resetearValores();
        }
        for (Carta c : mesa.getMesaRival()) {
            c.resetearValores();
        }
        for (Carta c : jugador.getMano()) {
            c.resetearValores(); // cartas que quedaron sin jugar, conservan su lugar en mano
        }
        for (Carta c : rival.getMano()) {
            c.resetearValores();
        }
        mesa.limpiarMesa();
    }

    public void jugarMano(int i) {
        if (i == 0) gestorJokers.disparar(EventoJuego.AL_JUGAR_PRIMERA_CARTA, crearContexto(), this);
        else if (i == 1) gestorJokers.disparar(EventoJuego.AL_JUGAR_SEGUNDA_CARTA, crearContexto(), this);
        ResultadoMano resultado = matoCarta(i);
        resultados.add(resultado);
        Carta cartaJugador = mesa.getMesaJugador().get(i);
        Carta cartaRival = mesa.getMesaRival().get(i);
        if (resultado == ResultadoMano.JUGADOR) {
            this.turnoActual = jugador;
            ContextoJuego ctxMato = crearContexto();
            ctxMato.setCartaEnResolucion(cartaJugador);
            gestorJokers.disparar(EventoJuego.AL_MATAR_CARTA, ctxMato, this);
            gestorJokers.disparar(EventoJuego.AL_GANAR_BAZA, crearContexto(), this);
        } else if (resultado == ResultadoMano.RIVAL) {
            this.turnoActual = rival;
            ContextoJuego ctxPerdio = crearContexto();
            ctxPerdio.setCartaEnResolucion(cartaJugador);          // la que perdio (del jugador)
            ctxPerdio.setCartaOponenteEnResolucion(cartaRival);    // la que gano (del rival)
            gestorJokers.disparar(EventoJuego.AL_SER_MATADO, ctxPerdio, this);
        } else {
            this.turnoActual = jugadorEsMano ? jugador : rival;
        }
    }

    public ResultadoMano matoCarta(int i) {
        double valorJugador = mesa.getMesaJugador().get(i).getValorTrucoActual();
        double valorRival = mesa.getMesaRival().get(i).getValorTrucoActual();
        if (valorJugador > valorRival) return ResultadoMano.JUGADOR;
        if (valorJugador < valorRival) return ResultadoMano.RIVAL;
        return ResultadoMano.PARDA;
    }

    public Jugador ganoTruco() {
        if (resultados.size() < 2) return null;
        ResultadoMano r1 = resultados.get(0);
        ResultadoMano r2 = resultados.get(1);
        ResultadoMano r3 = resultados.size() > 2 ? resultados.get(2) : null;
        boolean dobleParda = (r1 == ResultadoMano.PARDA && r2 == ResultadoMano.PARDA);
        if (r1 == ResultadoMano.JUGADOR && r2 == ResultadoMano.JUGADOR) return jugador;
        if (r1 == ResultadoMano.RIVAL && r2 == ResultadoMano.RIVAL) return rival;
        if (r1 == ResultadoMano.PARDA && r2 == ResultadoMano.JUGADOR) return jugador;
        if (r1 == ResultadoMano.PARDA && r2 == ResultadoMano.RIVAL) return rival;
        if (r1 == ResultadoMano.JUGADOR && r2 == ResultadoMano.PARDA) return jugador;
        if (r1 == ResultadoMano.RIVAL && r2 == ResultadoMano.PARDA) return rival;
        if (r3 == null) return null;
        if (r3 == ResultadoMano.JUGADOR) return jugador;
        if (r3 == ResultadoMano.RIVAL) return rival;
        if (dobleParda) return null;
        if (r1 == ResultadoMano.JUGADOR) return jugador;
        if (r1 == ResultadoMano.RIVAL) return rival;
        return null;
    }

    public Jugador obtenerGanadorTruco() {
        return ganoTruco();
    }

    public void finalizarManoTruco() {
        if (manoFinalizada) return;
        Jugador ganador = obtenerGanadorTruco();
        if (ganador == null) return;
        manoFinalizada = true;
        consumirHand();
        int bazasJugadas = resultados.size();
        if (ganador.equals(jugador)) {
            ArrayList<Carta> cartasGanadoras = new ArrayList<>();
            for (int i = 0; i < bazasJugadas; i++) {
                Carta cJ = mesa.getMesaJugador().get(i);
                Carta cR = mesa.getMesaRival().get(i);
                if (cJ.getValorTrucoActual() > cR.getValorTrucoActual()) {
                    cartasGanadoras.add(cJ);
                }
            }
            ResolucionPuntaje resolucion = new ResolucionPuntaje(0, jugador.getMultiplicadorTruco());
            ContextoJuego ctx = crearContexto();
            ctx.setResolucionActual(resolucion);
            resolutorSecuencia.resolver(cartasGanadoras, ctx, EventoJuego.AL_PUNTUAR_CARTA, EventoJuego.ANTES_DE_SUMAR_TRUCO);
            this.ultimaResolucion = resolucion;
            puntosJugador += resolucion.calcularPuntajeFinal();
            gestorJokers.disparar(EventoJuego.AL_GANAR_TRUCO, crearContexto(), this);
            manosGanadasConsecutivas++;
        } else {
            this.ultimaResolucion = null;
            double acumulador = 0;
            for (int i = 0; i < bazasJugadas; i++) {
                double vJ = mesa.getMesaJugador().get(i).getValorTrucoActual();
                double vR = mesa.getMesaRival().get(i).getValorTrucoActual();
                if (vR > vJ)
                    acumulador += mesa.getMesaRival().get(i).getValorTrucoEfectivo();
            }
            puntosRival += acumulador * rival.getMultiplicadorTruco();
            manosGanadasConsecutivas = 0;
            gestorJokers.disparar(EventoJuego.AL_PERDER_TRUCO, crearContexto(), this);
        }
        gestorJokers.disparar(EventoJuego.TERMINO_MANO, crearContexto(), this);
        verificarEstadoCombate();
    }

    public boolean terminoLaMano() {
        return obtenerGanadorTruco() != null;
    }

    public boolean cantarTruco(Jugador quien, int nivel) {
        if (nivelTrucoActual != 0) return false;
        return proponerTruco(quien, nivel);
    }

    public boolean escalarTruco(Jugador quien) {
        if (cantorTrucoPendiente == null) return false;
        if (quien.equals(cantorTrucoPendiente)) return false;
        int siguiente = nivelTrucoActual + 1;
        return proponerTruco(quien, siguiente);
    }

    private boolean proponerTruco(Jugador quien, int nivel) {
        if (nivel != nivelTrucoActual + 1) return false;
        if (nivel > 3) return false;
        if (quien.equals(ultimoCantorTruco)) return false;
        cantorTrucoPendiente = quien;
        nivelTrucoPendiente = nivel;
        nivelTrucoActual = nivel;
        ultimoCantorTruco = quien;
        return true;
    }

    public boolean hayCantoTrucoPendiente() {
        return cantorTrucoPendiente != null;
    }

    public Jugador getCantorTrucoPendiente() {
        return cantorTrucoPendiente;
    }

    public boolean isPrimeraCartaQueMataAplicada() {
        return primeraCartaQueMataAplicada;
    }

    public void marcarPrimeraCartaQueMataAplicada() {
        this.primeraCartaQueMataAplicada = true;
    }

    public boolean isPrimeraCartaQueNoMataAplicada() {
        return primeraCartaQueNoMataAplicada;
    }

    public void marcarPrimeraCartaQueNoMataAplicada() {
        this.primeraCartaQueNoMataAplicada = true;
    }

    public boolean puedeEscalarTruco(Jugador quien) {
        return cantorTrucoPendiente != null && !quien.equals(cantorTrucoPendiente) && nivelTrucoActual < 3;
    }

    public int proximoNivelTrucoDisponible(Jugador quien) {
        if (cantorTrucoPendiente != null) return -1;
        int siguiente = nivelTrucoActual + 1;
        if (siguiente > 3) return -1;
        if (quien.equals(ultimoCantorTruco)) return -1;
        return siguiente;
    }

    public void responderTruco(boolean quiero) {
        if (cantorTrucoPendiente == null) return;
        Jugador canter = cantorTrucoPendiente;
        agregarCantoTruco(nivelTrucoPendiente);
        ultimaRespuestaTrucoFueNoQuiero = !quiero;
        if (quiero) {
            double puntosCanto = valorNivelTruco(nivelTrucoPendiente);
            jugador.aumentarMultiplicadorTrucoTemporal(puntosCanto);
            rival.aumentarMultiplicadorTrucoTemporal(puntosCanto);
            gestorJokers.disparar(EventoJuego.AL_DECIR_QUIERO_TRUCO, crearContexto(), this);
        } else {
            resolverNoQuieroTruco(canter);
            gestorJokers.disparar(EventoJuego.AL_DECIR_NO_QUIERO_TRUCO, crearContexto(), this); // NUEVO
        }
        cantorTrucoPendiente = null;
        nivelTrucoPendiente = null;
        verificarEstadoCombate();
    }

    public boolean isUltimaRespuestaTrucoFueNoQuiero() {
        return ultimaRespuestaTrucoFueNoQuiero;
    }

    private double valorNivelTruco(int nivel) {
        switch (nivel) {
            case 1:
                return CantoTruco.TRUCO.getPuntos();
            case 2:
                return CantoTruco.RETRUCO.getPuntos();
            case 3:
                return CantoTruco.VALE_CUATRO.getPuntos();
        }
        return 0;
    }

    private void resolverNoQuieroTruco(Jugador canter) {
        double total = calcularPuntosTruco();
        double otorgado = total * 0.5;
        if (canter.equals(jugador)) puntosJugador += otorgado;
        else puntosRival += otorgado;
    }

    public double calcularPuntosTruco() {
        double total = 0;
        for (CantoTruco canto : cantosTruco) total += canto.getPuntos();
        return total;
    }

    private void agregarCantoTruco(int i) {
        switch (i) {
            case 1:
                cantosTruco.add(CantoTruco.TRUCO);
                break;
            case 2:
                cantosTruco.add(CantoTruco.RETRUCO);
                break;
            case 3:
                cantosTruco.add(CantoTruco.VALE_CUATRO);
                break;
        }
    }

    public boolean cantarEnvido(Jugador quien, int nivel) {
        if (nivelEnvidoActual != 0) return false;
        return proponerEnvido(quien, nivel);
    }

    public boolean escalarEnvido(Jugador quien) {
        if (cantorEnvidoPendiente == null) return false;
        if (quien.equals(cantorEnvidoPendiente)) return false;
        int siguiente = nivelEnvidoActual + 1;
        return proponerEnvido(quien, siguiente);
    }

    private boolean proponerEnvido(Jugador quien, int nivel) {
        if (!estaEnVentanaDeEnvido()) return false;
        if (nivel <= nivelEnvidoActual) return false;
        if (quien.equals(ultimoCantorEnvido)) return false;
        cantorEnvidoPendiente = quien;
        nivelEnvidoPendiente = nivel;
        nivelEnvidoActual = nivel;
        ultimoCantorEnvido = quien;
        gestorJokers.disparar(EventoJuego.AL_CANTAR_ENVIDO, crearContexto(), this); // NUEVO
        return true;
    }

    private boolean estaEnVentanaDeEnvido() {
        return mesa.getMesaJugador().isEmpty() || mesa.getMesaRival().isEmpty();
    }

    public boolean hayCantoEnvidoPendiente() {
        return cantorEnvidoPendiente != null;
    }

    public Jugador getCantorEnvidoPendiente() {
        return cantorEnvidoPendiente;
    }

    public boolean puedeEscalarEnvido(Jugador quien) {
        return cantorEnvidoPendiente != null && !quien.equals(cantorEnvidoPendiente) && nivelEnvidoActual < 3;
    }

    public boolean puedeCantarEnvidoNivel(Jugador quien, int nivel) {
        return estaEnVentanaDeEnvido() && cantorEnvidoPendiente == null && nivel > nivelEnvidoActual && !quien.equals(ultimoCantorEnvido);
    }

    public void responderEnvido(boolean quiero) {
        if (cantorEnvidoPendiente == null) return;
        Jugador canter = cantorEnvidoPendiente;
        agregarCantoEnvido(nivelEnvidoPendiente);
        if (quiero) {
            ganadorEnvido();
        } else {
            prepararResolucionNoQuieroEnvido(canter); // FIX: arma una ResolucionPuntaje simple, no aplica todavía
        }
        cantorEnvidoPendiente = null;
        nivelEnvidoPendiente = null;
    }

    private Jugador canterNoQuieroPendiente;

    private void prepararResolucionNoQuieroEnvido(Jugador canter) {
        double sumMult = calcularPuntosEnvido();
        double puntosCantor = canter.getPuntosEnvido();
        double total = puntosCantor * sumMult;
        double otorgado = total * 0.5;

        this.canterNoQuieroPendiente = canter;
        this.ultimoGanadorEnvidoFueJugador = canter.equals(jugador);

        ResolucionPuntaje resolucion = new ResolucionPuntaje(0, 1); // sin mult real, chips=otorgado directo
        resolucion.sumarChips(otorgado, "No Quiero", null);
        this.ultimaResolucionEnvido = resolucion;
    }

    public void aplicarResultadoEnvido() {
        if (ultimaResolucionEnvido != null) {
            double puntosFinales = ultimaResolucionEnvido.calcularPuntajeFinal();
            if (ultimoGanadorEnvidoFueJugador) {
                puntosJugador += puntosFinales;
                gestorJokers.disparar(EventoJuego.AL_GANAR_ENVIDO, crearContexto(), this);
            } else {
                puntosRival += puntosFinales;
            }
        }
        canterNoQuieroPendiente = null;
        verificarEstadoCombate();
    }

    private void resolverNoQuieroEnvido(Jugador canter) {
        double sumMult = calcularPuntosEnvido();
        double puntosCantor = canter.getPuntosEnvido();
        double total = puntosCantor * sumMult;
        double otorgado = total * 0.5;
        if (canter.equals(jugador)) puntosJugador += otorgado;
        else puntosRival += otorgado;
    }

    public void agregarCartaJugador(Carta carta) {
        jugador.eliminarCarta(carta);
        mesa.agregarCartaJugador(carta);
    }

    public void agregarCartaRival(Carta carta) {
        rival.eliminarCarta(carta);
        mesa.agregarCartaRival(carta);
    }

    public void vaciarTruco() {
        this.resultados.clear();
        this.cantosTruco.clear();
        this.nivelTrucoActual = 0;
        this.ultimoCantorTruco = null;
        this.cantorTrucoPendiente = null;
        this.nivelTrucoPendiente = null;
        this.primeraCartaQueMataAplicada = false;
        this.primeraCartaQueNoMataAplicada = false;
        jugador.multTrucoOriginal();
        rival.multTrucoOriginal();
    }

    public void vaciarCantos() {
        this.cantosEnvido.clear();
        this.nivelEnvidoActual = 0;
        this.ultimoCantorEnvido = null;
        this.cantorEnvidoPendiente = null;
        this.nivelEnvidoPendiente = null;
        jugador.multEnvidoOriginal();
        rival.multEnvidoOriginal();
    }

    public Mesa getMesa() {
        return this.mesa;
    }

    public Jugador getTurnoActual() {
        return this.turnoActual;
    }

    public Jugador getJugador() {
        return this.jugador;
    }

    public Jugador getRival() {
        return this.rival;
    }

    public void agregarCantoEnvido(int i) {
        switch (i) {
            case 1:
                this.cantosEnvido.add(CantoEnvido.ENVIDO);
                break;
            case 2:
                this.cantosEnvido.add(CantoEnvido.REAL_ENVIDO);
                break;
            case 3:
                this.cantosEnvido.add(CantoEnvido.FALTA_ENVIDO);
                break;
        }
    }

    public void avanzarMano() {
        jugadorEsMano = !jugadorEsMano;
        faseActual += 1;
        rondaActual = 1;
    }

    public double getPuntosJugador() {
        return puntosJugador;
    }

    public double getPuntosRival() {
        return puntosRival;
    }

    public void setRival(Jugador rival) {
        this.rival = rival;
    }

    public void setPuntajeMeta(double i) {
        this.puntajeMeta = i;
    }

    public EstadoCombate verificarEstadoCombate() {
        if (puntosJugador >= puntajeMeta) {
            if (!recompensaFinDeRondaAplicada) {
                recompensaFinDeRondaAplicada = true;
                gestorJokers.disparar(EventoJuego.TERMINO_MANO, crearContexto(), this);
            }
            return EstadoCombate.VICTORIA_JUGADOR;
        }
        if (puntosRival >= puntajeMeta) {
            return EstadoCombate.VICTORIA_RIVAL;
        }
        if (jugador.getManosActuales() <= 0) {
            return EstadoCombate.VICTORIA_RIVAL;
        }
        return EstadoCombate.EN_PROGRESO;
    }

    public void restaurarDescartes() {
        descartesActuales = jugador.getDescartesMaximos();
    }

    public double getPuntajeMeta() {
        return this.puntajeMeta;
    }

    public int getManosGanadasConsecutivas() {
        return manosGanadasConsecutivas;
    }

    public int getDescartesActuales() {
        return this.descartesActuales;
    }

    public void consumirHand() {
        jugador.consumirMano();
    }
}
