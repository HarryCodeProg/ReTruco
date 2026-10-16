package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.*;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.GestorJokers;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorRasgos;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Rasgos.EfectoRasgo;
import io.github.HarryCodeProg.TrucoSurvivors.Rasgos.EfectoTrucoBonus;
import io.github.HarryCodeProg.TrucoSurvivors.Rasgos.EfectoTrucoMultiplicador;

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
    private int envidosGanadosConsecutivos = 0;
    private int cartasJugadasTotal = 0;
    private int cartasDescartadasTotal = 0;
    private int cartasCompradasTotal = 0;
    private int renovacionesTotal = 0;
    private double mejorEnvidoLogrado = 0;
    private final GestorRasgos gestorRasgos = new GestorRasgos();

    public Juego(Jugador jugador, Jugador rival, Mazo mazoRival, boolean saltarRepartoInicial) {
        this.jugador = jugador;
        this.rival = rival;
        this.jugadorEsMano = true;
        this.turnoActual = jugador;
        this.mesa = new Mesa();
        this.cantosEnvido = new ArrayList<>();
        this.resultados = new ArrayList<>();
        this.rival.setMazo(mazoRival);
        this.gestorJokers = new GestorJokers(jugador);
        jugador.addJokerAddedListener(j -> gestorRasgos.recalcular(jugador.getJokers()));
        jugador.addJokerRemovedListener(j -> gestorRasgos.recalcular(jugador.getJokers()));
        gestorRasgos.recalcular(jugador.getJokers());
        gestorRasgos.setJuego(this);
        gestorRasgos.addListener(new GestorRasgos.Listener() {
            @Override public void onRasgoActivado(CategoriaJoker cat, int tierNuevo) {
                // aplicar efecto del rasgo al tierNuevo
            }
            @Override public void onRasgoDesactivado(CategoriaJoker cat, int tierAnterior) {
                // quitar efecto del tier que estaba activo
            }
            @Override public void onTierCambiado(CategoriaJoker cat, int a, int b) {
                // si sube: quitar el efecto del tier a, aplicar el del tier b
                // si baja: al revés
            }
        });
        this.resolutorSecuencia = new ResolutorSecuencia(gestorJokers, this);
        this.descartesActuales = jugador.getDescartesMaximos();
        Carta.setNotificadorCambioPalo(carta -> {
            ContextoJuego ctx = crearContexto();
            ctx.setCartaEnResolucion(carta);
            gestorJokers.disparar(EventoJuego.AL_CAMBIAR_PALO, ctx, this);
            gestorRasgos.dispararEvento(EventoJuego.AL_CAMBIAR_PALO, ctx, this);
        });
        if (!saltarRepartoInicial) {
            limpiarEstadoResidualDeCombateAnterior();
            jugador.multTrucoOriginal();
            jugador.multEnvidoOriginal();
            rival.multTrucoOriginal();
            rival.multEnvidoOriginal();
            jugador.reiniciarManos();
            repartir();
            dispararTodo(EventoJuego.INICIO_COMBATE);
        }
    }

    private ContextoJuego crearContexto() {
        return new ContextoJuego(jugador, rival, jugador.getMazo(), mesa, this);
    }

    private void dispararTodo(EventoJuego evento) {
        ContextoJuego ctx = crearContexto();
        gestorJokers.disparar(evento, ctx, this);
        gestorRasgos.dispararEvento(evento, ctx, this);
    }

    private void dispararTodo(EventoJuego evento, ContextoJuego ctx) {
        gestorJokers.disparar(evento, ctx, this);
        gestorRasgos.dispararEvento(evento, ctx, this);
    }

    public void repartir() {
        this.jugador.getMazo().barajar();
        this.rival.getMazo().barajar();
        int manoAntes = jugador.getMano().size();
        int faltanJugador = jugador.getTamañoMano() - jugador.getMano().size();
        if (faltanJugador > 0) {
            jugador.robar(jugador.getMazo(), faltanJugador);
        }
        jugador.ordenarMano();
        int cartasRepartidasJugador = jugador.getMano().size() - manoAntes;
        int faltanRival = rival.getTamañoMano() - rival.getMano().size();
        if (faltanRival > 0) {
            rival.robar(rival.getMazo(), faltanRival);
        }
        ContextoJuego ctx = crearContexto();
        ctx.setCartasRepartidasEsteEvento(cartasRepartidasJugador);
        dispararTodo(EventoJuego.POST_REPARTO, ctx);
    }

    private void limpiarEstadoResidualDeCombateAnterior() {
        jugador.getMazo().reciclarCartasTomadas();
        jugador.limpiarMano();
    }

    public int getEnvidosGanadosConsecutivos() { return envidosGanadosConsecutivos; }

    public static Mazo crearMazoRival(int nivelDificultad) {
        Mazo mazo = Mazo.crearMazoBase();
        for (int i = 0; i < nivelDificultad && i < mazo.getMazo().size(); i++) {
            mazo.getMazo().get(i).modificarValorTrucoPermanente(2); // ajustar nombre real del metodo en Carta
        }
        return mazo;
    }

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
            dispararTodo(EventoJuego.AL_GANAR_ENVIDO_CANTO, ctx);
            resolutorSecuencia.resolverEnvido(cartasEnvido, ctx, EventoJuego.AL_PUNTUAR_CARTA_ENVIDO, EventoJuego.ANTES_DE_SUMAR_ENVIDO);
            this.ultimaResolucionEnvido = resolucion;
        } else {
            // FIX: Ahora el rival también crea una resolución para disparar la animación visual
            ResolucionPuntaje resolucion = new ResolucionPuntaje(0, rival.getMultiplicadorEnvido());
            resolucion.sumarChips(puntosER, "Cartas Rival", null); // Esto hace que aparezca "+33 (Cartas Rival)" en pantalla
            this.ultimaResolucionEnvido = resolucion;
            dispararTodo(EventoJuego.AL_PERDER_ENVIDO);
        }
    }

    public double calcularPuntosEnvido() {
        double total = 0;
        for (CantoEnvido canto : cantosEnvido) {
            total += canto.getValorBase();
        }
        return total;
    }

    public GestorRasgos getGestorRasgos() { return gestorRasgos; }

    public void irAlMazo() {
        vaciarTruco();
        vaciarCantos();
        devolverCartas();
        avanzarMano();
        this.turnoActual = jugadorEsMano ? jugador : rival;
        repartir();
    }

    public void reciclarMazoAlGanar() {
        jugador.getMazo().reciclarCartasTomadas();
    }

    public void setManoFinalizada(boolean b) {
        this.manoFinalizada = b;
    }

    public ArrayList<Carta> descartarCartas(ArrayList<Carta> cartas) {
        ArrayList<Carta> cartasNuevas = new ArrayList<>();
        if (cartas.isEmpty()) return cartasNuevas;
        if (!hayDescartes()) return cartasNuevas;
        if (cartas.size() > jugador.getMazo().getCantidadDisponibles()) return cartasNuevas;
        for (Carta carta : cartas) {
            jugador.eliminarCarta(carta);
            jugador.getMazo().descartarCarta(carta);
            jugador.robar(jugador.getMazo(), 1);
            ArrayList<Carta> mano = jugador.getMano();
            cartasNuevas.add(mano.get(mano.size() - 1));
        }
        restarUnDescarte();
        cartasDescartadasTotal += cartas.size();
        ContextoJuego ctx = crearContexto();
        ctx.setCartasDescartadasEsteEvento(cartas.size());
        dispararTodo(EventoJuego.AL_DESCARTAR, ctx);
        return cartasNuevas;
    }

    public boolean isJugadorEsMano() { return jugadorEsMano; }
    public int getFaseActual() { return faseActual; }
    public int getRondaActual() { return rondaActual; }

    public void setTurnoActual(Jugador turnoActual) {
        this.turnoActual = turnoActual;
    }

    public boolean hayDescartes() {
        return this.descartesActuales > 0;
    }

    public void restarUnDescarte() {
        this.descartesActuales -= 1;
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

    public ArrayList<ResultadoMano> getResultados() { return resultados; }

    public void restaurarEstado(double puntosJugador, double puntosRival, boolean jugadorEsMano,
                                int faseActual, int rondaActual, int descartesActuales,
                                ArrayList<Carta> manoRival, ArrayList<Carta> mesaJugador, ArrayList<Carta> mesaRival,
                                int cartasJugadasTotal, int cartasDescartadasTotal, int cartasCompradasTotal, int renovacionesTotal,
                                ArrayList<ResultadoMano> resultadosGuardados, boolean manoFinalizadaGuardada) {
        this.puntosJugador = puntosJugador;
        this.puntosRival = puntosRival;
        this.jugadorEsMano = jugadorEsMano;
        this.faseActual = faseActual;
        this.rondaActual = rondaActual;
        this.descartesActuales = descartesActuales;
        this.rival.getMano().clear();
        this.rival.getMano().addAll(manoRival);
        for (Carta c : mesaJugador) this.mesa.agregarCartaJugador(c);
        for (Carta c : mesaRival) this.mesa.agregarCartaRival(c);
        this.cartasJugadasTotal = cartasJugadasTotal;
        this.cartasDescartadasTotal = cartasDescartadasTotal;
        this.cartasCompradasTotal = cartasCompradasTotal;
        this.renovacionesTotal = renovacionesTotal;
        this.resultados.clear();
        this.resultados.addAll(resultadosGuardados);
        this.manoFinalizada = manoFinalizadaGuardada;
    }

    public boolean isManoFinalizada() { return manoFinalizada; }

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
        if (i == 0) dispararTodo(EventoJuego.AL_JUGAR_PRIMERA_CARTA);
        else if (i == 1) dispararTodo(EventoJuego.AL_JUGAR_SEGUNDA_CARTA);
        ResultadoMano resultado = matoCarta(i);
        resultados.add(resultado);
        Carta cartaJugador = mesa.getMesaJugador().get(i);
        Carta cartaRival = mesa.getMesaRival().get(i);
        if (resultado == ResultadoMano.JUGADOR) {
            this.turnoActual = jugador;
            ContextoJuego ctxMato = crearContexto();
            ctxMato.setCartaEnResolucion(cartaJugador);
            dispararTodo(EventoJuego.AL_MATAR_CARTA, ctxMato);
            dispararTodo(EventoJuego.AL_GANAR_BAZA);
        } else if (resultado == ResultadoMano.RIVAL) {
            this.turnoActual = rival;
            ContextoJuego ctxPerdio = crearContexto();
            ctxPerdio.setCartaEnResolucion(cartaJugador);          // la que perdio (del jugador)
            ctxPerdio.setCartaOponenteEnResolucion(cartaRival);    // la que gano (del rival)
            dispararTodo(EventoJuego.AL_SER_MATADO, ctxPerdio);
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
            dispararTodo(EventoJuego.AL_GANAR_TRUCO);
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
            dispararTodo(EventoJuego.AL_PERDER_TRUCO);
        }
        dispararTodo(EventoJuego.TERMINO_MANO);
        verificarEstadoCombate();
    }

    public int getManosGanadasConsecutivas() {
        return manosGanadasConsecutivas;
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
            dispararTodo(EventoJuego.AL_DECIR_QUIERO_TRUCO);
        } else {
            resolverNoQuieroTruco(canter);
            dispararTodo(EventoJuego.AL_DECIR_NO_QUIERO_TRUCO); // NUEVO
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
        dispararTodo(EventoJuego.AL_CANTAR_ENVIDO); // NUEVO
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
                envidosGanadosConsecutivos++; // NUEVO
                dispararTodo(EventoJuego.AL_GANAR_ENVIDO);
            } else {
                puntosRival += puntosFinales;
                envidosGanadosConsecutivos = 0; // NUEVO
            }
        }
        canterNoQuieroPendiente = null;
        verificarEstadoCombate();
    }


    public void agregarCartaAlMazoJugador(Carta carta) {
        jugador.getMazo().agregarCarta(carta);
        ContextoJuego ctx = crearContexto();
        dispararTodo(EventoJuego.AL_AGREGAR_CARTA_AL_MAZO, ctx);
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

    public void sumarPuntosJugador(double puntos) {
        this.puntosJugador += puntos;
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
                ContextoJuego ctx = crearContexto();
                // Buscamos cuál fue la última resolución generada (Truco o Envido) para encadenarle las animaciones de victoria
                ResolucionPuntaje resolucionActiva = this.ultimaResolucion;
                if (resolucionActiva == null && this.ultimaResolucionEnvido != null) {
                    resolucionActiva = this.ultimaResolucionEnvido;
                }
                // Si por algún motivo no hay ninguna, creamos una en blanco
                if (resolucionActiva == null) {
                    resolucionActiva = new ResolucionPuntaje(0, 1);
                    this.ultimaResolucion = resolucionActiva;
                }
                ctx.setResolucionActual(resolucionActiva);
                dispararTodo(EventoJuego.AL_GANAR_COMBATE, ctx);
                dispararTodo(EventoJuego.TERMINO_MANO);
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

    public int getDescartesActuales() {
        return this.descartesActuales;
    }

    public void consumirHand() {
        jugador.consumirMano();
    }

    public int getCartasJugadasTotal() { return cartasJugadasTotal; }
    public int getCartasDescartadasTotal() { return cartasDescartadasTotal; }
    public int getCartasCompradasTotal() { return cartasCompradasTotal; }
    public int getRenovacionesTotal() { return renovacionesTotal; }
    public double getMejorEnvidoLogrado() { return mejorEnvidoLogrado; }

    public void agregarCartaJugador(Carta carta) {
        cartasJugadasTotal++; // NUEVO
        jugador.eliminarCarta(carta);
        mesa.agregarCartaJugador(carta);
    }

    public double getBonusCantoTruco() {
        EfectoRasgo e = gestorRasgos.getEfectoActivo(CategoriaJoker.TRUCO);
        if (e instanceof EfectoTrucoBonus) return ((EfectoTrucoBonus) e).getBonus();
        return 0.0;
    }

    public double getMultiplicadorCantoTruco() {
        EfectoRasgo e = gestorRasgos.getEfectoActivo(CategoriaJoker.TRUCO);
        if (e instanceof EfectoTrucoMultiplicador) return ((EfectoTrucoMultiplicador) e).getMultiplicador();
        return 1.0;
    }

    public double getBonusCantoEnvido() {
        EfectoRasgo e = gestorRasgos.getEfectoActivo(CategoriaJoker.ENVIDO);
        if (e instanceof EfectoTrucoBonus) return ((EfectoTrucoBonus) e).getBonus();
        return 0.0;
    }

    public double getMultiplicadorCantoEnvido() {
        EfectoRasgo e = gestorRasgos.getEfectoActivo(CategoriaJoker.ENVIDO);
        if (e instanceof EfectoTrucoMultiplicador) return ((EfectoTrucoMultiplicador) e).getMultiplicador();
        return 1.0;
    }

    public void notificarSantoConsumido() {
        dispararTodo(EventoJuego.AL_CONSUMIR_SANTO);
    }

    public void notificarJokerComprado() {
        dispararTodo(EventoJuego.AL_COMPRAR_JOKER);
        gestorRasgos.recalcular(jugador.getJokers());
    }

    public void notificarCartaComprada() {
        dispararTodo(EventoJuego.AL_COMPRAR_CARTA);
    }

    public void notificarJokerVendido() {
        dispararTodo(EventoJuego.AL_VENDER_JOKER);
        gestorRasgos.recalcular(jugador.getJokers());
    }
}
