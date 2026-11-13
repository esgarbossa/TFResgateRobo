package robos;

import enums.NivelRisco;
import enums.StatusRobo;
import enums.TipoMissao;
import enums.TipoTerreno;

public class Drone extends Robo {
    private double alcanceMaximo;

    public Drone(int id, int missoesRealizadas, String nome, double nivelBateria, StatusRobo status, double alcanceMaximo) {
        super(id, missoesRealizadas, nome, nivelBateria, status);
        if (!Double.isFinite(alcanceMaximo)
                || alcanceMaximo <= 0
                || alcanceMaximo > 100) {
            throw new IllegalArgumentException("O alcance maximo do drone deve ser maior que 0 e ate 100 km");
        }

        this.alcanceMaximo = alcanceMaximo;
    }

    public double getAlcanceMaximo() {
        return alcanceMaximo;
    }

    @Override
    public void executarMissao() {

    }

    @Override
    public boolean compativelCom(TipoMissao tipo, TipoTerreno terreno) {
        if (tipo == null || terreno == null) {
            throw new IllegalArgumentException("Tipo de missao e terreno sao obrigatorios");
        }

        return tipo == TipoMissao.LOCALIZACAO_DE_PESSOAS || tipo == TipoMissao.RECONHECIMENTO;
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

        if (tipo == TipoMissao.LOCALIZACAO_DE_PESSOAS) {
            switch (risco) {
                case BAIXO:
                    return 25;
                case MEDIO:
                    return 35;
                case ALTO:
                    return 60;
            }
        }

        if (tipo == TipoMissao.RECONHECIMENTO) {
            switch (risco) {
                case BAIXO:
                    return 20;
                case MEDIO:
                    return 30;
                case ALTO:
                    return 50;
            }
        }
        throw new IllegalArgumentException(
                "Nao existe consumo definido para essa missao e risco"
        );
    }
}