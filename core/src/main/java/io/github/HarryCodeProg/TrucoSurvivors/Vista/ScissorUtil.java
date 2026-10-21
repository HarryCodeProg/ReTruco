package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class ScissorUtil {
    private static Viewport viewportActual;

    private ScissorUtil() {}

    /** Cada pantalla lo llama en su render, antes de dibujar. */
    public static void setViewport(Viewport v) { viewportActual = v; }

    /** Calcula el recorte respetando las barras negras del FitViewport y lo apila. */
    public static boolean push(Camera camera, Matrix4 batchTransform, Rectangle area, Rectangle scissor) {
        if (viewportActual != null) viewportActual.calculateScissors(batchTransform, area, scissor);
        else ScissorStack.calculateScissors(camera, batchTransform, area, scissor);
        return ScissorStack.pushScissors(scissor);
    }
}
