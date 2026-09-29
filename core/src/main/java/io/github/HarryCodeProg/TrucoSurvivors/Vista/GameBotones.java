package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorBotones;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class GameBotones {
    public final Boton jugarCarta, truco, envido, quiero, descartar, valeCuatro, noQuiero;
    public final GestorBotones gestor = new GestorBotones();

    public GameBotones() {
        jugarCarta = new Boton(300, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.CELESTE, Accion.JUGAR_CARTA);
        jugarCarta.setHabilitado(false);
        truco = new Boton(440, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.TURQUESA, Accion.TRUCO);
        envido = new Boton(580, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.BRONCE, Accion.ENVIDO);
        quiero = new Boton(720, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.CELESTE, Accion.QUIERO);
        noQuiero = new Boton(860, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.BORDO, Accion.NO_QUIERO);
        valeCuatro = new Boton(1000, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.TURQUESA, Accion.VALE_CUATRO);
        descartar = new Boton(1140, GameLayout.Y_BOTONES, 120, GameLayout.ALTO_BOTON, Boton.TipoColor.BORDO, Accion.DESCARTAR);
        descartar.setHabilitado(false);

        valeCuatro.setHabilitado(false);
        quiero.setHabilitado(false);
        noQuiero.setHabilitado(false);

        for (Boton b : new Boton[]{envido, truco, jugarCarta, descartar, valeCuatro, quiero, noQuiero}) {
            gestor.agregar(b);
        }
    }

    public void actualizarEstados(Juego juego, boolean puedeInteractuar, int cartasSeleccionadas) {
        gestor.setHabilitado(Accion.DESCARTAR, puedeInteractuar);
        gestor.setHabilitado(Accion.TRUCO, puedeInteractuar);

        boolean hayCantoPendiente = juego.hayCantoEnvidoPendiente() || juego.hayCantoTrucoPendiente();
        boolean puedeEnvido = puedeInteractuar && juego.puedeCantarEnvidoNivel(juego.getJugador(), 1);
        boolean esTurnoJugador = juego.getTurnoActual().equals(juego.getJugador());

        gestor.setHabilitado(Accion.JUGAR_CARTA, puedeInteractuar && esTurnoJugador && cartasSeleccionadas == 1);

        envido.setHabilitado(puedeEnvido);
        envido.setVisible(!hayCantoPendiente);

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

    public void render(SpriteBatch batch, Texture pixelBlanco) {
        gestor.render(batch);
    }
}
