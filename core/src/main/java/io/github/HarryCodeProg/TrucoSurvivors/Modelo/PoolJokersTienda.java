package io.github.HarryCodeProg.TrucoSurvivors.Modelo;


import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun.BotellaCortada;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Legendario.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;
import java.util.List;

public class PoolJokersTienda {
    private final ArrayList<Supplier<Joker>> fabricas = new ArrayList<>();

    public PoolJokersTienda() {
        //comun
        fabricas.add(JokerEspañol::new);
        fabricas.add(Mate::new);
        fabricas.add(Gaseosa::new);
        fabricas.add(BotellaCortada::new);
        fabricas.add(Fernet::new);
        fabricas.add(VinoCaja::new);
        fabricas.add(BolasDeFraile::new);
        fabricas.add(Cañoncito::new);
        fabricas.add(Cerveza::new);
        fabricas.add(CervezaRota::new);
        fabricas.add(Churros::new);
        fabricas.add(DulceDeLeche::new);
        fabricas.add(Galletitas::new);
        fabricas.add(MediaLuna::new);
        fabricas.add(Milanesa::new);
        fabricas.add(Moñito::new);
        fabricas.add(Pastafrola::new);
        fabricas.add(Pastelitos::new);
        fabricas.add(Sacramento::new);
        fabricas.add(Termo::new);
        fabricas.add(TortaNegra::new);
        fabricas.add(Vigilante::new);
        fabricas.add(Agua::new);
        fabricas.add(AltoGuiso::new);
        fabricas.add(Damajuana::new);
        fabricas.add(AndaALaCancha::new);
        fabricas.add(AtiendoBoludos::new);
        fabricas.add(DVD::new);
        fabricas.add(Alfajor::new);
        fabricas.add(Flan::new);
        fabricas.add(Ñoquis::new);
        fabricas.add(Chimichurri::new);
        fabricas.add(Choripan::new);
        fabricas.add(Locro::new);
        fabricas.add(Pizza::new);
        fabricas.add(GalletaConRelleno::new);
        fabricas.add(Oblea::new);
        fabricas.add(Peso::new);
        fabricas.add(PastelDePapa::new);
        fabricas.add(Yerba::new);
        fabricas.add(Vacio::new);
        fabricas.add(Birome::new);
        fabricas.add(AK47::new);
        fabricas.add(FanaticoEnojado::new);
        fabricas.add(NoHayPolque::new);
        fabricas.add(SeTieneQueArrepentir::new);
        fabricas.add(NoMeMidas::new);
        fabricas.add(DameLaMochila::new);
        fabricas.add(NoEstaTanMal::new);
        fabricas.add(Empanada::new);
        fabricas.add(MatoAlJuez::new);
        fabricas.add(SanguchesDeMiga::new);
        fabricas.add(SosInumputable::new);
        fabricas.add(YoManejo::new);
        fabricas.add(CañaConRuda::new);
        fabricas.add(Soda::new);

        //raro
        fabricas.add(Granadero::new);
        fabricas.add(Catamarca::new);
        fabricas.add(MartinFierro::new);
        fabricas.add(Yaguarete::new);
        fabricas.add(Misiones::new);
        fabricas.add(CuentaCorriente::new);
        fabricas.add(Hornero::new);
        fabricas.add(RioNegro::new);
        fabricas.add(Cordoba::new);
        //fabricas.add(Asado::new);
        fabricas.add(Caballo::new);
        fabricas.add(Carpincho::new);
        fabricas.add(Ñandu::new);
        fabricas.add(Dogo::new);
        fabricas.add(Formosa::new);
        fabricas.add(Chaco::new);
        fabricas.add(SantaCruz::new);
        fabricas.add(Chubut::new);
        fabricas.add(Jujuy::new);
        fabricas.add(LaRioja::new);
        fabricas.add(SantaFe::new);
        fabricas.add(SanJuan::new);
        fabricas.add(SanLuis::new);
        fabricas.add(EntreRios::new);
        fabricas.add(Guanaco::new);
        fabricas.add(Mendoza::new);

        //muy raro
        fabricas.add(VirgenItati::new);
        fabricas.add(VirgenDeLujan::new);
        fabricas.add(PlazaDeMayo::new);
        fabricas.add(Obelisco::new);

        //epico
        fabricas.add(Aconcagua::new);
        fabricas.add(Andes::new);
        fabricas.add(Rosas::new);
        fabricas.add(GlaciarPeritoMoreno::new);
        fabricas.add(Iorio::new);
        fabricas.add(Gardel::new);
        fabricas.add(Favaloro::new);
        fabricas.add(Malvinas::new);
        fabricas.add(Cerati::new);
        fabricas.add(Cortazar::new);
        fabricas.add(SableCorvo::new);

        //legendario
        fabricas.add(Rivadavia::new);
        fabricas.add(Roca::new);
        fabricas.add(SanMartin::new);
        fabricas.add(Belgrano::new);
    }

    /** Devuelve un joker nuevo al azar, evitando (si es posible) los que el jugador ya tiene por clase. */
    public Joker tomarAleatorio(Random random, Jugador jugador, boolean permitirDuplicados, List<Integer> idsExcluidos) {
        ArrayList<Supplier<Joker>> disponibles = new ArrayList<>();
        for (Supplier<Joker> f : fabricas) {
            Joker candidato = f.get();
            if (!permitirDuplicados) {
                boolean yaLoTiene = jugador.getJokers().stream().anyMatch(j -> j.getClass().equals(candidato.getClass()));
                if (yaLoTiene) continue;
                if (idsExcluidos != null && idsExcluidos.contains(candidato.getId())) {
                    continue;
                }
            }
            disponibles.add(f);
        }
        if (disponibles.isEmpty()) {
            return null;
        }
        return disponibles.get(random.nextInt(disponibles.size())).get();
    }

    public Joker tomarAleatorio(Random random, Jugador jugador) {
        return tomarAleatorio(random, jugador, false, null);
    }

    public Joker tomarAleatorioDeRareza(Rareza rareza, Jugador jugador) {
        Random random = new Random();
        ArrayList<Supplier<Joker>> disponibles = new ArrayList<>();
        ArrayList<Supplier<Joker>> todosDeRareza = new ArrayList<>();
        for (Supplier<Joker> f : fabricas) {
            Joker candidato = f.get();
            if (candidato.getRareza() == rareza) {
                todosDeRareza.add(f);
                boolean yaLoTiene = jugador.getJokers().stream()
                    .anyMatch(j -> j.getClass().equals(candidato.getClass()));
                if (!yaLoTiene) disponibles.add(f);
            }
        }
        // Si ya tiene todos los jokers de esta rareza, permitimos repetidos
        if (disponibles.isEmpty()) {
            disponibles = todosDeRareza;
        }
        // Si no hay Jokers de esta rareza cargados en el pool, devolvemos null para no romper nada
        if (disponibles.isEmpty()) {
            return null;
        }
        return disponibles.get(random.nextInt(disponibles.size())).get();
    }

    public ArrayList<Joker> crearTodos() {
        ArrayList<Joker> resultado = new ArrayList<>();
        for (Supplier<Joker> fabrica : fabricas) {
            resultado.add(fabrica.get());
        }
        return resultado;
    }
}

