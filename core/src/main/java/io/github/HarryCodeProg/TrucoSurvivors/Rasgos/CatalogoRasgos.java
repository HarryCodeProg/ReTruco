package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;
import io.github.HarryCodeProg.TrucoSurvivors.Rasgos.EfectoRasgo.TipoCanto;

import java.util.concurrent.ThreadLocalRandom;

public final class CatalogoRasgos {
    private CatalogoRasgos() {}

    private static final EventoJuego EV_TRUCO = EventoJuego.ANTES_DE_SUMAR_TRUCO;
    private static final EventoJuego EV_ENVIDO = EventoJuego.ANTES_DE_SUMAR_ENVIDO;
    public static final String ACUM_PERSONA = "persona.vendidos";
    public static final String ACUM_CULTURA_PUNTOS = "cultura.puntos";
    public static final String ACUM_CULTURA_MULT = "cultura.mult";

    /** tier = indice de umbral (1 = primer umbral de CategoriaJoker). */
    public static EfectoRasgo getEfecto(CategoriaJoker cat, int tier) {
        EfectoRasgo[] t;
        switch (cat) {
            case TRUCO:
                t = new EfectoRasgo[]{ canto(TipoCanto.TRUCO, 0.25, 1), canto(TipoCanto.TRUCO, 1, 1), canto(TipoCanto.TRUCO, 0, 1.5) }; break;
            case ENVIDO:
                t = new EfectoRasgo[]{ canto(TipoCanto.ENVIDO, 0.25, 1), canto(TipoCanto.ENVIDO, 1, 1), canto(TipoCanto.ENVIDO, 0, 1.5) }; break;
            case GASTRONOMIA: // igual a Truco (como lo pasaste)
                t = new EfectoRasgo[]{ canto(TipoCanto.TRUCO, 0.25, 1), canto(TipoCanto.TRUCO, 1, 1), canto(TipoCanto.TRUCO, 0, 1.5) }; break;
            case TERRITORIO: // igual a Envido (como lo pasaste)
                t = new EfectoRasgo[]{ canto(TipoCanto.ENVIDO, 0.25, 1), canto(TipoCanto.ENVIDO, 1, 1), canto(TipoCanto.ENVIDO, 0, 1.5) }; break;
            case ACTIVACION:
                t = new EfectoRasgo[]{ new Reactivacion(1), new Reactivacion(2), new Reactivacion(3) }; break;
            case MAZO:
                t = new EfectoRasgo[]{ rec().descartes(1), rec().mano(1), rec().mano(1).descartes(1) }; break;
            case ECONOMIA:
                t = new EfectoRasgo[]{ rec().pesos(2), rec().interes(2), rec().pesos(3).interes(2) }; break;
            case CONSUMIBLES:
                t = new EfectoRasgo[]{ rec().santos(1),
                    junto(rec().santos(1), new PuntosPorSantoUsado(25)),
                    junto(rec().santos(2), new PuntosPorSantoUsado(50)) }; break;
            case BEBIDA:
                t = new EfectoRasgo[]{ new PuntosPorJoker(CategoriaJoker.BEBIDA, EV_TRUCO, 10),
                    new PuntosPorJoker(CategoriaJoker.BEBIDA, EV_TRUCO, 50),
                    new PuntosPorJoker(CategoriaJoker.BEBIDA, EV_TRUCO, 100) }; break;
            case FAUNA:
                t = new EfectoRasgo[]{ new PuntosPorJoker(CategoriaJoker.FAUNA, EV_ENVIDO, 10),
                    new PuntosPorJoker(CategoriaJoker.FAUNA, EV_ENVIDO, 50),
                    new PuntosPorJoker(CategoriaJoker.FAUNA, EV_ENVIDO, 100) }; break;
            case NACIONAL: // tier 4 (8 jokers) ademas habilita al CarpinchoGauchoMatero: ver GestorRasgos.isLegendarioNacionalDisponible()
                t = new EfectoRasgo[]{ rec().pesos(2), rec().jokers(1), rec().jokers(1).mano(1), rec().jokers(1).mano(1) }; break;
            case PERSONA:
                t = new EfectoRasgo[]{ new Persona(2), new Persona(5), new Persona(10) }; break;
            case CULTURA:
                t = new EfectoRasgo[]{ new Cultura(false), new Cultura(true) }; break;
            case OBJETO:
                t = new EfectoRasgo[]{ rec().santos(1), junto(rec().santos(1), new ObjetoLleno()) }; break;
            case RACHAS:
                t = new EfectoRasgo[]{ new Rachas(1, 0), new Rachas(2, 0), new Rachas(3, 1) }; break;
            case FIGURA:
                t = new EfectoRasgo[]{ new Figuras(5, 0), new Figuras(10, 0), new Figuras(15, 2) }; break;
            case AZAR:
                t = new EfectoRasgo[]{ new AzarMoneda(), new AzarProgresivo() }; break;
            case DEPORTE:
                t = new EfectoRasgo[]{ new Deporte(1) }; break;
            case TRANSPORTE:
                t = new EfectoRasgo[]{ rec().tienda(1, 1, 1) }; break;
            default:
                return null;
        }
        return (tier >= 1 && tier <= t.length) ? t[tier - 1] : null;
    }

    /** Cantidad de figuras (10, 11, 12) del mazo del jugador. */
    public static int contarFiguras(Jugador jug) {
        int n = 0;
        for (Carta c : jug.getMazo().getMazo()) if (c.getNumero() >= 10) n++;
        return n;
    }

    private static EfectoRasgo canto(TipoCanto tipo, double bonus, double mult) { return new Canto(tipo, bonus, mult); }
    private static Recursos rec() { return new Recursos(); }
    private static EfectoRasgo junto(EfectoRasgo... partes) { return new Compuesto(partes); }

    // ======================= EFECTOS =======================

    private static class Canto implements EfectoRasgo {
        private final TipoCanto tipo; private final double bonus, mult;
        Canto(TipoCanto tipo, double bonus, double mult) { this.tipo = tipo; this.bonus = bonus; this.mult = mult; }
        @Override public double getBonusCanto(TipoCanto t) { return t == tipo ? bonus : 0.0; }
        @Override public double getMultiplicadorCanto(TipoCanto t) { return t == tipo ? mult : 1.0; }
    }

    private static class Reactivacion implements EfectoRasgo {
        private final int n;
        Reactivacion(int n) { this.n = n; }
        @Override public int getReactivacionesPrimeraCarta() { return n; }
    }

    /** Stats y economia: todo lo que se resuelve sumando recursos. */
    private static class Recursos implements EfectoRasgo {
        int mano, descartes, jokers, santos, interes, pesos, tJokers, tCartas, tSantos;
        Recursos mano(int v) { mano = v; return this; }
        Recursos descartes(int v) { descartes = v; return this; }
        Recursos jokers(int v) { jokers = v; return this; }
        Recursos santos(int v) { santos = v; return this; }
        Recursos interes(int v) { interes = v; return this; }
        Recursos pesos(int v) { pesos = v; return this; }
        Recursos tienda(int j, int c, int s) { tJokers = j; tCartas = c; tSantos = s; return this; }

        @Override public void onActivar(Juego j) { aplicar(j, 1); }
        @Override public void onDesactivar(Juego j) { aplicar(j, -1); }

        private void aplicar(Juego j, int s) {
            if (j == null) return;
            BonosRasgos b = j.getJugador().getBonosRasgos();
            b.tamanoMano += s * mano;
            b.descartes += s * descartes;
            b.espacioJokers += s * jokers;
            b.espacioSantos += s * santos;
            b.topeInteres += s * interes;
            b.tiendaJokers += s * tJokers;
            b.tiendaCartas += s * tCartas;
            b.tiendaSantos += s * tSantos;
            if (descartes != 0) j.ajustarDescartesActuales(s * descartes);
        }

        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            if (e == EventoJuego.AL_GANAR_COMBATE && pesos > 0) ctx.getJugador().sumarPesos(pesos);
        }
    }

    /** Bebida / Fauna: +puntos por cada joker en el tablero. */
    private static class PuntosPorJoker implements EfectoRasgo {
        private final CategoriaJoker cat; private final EventoJuego ev; private final double puntos;
        PuntosPorJoker(CategoriaJoker cat, EventoJuego ev, double puntos) { this.cat = cat; this.ev = ev; this.puntos = puntos; }
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            ResolucionPuntaje res = ctx.getResolucionActual();
            if (e != ev || res == null) return;
            int n = ctx.getJugador().getJokers().size();
            if (n > 0) res.sumarChips(puntos * n, cat.getTexto() + " x" + n, null);
        }
    }

    private static class PuntosPorSantoUsado implements EfectoRasgo {
        private final double puntos;
        PuntosPorSantoUsado(double puntos) { this.puntos = puntos; }
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            ResolucionPuntaje res = ctx.getResolucionActual();
            if (e != EV_TRUCO || res == null) return;
            int n = (int) juego.getGestorRasgos().getContador("santosUsados");
            if (n > 0) res.sumarChips(puntos * n, CategoriaJoker.CONSUMIBLES.getTexto() + " x" + n, null);
        }
    }

    private static class Persona implements EfectoRasgo {
        private final double multPorVenta;
        Persona(double m) { multPorVenta = m; }
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            Jugador jug = ctx.getJugador();
            if (e == EventoJuego.AL_VENDER_JOKER) {
                Joker vendido = juego.getGestorRasgos().getUltimoJokerVendido();
                if (vendido == null || !vendido.tieneCategoria(CategoriaJoker.PERSONA)) {
                    jug.sumarAcumuladoRasgo(ACUM_PERSONA, 1);
                }
            } else if (EfectoRasgo.esPuntaje(e) && ctx.getResolucionActual() != null) {
                double n = jug.getAcumuladoRasgo(ACUM_PERSONA);
                if (n > 0) ctx.getResolucionActual().sumarMult(n * multPorVenta, CategoriaJoker.PERSONA.getTexto(), null);
            }
        }
    }

    private static class Cultura implements EfectoRasgo {
        private final boolean conMult;
        Cultura(boolean conMult) { this.conMult = conMult; }
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            Jugador jug = ctx.getJugador();
            if (e == EventoJuego.AL_COMPRAR_JOKER) {
                Joker comprado = juego.getGestorRasgos().getUltimoJokerComprado();
                double f = (comprado != null && comprado.tieneCategoria(CategoriaJoker.CULTURA)) ? 2 : 1;
                jug.sumarAcumuladoRasgo(ACUM_CULTURA_PUNTOS, 10 * f);
                if (conMult) jug.sumarAcumuladoRasgo(ACUM_CULTURA_MULT, 1 * f);
            } else if (e == EV_TRUCO && ctx.getResolucionActual() != null) {
                ResolucionPuntaje res = ctx.getResolucionActual();
                String txt = CategoriaJoker.CULTURA.getTexto();
                double p = jug.getAcumuladoRasgo(ACUM_CULTURA_PUNTOS);
                double m = jug.getAcumuladoRasgo(ACUM_CULTURA_MULT);
                if (p > 0) res.sumarChips(p, txt, null);
                if (m > 0) res.sumarMult(m, txt, null);
            }
        }
    }

    private static class ObjetoLleno implements EfectoRasgo {
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            if (!EfectoRasgo.esPuntaje(e) || ctx.getResolucionActual() == null) return;
            Jugador j = ctx.getJugador();
            if (j.getTamañoSantos() > 0 && j.getSantos().size() >= j.getTamañoSantos()) {
                ctx.getResolucionActual().multiplicarMult(2, CategoriaJoker.OBJETO.getTexto(), null);
            }
        }
    }

    private static class Rachas implements EfectoRasgo {
        private final double multPorVictoria; private final int pesosPorVictoria;
        Rachas(double m, int p) { multPorVictoria = m; pesosPorVictoria = p; }
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            int racha = juego.getManosGanadasConsecutivas();
            if (racha <= 0) return;
            if (e == EV_TRUCO && ctx.getResolucionActual() != null) {
                ctx.getResolucionActual().sumarMult(racha * multPorVictoria,
                    CategoriaJoker.RACHAS.getTexto() + " x" + racha, null);
            } else if (e == EventoJuego.AL_GANAR_COMBATE && pesosPorVictoria > 0) {
                ctx.getJugador().sumarPesos(racha * pesosPorVictoria);
            }
        }
    }

    private static class Figuras implements EfectoRasgo {
        private final double puntos, mult;
        Figuras(double puntos, double mult) { this.puntos = puntos; this.mult = mult; }
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            ResolucionPuntaje res = ctx.getResolucionActual();
            if (!EfectoRasgo.esPuntaje(e) || res == null) return;
            int n = contarFiguras(ctx.getJugador());
            if (n == 0) return;
            String txt = CategoriaJoker.FIGURA.getTexto() + " x" + n;
            if (e == EV_TRUCO && puntos > 0) res.sumarChips(puntos * n, txt, null);
            if (mult > 0) res.sumarMult(mult * n, txt, null);
        }
    }

    /** Tier 1: cada mano ganada, 50% +4 mult de Truco, 50% +$2. */
    private static class AzarMoneda implements EfectoRasgo {
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            if (e != EV_TRUCO || ctx.getResolucionActual() == null) return;
            if (ThreadLocalRandom.current().nextBoolean()) {
                ctx.getResolucionActual().sumarMult(4, CategoriaJoker.AZAR.getTexto(), null);
            } else {
                ctx.getJugador().sumarPesos(2);
            }
        }
    }

    /** Tier 2: 25% de x2 al mult de Truco; cada fallo suma +10% hasta que sale. */
    private static class AzarProgresivo implements EfectoRasgo {
        static final String CLAVE = "azar.prob";
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            if (e != EV_TRUCO || ctx.getResolucionActual() == null) return;
            double p = juego.getGestorRasgos().getContador(CLAVE, 0.25);
            if (ThreadLocalRandom.current().nextDouble() < p) {
                ctx.getResolucionActual().multiplicarMult(2, CategoriaJoker.AZAR.getTexto(), null);
                juego.getGestorRasgos().setContador(CLAVE, 0.25);
            } else {
                juego.getGestorRasgos().setContador(CLAVE, Math.min(1.0, p + 0.10));
            }
        }
    }

    private static class Deporte implements EfectoRasgo {
        private final double multPorMano;
        Deporte(double m) { multPorMano = m; }
        @Override public void onEvento(EventoJuego e, ContextoJuego ctx, Juego juego) {
            if (e != EV_ENVIDO || ctx.getResolucionActual() == null) return;
            int n = (int) juego.getGestorRasgos().getContador("manosGanadas");
            if (n > 0) ctx.getResolucionActual().sumarMult(n * multPorMano,
                CategoriaJoker.DEPORTE.getTexto() + " x" + n, null);
        }
    }

    /** Combina varios efectos en un solo tier. */
    private static class Compuesto implements EfectoRasgo {
        private final EfectoRasgo[] partes;
        Compuesto(EfectoRasgo... partes) { this.partes = partes; }
        @Override public void onActivar(Juego j) { for (EfectoRasgo p : partes) p.onActivar(j); }
        @Override public void onDesactivar(Juego j) { for (EfectoRasgo p : partes) p.onDesactivar(j); }
        @Override public void onEvento(EventoJuego e, ContextoJuego c, Juego j) { for (EfectoRasgo p : partes) p.onEvento(e, c, j); }
        @Override public double getBonusCanto(TipoCanto t) { double s = 0; for (EfectoRasgo p : partes) s += p.getBonusCanto(t); return s; }
        @Override public double getMultiplicadorCanto(TipoCanto t) { double m = 1; for (EfectoRasgo p : partes) m *= p.getMultiplicadorCanto(t); return m; }
        @Override public int getReactivacionesPrimeraCarta() { int n = 0; for (EfectoRasgo p : partes) n += p.getReactivacionesPrimeraCarta(); return n; }
    }
}
