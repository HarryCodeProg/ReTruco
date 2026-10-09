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
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ConfiguracionJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.DatosRival;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado.DatosGuardado;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PerfilJugador;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.LoadingScreenCentered;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.MainMenuScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import java.util.ArrayList;
import java.util.Locale;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Main extends Game {
    public SpriteBatch batch;
    private Music musicaFondo;
    private ArrayList<DatosRival> listaRivales;
    private BitmapFont fuentePrincipal;
    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteBotones;
    private BitmapFont fuenteNumeros;
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
    private BitmapFont fuenteUI;
    private ConfiguracionJuego configuracionJuego;
    private BitmapFont fuenteTooltipTitulo;
    private BitmapFont fuenteTooltipDescripcion;

    @Override
    public void create() {
        System.out.println("Main create");
        perfilJugador = new PerfilJugador();
        instancia = this;
        batch = new SpriteBatch();
        listaRivales = new ArrayList<>();
        crearRivales();
        configuracionJuego = new ConfiguracionJuego(); // FIX
        configuracionJuego.cargar();                   // FIX: lee lo guardado la sesión anterior
        configuracionJuego.aplicarVideo();              // FIX: aplica modo ventana/resolución/vsync guardados
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        pixelBlanco = new Texture(pixmap);
        pixelBlancoRegion = new TextureRegion(pixelBlanco);
        inicializarFuentes();
        int indiceGuardado = configuracionJuego.getMusicaIndex();
        if (indiceGuardado < 0 || indiceGuardado >= PISTAS_MUSICA.length) indiceGuardado = 0;
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal(PISTAS_MUSICA[indiceGuardado][1]));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.05f * configuracionJuego.getVolumenMusica());
        musicaFondo.play();
        gestorSonidos = new GestorSonidos();
        configuracionJuego.aplicarAudio(gestorSonidos); // FIX: aplica volumen general/efectos guardados
        assets = new AssetManager();
        setScreen(new LoadingScreenCentered(this, assets, "ui/unpeso-spritesheet.png", 12, 1.0f, () -> {
            this.setScreen(new MainMenuScreen(this));
        }));
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

    public BitmapFont getFuenteUI() {return fuenteUI;}

    public ConfiguracionJuego getConfiguracionJuego() { return configuracionJuego; }

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

        listaRivales.add(new DatosRival("Javito", "Siempre esta perdido", 800000, false,15));
        listaRivales.add(new DatosRival("El Z", "Le gusta Dragon Ball", 1000000, false,16));
        listaRivales.add(new DatosRival("Liguo", "La parca oscura", 1500000, false,17));

        listaRivales.add(new DatosRival("Mia", "Le gusta mirar el celular", 2000000, false,18));
        listaRivales.add(new DatosRival("Thian", "Le gusta el futbol", 3000000, false,19));
        listaRivales.add(new DatosRival("Thiago", "Es muy erratico", 3500000, false,20));

        listaRivales.add(new DatosRival("Marita", "Hermana mayor mas joven", 4000000, false,21));
        listaRivales.add(new DatosRival("Yanina", "Hermana mayor mas grande", 5000000, false,22));
        listaRivales.add(new DatosRival("Susana", "Doña de doñas", 5500000, false,23));

        listaRivales.add(new DatosRival("Harry", "El creador del juego", 6666666, false,24));
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
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        // Lectura: conserva un tono clásico para descripciones, carteles y paneles.
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/IMFellEnglish-Regular.ttf"));
        parameter.size = 20;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1.5f;
        parameter.borderColor = Color.BLACK;
        fuentePrincipal = generator.generateFont(parameter);
        generator.dispose();
        // Títulos: ornamental, cálida y asociada a la estética tradicional del juego.
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Almendra-Bold.ttf"));
        parameter.size = 48;
        fuenteTitulo = generator.generateFont(parameter);
        generator.dispose();
        // Acciones: fuerte y breve para botones, con personalidad sin perder contraste.
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PirataOne-Regular.ttf"));
        parameter.size = 22;
        fuenteBotones = generator.generateFont(parameter);
        generator.dispose();
        // HUD: formas nítidas para contadores, multiplicadores y etiquetas compactas.
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Cinzel-VariableFont_wght.ttf"));
        parameter.size = 20;
        fuenteUI = generator.generateFont(parameter);
        fuenteUI.getData().setScale(0.9f);
        fuenteUI.setColor(Color.WHITE);
        generator.dispose();
        // Valores: dígitos de Cinzel con mayor tamaño para lectura inmediata en HUD y stats.
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Cinzel-VariableFont_wght.ttf"));
        parameter.size = 22;
        fuenteNumeros = generator.generateFont(parameter);
        fuenteNumeros.setColor(Color.WHITE);
        cargarFuentesTooltip();
        generator.dispose();
    }

    private void cargarFuentesTooltip() {
        // --- TÍTULO: gruesa, alto contraste ---
        // Alternativas para probar el título (comentar la de arriba, descomentar una de estas):
        // FreeTypeFontGenerator genTitulo = new FreeTypeFontGenerator(Gdx.files.internal("fonts/m6x11plus.ttf"));
        // FreeTypeFontGenerator genTitulo = new FreeTypeFontGenerator(Gdx.files.internal("fonts/UnifrakturCook-Bold.ttf")); // gótica, no pixel — probablemente NO
        // FreeTypeFontGenerator genTitulo = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Rye-Regular.ttf")); // western, tampoco pixel
        FreeTypeFontGenerator genTitulo = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelOperator8-Bold.ttf"));
        FreeTypeFontParameter paramTitulo = new FreeTypeFontParameter();
        paramTitulo.size = 22;
        paramTitulo.color = Color.WHITE;
        paramTitulo.borderWidth = 0f;
        fuenteTooltipTitulo = genTitulo.generateFont(paramTitulo);
        genTitulo.dispose();
        // --- DESCRIPCIÓN: legible, más espaciada que m6x11 ---
        // Alternativas para probar la descripción:
        FreeTypeFontGenerator genDesc = new FreeTypeFontGenerator(Gdx.files.internal("fonts/m6x11plus.ttf"));
        // FreeTypeFontGenerator genDesc = new FreeTypeFontGenerator(Gdx.files.internal("fonts/scientifica.ttf")); // muy chica/densa, probablemente difícil de leer a este tamaño
        // FreeTypeFontGenerator genDesc = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CozetteVector.ttf")); // pixel muy fina, riesgo de apretarse igual que antes
       // FreeTypeFontGenerator genDesc = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelOperator.ttf"));
        FreeTypeFontParameter paramDesc = new FreeTypeFontParameter();
        paramDesc.size = 20;
        //paramDesc.color = new Color(0.15f, 0.15f, 0.15f, 1f);
        paramDesc.color = Color.WHITE;
        paramDesc.borderWidth = 0f;
        fuenteTooltipDescripcion = genDesc.generateFont(paramDesc);
        genDesc.dispose();
    }

    public BitmapFont getFuenteTooltipTitulo() { return fuenteTooltipTitulo; }
    public BitmapFont getFuenteTooltipDescripcion() { return fuenteTooltipDescripcion; }

    public BitmapFont getFuentePrincipal() { return fuentePrincipal; }

    public BitmapFont getFuenteTitulo() { return fuenteTitulo; }

    public BitmapFont getFuenteBotones() { return fuenteBotones; }

    public BitmapFont getFuenteNumeros() { return fuenteNumeros; }

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

    // Agregalo en Main.java
    public void sincronizarRivalesConProgreso(int indiceGuardado) {
        for (int i = 0; i < listaRivales.size(); i++) {
            // Solo dejamos desbloqueado al rival contra el que te toca pelear ahora
            listaRivales.get(i).setDesbloqueado(i == indiceGuardado);
        }
    }

    private final String[][] PISTAS_MUSICA = {
        {"Second Dealing", "music/Second_Dealing/second_dealing_full.ogg"},
        {"retrucoEnergetico1", "music/retrucoEnergetico1.ogg"},
        {"retrucoEnergetico2", "music/retrucoEnergetico2.ogg"},
        {"retrucoMetal1", "music/retrucoMetal1.ogg"},
        {"retrucoMetal2", "music/retrucoMetal2.ogg"},
        {"retrucoPhonk1", "music/retrucoPhonk1.ogg"},
        {"retrucoPhonk2", "music/retrucoPhonk2.ogg"},
        {"retrucoPummel1", "music/retrucoPummel1.ogg"},
        {"retrucoPummel2", "music/retrucoPummel2.ogg"},
        {"retrucoScattering1", "music/retrucoScattering1.ogg"},
        {"retrucoScattering2", "music/retrucoScattering2.ogg"},
        {"balatruco1", "music/balatruco1.ogg"},
        {"balatruco2", "music/balatruco2.ogg"},
        {"La Ultima Mano1", "music/La Ultima Mano1.ogg"},
        {"La Ultima Mano2", "music/La Ultima Mano2.ogg"},
        {"musicaFondo1", "music/musicaFondo1.ogg"},
        {"musicaFondo2", "music/musicaFondo2.ogg"},
        {"musicaFondo3", "music/musicaFondo3.ogg"},
        {"musicaFondo4", "music/musicaFondo4.ogg"},
        {"Not a freak1", "music/Not a freak1.ogg"},
        {"Not a freak2", "music/Not a freak2.ogg"},
        {"real envido", "music/real envido.ogg"},
        {"real envido2", "music/real envido2.ogg"},
        {"retruco dance1", "music/retruco dance1.ogg"},
        {"retruco dance2", "music/retruco dance2.ogg"},
        {"retruco db1", "music/retruco db1.ogg"},
        {"retruco db2", "music/retruco db2.ogg"},
        {"retruco one punch1", "music/retruco one punch1.ogg"},
        {"retruco one punch2", "music/retruco one punch2.ogg"},
        {"retruco persona1", "music/retruco persona1.ogg"},
        {"retruco persona2", "music/retruco persona2.ogg"},
        {"retruco silent1", "music/retruco silent1.ogg"},
        {"retruco silent2", "music/retruco silent2.ogg"},
        {"retruco8bit", "music/retruco8bit.ogg"},
        {"retrucoAnime1", "music/retrucoAnime1.ogg"},
        {"retrucoAnime2", "music/retrucoAnime2.ogg"},
        {"retrucoBalatro", "music/retrucoBalatro.ogg"},
        {"retrucoBoss1", "music/retrucoBoss1.ogg"},
        {"retrucoBoss2", "music/retrucoBoss2.ogg"},
        {"retrucoDerrota1", "music/retrucoDerrota1.ogg"},
        {"retrucoDerrota2", "music/retrucoDerrota2.ogg"},
        {"retrucoDramatico", "music/retrucoDramatico.ogg"},
        {"retrucoElectronic", "music/retrucoElectronic.ogg"},
        {"retrucoEmotional", "music/retrucoEmotional.ogg"},
        {"retrucoEmotional2", "music/retrucoEmotional2.ogg"},
        {"retrucoGameplay", "music/retrucoGameplay.ogg"},
        {"retrucoGameplay2", "music/retrucoGameplay2.ogg"},
        {"retrucoJazz", "music/retrucoJazz.ogg"},
        {"retrucoJazz2", "music/retrucoJazz2.ogg"},
        {"retrucoKuze1", "music/retrucoKuze1.ogg"},
        {"retrucoKuze2", "music/retrucoKuze2.ogg"},
        {"retrucoKuze3", "music/retrucoKuze3.ogg"},
        {"retrucoMedieval", "music/retrucoMedieval.ogg"},
        {"retrucoMedieval1", "music/retrucoMedieval1.ogg"},
        {"retrucoMenu1", "music/retrucoMenu1.ogg"},
        {"retrucoMenu2", "music/retrucoMenu2.ogg"},
        {"retrucoMetal1", "music/retrucoMetal1.ogg"},
        {"retrucoMetal2", "music/retrucoMetal2.ogg"},
        {"retrucoOst", "music/retrucoOst.ogg"},
        {"retrucoOst2", "music/retrucoOst2.ogg"},
        {"retrucoPlants1", "music/retrucoPlants1.ogg"},
        {"retrucoPlants2", "music/retrucoPlants2.ogg"},
        {"retrucoPlants3", "music/retrucoPlants3.ogg"},
        {"retrucoResident", "music/retrucoResident.ogg"},
        {"retrucoResident2", "music/retrucoResident2.ogg"},
        {"retrucoRondas1", "music/retrucoRondas1.ogg"},
        {"retrucoRondas2", "music/retrucoRondas2.ogg"},
        {"retrucoSave1", "music/retrucoSave1.ogg"},
        {"retrucoSave2", "music/retrucoSave2.ogg"},
        {"retrucoShop1", "music/retrucoShop1.ogg"},
        {"retrucoShop2", "music/retrucoShop2.ogg"},
        {"retrucoSoft", "music/retrucoSoft.ogg"},
        {"retrucoSoft2", "music/retrucoSoft2.ogg"},
        {"retrucoSynth", "music/retrucoSynth.ogg"},
        {"retrucoSynth2", "music/retrucoSynth2.ogg"},
        {"retrucoTension", "music/retrucoTension.ogg"},
        {"retrucoTension2", "music/retrucoTension2.ogg"},
        {"retrucoVictoria1", "music/retrucoVictoria1.ogg"},
        {"retrucoVictoria2", "music/retrucoVictoria2.ogg"},
        {"retrucoYakuza1", "music/retrucoYakuza1.ogg"}
    };

    public String[][] getPistasMusica() { return PISTAS_MUSICA; }

    public int getIndicePistaActual() { return configuracionJuego.getMusicaIndex(); }

    public void cambiarPistaMusica(int indice) {
        if (indice < 0 || indice >= PISTAS_MUSICA.length) return;
        boolean sonando = musicaFondo != null && musicaFondo.isPlaying();
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.dispose();
        }
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal(PISTAS_MUSICA[indice][1]));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.05f * configuracionJuego.getVolumenMusica());
        if (sonando) musicaFondo.play();
        configuracionJuego.setMusicaIndex(indice);
        configuracionJuego.guardar();
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
        if (fuenteBotones != null) fuenteBotones.dispose();
        if (fuenteNumeros != null) fuenteNumeros.dispose();
        gestorSonidos.dispose();
        if (atlasCartas != null) atlasCartas.dispose();
        if (atlasJokers != null) atlasJokers.dispose();
        if (atlasZodiaco != null) atlasZodiaco.dispose();
        if (texturaRuletaFondo != null) texturaRuletaFondo.dispose();
        if (fuenteUI != null) fuenteUI.dispose();
        if (fuenteTooltipTitulo != null) fuenteTooltipTitulo.dispose();
        if (fuenteTooltipDescripcion != null) fuenteTooltipDescripcion.dispose();
    }

    public void reiniciarDesbloqueoRivales() {
        for (int i = 0; i < listaRivales.size(); i++) {
            listaRivales.get(i).setDesbloqueado(i == 0);
        }
    }

    public boolean hayGuardadoDisponible() {
        return io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorGuardado.existeGuardado();
    }

    public io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado.DatosGuardado getGuardadoDetectado() {
        // Siempre relee del archivo real, nunca confía en una copia vieja en memoria
        return io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorGuardado.cargar();
    }

    // limpiarGuardadoDetectado() ya no necesita hacer nada relevante, pero la dejamos
// por si algo más la llama, para no romper compilación:
    public void limpiarGuardadoDetectado() {
        // no-op: ya no hay estado en memoria que limpiar
    }

}
