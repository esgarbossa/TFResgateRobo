package robos;

import enums.NivelRisco;
import enums.StatusRobo;
import enums.TipoMissao;
import enums.TipoTerreno;

public class RoboTerrestre extends Robo {
    private double capacidadeCarga;

    public RoboTerrestre(int id, int missoesRealizadas, String nome, double nivelBateria, StatusRobo status, double capacidadeCarga) {
        super(id, missoesRealizadas, nome, nivelBateria, status);
        if (!Double.isFinite(capacidadeCarga)
                || capacidadeCarga < 20
                || capacidadeCarga > 1000) {
            throw new IllegalArgumentException(
                    "O robo deve ter uma capacidade de carga entre 20 e 1000 kg"
            );
        }

        this.capacidadeCarga = capacidadeCarga;
    }

    public double getCapacidadeCarga() {
        return capacidadeCarga;
    }

    @Override
    public void executarMissao() {

    }

    @Override
    public boolean compativelCom(TipoMissao tipo, TipoTerreno terreno) {
        if (tipo == null || terreno == null) {
            throw new IllegalArgumentException(
                    "Tipo de missao e terreno sao obrigatorios"
            );
        }

        return terreno != TipoTerreno.ALAGADO &&
                (tipo == TipoMissao.LOCALIZACAO_DE_PESSOAS || tipo == TipoMissao.TRANSPORTE_DE_SUPRIMENTOS);
    }

    @Override
    public double calcularConsumo(TipoMissao tipo, TipoTerreno terreno, NivelRisco risco) {
        if (risco == null) {
            throw new IllegalArgumentException(
                    "O nivel de risco e obrigatorio"
            );
        }

        if (!compativelCom(tipo, terreno)) {
            throw new IllegalArgumentException("Missao incompativel");
        }

        double consumo = 0;

        switch (risco) {
            case BAIXO:
                consumo += 5;
                break;
            case MEDIO:
                consumo += 10;
                break;
            case ALTO:
                consumo += 15;
                break;
        }

        switch (tipo) {
            case LOCALIZACAO_DE_PESSOAS:
                consumo += 10;
                break;
            case TRANSPORTE_DE_SUPRIMENTOS:
                consumo += 20;
                break;
        }

        switch (terreno) {
            case URBANO:
                consumo += 10;
                break;
            case IRREGULAR:
                consumo += 20;
                break;
        }

        return consumo;
    }
}