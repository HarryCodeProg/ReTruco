package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.Background;

import java.util.ArrayList;

public class PanelPersonalizar {
    public enum Estado { MENU, MUSICA, FONDO }

    private final Main game;
    private final Background fondoPlasma;
    private Estado estado = Estado.MENU;

    private final Boton botonMusica, botonFondo, botonVolver;

    // Almacenamiento de coordenadas del mouse en el mundo
    private float mouseMundoX = -9999f, mouseMundoY = -9999f;

    // --- Lógica de Música ---
    private static final float FILA_ALTO = 44f, FILA_GAP = 2f, PANEL_MUSICA_ANCHO = 420f, PANEL_PAD_X = 22f;
    private static final float HEADER_ALTO = 145f, FOOTER_ALTO = 95f;
    private final ArrayList<Rectangle> filasCanciones = new ArrayList<>();
    private float scrollY = 0f, maxScrollY = 0f, listaAltoVisible = 0f, listaAltoTotal = 0f;
    private Rectangle areaLista = new Rectangle();
    private boolean arrastrandoLista = false;
    private float dragListaStartY = 0f, scrollYStart = 0f;
    private float panelMusicaX, panelMusicaY, panelMusicaAlto;
    private Boton botonVolverMusica;

    // --- Lógica de Fondo ---
    private int fondoIndexPendiente;
    private int hoverFondo = -1;
    private final Boton botonAplicarFondo, botonDescartarFondo;

    // Coordenadas de la grilla de fondos
    private static final float GRID_START_X = 25f;
    private static final float GRID_START_Y = 140f;
    private static final float BTN_W = 120f, BTN_H = 45f, GAP_X = 12f, GAP_Y = 15f;

    public PanelPersonalizar(Main game, Background fondoPlasma) {
        this.game = game;
        this.fondoPlasma = fondoPlasma;
        float centroX = 640f, anchoBoton = 220f, altoBoton = 58f;
        botonMusica = new Boton(centroX - anchoBoton / 2f, 380f, anchoBoton, altoBoton, "MÚSICA", Boton.TipoColor.DORADO, Accion.OPCIONES);
        botonFondo = new Boton(centroX - anchoBoton / 2f, 300f, anchoBoton, altoBoton, "FONDO", Boton.TipoColor.CELESTE, Accion.OPCIONES);
        botonVolver = new Boton(centroX - anchoBoton / 2f, 180f, anchoBoton, altoBoton, "VOLVER", Boton.TipoColor.BORDO, Accion.OPCIONES);
        // Ya no usamos los botones de flecha (< y >)
        botonAplicarFondo = new Boton(centroX - 180f, 120f, 170f, 50f, "APLICAR", Accion.OPCIONES);
        botonDescartarFondo = new Boton(centroX + 10f, 120f, 170f, 50f, "DESCARTAR", Accion.OPCIONES);
        calcularLayoutMusica();
    }

    public void abrir() {
        estado = Estado.MENU;
        fondoIndexPendiente = game.getConfiguracionJuego().getFondoIndex();
    }

    public boolean update(float delta, float mx, float my, boolean click) {
        mouseMundoX = mx;
        mouseMundoY = my;

        if (estado == Estado.MENU) {
            botonMusica.update(mx, my); botonFondo.update(mx, my); botonVolver.update(mx, my);
            if (botonMusica.fueCliqueado(mx, my)) estado = Estado.MUSICA;
            if (botonFondo.fueCliqueado(mx, my)) {
                fondoPlasma.setTema(fondoIndexPendiente);
                estado = Estado.FONDO;
            }
            if (botonVolver.fueCliqueado(mx, my)) return true;
        } else if (estado == Estado.MUSICA) {
            botonVolverMusica.update(mx, my);
            if (botonVolverMusica.fueCliqueado(mx, my)) estado = Estado.MENU;

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && areaLista.contains(mx, my)) {
                arrastrandoLista = true; dragListaStartY = my; scrollYStart = scrollY;
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && arrastrandoLista) {
                scrollY = MathUtils.clamp(scrollYStart + (my - dragListaStartY), 0, maxScrollY);
            } else if (arrastrandoLista) {
                arrastrandoLista = false;
                if (Math.abs(my - dragListaStartY) < 10f) {
                    for (int i = 0; i < filasCanciones.size(); i++) {
                        Rectangle r = filasCanciones.get(i);
                        if (mx >= r.x && mx <= r.x + r.width && my >= r.y + scrollY && my <= r.y + scrollY + r.height) {
                            game.cambiarPistaMusica(i);
                            break;
                        }
                    }
                }
            }
        } else if (estado == Estado.FONDO) {
            botonAplicarFondo.update(mx, my); botonDescartarFondo.update(mx, my);

            // --- LÓGICA DE LA GRILLA DE FONDOS ---
            hoverFondo = -1;
            float panelX = 640f - PANEL_MUSICA_ANCHO / 2f;
            float panelY = 60f; // Nuevo Y
            float panelH = 500f; // Nuevo Alto

            for (int i = 0; i < Background.TEMAS.length; i++) {
                int col = i % 3;
                int row = i / 3;
                float bx = panelX + GRID_START_X + col * (BTN_W + GAP_X);
                float by = panelY + panelH - GRID_START_Y - row * (BTN_H + GAP_Y);

                if (mx >= bx && mx <= bx + BTN_W && my >= by && my <= by + BTN_H) {
                    hoverFondo = i;
                    if (click) {
                        fondoIndexPendiente = i;
                        fondoPlasma.setTema(fondoIndexPendiente);
                    }
                    break;
                }
            }
            // --------------------------------------

            if (botonAplicarFondo.fueCliqueado(mx, my)) {
                game.getConfiguracionJuego().setFondoIndex(fondoIndexPendiente);
                game.getConfiguracionJuego().guardar();
                estado = Estado.MENU;
            }
            if (botonDescartarFondo.fueCliqueado(mx, my)) {
                fondoPlasma.setTema(game.getConfiguracionJuego().getFondoIndex());
                fondoIndexPendiente = game.getConfiguracionJuego().getFondoIndex();
                estado = Estado.MENU;
            }
        }
        return false;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (estado == Estado.MENU) {
            dibujarCajaMenu(batch);
            botonMusica.render(batch); botonFondo.render(batch); botonVolver.render(batch);
        } else if (estado == Estado.MUSICA) {
            dibujarPanelMusica(batch, camera);
        } else if (estado == Estado.FONDO) {
            dibujarPanelFondo(batch);
            botonAplicarFondo.render(batch); botonDescartarFondo.render(batch);
        }
    }

    public boolean scrolled(float amountY) {
        if (estado == Estado.MUSICA) {
            scrollY = MathUtils.clamp(scrollY + amountY * 30f, 0, maxScrollY);
            return true;
        }
        return false;
    }

    private void calcularLayoutMusica() {
        float centroX = 1280f / 2f;
        String[][] pistas = game.getPistasMusica();
        int cantidad = pistas.length;
        int MAX_FILAS_VISIBLES = 5;
        int filasParaMostrar = Math.min(cantidad, MAX_FILAS_VISIBLES);
        listaAltoVisible = filasParaMostrar * FILA_ALTO + Math.max(0, filasParaMostrar - 1) * FILA_GAP;
        listaAltoTotal = cantidad * FILA_ALTO + Math.max(0, cantidad - 1) * FILA_GAP;
        maxScrollY = Math.max(0, listaAltoTotal - listaAltoVisible);
        panelMusicaAlto = HEADER_ALTO + listaAltoVisible + FOOTER_ALTO;
        panelMusicaX = centroX - PANEL_MUSICA_ANCHO / 2f;
        panelMusicaY = 45f;
        areaLista.set(panelMusicaX, panelMusicaY + FOOTER_ALTO, PANEL_MUSICA_ANCHO, listaAltoVisible);
        filasCanciones.clear();
        float anchoFila = PANEL_MUSICA_ANCHO - PANEL_PAD_X * 2f;
        float xFila = panelMusicaX + PANEL_PAD_X;
        float yTopeLocal = areaLista.y + areaLista.height;
        for (int i = 0; i < cantidad; i++) {
            float yFila = yTopeLocal - (i + 1) * FILA_ALTO - i * FILA_GAP;
            filasCanciones.add(new Rectangle(xFila, yFila, anchoFila, FILA_ALTO));
        }
        float anchoVolver = 180f;
        float yVolver = panelMusicaY + (FOOTER_ALTO - 50f) / 2f;
        botonVolverMusica = new Boton(centroX - anchoVolver / 2f, yVolver, anchoVolver, 48f, "VOLVER", Boton.TipoColor.BORDO, Accion.OPCIONES);
    }

    private void dibujarPanelMusica(SpriteBatch batch, OrthographicCamera camera) {
        Texture pixel = game.getPixelBlanco();
        float x = panelMusicaX, y = panelMusicaY, w = PANEL_MUSICA_ANCHO, h = panelMusicaAlto;
        dibujarRectRedondeado(batch, pixel, x + 6f, y - 8f, w, h, 12f, UITheme.SOMBRA);
        dibujarRectRedondeado(batch, pixel, x - 4f, y - 4f, w + 8f, h + 8f, 13f, UITheme.BORDE);
        dibujarRectRedondeado(batch, pixel, x, y, w, h, 10f, UITheme.PANEL_PRINCIPAL);
        batch.setColor(UITheme.DORADO);
        batch.draw(pixel, x + 18f, y + h - 6f, w - 36f, 3f);
        batch.setColor(Color.WHITE);
        BitmapFont fTitulo = game.getFuentePrincipal();
        BitmapFont fUI = game.getFuenteUI();
        BitmapFont fNum = game.getFuenteNumeros();
        float centroX = x + w / 2f;
        float cursorY = y + h - 30f;
        fTitulo.getData().setScale(1.05f);
        fTitulo.setColor(UITheme.DORADO_BRILLANTE);
        fTitulo.draw(batch, "MUSICA DE FONDO", x, cursorY, w, Align.center, false);
        fTitulo.getData().setScale(1f);
        fTitulo.setColor(Color.WHITE);
        cursorY -= 26f;
        fUI.setColor(UITheme.TEXTO_SECUNDARIO);
        fUI.draw(batch, "Selecciona una cancion", x, cursorY, w, Align.center, false);
        fUI.setColor(Color.WHITE);
        cursorY -= 28f;
        int indiceActual = game.getIndicePistaActual();
        String[][] pistas = game.getPistasMusica();
        String nombreActual = (indiceActual >= 0 && indiceActual < pistas.length) ? pistas[indiceActual][0] : "-";
        fUI.getData().setScale(0.65f);
        fUI.setColor(UITheme.TEXTO_SECUNDARIO);
        fUI.draw(batch, "REPRODUCIENDO", x, cursorY, w, Align.center, false);
        fUI.getData().setScale(1f);
        cursorY -= 20f;
        fNum.setColor(UITheme.TEXTO_PRINCIPAL);
        fNum.draw(batch, nombreActual, x, cursorY, w, Align.center, false);
        fUI.setColor(Color.WHITE);
        fNum.setColor(Color.WHITE);
        cursorY -= 22f;
        float barraBaseX = centroX - 24f;
        float barraY = cursorY - 10f;
        for (int b = 0; b < 4; b++) {
            float fase = (System.currentTimeMillis() % 10000) / 1000f * 6f + b * 1.4f;
            float altoBarra = 5f + (float) (Math.abs(Math.sin(fase)) * 11f);
            batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.9f);
            batch.draw(pixel, barraBaseX + b * 14f, barraY, 8f, altoBarra);
        }
        batch.setColor(Color.WHITE);
        batch.flush();
        Rectangle scissor = new Rectangle();
        Rectangle clipBounds = new Rectangle(areaLista.x, areaLista.y, areaLista.width, areaLista.height);
        // Implementación usando ScissorUtil
        boolean enmascaradoExitoso = ScissorUtil.push(camera, batch.getTransformMatrix(), clipBounds, scissor);
        if (enmascaradoExitoso) {
            float mx = mouseMundoX;
            float my = mouseMundoY;
            boolean mouseDentroDePanel = areaLista.contains(mx, my);
            for (int i = 0; i < filasCanciones.size(); i++) {
                Rectangle rBase = filasCanciones.get(i);
                float filaYActual = rBase.y + scrollY;
                if (filaYActual + rBase.height < areaLista.y || filaYActual > areaLista.y + areaLista.height) continue;
                boolean seleccionada = (i == indiceActual);
                boolean hover = mouseDentroDePanel && !arrastrandoLista &&
                    mx >= rBase.x && mx <= rBase.x + rBase.width &&
                    my >= filaYActual && my <= filaYActual + rBase.height;
                Color colorFondo, colorBorde, colorTexto;
                if (seleccionada) {
                    colorFondo = UITheme.DORADO; colorBorde = UITheme.NARANJA; colorTexto = new Color(0.08f, 0.08f, 0.08f, 1f);
                } else if (hover) {
                    colorFondo = UITheme.AZUL_HOVER; colorBorde = UITheme.AZUL; colorTexto = UITheme.TEXTO_PRINCIPAL;
                } else {
                    colorFondo = UITheme.PANEL_SECUNDARIO; colorBorde = UITheme.AZUL; colorTexto = UITheme.TEXTO_PRINCIPAL;
                }
                dibujarRectRedondeado(batch, pixel, rBase.x - 2f, filaYActual - 2f, rBase.width + 4f, rBase.height + 4f, 7f, colorBorde);
                dibujarRectRedondeado(batch, pixel, rBase.x, filaYActual, rBase.width, rBase.height, 6f, colorFondo);
                if (seleccionada) {
                    batch.setColor(colorTexto);
                    float indicadorX = rBase.x + 14f;
                    float indicadorCy = filaYActual + rBase.height / 2f;
                    batch.draw(pixel, indicadorX, indicadorCy - 6f, 3f, 12f);
                    batch.draw(pixel, indicadorX + 3f, indicadorCy - 4f, 3f, 8f);
                    batch.draw(pixel, indicadorX + 6f, indicadorCy - 2f, 3f, 4f);
                    batch.setColor(Color.WHITE);
                }
                String nombre = pistas[i][0].toUpperCase();
                fUI.setColor(colorTexto);
                fUI.draw(batch, nombre, rBase.x + 34f, filaYActual + rBase.height / 2f + 7f, rBase.width - 46f, Align.left, false);
                fUI.setColor(Color.WHITE);
            }
            batch.flush();
            ScissorStack.popScissors();
        }
        if (maxScrollY > 0) {
            float barraTotalH = areaLista.height;
            float barraVisibleH = (listaAltoVisible / listaAltoTotal) * barraTotalH;
            float proporcionScroll = scrollY / maxScrollY;
            float barraScrollY = areaLista.y + barraTotalH - barraVisibleH - (proporcionScroll * (barraTotalH - barraVisibleH));
            batch.setColor(UITheme.BORDE.r, UITheme.BORDE.g, UITheme.BORDE.b, 0.6f);
            dibujarRectRedondeado(batch, pixel, areaLista.x + areaLista.width - 8f, areaLista.y, 6f, barraTotalH, 3f, batch.getColor());
            batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.9f);
            dibujarRectRedondeado(batch, pixel, areaLista.x + areaLista.width - 8f, barraScrollY, 6f, barraVisibleH, 3f, batch.getColor());
            batch.setColor(Color.WHITE);
        }
        botonVolverMusica.render(batch);
    }

    private void dibujarPanelFondo(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        float centroX = 1280f / 2f;
        float w = PANEL_MUSICA_ANCHO;
        float h = 500f; // Panel más alto para la grilla
        float x = centroX - w / 2f, y = 60f;
        dibujarRectRedondeado(batch, pixel, x + 6f, y - 8f, w, h, 12f, UITheme.SOMBRA);
        dibujarRectRedondeado(batch, pixel, x - 4f, y - 4f, w + 8f, h + 8f, 13f, UITheme.BORDE);
        dibujarRectRedondeado(batch, pixel, x, y, w, h, 10f, UITheme.PANEL_PRINCIPAL);
        batch.setColor(UITheme.DORADO);
        batch.draw(pixel, x + 18f, y + h - 6f, w - 36f, 3f);
        batch.setColor(Color.WHITE);
        BitmapFont fTitulo = game.getFuentePrincipal();
        BitmapFont fUI = game.getFuenteUI();
        fTitulo.getData().setScale(1.05f);
        fTitulo.setColor(UITheme.DORADO_BRILLANTE);
        fTitulo.draw(batch, "FONDO", x, y + h - 40f, w, Align.center, false);
        fTitulo.getData().setScale(1f);
        fTitulo.setColor(Color.WHITE);
        fUI.setColor(UITheme.TEXTO_SECUNDARIO);
        fUI.draw(batch, "Selecciona un fondo", x, y + h - 70f, w, Align.center, false);
        fUI.setColor(Color.WHITE);
        // --- DIBUJADO DE LA GRILLA ---
        for (int i = 0; i < Background.TEMAS.length; i++) {
            int col = i % 3;
            int row = i / 3;
            float bx = x + GRID_START_X + col * (BTN_W + GAP_X);
            float by = y + h - GRID_START_Y - row * (BTN_H + GAP_Y);
            boolean seleccionado = (i == fondoIndexPendiente);
            boolean hover = (i == hoverFondo);
            Color colorBorde, colorFondo, colorTexto;
            if (seleccionado) {
                colorBorde = UITheme.NARANJA;
                colorFondo = UITheme.DORADO;
                colorTexto = new Color(0.08f, 0.08f, 0.08f, 1f);
            } else if (hover) {
                colorBorde = UITheme.AZUL;
                colorFondo = UITheme.AZUL_HOVER;
                colorTexto = Color.WHITE;
            } else {
                colorBorde = UITheme.BORDE;
                colorFondo = UITheme.PANEL_SECUNDARIO;
                colorTexto = Color.WHITE; // Texto blanco para que se lea bien
            }
            // Dibujamos el botón
            dibujarRectRedondeado(batch, pixel, bx - 2f, by - 2f, BTN_W + 4f, BTN_H + 4f, 6f, colorBorde);
            dibujarRectRedondeado(batch, pixel, bx, by, BTN_W, BTN_H, 5f, colorFondo);
            // Dibujamos el nombre del tema
            fUI.setColor(colorTexto);
            fUI.getData().setScale(0.52f); // Más chico para que entre en el botón
            String nombre = Background.TEMAS[i].nombre.toUpperCase();
            // El último 'true' permite que el texto baje de línea si es muy largo
            fUI.draw(batch, nombre, bx, by + BTN_H / 2f + 5f, BTN_W, Align.center, true);
            fUI.getData().setScale(1f);
            fUI.setColor(Color.WHITE);
        }
    }

    private void dibujarCajaMenu(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        float anchoBoton = 220f, altoBoton = 58f, padding = 20f;
        float boxW = anchoBoton + (padding * 2f);
        float boxH = (380f + altoBoton - 180f) + (padding * 2f);
        float boxX = (1280f / 2f) - (boxW / 2f);
        float boxY = 180f - padding;
        dibujarRectRedondeado(batch, pixel, boxX - 4f, boxY - 4f, boxW + 8f, boxH + 8f, 12f, new Color(0.12f, 0.15f, 0.17f, 1f));
        dibujarRectRedondeado(batch, pixel, boxX, boxY, boxW, boxH, 10f, new Color(0.20f, 0.25f, 0.28f, 1f));
    }

    private void dibujarRectRedondeado(SpriteBatch batch, Texture pixel, float x, float y, float width, float height, float radio, Color color) {
        if (width <= 0f || height <= 0f) return;
        radio = Math.min(radio, Math.min(width, height) / 2f);
        batch.setColor(color);
        batch.draw(pixel, x + radio, y, width - radio * 2f, height);
        batch.draw(pixel, x, y + radio, radio, height - radio * 2f);
        batch.draw(pixel, x + width - radio, y + radio, radio, height - radio * 2f);
        int pasos = Math.max(2, (int) radio);
        for (int i = 0; i < pasos; i++) {
            float dy = i + 0.5f;
            float distancia = radio - dy;
            float raiz = (float) Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));
            float inset = radio - raiz;
            batch.draw(pixel, x + inset, y + i, width - inset * 2f, 1f);
            batch.draw(pixel, x + inset, y + height - i - 1f, width - inset * 2f, 1f);
        }
        batch.setColor(Color.WHITE);
    }
}
