package controle;

import ambiente.AreaOperacao;
import enums.StatusMissao;
import enums.StatusRobo;
import missoes.Relatorio;
import robos.Robo;
import missoes.Missao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.io.IOException;
import java.io.Serializable;

public class CentralResgate implements Serializable {

    private String nome;
    private HashMap<Integer, Robo> robos;
    private HashMap<Integer, AreaOperacao> areas;
    private ArrayList<Missao> missoes;
    private Queue<Missao> missoesPendentes;

    public CentralResgate(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome invalido");
        }

        this.nome = nome;
        this.robos = new HashMap<>();
        this.areas = new HashMap<>();
        this.missoes = new ArrayList<>();
        this.missoesPendentes = new LinkedList<>();
    }

    public void cadastrarRobo(Robo robo) {
        if (robo == null) {
            throw new IllegalArgumentException("Robo invalido");
        }
        if (robos.containsKey(robo.getId())) {
            throw new IllegalArgumentException("Robo ja cadastrado");
        }
        robos.put(robo.getId(), robo);
    }

    public void cadastrarArea(AreaOperacao area) {
        if (area == null) {
            throw new IllegalArgumentException("Area invalida");
        }
        if (areas.containsKey(area.getId())) {
            throw new IllegalArgumentException("Area ja cadastrada");
        }
        areas.put(area.getId(), area);
    }

    public void registrarMissao(Missao missao) {
        if (missao == null) {
            throw new IllegalArgumentException("Missao invalida");
        }
        for (int i = 0; i < missoes.size(); i++) {
            if (missoes.get(i).getId() == missao.getId()) {
                throw new IllegalArgumentException("Missao ja cadastrada");
            }
        }
        if (missao.getStatus() != StatusMissao.PENDENTE || missao.getRoboResponsavel() != null) {
            throw new IllegalArgumentException("Missao deve estar pendente e sem robo");
        }
        if (areas.get(missao.getArea().getId()) != missao.getArea()) {
            throw new IllegalArgumentException("Area nao cadastrada");
        }
        missoes.add(missao);
        missoesPendentes.add(missao);
    }

    public ArrayList<Robo> listarRobosDisponiveis() {
        ArrayList<Robo> disponiveis = new ArrayList<>();
        for (Robo robo : robos.values()) {
            if (robo.verificarDisponibilidade()) {
                disponiveis.add(robo);
            }
        }
        return disponiveis;
    }

    public void atribuirRoboMissao(int idMissao, int idRobo) {
        Missao missaoEncontrada = null;
        for (int i = 0; i < missoes.size(); i++) {
            if (missoes.get(i).getId() == idMissao) {
                missaoEncontrada = missoes.get(i);
                break;
            }
        }
        if (missaoEncontrada == null) {
            throw new IllegalArgumentException("Missao nao encontrada");
        }
        if (missaoEncontrada.getStatus() != StatusMissao.PENDENTE) {
            throw new IllegalStateException("Missao nao esta pendente");
        }
        if (missaoEncontrada.getRoboResponsavel() != null) {
            throw new IllegalStateException("Missao ja possui robo");
        }
        Robo robo = robos.get(idRobo);
        if (robo == null) {
            throw new IllegalArgumentException("Robo nao encontrado");
        }
        if (robo.getStatus() != StatusRobo.DISPONIVEL) {
            throw new IllegalStateException("Robo indisponivel");
        }
        if (!robo.compativelCom(missaoEncontrada.getTipo(), missaoEncontrada.getArea().getTipoTerreno())) {
            throw new IllegalArgumentException("Robo incompativel com a missao");
        }
        double consumo = robo.calcularConsumo(missaoEncontrada.getTipo(), missaoEncontrada.getArea().getTipoTerreno(),
                missaoEncontrada.getArea().getNivelRisco());
        if (robo.getNivelBateria() < consumo) {
            throw new IllegalStateException("Bateria insuficiente");
        }
        missaoEncontrada.atribuirRobo(robo);
        robo.setStatus(StatusRobo.EM_MISSAO);
        robo.receberMensagem("Atribuido a missao " + idMissao);
    }

    public void listarMissoesPendentes() {
        for (Missao missao : missoesPendentes) {
            missao.exibirResumo();
        }
    }

    public void gerarRelatorioGeral() {
        Relatorio relatorio = new Relatorio(robos.size(), missoes);
        System.out.println(relatorio.gerarResumo());
    }

    public void iniciarMissao(int idMissao) {
        Missao missaoEncontrada = null;
        for (int i = 0; i < missoes.size(); i++) {
            if (missoes.get(i).getId() == idMissao) {
                missaoEncontrada = missoes.get(i);
                break;
            }
        }
        if (missaoEncontrada == null) {
            throw new IllegalArgumentException("Missao nao encontrada");
        }
        if (missaoEncontrada.getStatus() != StatusMissao.PENDENTE) {
            throw new IllegalStateException("Missao nao esta pendente");
        }
        if (missaoEncontrada.getRoboResponsavel() == null) {
            throw new IllegalStateException("Missao nao possui robo atribuido");
        }
        Robo robo = missaoEncontrada.getRoboResponsavel();
        if (robo.getStatus() != StatusRobo.EM_MISSAO) {
            throw new IllegalStateException("Robo nao esta reservado para a missao");
        }
        missaoEncontrada.iniciar();
        missoesPendentes.remove(missaoEncontrada);
        robo.receberMensagem("Iniciar missao " + idMissao);
    }

    public void concluirMissao(int idMissao) {
        Missao missaoEncontrada = null;
        for (int i = 0; i < missoes.size(); i++) {
            if (missoes.get(i).getId() == idMissao) {
                missaoEncontrada = missoes.get(i);
                break;
            }
        }
        if (missaoEncontrada == null) {
            throw new IllegalArgumentException("Missao nao encontrada");
        }
        if (missaoEncontrada.getStatus() != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("Missao nao esta em andamento");
        }
        Robo robo = missaoEncontrada.getRoboResponsavel();
        if (robo == null) {
            throw new IllegalStateException("Missao nao possui robo");
        }
        String resultado = robo.executarMissao(missaoEncontrada);
        missaoEncontrada.concluir(resultado);
    }

    public void cancelarMissao(int idMissao) {
        Missao missaoEncontrada = null;
        for (int i = 0; i < missoes.size(); i++) {
            if (missoes.get(i).getId() == idMissao) {
                missaoEncontrada = missoes.get(i);
                break;
            }
        }
        if (missaoEncontrada == null) {
            throw new IllegalArgumentException("Missao nao encontrada");
        }
        if (missaoEncontrada.getStatus() == StatusMissao.CONCLUIDA ||
                missaoEncontrada.getStatus() == StatusMissao.CANCELADA) {
            throw new IllegalStateException("Missao ja foi encerrada");
        }
        Robo robo = missaoEncontrada.getRoboResponsavel();
        missaoEncontrada.cancelar();
        if (robo != null) {
            robo.setStatus(StatusRobo.DISPONIVEL);
        }
        missoesPendentes.remove(missaoEncontrada);
    }

    public Robo recomendarRobo(int idMissao) { // funcionalidade inovadora

        Missao missaoEncontrada = null;
        for (int i = 0; i < missoes.size(); i++) {
            if (missoes.get(i).getId() == idMissao) {
                missaoEncontrada = missoes.get(i);
                break;
            }
        }
        if (missaoEncontrada == null) {
            throw new IllegalArgumentException("ID nao encontrado");
        }
        if (missaoEncontrada.getStatus() != StatusMissao.PENDENTE) {
            throw new IllegalStateException("A missao nao esta pendente");
        }
        if (missaoEncontrada.getRoboResponsavel() != null) {
            throw new IllegalStateException("Missao ja possui robo");
        }

        Robo melhorRobo = null;
        double melhorBateriaRestante = -1;

        for (Robo robo : robos.values()) {
            if (robo.verificarDisponibilidade()) {
                if (robo.compativelCom(
                        missaoEncontrada.getTipo(),
                        missaoEncontrada.getArea().getTipoTerreno())) {
                    double consumo = robo.calcularConsumo(
                            missaoEncontrada.getTipo(),
                            missaoEncontrada.getArea().getTipoTerreno(),
                            missaoEncontrada.getArea().getNivelRisco());
                    if (robo.getNivelBateria() >= consumo) {

                        double bateriaRestante = robo.getNivelBateria() - consumo;

                        if (melhorRobo == null || bateriaRestante > melhorBateriaRestante ||
                                (bateriaRestante == melhorBateriaRestante
                                        && (robo.getMissoesRealizadas() < melhorRobo.getMissoesRealizadas()
                                        || (robo.getMissoesRealizadas() == melhorRobo.getMissoesRealizadas()
                                        && robo.getId() < melhorRobo.getId())))) {

                            melhorRobo = robo;
                            melhorBateriaRestante = bateriaRestante;
                        }
                    }
                }
            }
        }

        return melhorRobo;
    }

    // Eu (Gustavo) criei esse método aqui pra conseguir buscar a area por id!
    public AreaOperacao buscarArea(int id) {
        AreaOperacao area = areas.get(id);

        if (area == null) {
            throw new IllegalArgumentException("Area nao encontrada");
        }

        return area;
    }

    // Criei esse pra conseguir salvar o relatorio!
    public void salvarRelatorio(String nomeArquivo) throws IOException {
        Relatorio relatorio = new Relatorio(robos.size(), missoes);
        relatorio.salvarEmArquivo(nomeArquivo);
    }

    // Criei esses métodos de baixo para retornar as listas e eu conseguir criar as
    // consultas
    public ArrayList<Robo> listarTodosRobos() {
        return new ArrayList<>(robos.values());
    }

    public ArrayList<AreaOperacao> listarAreas() {
        return new ArrayList<>(areas.values());
    }

    public ArrayList<Missao> listarMissoes() {
        return new ArrayList<>(missoes);
    }

    public Robo buscarRobo(int id) {
        Robo robo = robos.get(id);

        if (robo == null) {
            throw new IllegalArgumentException("Robo nao encontrado");
        }

        return robo;
    }
}
