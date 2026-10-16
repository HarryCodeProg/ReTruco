package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;

public class CatalogoRasgos {

    /**
     * Devuelve el efecto del tier indicado, o null si esa categoría/tier
     * no tiene efecto (todavía).
     * Tier es 1-based.
     */
    public static EfectoRasgo getEfecto(CategoriaJoker cat, int tier) {
        switch (cat) {
            case TRUCO:
                switch (tier) {
                    case 1: return new EfectoTrucoBonus(0.25);
                    case 2: return new EfectoTrucoBonus(1.0);
                    case 3: return new EfectoTrucoMultiplicador(1.5);
                }
                break;
            case ENVIDO:
                switch (tier) {
                    case 1: return new EfectoEnvidoBonus(0.25);
                    case 2: return new EfectoEnvidoBonus(1.0);
                    case 3: return new EfectoEnvidoMultiplicador(1.5);
                }
                break;
            // TODO: agregar el resto cuando los implementes
            default: break;
        }
        return null;
    }
}
