package io.github.HarryCodeProg.TrucoSurvivors.Estados;

public enum EventoJuego {
    POST_REPARTO,
    AL_JUGAR_CARTA,
    AL_PUNTUAR_CARTA,
    AL_JUGAR_PRIMERA_CARTA,
    AL_GANAR_BAZA,
    AL_GANAR_TRUCO,
    AL_GANAR_ENVIDO,
    AL_DESCARTAR,
    TERMINO_MANO,
    AL_JUGAR_SEGUNDA_CARTA,
    AL_CANTAR_ENVIDO,      // el jugador o el rival propone/escala un canto de envido
    AL_GANAR_ENVIDO_CANTO, // se resolvio el envido y el jugador gano (antes de sumar puntos)
    AL_PERDER_ENVIDO,      // se resolvio el envido y el jugador perdio
    AL_MATAR_CARTA,      // la carta del jugador gano su baza individual (dispara CON ctx.cartaEnResolucion = esa carta)
    AL_SER_MATADO,       // la carta del jugador perdio su baza individual (idem)

    ANTES_DE_SUMAR_ENVIDO,
    ANTES_DE_SUMAR_TRUCO,
    AL_CANTAR_QUIERO,
    AL_DECIR_QUIERO_TRUCO,
    AL_DECIR_NO_QUIERO_TRUCO,
    AL_DECIR_NO_QUIERO_ENVIDO,
    AL_DECIR_QUIERO_ENVIDO,
}
