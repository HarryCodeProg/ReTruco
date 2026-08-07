package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorBotones;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class GameBotones {
    public final Boton jugarCarta, truco, envidoOpciones, envido, realEnvido, faltaEnvido,
        quiero, descartar, valeCuatro, noQuiero, mazo;
    public final GestorBotones gestor = new GestorBotones();
    private final EnvidoMenuState envidoMenuState = new EnvidoMenuState();

    public GameBotones() {
        jugarCarta = new Boton(300, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.CELESTE, Accion.JUGAR_CARTA);
        jugarCarta.setHabilitado(false);
        truco = new Boton(440, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.TURQUESA, Accion.TRUCO);
        envidoOpciones = new Boton(580, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.BRONCE, Accion.ENVIDO);

        float xInicial = 580, gapX = 8f, anchoBoton = 120f;
        float yDesplegable = GameLayout.Y_BOTONES + GameLayout.ALTO_BOTON + 10f;
        envido = new Boton(xInicial, yDesplegable, anchoBoton, GameLayout.ALTO_BOTON, Boton.TipoColor.BRONCE, Accion.ENVIDO);
        realEnvido = new Boton(xInicial + anchoBoton + gapX, yDesplegable, anchoBoton, GameLayout.ALTO_BOTON, Boton.TipoColor.BRONCE, Accion.REAL_ENVIDO);
        faltaEnvido = new Boton(xInicial + (anchoBoton + gapX) * 2, yDesplegable, anchoBoton, GameLayout.ALTO_BOTON, Boton.TipoColor.BRONCE, Accion.FALTA_ENVIDO);

        quiero = new Boton(720, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.CELESTE, Accion.QUIERO);
        descartar = new Boton(1000, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.BORDO, Accion.DESCARTAR);
        descartar.setHabilitado(false);
        valeCuatro = new Boton(440, GameLayout.Y_BOTONES_CANTOS, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.TURQUESA, Accion.VALE_CUATRO);
        noQuiero = new Boton(860, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.BORDO, Accion.NO_QUIERO);
        mazo = new Boton(1140, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.BORDO, Accion.IR_AL_MAZO);

        realEnvido.setHabilitado(false);
        faltaEnvido.setHabilitado(false);
        valeCuatro.setHabilitado(false);
        quiero.setHabilitado(false);
        noQuiero.setHabilitado(false);

        for (Boton b : new Boton[]{envidoOpciones, envido, truco, mazo, jugarCarta, descartar, realEnvido, faltaEnvido, valeCuatro, quiero, noQuiero}) {
            gestor.agregar(b);
        }
    }

    public void actualizarEstados(Juego juego, boolean puedeInteractuar, int cartasSeleccionadas) {
        gestor.setHabilitado(Accion.DESCARTAR, puedeInteractuar);
        gestor.setHabilitado(Accion.IR_AL_MAZO, puedeInteractuar);
        gestor.setHabilitado(Accion.TRUCO, puedeInteractuar);
        gestor.setHabilitado(Accion.ENVIDO, puedeInteractuar);

        boolean hayCantoPendiente = juego.hayCantoEnvidoPendiente() || juego.hayCantoTrucoPendiente();
        boolean puedeEnvido = puedeInteractuar && juego.puedeCantarEnvidoNivel(juego.getJugador(), 1);
        boolean puedeReal   = puedeInteractuar && (juego.puedeEscalarEnvido(juego.getJugador()) || juego.puedeCantarEnvidoNivel(juego.getJugador(), 2));
        boolean puedeFalta  = puedeInteractuar && (juego.puedeEscalarEnvido(juego.getJugador()) || juego.puedeCantarEnvidoNivel(juego.getJugador(), 3));
        boolean sePuedeCantarAlgo = puedeEnvido || puedeReal || puedeFalta;
        boolean esTurnoJugador = juego.getTurnoActual().equals(juego.getJugador());

        gestor.setHabilitado(Accion.JUGAR_CARTA, puedeInteractuar && esTurnoJugador && cartasSeleccionadas == 1);

        envidoOpciones.setHabilitado(sePuedeCantarAlgo);
        envidoOpciones.setVisible(!hayCantoPendiente);

        boolean mostrarOpciones = envidoMenuState.debeMostrarOpciones(juego.hayCantoEnvidoPendiente());
        envido.setHabilitado(puedeEnvido);
        envido.setVisible(mostrarOpciones && puedeEnvido);
        realEnvido.setHabilitado(puedeReal);
        realEnvido.setVisible(mostrarOpciones && puedeReal);
        faltaEnvido.setHabilitado(puedeFalta);
        faltaEnvido.setVisible(mostrarOpciones && puedeFalta);

        int proximoTruco = juego.proximoNivelTrucoDisponible(juego.getJugador());
        truco.setHabilitado(puedeInteractuar && proximoTruco == 1);
        truco.setVisible(proximoTruco == 1 || (juego.getMesa().getMesaJugador().isEmpty() && juego.getMesa().getMesaRival().isEmpty()));

        valeCuatro.setHabilitado(puedeInteractuar && juego.puedeEscalarTruco(juego.getJugador()));
        valeCuatro.setVisible(juego.hayCantoTrucoPendiente() && juego.puedeEscalarTruco(juego.getJugador()));

        quiero.setHabilitado(puedeInteractuar && hayCantoPendiente);
        quiero.setVisible(hayCantoPendiente);

        noQuiero.setHabilitado(puedeInteractuar && hayCantoPendiente);
        noQuiero.setVisible(hayCantoPendiente);
    }

    public EnvidoMenuState getEnvidoMenuState() {
        return envidoMenuState;
    }

    public void render(SpriteBatch batch, Texture pixelBlanco) {
        int botonesVisibles = 0;
        if (envido.isVisible()) botonesVisibles++;
        if (realEnvido.isVisible()) botonesVisibles++;
        if (faltaEnvido.isVisible()) botonesVisibles++;

        // Utiliza directamente el método renderFondo de tu clase EnvidoMenuState
        envidoMenuState.renderFondo(batch, pixelBlanco, envidoOpciones, botonesVisibles);
        gestor.render(batch);
    }
}
