package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.HarryCodeProg.TrucoSurvivors.Main;

/**
 * LoadingScreenCentered
 * - centra el sprite animado (spritesheet horizontal) en el centro de la pantalla
 * - reproduce los frames en bucle (sin desplazamiento)
 * - puede usar AssetManager para carga real; o solo mostrar por minDisplaySeconds
 *
 * Constructor recomendado (con AssetManager):
 * new LoadingScreenCentered(game, assets, "ui/unpeso-spritesheet.png", 12, 1.0f, () -> { ... });
 *
 * Constructor simple (sin AssetManager):
 * new LoadingScreenCentered(game, null, "ui/unpeso-spritesheet.png", 12, 2.0f, () -> { ... });
 */
public class LoadingScreenCentered implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    private Texture spriteStrip;
    private Animation<TextureRegion> anim;
    private float stateTime = 0f;
    private final int frameCount;
    private final float frameDuration; // seconds per frame

    private float spriteWidth;
    private float spriteHeight;
    private float displayScale = 1f; // escala aplicada al dibujado

    private BitmapFont font;
    private final AssetManager assets; // puede ser null si no usás AssetManager
    private final Runnable onFinished;
    private final float minDisplaySeconds;
    private boolean startedLoading = false;
    private boolean finished = false;

    public LoadingScreenCentered(Main game, AssetManager assets, String stripPath, int frames, float minDisplaySeconds, Runnable onFinished) {
        this.game = game;
        this.assets = assets;
        this.frameCount = frames;
        this.minDisplaySeconds = minDisplaySeconds;
        this.onFinished = onFinished;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(1280, 720, camera);
        this.font = new BitmapFont();
        this.frameDuration = 1f / 12f; // 12 FPS por defecto (ajustable)

        loadStrip(stripPath);
    }

    private void loadStrip(String path) {
        spriteStrip = new Texture(Gdx.files.internal(path));
        int w = spriteStrip.getWidth();
        int h = spriteStrip.getHeight();
        int frameW = w / frameCount;
        int frameH = h;
        spriteWidth = frameW;
        spriteHeight = frameH;

        TextureRegion[][] tmp = TextureRegion.split(spriteStrip, frameW, frameH);
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) frames[i] = tmp[0][i];
        anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(Animation.PlayMode.LOOP);

        // calcula escala para que no sea gigantesco en pantalla (ajusta maxDisplayWidth)
        float maxDisplayWidth = viewport.getWorldWidth() * 0.25f; // que ocupe hasta 25% del ancho
        if (spriteWidth > maxDisplayWidth) {
            displayScale = maxDisplayWidth / spriteWidth;
        } else {
            displayScale = 1f;
        }
    }

    private void queueAssets() {
        if (assets == null) return;
        // Encolá acá todos los recursos que querés cargar. Ej:
        // assets.load("ui/some_texture.png", Texture.class);
        // assets.load("sounds/music.mp3", Music.class);
        // assets.load(...);

        // Si no hay nada que cargar, no hacemos nada; startedLoading se marca igual.
        startedLoading = true;
    }

    @Override
    public void show() {
        stateTime = 0f;
        startedLoading = false;
    }

    @Override
    public void render(float delta) {
        // avance del tiempo de pantalla
        stateTime += delta;
        // encolar assets una sola vez
        if (!startedLoading) queueAssets();
        // limpieza de pantalla
        Gdx.gl.glClearColor(0.06f, 0.07f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        // dibujado de la animación centrada
        batch.begin();
        TextureRegion current = anim.getKeyFrame(stateTime, true);
        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();
        float drawW = spriteWidth * displayScale;
        float drawH = spriteHeight * displayScale;
        float x = (screenWidth - drawW) / 2f;
        float y = (screenHeight - drawH) / 2f;
        batch.draw(current, x, y, drawW, drawH);
        // texto centrado debajo de la moneda / con % si hay AssetManager
        String text;
        float progress = 0f;
        if (assets != null && startedLoading) {
            progress = assets.getProgress(); // 0..1
            text = "Cargando... " + Math.round(progress * 100) + "%";
        } else {
            text = "Cargando...";
        }
        font.getData().setScale(1.3f);
        GlyphLayout layout = new GlyphLayout(font, text);
        float tx = (screenWidth - layout.width) / 2f;
        float ty = y - 18f; // 18 px por debajo del sprite
        if (ty < 24f) ty = 24f;
        font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        font.draw(batch, layout, tx, ty);
        batch.end();
        // actualizar AssetManager UNA vez por frame y comprobar finalización
        if (!finished) {
            if (assets != null) {
                boolean done = assets.update(); // procesa una porción; devuelve true si terminó
                if (done && stateTime >= minDisplaySeconds) {
                    finished = true;
                    if (onFinished != null) onFinished.run();
                }
            } else {
                // sin AssetManager: esperar el tiempo mínimo
                if (stateTime >= minDisplaySeconds) {
                    finished = true;
                    if (onFinished != null) onFinished.run();
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        // recalcula escala si la ventana cambia:
        float maxDisplayWidth = viewport.getWorldWidth() * 0.25f;
        if (spriteWidth > maxDisplayWidth) displayScale = maxDisplayWidth / spriteWidth;
        else displayScale = 1f;
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (spriteStrip != null) spriteStrip.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
