package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;

import java.util.EnumMap;
import java.util.Map;

public class AssetsRasgos {

    private static final String PATH = "ui/categorias-spritesheet.png";

    // El spritesheet completo mide 1536 x 1024 y está organizado en 10 x 9 celdas.
    private static final int SHEET_W = 1536;
    private static final int SHEET_H = 1024;
    private static final int COLS = 10;
    private static final int ROWS = 9;

    private final Texture sheet;
    private final Map<CategoriaJoker, TextureRegion[]> cache =
        new EnumMap<>(CategoriaJoker.class);

    public AssetsRasgos() {
        sheet = new Texture(Gdx.files.internal(PATH));
        sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Evita asumir que cada celda tiene un ancho/alto entero: 1536/10 y 1024/9
        // no son divisiones exactas. Se calculan los bordes de cada celda por separado.
        if (sheet.getWidth() != SHEET_W || sheet.getHeight() != SHEET_H) {
            throw new IllegalStateException(
                "Tamaño inesperado del spritesheet de rasgos: "
                    + sheet.getWidth() + "x" + sheet.getHeight()
                    + " (esperado " + SHEET_W + "x" + SHEET_H + ")"
            );
        }

        for (CategoriaJoker cat : CategoriaJoker.values()) {
            if (!cat.tieneIcono()) continue;

            TextureRegion[] tiers = new TextureRegion[cat.getMaxTier() + 1];
            int colBase = cat.getMitad() * 5;
            int fila = cat.getFila();

            if (fila >= ROWS || colBase < 0 || colBase >= COLS) {
                throw new IllegalArgumentException(
                    "Coordenadas de spritesheet inválidas para " + cat.name()
                );
            }

            for (int tier = 0; tier <= cat.getMaxTier(); tier++) {
                int col = colBase + tier;
                if (col >= COLS) {
                    throw new IllegalArgumentException(
                        "El tier " + tier + " de " + cat.name()
                            + " queda fuera del spritesheet (columna " + col + ")"
                    );
                }

                // Límites de celda redondeados desde la proporción exacta.
                int x0 = Math.round(col * (SHEET_W / (float) COLS));
                int x1 = Math.round((col + 1) * (SHEET_W / (float) COLS));

                // TextureRegion(Texture, x, y, width, height) usa y desde arriba,
                // igual que CategoriaJoker.fila. No invertir la coordenada vertical.
                int yTop = Math.round(fila * (SHEET_H / (float) ROWS));
                int yBottom = Math.round((fila + 1) * (SHEET_H / (float) ROWS));
                int y0 = yTop;

                int width = x1 - x0;
                int height = yBottom - yTop;
                if (width <= 0 || height <= 0) {
                    throw new IllegalStateException(
                        "Recorte vacío para " + cat.name() + ", tier " + tier
                    );
                }

                tiers[tier] = new TextureRegion(sheet, x0, y0, width, height);
            }
            cache.put(cat, tiers);
        }
    }

    public TextureRegion get(CategoriaJoker cat, int tier) {
        if (cat == null) return null;
        TextureRegion[] tiers = cache.get(cat);
        if (tiers == null || tier < 0 || tier >= tiers.length) return null;
        return tiers[tier];
    }

    public void dispose() {
        if (sheet != null) sheet.dispose();
    }
}

// Si los iconos aún se ven desplazados después de este cambio, el problema ya no estaría
// en la inversión de filas: habría que ajustar el margen interno de cada celda del PNG o
// la posición usada por TooltipUI.dibujarBadge(...). La lógica de este archivo presupone
// celdas uniformes, sin gutters externos, y filas numeradas desde arriba.
