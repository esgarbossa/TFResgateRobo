package robos;

import controle.Comunicavel;
import enums.StatusRobo;
import enums.TipoMissao;
import enums.TipoTerreno;
import enums.NivelRisco;
import enums.StatusMissao;
import missoes.Missao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class Robo implements Comunicavel, Serializable {
    private int id;
    private String nome;
    private double nivelBateria;
    private StatusRobo status;
    private int missoesRealizadas;
    private final Set<Integer> missoesExecutadas = new HashSet<>();

    public Robo(int id, int missoesRealizadas, String nome, double nivelBateria, StatusRobo status) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O robo deve ter nome");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("ID invalido");
        }
        this.id = id;
        if (missoesRealizadas < 0) {
            throw new IllegalArgumentException("A quantidade de missoes nao pode ser negativa");
        }
        this.missoesRealizadas = missoesRealizadas;
        this.nome = nome;
        setNivelBateria(nivelBateria);
        setStatus(status);
    }

    public StatusRobo getStatus() {
        return status;
    }

    public double getNivelBateria() {
        return nivelBateria;
    }

    public int getId() {
        return id;
    }

    public int getMissoesRealizadas() {
        return missoesRealizadas;
    }

    public String getNome() {
        return nome;
    }

    public void setNivelBateria(double nivelBateria) {
        if (!Double.isFinite(nivelBateria) || nivelBateria < 0 || nivelBateria > 100) {
            throw new IllegalArgumentException("A bateria deve estar entre 0 e 100");
        }
        this.nivelBateria = nivelBateria;
        if (nivelBateria == 0 && status != StatusRobo.EM_MANUTENCAO) {
            status = StatusRobo.SEM_BATERIA;
        } else if (nivelBateria > 0 && status == StatusRobo.SEM_BATERIA) {
            status = StatusRobo.DISPONIVEL;
        }
    }

    public void setStatus(StatusRobo status) {
        if (status == null) {
            throw new IllegalArgumentException("O status do robo e obrigatorio");
        }
        if (status == StatusRobo.SEM_BATERIA && nivelBateria > 0) {
            throw new IllegalArgumentException("O robo ainda possui bateria");
        }
        if (status == StatusRobo.EM_MISSAO && nivelBateria == 0) {
            throw new IllegalStateException("Um robo sem bateria nao pode entrar em missao");
        }
        if (status == StatusRobo.DISPONIVEL && nivelBateria == 0) {
            status = StatusRobo.SEM_BATERIA;
        }
        this.status = status;
    }

    public boolean verificarDisponibilidade() {
        return status == StatusRobo.DISPONIVEL && nivelBateria > 0;
    }

    public double consultarBateria() {
        return getNivelBateria();
    }

    public void recarregar() {
        if (status == StatusRobo.EM_MISSAO) {
            throw new IllegalStateException("Nao e possivel recarregar durante uma missao!");
        }
        setNivelBateria(100);
        if (status == StatusRobo.SEM_BATERIA) {
            status = StatusRobo.DISPONIVEL;
        }
    }

    public void exibirResumo() {
        System.out.println("id:" + getId());
        System.out.println("Nome: " + getNome());
        System.out.println("Nivel da bateria: " + consultarBateria());
        System.out.println("Status: " + getStatus());
        System.out.println("Missoes realizadas: " + getMissoesRealizadas());
    }

    public abstract String executarMissao(Missao missao);

    // A central atribui e inicia a missao antes de chamar a execucao.
    protected double validarExecucao(Missao missao) {
        if (missao == null || missao.getArea() == null) {
            throw new IllegalArgumentException("A missao e sua area sao obrigatorias");
        }
        if (missao.getRoboResponsavel() != this) {
            throw new IllegalStateException("O robo nao e o responsavel pela missao");
        }
        if (missao.getStatus() != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("A missao deve estar em andamento");
        }
        if (missoesExecutadas.contains(missao.getId())) {
            throw new IllegalStateException("Este robo ja executou essa missao");
        }
        if (status != StatusRobo.EM_MISSAO) {
            throw new IllegalStateException("O robo nao esta reservado para a missao");
        }
        double consumo = calcularConsumo(missao.getTipo(),
                missao.getArea().getTipoTerreno(), missao.getArea().getNivelRisco());
        if (nivelBateria < consumo) {
            throw new IllegalStateException("Bateria insuficiente. Necessario: " + consumo + "%");
        }
        return consumo;
    }

    protected void finalizarExecucao(Missao missao, double consumo) {
        setNivelBateria(nivelBateria - consumo);
        registrarMissaoConcluida();
        missoesExecutadas.add(missao.getId());
        setStatus(nivelBateria == 0 ? StatusRobo.SEM_BATERIA : StatusRobo.DISPONIVEL);
    }

    public abstract boolean compativelCom(TipoMissao tipo, TipoTerreno terreno);

    @Override
    public void enviarMensagem(String mensagem) {
        validarMensagem(mensagem);
        System.out.println("Robo " + nome + " enviou: " + mensagem);
    }

    @Override
    public void receberMensagem(String mensagem) {
        validarMensagem(mensagem);
        System.out.println("Robo " + nome + " recebeu: " + mensagem);
    }

    private void validarMensagem(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem nao pode ser vazia");
        }
    }

    protected void registrarMissaoConcluida() {
        missoesRealizadas++;
    }

    public abstract double calcularConsumo(TipoMissao tipo, TipoTerreno terreno, NivelRisco risco);
}
