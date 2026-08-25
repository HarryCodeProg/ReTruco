package io.github.HarryCodeProg.TrucoSurvivors;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.I18NBundle;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.DatosRival;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PerfilJugador;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.LoadingScreenCentered;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.MainMenuScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import java.util.ArrayList;
import java.util.Locale;

public class Main extends Game {
    public SpriteBatch batch;
    private Music musicaFondo;
    private ArrayList<DatosRival> listaRivales;
    private BitmapFont fuentePrincipal;
    private BitmapFont fuenteTitulo;
    private Texture pixelBlanco;
    private GestorSonidos gestorSonidos;
    private static Main instancia;
    private I18NBundle idiomaBundle;
    private TextureAtlas atlasCartas;
    private TextureAtlas atlasJokers;
    private PerfilJugador perfilJugador;
    private TextureRegion pixelBlancoRegion;
    public AssetManager assets;
    private TextureAtlas atlasZodiaco;
    private Texture texturaRuletaFondo;
    private TextureAtlas atlasSantos;

    @Override
    public void create() {
        System.out.println("Main create");
        perfilJugador = new PerfilJugador();
        instancia = this;
        batch = new SpriteBatch();
        listaRivales = new ArrayList<>();
        crearRivales();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1); // Color blanco sólido (R, G, B, A)
        pixmap.fill();
        pixelBlanco = new Texture(pixmap);
        pixelBlancoRegion = new TextureRegion(pixelBlanco);
        inicializarFuentes();
        // 1. Cargamos el archivo .wav desde tu nueva estructura de carpetas.
        // Reemplazá "NombreDeTuCarpeta" y "cancion_completa.wav" por los nombres reales de tus archivos.
        //musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("music/The Shadows of Tango/The Shadows of Tango - 48kHz_fixed.wav"));
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("music/Second_Dealing/second_dealing_full.ogg"));
        // 2. Configuraciones de la música de fondo
        musicaFondo.setLooping(true); // Hace que cuando termine, vuelva a empezar automáticamente
        musicaFondo.setVolume(0.05f);  // Volumen entre 0.0 (muto) y 1.0 (máximo). 0.25f es ideal para que no tape los sonidos.
        // 3. ¡Le damos Play! Empezará a sonar apenas se abra el juego
        musicaFondo.play();
        gestorSonidos = new GestorSonidos();
        assets = new AssetManager();
        setScreen(new LoadingScreenCentered(this, assets, "ui/unpeso-spritesheet.png", 12, 1.0f, () -> {
            this.setScreen(new MainMenuScreen(this));
        }));
        //this.setScreen(new MainMenuScreen(this));
        atlasCartas = new TextureAtlas(Gdx.files.internal("atlas/cartas.atlas"));
        for (Texture texture : atlasCartas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        atlasJokers = new TextureAtlas(Gdx.files.internal("atlas/jokers.atlas"));
        for (Texture texture : atlasJokers.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        atlasSantos = new TextureAtlas(Gdx.files.internal("atlas/santos.atlas"));
        for (Texture texture : atlasSantos.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        atlasZodiaco = new TextureAtlas(Gdx.files.internal("atlas/zodiaco.atlas"));
        texturaRuletaFondo = new Texture("ui/zodiaco_ruleta.png");

        cambiarIdioma("es");
    }

    public PerfilJugador getPerfilJugador() { return perfilJugador; }

    public TextureAtlas getAtlasCartas() { return atlasCartas; }

    public TextureAtlas getAtlasJokers() {return atlasJokers;}

    public GestorSonidos getGestorSonidos() {return gestorSonidos;}

    public Music getMusicaFondo() {
        return this.musicaFondo;
    }

    public TextureAtlas getAtlasZodiaco() { return atlasZodiaco; }

    public TextureRegion getPixelBlancoRegion() {
        return pixelBlancoRegion;
    }

    public Texture getTexturaRuletaFondo() { return texturaRuletaFondo; }

    public TextureAtlas getAtlasSantos(){return atlasSantos;}

    public static Main getInstance() {return instancia;}

    public void crearRivales(){
        listaRivales.add(new DatosRival("Maty", "Vive en chaco, pobre tipo", 30, true,0));
        listaRivales.add(new DatosRival("Enzo", "El incel definitivo", 60, false,1));
        listaRivales.add(new DatosRival("Sharky", "Un tiburon humanoide con traje", 100, false,2));

        listaRivales.add(new DatosRival("Jere", "Se sabe que trabaja", 500, false,3));
        listaRivales.add(new DatosRival("Snowy", "Se la pasa jugando CS", 1500, false,4));
        listaRivales.add(new DatosRival("Lau", "Cometio todos los delitos existentes", 3000, false,5));

        listaRivales.add(new DatosRival("Fede", "Juega diablo", 10000, false,6));
        listaRivales.add(new DatosRival("Fran", "Probablemente tenga el auto roto", 15000, false,7));
        listaRivales.add(new DatosRival("Guille", "Probablemente este en una banda", 30000, false,8));

        listaRivales.add(new DatosRival("Geno", "Le gustan los gatos mas que las personas", 50000, false,9));
        listaRivales.add(new DatosRival("Sol", "Es diseñadora, no se de que", 80000, false,10));
        listaRivales.add(new DatosRival("Valen", "Es profesora de literatura (¿sabe jugar al truco?)", 100000, false,11));

        listaRivales.add(new DatosRival("Negro", "Trabaja en el gym, te presta mas atencion si sos mujer", 150000, false,12));
        listaRivales.add(new DatosRival("Agusto", "Se hace odiar muy facil", 250000, false,13));
        listaRivales.add(new DatosRival("Benja", "No tiene VTV, es un peligro", 500000, false,14));

        listaRivales.add(new DatosRival("Juanse", "Se autonombra el secas", 800000, false,15));
        listaRivales.add(new DatosRival("El Z", "Le gusta Dragon Ball", 1000000, false,16));
        listaRivales.add(new DatosRival("Liguo", "La parca oscura", 1500000, false,17));

        listaRivales.add(new DatosRival("Mia", "Le gusta mirar el celular", 2000000, false,18));
        listaRivales.add(new DatosRival("Thian", "Le gusta el futbol", 3000000, false,19));
        listaRivales.add(new DatosRival("Thiago", "Es muy erratico", 3500000, false,20));

        listaRivales.add(new DatosRival("Susana", "Doña de doñas", 4000000, false,21));
        listaRivales.add(new DatosRival("Nahuel", "¿Quien?", 5000000, false,22));
        listaRivales.add(new DatosRival("Harry", "El creador del juego", 6666666, false,23));
    }

    public ArrayList<DatosRival> getListaRivales() {
        return this.listaRivales;
    }

    public void habilitarSiguiente(int indiceActual) {
        listaRivales.get(indiceActual).setDesbloqueado(false);
        if (indiceActual + 1 < listaRivales.size()) {
            listaRivales.get(indiceActual + 1).setDesbloqueado(true);
        } else {
            listaRivales.get(0).setDesbloqueado(true);
        }
    }

    private void inicializarFuentes() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Cinzel-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        // Fuente normal para descripciones, botones y nombres
        parameter.size = 20;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.5f; // Un pequeño borde negro para que se lea impecable siempre
        parameter.borderColor = Color.BLACK;
        fuentePrincipal = generator.generateFont(parameter);
        // Fuente grande para títulos (reutilizamos el generador)
        parameter.size = 48;
        fuenteTitulo = generator.generateFont(parameter);
        generator.dispose(); // Liberamos memoria
    }

    public BitmapFont getFuentePrincipal() { return fuentePrincipal; }

    public BitmapFont getFuenteTitulo() { return fuenteTitulo; }

    public Texture getPixelBlanco() {
        return this.pixelBlanco;
    }

    public static String getTexto(String clave) {
        if (instancia == null || instancia.idiomaBundle == null) return clave;
        return instancia.idiomaBundle.get(clave);
    }

    public void cambiarIdioma(String codigoIdioma) {
        Locale locale = new Locale(codigoIdioma);
        // Si codigoIdioma es "es", buscamos "idiomas/es" como archivo base.
        idiomaBundle = I18NBundle.createBundle(Gdx.files.internal("idiomas/" + codigoIdioma), locale);
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.dispose();
        }
        if (fuentePrincipal != null) fuentePrincipal.dispose();
        if (fuenteTitulo != null) fuenteTitulo.dispose();
        gestorSonidos.dispose();
        if (atlasCartas != null) atlasCartas.dispose();
        if (atlasJokers != null) atlasJokers.dispose();
        if (atlasZodiaco != null) atlasZodiaco.dispose();
        if (texturaRuletaFondo != null) texturaRuletaFondo.dispose();
    }
}
