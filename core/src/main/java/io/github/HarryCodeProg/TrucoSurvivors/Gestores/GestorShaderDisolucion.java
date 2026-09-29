package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class GestorShaderDisolucion implements Disposable {
    private final ShaderProgram shader;
    private float tiempo = 0f;

    public GestorShaderDisolucion() {
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
            Gdx.files.internal("shaders/disolucion.vert"),
            Gdx.files.internal("shaders/disolucion.frag")
        );
        if (!shader.isCompiled()) {
            Gdx.app.error("GestorShaderDisolucion", "Error compilando shader: " + shader.getLog());
        }
    }

    public void update(float delta) {tiempo += delta;}

    /**
     * Dibuja una region con el efecto de disolucion/quemado aplicado.
     * @param progreso 0 = intacta, 1 = totalmente disuelta
     * @param sombra true para la version "sombra" (silueta negra semitransparente)
     */
    public void dibujarConDisolucion(SpriteBatch batch, TextureRegion region, float x, float y, float w, float h, float progreso, boolean sombra, Color colorQuema1, Color colorQuema2) {
        if (!shader.isCompiled()) {
            batch.draw(region, x, y, w, h); // fallback sin efecto si el shader no compilo
            return;
        }
        ShaderProgram anterior = batch.getShader();
        batch.setShader(shader);
        float u0 = region.getU();
        float v0 = region.getV();
        float u1 = region.getU2();
        float v1 = region.getV2();
        shader.bind();
        shader.setUniformf("u_dissolve", progreso);
        shader.setUniformf("u_time", tiempo);
        shader.setUniformf("u_regionUV", u0, v0, u1, v1);
        shader.setUniformi("u_shadow", sombra ? 1 : 0);
        shader.setUniformf("u_burnColor1", colorQuema1.r, colorQuema1.g, colorQuema1.b, colorQuema1.a);
        shader.setUniformf("u_burnColor2", colorQuema2.r, colorQuema2.g, colorQuema2.b, colorQuema2.a);
        batch.draw(region, x, y, w, h);
        batch.setShader(anterior);
    }

    @Override
    public void dispose() {
        shader.dispose();
    }
}
