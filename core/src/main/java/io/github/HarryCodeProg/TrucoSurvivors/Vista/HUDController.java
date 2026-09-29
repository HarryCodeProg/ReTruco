package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorAccion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorInputArrastrable;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.List;

public class HUDController {

    private final GameBotones botones;
    private final VistaMazo vistaMazo;

    public HUDController(VistaMazo vistaMazo) {
        this.botones = new GameBotones();
        this.vistaMazo = vistaMazo;
    }

    public void actualizarSeleccion(Juego juego, boolean puedeInteractuar,
                                    GestorInputArrastrable<VistaCarta> gestorCartas,
                                    GestorInputArrastrable<VistaJoker> gestorJokers, Iterable<VistaCarta> cartasJugador,
                                    Iterable<VistaJoker> jokers) {
        gestorJokers.getSeleccionados().clear();
        gestorCartas.getSeleccionados().clear();
        for (VistaJoker j : jokers) {
            if (j.isSeleccionada()) gestorJokers.getSeleccionados().add(j);
        }
        for (VistaCarta c : cartasJugador) {
            if (c.isSeleccionada()) gestorCartas.getSeleccionados().add(c);
        }
        botones.actualizarEstados(juego, puedeInteractuar, gestorCartas.getSeleccionados().size());
    }

    public void update(Vector3 mouseWorld, boolean puedeInteractuar, GestorAccion gestorAccion) {
        if (vistaMazo != null && vistaMazo.isModalAbierto()) {
            botones.gestor.update(-1000, -1000);
            return;
        }
        if (puedeInteractuar) {
            Boton boton = botones.gestor.obtenerBotonCliqueado();
            if (boton != null && gestorAccion != null) {
                gestorAccion.ejecutarAccion(boton.getAccion());
            }
        }
        botones.gestor.update(mouseWorld.x, mouseWorld.y);
    }

    public void renderBotones(SpriteBatch batch, com.badlogic.gdx.graphics.g2d.TextureRegion pixelBlanco) {
        botones.render(batch, pixelBlanco.getTexture());
    }

    public void renderMazo(SpriteBatch batch, Iterable<Carta> cartasRestantes, int tamanoMazo) {
        if (vistaMazo != null) {
            vistaMazo.render(batch, (List<Carta>) cartasRestantes, tamanoMazo);
        }
    }

    public GameBotones getBotones() { return botones; }
    public VistaMazo getVistaMazo() { return vistaMazo; }
}
