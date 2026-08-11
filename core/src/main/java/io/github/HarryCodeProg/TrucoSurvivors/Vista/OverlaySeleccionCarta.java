package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.SignoZodiaco;

import java.util.ArrayList;

public class OverlaySeleccionCarta {
    public enum Estado { OCULTO, MOSTRANDO, ESPERANDO_SELECCION, APLICANDO, CERRANDO }
    private Estado estado = Estado.OCULTO;
    private SignoZodiaco signoPendiente;
    private ArrayList<VistaCarta> cartasMostradas = new ArrayList<>();
    private VistaCarta seleccionada;
    private Runnable alCerrar;

    public void abrir(SignoZodiaco signo, ArrayList<Carta> manoJugador, TextureAtlas atlasCartas, Runnable alCerrar) {
        this.signoPendiente = signo;
        this.alCerrar = alCerrar;
        cartasMostradas.clear();
        float espacioX = 140f;
        float inicioX = 640f - (manoJugador.size() * espacioX) / 2f;
        for (int i = 0; i < manoJugador.size(); i++) {
            VistaCarta v = new VistaCarta(manoJugador.get(i), false, atlasCartas);
            v.setPosition(inicioX + i * espacioX, 300f);
            cartasMostradas.add(v);
        }
        estado = Estado.MOSTRANDO;
    }

    public void update(float mouseX, float mouseY, float delta) {
        if (estado == Estado.OCULTO) return;
        for (VistaCarta v : cartasMostradas) v.update(mouseX, mouseY, delta);
        if (estado == Estado.MOSTRANDO) estado = Estado.ESPERANDO_SELECCION;
    }

    public void click(float mouseX, float mouseY, Jugador jugador, Juego juego) {
        if (estado != Estado.ESPERANDO_SELECCION) return;
        for (VistaCarta v : cartasMostradas) {
            if (v.contiene(mouseX, mouseY)) {
                seleccionada = v;
                signoPendiente.aplicarEfecto(jugador, juego, null, v.getCarta());
                estado = Estado.CERRANDO;
                break;
            }
        }
    }

    public void render(SpriteBatch batch, Main game) {
        if (estado == Estado.OCULTO) return;
        Texture pixel = game.getPixelBlanco();
        batch.setColor(0, 0, 0, 0.75f);
        batch.draw(pixel, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, 1);
        for (VistaCarta v : cartasMostradas) v.render(batch, game);
    }

    public boolean debeCerrarse() { return estado == Estado.CERRANDO; }

    public void confirmarCierre() {
        estado = Estado.OCULTO;
        if (alCerrar != null) alCerrar.run();
        alCerrar = null;
    }
}
