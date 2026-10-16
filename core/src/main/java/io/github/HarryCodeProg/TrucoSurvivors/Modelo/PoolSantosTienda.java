package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class PoolSantosTienda {

    private final ArrayList<Supplier<Santo>> fabricas = new ArrayList<>();

    public PoolSantosTienda() {
        fabricas.add(AlmaMula::new);
        fabricas.add(Ceferino::new);
        fabricas.add(CuraBrochero::new);
        fabricas.add(DifuntaCorrea::new);
        fabricas.add(ElFamiliar::new);
        fabricas.add(GauchitoGil::new);
        fabricas.add(LuzMala::new);
        fabricas.add(MadreMaria::new);
        fabricas.add(MariaDeLosRemedios::new);
        fabricas.add(MboiTui::new);
        fabricas.add(Pachamama::new);
        fabricas.add(Pombero::new);
        fabricas.add(SanAntonioDePadua::new);
        fabricas.add(SanCayetano::new);
        fabricas.add(SanBenito::new);
        fabricas.add(SanExpedito::new);
        fabricas.add(SanFrancisco::new);
        fabricas.add(SanJorge::new);
        fabricas.add(SanRoque::new);
        fabricas.add(SanMartinDePorres::new);
        fabricas.add(SanNicolas::new);
        fabricas.add(SanPantaleon::new);
        fabricas.add(SantaRita::new);
        fabricas.add(YasyYatere::new);
        fabricas.add(SanLaMuerte::new);
    }

    public Santo tomarAleatorio(Random random) {
        if (fabricas.isEmpty()) return null;
        Supplier<Santo> fabrica =
            fabricas.get(random.nextInt(fabricas.size()));
        return fabrica.get();
    }

    public Santo tomarAleatorio() {
        return tomarAleatorio(new Random());
    }

    public ArrayList<Santo> crearTodos() {
        ArrayList<Santo> resultado = new ArrayList<>();
        for (Supplier<Santo> fabrica : fabricas) {
            resultado.add(fabrica.get());
        }
        return resultado;
    }

    public Santo crearPorId(int id) {
        for (Supplier<Santo> f : fabricas) {
            Santo s = f.get();
            if (s.getId() == id) return s;
        }
        return null;
    }
}
