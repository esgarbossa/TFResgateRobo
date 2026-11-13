package robos;
import controle.Comunicavel;
import enums.StatusRobo;
import enums.TipoMissao;
import enums.TipoTerreno;
import enums.NivelRisco;

public abstract class Robo implements Comunicavel{
    private int id;
    private String nome;
    private double nivelBateria;
    private StatusRobo status;
    private int missoesRealizadas;

    public Robo(int id, int missoesRealizadas, String nome, double nivelBateria, StatusRobo status) {
        if(nome == null || nome.isBlank()){
            throw new IllegalArgumentException("O robo deve ter nome");
        }

        this.id = id;
        this.missoesRealizadas = 0;
        this.nome = nome;
        setNivelBateria(nivelBateria);
        this.status = nivelBateria == 0
                ?StatusRobo.SEM_BATERIA
                :StatusRobo.DISPONIVEL;
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
        if(!Double.isFinite(nivelBateria) || nivelBateria < 0 || nivelBateria > 100){
            throw new IllegalArgumentException("A bateria deve estar entre 0 e 100");
        }
        this.nivelBateria = nivelBateria;
    }

    public void setStatus(StatusRobo status) {
        this.status = status;
    }

    public boolean verificarDisponibilidade() {
        if (getStatus() == StatusRobo.DISPONIVEL) {
            System.out.println("Robo disponivel!");
            return true;
        } else{
            System.out.println("Robo indisponivel por motivo: "+getStatus());
            return false;
        }
    }
    public double consultarBateria(){
        return getNivelBateria();
    }
    public void recarregar(){
        if(status == StatusRobo.EM_MISSAO){
            throw new IllegalArgumentException("Nao e possivel recarregar durante uma missao!");
        }
        setNivelBateria(100);
        if(status == StatusRobo.SEM_BATERIA){
            status = StatusRobo.DISPONIVEL;
        }
    }
    public void exibirResumo(){
        System.out.println("id:"+ getId());
        System.out.println("Nome: "+getNome());
        System.out.println("Nivel da bateria: "+consultarBateria());
        System.out.println("Status: "+getStatus());
        System.out.println("Missoes realizadas"+getMissoesRealizadas());
    }
    public abstract void executarMissao();
    public abstract boolean compativelCom(TipoMissao tipo, TipoTerreno terreno);
    @Override
    public void enviarMensagem(String mensagem) {
        System.out.println("Robo " + nome + " enviou: " + mensagem);
    }

    @Override
    public void receberMensagem(String mensagem) {
        System.out.println("Robo " + nome + " recebeu: " + mensagem);
    }
    protected void registrarMissaoConcluida() {
        missoesRealizadas++;
    }
    public abstract double calcularConsumo(TipoMissao tipo, TipoTerreno terreno, NivelRisco risco);
}
