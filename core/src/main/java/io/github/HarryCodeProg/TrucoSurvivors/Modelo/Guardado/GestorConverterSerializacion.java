package io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.*;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

import java.util.ArrayList;

public class GestorConverterSerializacion {

    private static final PoolJokersTienda poolJokers = new PoolJokersTienda();
    private static final PoolSantosTienda poolSantos = new PoolSantosTienda();

    public static DatosCarta aDatos(Carta c) {
        DatosCarta d = new DatosCarta();
        d.numero = c.getNumero();
        d.palo = c.getPalo().name();
        d.bonusPoderTrucoPermanente = c.getBonusPoderTrucoPermanente();
        d.bonusAporteTrucoPermanente = c.getBonusAporteTrucoPermanente();
        d.bonusPoderEnvidoPermanente = c.getBonusPoderEnvidoPermanente();
        d.bonusAporteEnvidoPermanente = c.getBonusAporteEnvidoPermanente();
        d.multiplicadorPoderTrucoBase = c.getMultiplicadorPoderTrucoBase();
        d.multiplicadorAporteTrucoBase = c.getMultiplicadorAporteTrucoBase();
        d.multiplicadorPoderEnvidoBase = c.getMultiplicadorPoderEnvidoBase();
        d.multiplicadorAporteEnvidoBase = c.getMultiplicadorAporteEnvidoBase();
        return d;
    }

    public static Carta desdeDatos(DatosCarta d) {
        Carta c = new Carta(d.numero, Palo.valueOf(d.palo));
        c.setBonusPermanentesYMultiplicadores(
            d.bonusPoderTrucoPermanente, d.bonusAporteTrucoPermanente,
            d.bonusPoderEnvidoPermanente, d.bonusAporteEnvidoPermanente,
            d.multiplicadorPoderTrucoBase, d.multiplicadorAporteTrucoBase,
            d.multiplicadorPoderEnvidoBase, d.multiplicadorAporteEnvidoBase
        );
        return c;
    }

    public static DatosMazo aDatos(Mazo m) {
        DatosMazo d = new DatosMazo();
        for (Carta c : m.getMazo()) d.mazo.add(aDatos(c));
        for (Carta c : m.getCartasTomadas()) d.cartasTomadas.add(aDatos(c));
        for (Carta c : m.getCartasDescartadas()) d.cartasDescartadas.add(aDatos(c));
        d.tamañoMazo = m.getTamañoMazo();
        return d;
    }

    public static Mazo desdeDatos(DatosMazo d) {
        Mazo m = new Mazo();
        for (DatosCarta dc : d.mazo) m.agregarCarta(desdeDatos(dc));
        for (DatosCarta dc : d.cartasTomadas) m.getCartasTomadas().add(desdeDatos(dc));
        for (DatosCarta dc : d.cartasDescartadas) m.getCartasDescartadas().add(desdeDatos(dc));
        return m;
    }

    public static DatosJoker aDatos(Joker j) {
        DatosJoker d = new DatosJoker();
        d.id = j.getId();
        d.acumulado = j.getAcumulado();
        d.precioVenta = j.getPrecioVenta();
        d.estadoExtra = j.getEstadoExtra();
        return d;
    }

    public static Joker desdeDatos(DatosJoker d) {
        Joker j = poolJokers.crearPorId(d.id);
        if (j == null) return null;
        j.setAcumulado(d.acumulado);
        j.setEstadoExtra(d.estadoExtra);
        return j;
    }

    public static DatosSanto aDatos(Santo s) {
        DatosSanto d = new DatosSanto();
        d.id = s.getId();
        d.estadoExtra = s.getEstadoExtra();
        return d;
    }

    public static Santo desdeDatos(DatosSanto d) {
        Santo s = poolSantos.crearPorId(d.id);
        if (s == null) return null;
        s.setEstadoExtra(d.estadoExtra);
        return s;
    }

    public static DatosJugador aDatos(Jugador j) {
        DatosJugador d = new DatosJugador();
        d.pesos = j.getPesos();
        d.tamañoMano = j.getTamañoMano();
        d.tamañoJokers = j.getTamañoJokers();
        d.manosMaximas = j.getManosMaximas();
        d.manosActuales = j.getManosActuales();
        d.multiplicadorTruco = j.getMultiplicadorTruco();
        d.multiplicadorEnvido = j.getMultiplicadorEnvido();
        d.totalPesosGanados = j.getTotalPesosGanados();
        d.totalPesosGastados = j.getTotalPesosGastados();
        d.mazo = aDatos(j.getMazo());
        for (Carta c : j.getMano()) d.mano.add(aDatos(c));
        for (Joker jo : j.getJokers()) d.jokers.add(aDatos(jo));
        for (Santo s : j.getSantos()) d.santos.add(aDatos(s));
        j.exportarExtras(d);
        return d;
    }

    public static void aplicarDatos(Jugador j, DatosJugador d) {
        j.resetearABase();
        j.setCargandoGuardado(true);
        try {
            for (DatosJoker dj : d.jokers) {
                Joker jo = desdeDatos(dj);
                if (jo != null) j.agregarJoker(jo);
            }
            for (DatosSanto ds : d.santos) {
                Santo s = desdeDatos(ds);
                if (s != null) j.agregarSanto(s);
            }
        } finally {
            j.setCargandoGuardado(false);
        }
        if (d.mazo != null) j.setMazo(desdeDatos(d.mazo));
        for (DatosCarta dc : d.mano) j.agregarCarta(desdeDatos(dc));
        j.aplicarExtrasGuardados(d);
        j.setUltimoSantoUsado(d.ultimoSantoUsadoId >= 0 ? poolSantos.crearPorId(d.ultimoSantoUsadoId) : null);
        j.sumarPesos(d.pesos - j.getPesos());
        j.setTotalPesosGanados(d.totalPesosGanados);
        j.setTotalPesosGastados(d.totalPesosGastados);
    }

    public static void vincularTomadas(java.util.List<Carta> tomadas, java.util.List<Carta> reales) {
        if (tomadas == null || reales == null) return;
        boolean[] usada = new boolean[tomadas.size()];
        for (Carta real : reales) {
            boolean yaEsta = false;
            for (int i = 0; i < tomadas.size(); i++) {
                if (tomadas.get(i) == real) { usada[i] = true; yaEsta = true; break; }
            }
            if (yaEsta) continue;
            for (int i = 0; i < tomadas.size(); i++) {
                if (!usada[i] && mismaCarta(tomadas.get(i), real)) { tomadas.set(i, real); usada[i] = true; break; }
            }
        }
    }

    private static boolean mismaCarta(Carta a, Carta b) {
        DatosCarta x = aDatos(a), y = aDatos(b);
        return x.numero == y.numero && x.palo.equals(y.palo)
            && x.bonusPoderTrucoPermanente == y.bonusPoderTrucoPermanente
            && x.bonusAporteTrucoPermanente == y.bonusAporteTrucoPermanente
            && x.bonusPoderEnvidoPermanente == y.bonusPoderEnvidoPermanente
            && x.bonusAporteEnvidoPermanente == y.bonusAporteEnvidoPermanente
            && x.multiplicadorPoderTrucoBase == y.multiplicadorPoderTrucoBase
            && x.multiplicadorAporteTrucoBase == y.multiplicadorAporteTrucoBase
            && x.multiplicadorPoderEnvidoBase == y.multiplicadorPoderEnvidoBase
            && x.multiplicadorAporteEnvidoBase == y.multiplicadorAporteEnvidoBase;
    }

    public static DatosItemTienda aDatos(ItemTienda item) {
        DatosItemTienda d = new DatosItemTienda();
        d.tipo = item.getTipo().name();
        d.precio = item.getPrecio();
        switch (item.getTipo()) {
            case CARTA: d.carta = aDatos(item.getCarta()); break;
            case JOKER: d.jokerId = item.getJoker().getId(); break;
            case SANTO: d.santoId = item.getSanto().getId(); break;
            default: break;
        }
        return d;
    }

    public static ItemTienda desdeDatos(DatosItemTienda d) {
        switch (ItemTienda.Tipo.valueOf(d.tipo)) {
            case CARTA: return ItemTienda.deCarta(desdeDatos(d.carta), d.precio);
            case JOKER:
                Joker j = poolJokers.crearPorId(d.jokerId);
                return j != null ? ItemTienda.deJoker(j, d.precio) : null;
            case SANTO:
                Santo s = poolSantos.crearPorId(d.santoId);
                return s != null ? ItemTienda.deSanto(s, d.precio) : null;
            default: return null;
        }
    }

    public static DatosEstadoTienda aDatos(EstadoTienda et) {
        DatosEstadoTienda d = new DatosEstadoTienda();
        for (ItemTienda it : et.getFilaCartas()) d.filaCartas.add(aDatos(it));
        for (ItemTienda it : et.getFilaJokers()) d.filaJokers.add(aDatos(it));
        for (ItemTienda it : et.getFilaSantos()) d.filaSantos.add(aDatos(it));
        d.rerollsTienda = et.getRerollsTiendaActual();
        d.ruedaUsada = et.isRuedaUsada();
        d.signoPendiente = et.getSignoPendiente() != null ? et.getSignoPendiente().name() : null;
        d.multiplicadorPrecio = et.getMultiplicadorPrecio();
        d.rerollsGratis = et.getRerollsGratis();
        d.cantidadCartas = et.getCantidadCartas();
        d.cantidadJokers = et.getCantidadJokers();
        d.cantidadSantos = et.getCantidadSantos();
        return d;
    }

    public static ArrayList<Carta> listaDesdeDatos(ArrayList<DatosCarta> lista) {
        ArrayList<Carta> r = new ArrayList<>();
        for (DatosCarta dc : lista) r.add(desdeDatos(dc));
        return r;
    }
}
