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

public class CentralResgate {

    private String nome;
    private HashMap<Integer, Robo> robos;
    private HashMap<Integer, AreaOperacao> areas;
    private ArrayList<Missao> missoes;
    private Queue<Missao> missoesPendentes;

    public CentralResgate(String nome) {

        if (nome == null) {
            throw new IllegalArgumentException("Nome invalido");
        }

        this.nome = nome;
        this.robos = new HashMap<>();
        this.areas = new HashMap<>();
        this.missoes = new ArrayList<>();
        this.missoesPendentes = new LinkedList<>();
    }
    public void cadastrarRobo(Robo robo){
        if(robo==null){
            throw new IllegalArgumentException("Robo invalido");
        }
        if(robos.containsKey(robo.getId())){
            throw new IllegalArgumentException("Robo ja cadastrado");
        }
        robos.put(robo.getId(),robo);
    }
   public void cadastrarArea(AreaOperacao area){
       if (area == null) {
           throw new IllegalArgumentException("Area invalida");
       }
       if (areas.containsKey(area.getId())) {
           throw new IllegalArgumentException("Area ja cadastrada");
       }
       areas.put(area.getId(), area);
   }
   public void registrarMissao(Missao missao){
       if (missao == null) {
           throw new IllegalArgumentException("Missao invalida");
       }
       for(int i=0;i<missoes.size();i++){
           if(missoes.get(i).getId() == missao.getId()){
               throw new IllegalArgumentException("Missao ja cadastrada");
           }
       }
       if(missao.getStatus()!= StatusMissao.PENDENTE || missao.getRoboResponsavel()!= null){
           throw new IllegalArgumentException("Missao deve estar pendente e sem robo");
       }
       if(!areas.containsKey(missao.getArea().getId())){
           throw new IllegalArgumentException("Area nao cadastrada");
       }
       missoes.add(missao);
       missoesPendentes.add(missao);
   }
   public ArrayList<Robo>listarRobosDisponiveis(){
        ArrayList<Robo> disponiveis = new ArrayList<>();
        for(Robo robo: robos.values()){
            if(robo.getStatus()== StatusRobo.DISPONIVEL){
                disponiveis.add(robo);
            }
        }
        return disponiveis;
   }
   public void atribuirRoboMissao(int idMissao, int idRobo){
        Missao missaoEncontrada=null;
        for(int i=0;i<missoes.size();i++){
            if(missoes.get(i).getId()== idMissao){
                missaoEncontrada=missoes.get(i);
                break;
            }
        }
        if(missaoEncontrada==null){
            throw new IllegalArgumentException("Missao nao encontrada");
        }
       if (missaoEncontrada.getStatus() != StatusMissao.PENDENTE) {
           throw new IllegalStateException("Missao nao esta pendente");
       }
       if (missaoEncontrada.getRoboResponsavel() != null) {
           throw new IllegalStateException("Missao ja possui robo");
       }
        Robo robo= robos.get(idRobo);
        if(robo==null){
            throw  new IllegalArgumentException("Robo nao encontrado");
        }
        if(robo.getStatus()!=StatusRobo.DISPONIVEL){
            throw new IllegalStateException("Robo indisponivel");
        }
        if(!robo.compativelCom(missaoEncontrada.getTipo(),missaoEncontrada.getArea().getTipoTerreno())){
            throw new IllegalArgumentException("Robo incompativel com a missao");
        }
        double consumo=robo.calcularConsumo(missaoEncontrada.getTipo(),missaoEncontrada.getArea().getTipoTerreno(),missaoEncontrada.getArea().getNivelRisco());
        if(robo.getNivelBateria()<consumo){
            throw new IllegalStateException("Bateria insuficiente");
        }
        missaoEncontrada.atribuirRobo(robo);
        robo.setStatus(StatusRobo.EM_MISSAO);
        missoesPendentes.remove(missaoEncontrada);
   }

    public void listarMissoesPendentes() {
       for(Missao missao:missoesPendentes){
           missao.exibirResumo();
       }
    }

    public void gerarRelatorioGeral(){
        Relatorio relatorio = new Relatorio(robos.size(), missoes);
        System.out.println(relatorio.gerarResumo());
    }
}