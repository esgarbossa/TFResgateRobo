import ambiente.AreaOperacao;
import controle.CentralResgate;
import enums.*;
import missoes.*;
import persistencia.PersistenciaArquivo;
import robos.*;
import java.nio.file.*;
import java.io.*;

public class TestesSistema {
    private static int total;
    private static void conferir(String nome, boolean valor) {
        if (!valor) throw new AssertionError(nome);
        total++;
        System.out.println("PASSOU: " + nome);
    }
    private static void rejeitar(String nome, Runnable tarefa) {
        try { tarefa.run(); }
        catch (IllegalArgumentException | IllegalStateException e) {
            conferir(nome, true); return;
        }
        throw new AssertionError(nome + ": deveria rejeitar");
    }
    private static AreaOperacao area(int id) {
        return new AreaOperacao("Area de teste", id, NivelRisco.BAIXO, "Centro", "Area " + id, TipoTerreno.URBANO);
    }
    private static Drone drone(int id, double bateria) {
        return new Drone(id,0,"Drone " + id,bateria,StatusRobo.DISPONIVEL,50);
    }
    private static Missao missao(int id, AreaOperacao area, TipoMissao tipo) {
        return new Missao(id,"Missao de teste",tipo,area,PrioridadeMissao.ALTA);
    }
    private static String capturar(Runnable tarefa) {
        PrintStream anterior=System.out;
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        try { System.setOut(new PrintStream(bytes)); tarefa.run(); }
        finally { System.setOut(anterior); }
        return bytes.toString();
    }
    public static void main(String[] args) throws Exception {
        for(double bateria : new double[]{-1,101,Double.NaN,Double.POSITIVE_INFINITY})
            rejeitar("bateria invalida " + bateria, () -> drone(1,bateria));
        rejeitar("nome vazio", () -> new Drone(1,0," ",100,StatusRobo.DISPONIVEL,50));
        rejeitar("contador negativo", () -> new Drone(1,-1,"D",100,StatusRobo.DISPONIVEL,50));
        rejeitar("status nulo", () -> new Drone(1,0,"D",100,null,50));
        rejeitar("alcance zero", () -> new Drone(1,0,"D",100,StatusRobo.DISPONIVEL,0));
        rejeitar("carga invalida", () -> new RoboTerrestre(1,0,"T",100,StatusRobo.DISPONIVEL,19));
        AreaOperacao a=area(1);
        rejeitar("nome da area vazio", () -> a.setNome(" "));
        rejeitar("localizacao nula", () -> a.setLocalizacao(null));
        rejeitar("risco nulo", () -> a.atualizarNivelRisco(null));
        rejeitar("terreno nulo", () -> a.setTipoTerreno(null));
        rejeitar("ID da area nao muda", () -> a.setId(2));
        a.atualizarNivelRisco(NivelRisco.ALTO);
        conferir("atualizacao do risco",a.getNivelRisco()==NivelRisco.ALTO);
        a.atualizarNivelRisco(NivelRisco.BAIXO);
        Drone d=drone(1,100);
        RoboTerrestre t=new RoboTerrestre(2,0,"Terrestre",100,StatusRobo.DISPONIVEL,200);
        for(TipoMissao tipo:TipoMissao.values()) for(TipoTerreno terreno:TipoTerreno.values()) for(NivelRisco risco:NivelRisco.values()) {
            if(d.compativelCom(tipo,terreno)) {
                double esperado=tipo==TipoMissao.RECONHECIMENTO?(risco==NivelRisco.BAIXO?20:risco==NivelRisco.MEDIO?30:50):(risco==NivelRisco.BAIXO?25:risco==NivelRisco.MEDIO?35:60);
                conferir("consumo drone "+tipo+"/"+terreno+"/"+risco,d.calcularConsumo(tipo,terreno,risco)==esperado);
            }
            if(t.compativelCom(tipo,terreno)) {
                double esperado=(risco==NivelRisco.BAIXO?5:risco==NivelRisco.MEDIO?10:15)+(tipo==TipoMissao.LOCALIZACAO_DE_PESSOAS?10:20)+(terreno==TipoTerreno.URBANO?10:20);
                conferir("consumo terrestre "+tipo+"/"+terreno+"/"+risco,t.calcularConsumo(tipo,terreno,risco)==esperado);
            }
        }
        rejeitar("tipo nulo", () -> d.compativelCom(null,TipoTerreno.URBANO));
        rejeitar("risco nulo no consumo", () -> d.calcularConsumo(TipoMissao.RECONHECIMENTO,TipoTerreno.URBANO,null));
        conferir("drone nao transporta",!d.compativelCom(TipoMissao.TRANSPORTE_DE_SUPRIMENTOS,TipoTerreno.URBANO));
        conferir("terrestre nao entra em alagado",!t.compativelCom(TipoMissao.LOCALIZACAO_DE_PESSOAS,TipoTerreno.ALAGADO));
        CentralResgate c=new CentralResgate("Teste");
        c.cadastrarArea(a); c.cadastrarRobo(d); c.cadastrarRobo(t);
        rejeitar("robo duplicado", () -> c.cadastrarRobo(drone(1,100)));
        rejeitar("area duplicada", () -> c.cadastrarArea(area(1)));
        Missao m=missao(1,a,TipoMissao.RECONHECIMENTO); c.registrarMissao(m);
        rejeitar("missao duplicada", () -> c.registrarMissao(missao(1,a,TipoMissao.RECONHECIMENTO)));
        rejeitar("area externa", () -> c.registrarMissao(missao(99,area(1),TipoMissao.RECONHECIMENTO)));
        rejeitar("iniciar sem robo", () -> c.iniciarMissao(1));
        rejeitar("execucao direta sem atribuicao", () -> d.executarMissao(m));
        rejeitar("atribuir robo incompativel", () -> c.atribuirRoboMissao(1,2));
        conferir("pendente permanece na fila",capturar(c::listarMissoesPendentes).contains("ID: 1"));
        c.atribuirRoboMissao(1,1);
        conferir("reserva preserva pendente na fila",capturar(c::listarMissoesPendentes).contains("ID: 1"));
        rejeitar("nao executa antes de iniciar", () -> d.executarMissao(m));
        Missao segunda=missao(2,a,TipoMissao.RECONHECIMENTO); c.registrarMissao(segunda);
        rejeitar("duas missoes no mesmo robo", () -> c.atribuirRoboMissao(2,1));
        rejeitar("recarga ocupado",d::recarregar);
        c.iniciarMissao(1);
        conferir("inicio remove da fila",!capturar(c::listarMissoesPendentes).contains("ID: 1"));
        c.concluirMissao(1);
        conferir("missao concluida com resultado",m.getStatus()==StatusMissao.CONCLUIDA && m.getResultado().contains("Reconhecimento"));
        conferir("bateria descontada",d.getNivelBateria()==80);
        conferir("contador incrementado",d.getMissoesRealizadas()==1);
        rejeitar("nao repete conclusao", () -> c.concluirMissao(1));
        rejeitar("nao reinicia concluida", () -> c.iniciarMissao(1));
        c.cancelarMissao(2);
        rejeitar("nao reinicia cancelada", () -> c.iniciarMissao(2));
        Missao transporte=missao(3,a,TipoMissao.TRANSPORTE_DE_SUPRIMENTOS); c.registrarMissao(transporte); c.atribuirRoboMissao(3,2); c.iniciarMissao(3); c.concluirMissao(3);
        conferir("execucao terrestre",t.getNivelBateria()==65 && t.getMissoesRealizadas()==1);
        Drone exato=drone(3,20); c.cadastrarRobo(exato);
        Missao me=missao(4,a,TipoMissao.RECONHECIMENTO); c.registrarMissao(me); c.atribuirRoboMissao(4,3); c.iniciarMissao(4); c.concluirMissao(4);
        conferir("bateria exata termina sem bateria",exato.getNivelBateria()==0 && exato.getStatus()==StatusRobo.SEM_BATERIA);
        exato.recarregar(); conferir("recarga restaura disponibilidade",exato.verificarDisponibilidade() && exato.getNivelBateria()==100);
        exato.setStatus(StatusRobo.EM_MANUTENCAO); exato.recarregar();
        conferir("recarga preserva manutencao",exato.getStatus()==StatusRobo.EM_MANUTENCAO);
        rejeitar("setter status nulo", () -> exato.setStatus(null));
        rejeitar("mensagem vazia", () -> exato.receberMensagem(" "));
        conferir("comunicacao recebida",capturar(()->d.receberMensagem("Teste")).contains("recebeu: Teste"));
        Drone pouco=drone(4,19); c.cadastrarRobo(pouco);
        Missao mp=missao(5,a,TipoMissao.RECONHECIMENTO); c.registrarMissao(mp);
        rejeitar("atribuicao com bateria insuficiente", () -> c.atribuirRoboMissao(5,4));
        conferir("sem bateria suficiente permanece pendente",mp.getStatus()==StatusMissao.PENDENTE && mp.getRoboResponsavel()==null);
        Drone empate1=drone(6,100), empate2=drone(7,100); c.cadastrarRobo(empate1); c.cadastrarRobo(empate2);
        conferir("recomendacao desempata por menor ID",c.recomendarRobo(5)==empate1);
        Path pasta=Files.createTempDirectory("tfresgate-teste-");
        PersistenciaArquivo persistencia=new PersistenciaArquivo(pasta.resolve("central.dat").toString());
        persistencia.salvar(c);
        CentralResgate recuperada=persistencia.carregar();
        conferir("persistencia recupera robos",recuperada.listarTodosRobos().size()==6);
        conferir("persistencia preserva bateria e contador",recuperada.buscarRobo(1).getNivelBateria()==80 && recuperada.buscarRobo(1).getMissoesRealizadas()==1);
        conferir("persistencia preserva referencias",recuperada.listarMissoes().get(0).getRoboResponsavel()==recuperada.buscarRobo(1));
        conferir("persistencia preserva fila",capturar(recuperada::listarMissoesPendentes).contains("ID: 5"));
        recuperada.atribuirRoboMissao(5,6); recuperada.iniciarMissao(5); recuperada.concluirMissao(5);
        conferir("continuidade depois de carregar",recuperada.buscarRobo(6).getNivelBateria()==80);
        persistencia.salvar(recuperada); conferir("backup criado",Files.exists(pasta.resolve("central.dat.bak")));
        c.salvarRelatorio(pasta.resolve("relatorio.txt").toString());
        String relatorio=Files.readString(pasta.resolve("relatorio.txt"));
        conferir("relatorio totais corretos",relatorio.contains("Total de robos: 6") && relatorio.contains("Missoes concluidas: 3") && relatorio.contains("Missoes pendentes: 1"));
        System.out.println("RESULTADO: " + total + " verificacoes passaram; 0 falhas.");
    }
}
