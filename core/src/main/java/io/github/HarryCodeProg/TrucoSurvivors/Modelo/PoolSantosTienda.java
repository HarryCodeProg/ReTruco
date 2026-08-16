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
        fabricas.add(Caferino::new);
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
    }

    public Santo tomarAleatorio(Random random) {
        if (fabricas.isEmpty()) return null;
        Supplier<Santo> fabrica =
            fabricas.get(random.nextInt(fabricas.size()));
        return fabrica.get();
    }
}
