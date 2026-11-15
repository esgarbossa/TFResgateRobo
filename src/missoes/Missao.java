package missoes;

import ambiente.AreaOperacao;
import robos.Robo;
import enums.TipoMissao;
import enums.PrioridadeMissao;
import enums.StatusMissao;
import java.io.Serializable;

public class Missao implements Serializable {
    private int id;
    private String descricao;
    private TipoMissao tipo;
    private AreaOperacao area;
    private PrioridadeMissao prioridade;
    private StatusMissao status;
    private Robo roboResponsavel;
    private String resultado;

    public Missao(int id, String descricao, TipoMissao tipo, AreaOperacao area, PrioridadeMissao prioridade) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID invalido");
        }
        if (descricao == null) {
            throw new IllegalArgumentException("Descricao invalida");
        }
        if (tipo == null || area == null || prioridade == null) {
            throw new IllegalArgumentException("Dados invalidos");
        }
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
        this.area = area;
        this.prioridade = prioridade;
        this.status = StatusMissao.PENDENTE;
        this.roboResponsavel = null;
        this.resultado = null;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoMissao getTipo() {
        return tipo;
    }

    public AreaOperacao getArea() {
        return area;
    }

    public PrioridadeMissao getPrioridade() {
        return prioridade;
    }

    public StatusMissao getStatus() {
        return status;
    }

    public Robo getRoboResponsavel() {
        return roboResponsavel;
    }

    public String getResultado() {
        return resultado;
    }

    public void atribuirRobo(Robo robo) {
        if (robo == null) {
            throw new IllegalArgumentException("Robo invalido");
        }
        if (status != StatusMissao.PENDENTE) {
            throw new IllegalStateException("Missao nao esta pendente");
        }
        if (roboResponsavel != null) {
            throw new IllegalStateException("Missao ja possui um robo");
        }
        this.roboResponsavel = robo; // robo vira o responsavel
    }

    public void iniciar() {
        if (status != StatusMissao.PENDENTE) {
            throw new IllegalStateException("Nao e possivel inicar missoes concluidas ou canceladas");
        }
        if (roboResponsavel == null) {
            throw new IllegalArgumentException("Missao nao pode ser inicada sem robo atribuido");
        }
        this.status = StatusMissao.EM_ANDAMENTO; // missao passa a estar em andamento
    }

    public void concluir(String resultado) {
        if (status != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("Missao deve estar em andamento");
        }
        if (resultado == null) {
            throw new IllegalArgumentException("Resultado deve ser registrado");
        }
        this.resultado = resultado;
        this.status = StatusMissao.CONCLUIDA;
    }

    public void cancelar() {
        if (status == StatusMissao.CONCLUIDA || status == StatusMissao.CANCELADA) {
            throw new IllegalStateException("Nao e possivel cancelar missoes concluidas ou canceladas");
        }
        this.status = StatusMissao.CANCELADA;
    }

    public void exibirResumo() {
        System.out.println("ID: " + id);
        System.out.println("Descricao: " + descricao);
        System.out.println("Tipo: " + tipo);
        System.out.println("Area: " + area.getNome());
        System.out.println("Prioridade: " + prioridade);
        System.out.println("Status: " + status);
        if (roboResponsavel != null) {
            System.out.println("Robo: " + roboResponsavel.getNome());
        } else {
            System.out.println("Robo: Nenhum robo atribuido");
        }
        if (resultado != null) {
            System.out.println("Resultado: " + resultado);
        } else {
            System.out.println("Resultado: Ainda nao disponivel");
        }
    }

}
