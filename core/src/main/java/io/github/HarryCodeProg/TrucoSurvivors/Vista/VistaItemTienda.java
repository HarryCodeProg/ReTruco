package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ItemTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class VistaItemTienda {
    private final ItemTienda item;
    private TextureRegion region;
    private float x, y;
    private float width = 100, height = 150;
    private boolean seleccionado = false;
    private boolean hover = false;
    private float visualOffsetY = 0f;
    private float targetOffsetY = 0f;
    private float scale = 1f;
    private float targetScale = 1f;
    private static final float OFFSET_SELECCIONADO = 25f;
    private static final float OFFSET_HOVER = 8f;
    private static final float ESCALA_HOVER = 1.04f;
    private static final float VELOCIDAD_OFFSET = 250f;
    private static final float VELOCIDAD_ESCALA = 4f;
    private VistaJoker vistaJokerInterna;
    private VistaCarta vistaCartaInterna;
    private VistaSanto vistaSantoInterna;
    private Juego juego;

    public VistaItemTienda(ItemTienda item, TextureAtlas atlasCartas, TextureAtlas atlasJokers, Juego juego) {
        this.item = item;
        this.juego = juego;
        if (item.getTipo() == ItemTienda.Tipo.CARTA) {
            this.vistaCartaInterna = new VistaCarta(item.getCarta(), false, atlasCartas);
            this.vistaCartaInterna.setEnModal(true); // Permite hover individual dentro de paneles/tienda
        } else if (item.getTipo() == ItemTienda.Tipo.JOKER) {
            if (item.getJoker() != null) {
                this.vistaJokerInterna = new VistaJoker(item.getJoker(), atlasJokers);
            } else {
                this.vistaJokerInterna = null;
            }
        } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
            if (item.getSanto() != null) {
                TextureAtlas atlasSantos = (Main.getInstance() != null) ? Main.getInstance().getAtlasSantos() : null;
                this.vistaSantoInterna = new VistaSanto(item.getSanto(), atlasSantos);
            }
        } else {
            this.vistaJokerInterna = null;
        }
    } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
        // Crear VistaSanto usando atlas de Main (si está disponible)
        if (item.getSanto() != null) {
            TextureAtlas atlasSantos = (Main.getInstance() != null) ? Main.getInstance().getAtlasSantos() : null;
            this.vistaSantoInterna = new VistaSanto(item.getSanto(), atlasSantos);
        }
    } else {
        // otros tipos (ZODIACO, etc) — inicializar region si hace falta
        this.region = null;
    }
}

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        if (vistaJokerInterna != null) {
            vistaJokerInterna.setPosition(x, y);
            vistaJokerInterna.setHandPosition(x, y);
            vistaJokerInterna.setTamaño(width, height);
        }
        if (vistaCartaInterna != null) {
            vistaCartaInterna.setPosition(x, y);
            vistaCartaInterna.setHandPosition(x, y);
            vistaCartaInterna.setTamaño(width, height);
        }
        if (vistaSantoInterna != null) {
            vistaSantoInterna.setPosition(x, y);
            vistaSantoInterna.setHandPosition(x, y);
            vistaSantoInterna.setTamaño(width, height);
        }
    }

    public ItemTienda getItem() { return item; }
    public boolean isSeleccionado() { return seleccionado; }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
        if (vistaJokerInterna != null) vistaJokerInterna.setSeleccionada(seleccionado);
        if (vistaCartaInterna != null) vistaCartaInterna.setSeleccionada(seleccionado);
        if (vistaSantoInterna != null) vistaSantoInterna.setSeleccionada(seleccionado);
    }

    public boolean contiene(float mx, float my) {
        float w = width * scale;
        float h = height * scale;
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private float moverHacia(float value, float target, float maxDelta) {
        float diferencia = target - value;
        if (Math.abs(diferencia) <= maxDelta) return target;
        return value + Math.signum(diferencia) * maxDelta;
    }

    public void render(SpriteBatch batch, Main game) {
        if (vistaJokerInterna != null) {
            vistaJokerInterna.render(batch);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.render(batch, game);
        } else if (region != null) {
            float drawY = y + visualOffsetY;
            float w = width * scale;
            float h = height * scale;
            batch.draw(region, x, drawY, w, h);
        }
        if (vistaSantoInterna != null) vistaSantoInterna.render(batch);
    }

    public boolean isHover() {return hover;}

    public void update(float mouseX, float mouseY, float delta) {
        hover = contiene(mouseX, mouseY);
        if (vistaJokerInterna != null) {
            vistaJokerInterna.update(mouseX, mouseY, delta);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.update(mouseX, mouseY, delta);
        } else {
            float offsetHover = hover ? OFFSET_HOVER : 0f;
            float offsetSeleccion = seleccionado ? OFFSET_SELECCIONADO : 0f;
            targetScale = hover ? ESCALA_HOVER : 1f;
            targetOffsetY = offsetHover + offsetSeleccion;
            scale = moverHacia(scale, targetScale, VELOCIDAD_ESCALA * delta);
            visualOffsetY = moverHacia(visualOffsetY, targetOffsetY, VELOCIDAD_OFFSET * delta);
        }
        if (vistaSantoInterna != null) vistaSantoInterna.update(mouseX, mouseY, delta);
    }

    public void renderCartelStats(SpriteBatch batch, Main game) {
        if (!hover) return;
        if (vistaSantoInterna != null) {
            vistaSantoInterna.renderCartelStats(batch, game);
        } else if (vistaJokerInterna != null) {
            vistaJokerInterna.renderCartelStats(batch, game, juego);
        } else if (vistaCartaInterna != null) {
            vistaCartaInterna.renderCartelStats(batch, game);
        } else if (item.getTipo() == ItemTienda.Tipo.SANTO) {
            // Si queremos mostrar info del santo al hover, podríamos dibujar un texto simple
            // Por ahora no hacemos nada extra para evitar NPEs.
        }
    }

    public float getX() {return x;}

    public float getY() {return y;}

    public void setTamaño(float width, float height) {
        this.width = width;
        this.height = height;
        if (vistaJokerInterna != null) {
            vistaJokerInterna.setTamaño(width, height);
        }
        if (vistaCartaInterna != null) {
            vistaCartaInterna.setTamaño(width, height);
        }
    }
}
