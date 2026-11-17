import controle.CentralResgate;
import persistencia.PersistenciaArquivo;
import robos.Robo;
import robos.Drone;
import robos.RoboTerrestre;
import ambiente.AreaOperacao;
import missoes.Missao;
import enums.NivelRisco;
import enums.TipoTerreno;
import enums.StatusRobo;
import enums.TipoMissao;
import enums.PrioridadeMissao;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class App {
    private Scanner sc;
    private CentralResgate central;
    private PersistenciaArquivo persistencia;

    public App(CentralResgate central, PersistenciaArquivo persistencia) {
        this.sc = new Scanner(System.in);
        this.central = central;
        this.persistencia = persistencia;
    }

    private void cadastrarArea() {
        try {
            System.out.println("ID: ");
            int id = lerInt();

            System.out.println("Nome: ");
            String nome = sc.nextLine();

            System.out.println("Localizacao: ");
            String localizacao = sc.nextLine();

            System.out.println("Descricao: ");
            String descricao = sc.nextLine();

            System.out.println("Tipo de terreno: ");
            System.out.println("1 - Urbano");
            System.out.println("2 - Alagado");
            System.out.println("3 - Irregular");

            int opcaoTerreno = lerInt();

            TipoTerreno terreno;
            switch (opcaoTerreno) {
                case 1:
                    terreno = TipoTerreno.URBANO;
                    break;

                case 2:
                    terreno = TipoTerreno.ALAGADO;
                    break;

                case 3:
                    terreno = TipoTerreno.IRREGULAR;
                    break;

                default:
                    throw new IllegalArgumentException("Tipo de terreno invalido.");
            }

            System.out.println("Nivel de risco: ");
            System.out.println("1 - Baixo");
            System.out.println("2 - Medio");
            System.out.println("3 - Alto");

            int opcaoRisco = lerInt();

            NivelRisco risco;
            switch (opcaoRisco) {
                case 1:
                    risco = NivelRisco.BAIXO;
                    break;

                case 2:
                    risco = NivelRisco.MEDIO;
                    break;

                case 3:
                    risco = NivelRisco.ALTO;
                    break;

                default:
                    throw new IllegalArgumentException("Nivel de risco invalido");
            }

            AreaOperacao area = new AreaOperacao(descricao, id, risco, localizacao, nome, terreno);

            central.cadastrarArea(area);

            System.out.println("Area cadastrada com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar area: " + e.getMessage());
        }
    }

    private void cadastrarRobo() {
        try {
            System.out.println("ID: ");
            int id = lerInt();

            System.out.println("Nome: ");
            String nome = sc.nextLine();

            System.out.println("Nivel de bateria: ");
            double bateria = lerDouble();

            System.out.println("Tipo de robo: ");
            System.out.println("1 - Drone");
            System.out.println("2 - Robo terrestre;");

            int tipo = lerInt();

            Robo robo;

            if (tipo == 1) {
                System.out.println("Alcance maximo: ");
                double alcance = lerDouble();

                robo = new Drone(id, 0, nome, bateria, StatusRobo.DISPONIVEL, alcance);
            } else if (tipo == 2) {
                System.out.println("Capacidade de carga: ");
                double capacidade = lerDouble();

                robo = new RoboTerrestre(id, 0, nome, bateria, StatusRobo.DISPONIVEL, capacidade);
            } else {
                throw new IllegalArgumentException("Tipo de robo invalido");
            }

            central.cadastrarRobo(robo);

            System.out.println("Robo cadastrado com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar robo: " + e.getMessage());
        }
    }

    private void cadastrarMissao() {
        try {
            System.out.println("ID: ");
            int id = lerInt();

            System.out.println("Descricao: ");
            String descricao = sc.nextLine();

            System.out.println("ID da area: ");
            int idArea = lerInt();
            AreaOperacao area = central.buscarArea(idArea);

            System.out.println("Tipo de missao: ");
            System.out.println("1 - Reconhecimento");
            System.out.println("2 - Localizacao de pessoas");
            System.out.println("3 - Transporte de suprimentos");

            int opcaoTipo = lerInt();

            TipoMissao tipo;

            switch (opcaoTipo) {
                case 1:
                    tipo = TipoMissao.RECONHECIMENTO;
                    break;

                case 2:
                    tipo = TipoMissao.LOCALIZACAO_DE_PESSOAS;
                    break;

                case 3:
                    tipo = TipoMissao.TRANSPORTE_DE_SUPRIMENTOS;
                    break;

                default:
                    throw new IllegalArgumentException("Tipo de missao invalido.");
            }

            System.out.println("Prioridade: ");
            System.out.println("1 - Baixa");
            System.out.println("2 - Media");
            System.out.println("3 - Alta");
            System.out.println("4 - Urgente");

            int opcaoPrioridade = lerInt();

            PrioridadeMissao prioridade;

            switch (opcaoPrioridade) {
                case 1:
                    prioridade = PrioridadeMissao.BAIXA;
                    break;

                case 2:
                    prioridade = PrioridadeMissao.MEDIA;
                    break;

                case 3:
                    prioridade = PrioridadeMissao.ALTA;
                    break;

                case 4:
                    prioridade = PrioridadeMissao.URGENTE;
                    break;

                default:
                    throw new IllegalArgumentException("Prioridade invalida.");
            }

            Missao missao = new Missao(id, descricao, tipo, area, prioridade);
            central.registrarMissao(missao);
            System.out.println("Missao cadastrada com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro em cadastrar missao: " + e.getMessage());
        }
    }

    private void atribuirRobo() {
        try {
            System.out.println("ID da missao: ");
            int idMissao = lerInt();

            System.out.println("ID do robo: ");
            int idRobo = lerInt();

            central.atribuirRoboMissao(idMissao, idRobo);

            System.out.println("Robo atribuido com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao atribuir robo: " + e.getMessage());
        }
    }

    private void recomendarRobo() {
        try {
            System.out.println("ID da missao: ");
            int idMissao = lerInt();

            Robo robo = central.recomendarRobo(idMissao);

            if (robo == null) {
                System.out.println("Nenhum robo compativel disponivel.");
            } else {
                System.out.println("Robo recomendado: ");
                robo.exibirResumo();
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao recomendar robo: " + e.getMessage());
        }
    }

    private void iniciarMissao() {
        try {
            System.out.println("ID da missao: ");
            int idMissao = lerInt();

            central.iniciarMissao(idMissao);

            System.out.println("Missao iniciada com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao iniciar missao: " + e.getMessage());
        }
    }

    private void concluirMissao() {
        try {
            System.out.println("ID da missao: ");
            int idMissao = lerInt();

            central.concluirMissao(idMissao);

            System.out.println("Missao concluida com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao concluir missao: " + e.getMessage());
        }
    }

    private void cancelarMissao() {
        try {
            System.out.println("ID da missao: ");
            int idMissao = lerInt();

            central.cancelarMissao(idMissao);

            System.out.println("Missao cancelada.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao cancelar missao: " + e.getMessage());
        }
    }

    private void consultarRobos() {
        ArrayList<Robo> robos = central.listarTodosRobos();

        if (robos.isEmpty()) {
            System.out.println("Nenhum robo cadastrado.");
        } else {
            for (Robo robo : robos) {
                robo.exibirResumo();
                System.out.println();
            }
        }
    }

    private void consultarAreas() {
        ArrayList<AreaOperacao> areas = central.listarAreas();

        if (areas.isEmpty()) {
            System.out.println("Nenhuma area cadastrada.");
        } else {
            for (AreaOperacao area : areas) {
                area.exibirInformacoes();
                System.out.println();
            }
        }
    }

    private void consultarMissoes() {
        ArrayList<Missao> missoes = central.listarMissoes();

        if (missoes.isEmpty()) {
            System.out.println("Nenhuma missao cadastrada.");
        } else {
            for (Missao missao : missoes) {
                missao.exibirResumo();
                System.out.println();
            }
        }
    }

    private void recarregarRobo() {
        try {
            System.out.println("ID do robo: ");
            int idRobo = lerInt();

            Robo robo = central.buscarRobo(idRobo);

            robo.recarregar();

            System.out.println("Robo recarregado com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void salvarRelatorio() {
        try {
            System.out.println("Nome do arquivo: ");
            String nomeArquivo = sc.nextLine();

            if (!nomeArquivo.endsWith(".txt")) {
                nomeArquivo += ".txt";
            }

            central.salvarRelatorio(nomeArquivo);

            System.out.println("Relatorio salvo com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao salvar relatorio: " + e.getMessage());
        }
    }

    private void atualizarRisco() {
        try {
            System.out.println("ID da area: ");
            AreaOperacao area = central.buscarArea(lerInt());
            System.out.println("Risco: 1 - Baixo, 2 - Medio, 3 - Alto");
            int risco = lerInt();
            if (risco < 1 || risco > 3) {
                throw new IllegalArgumentException("Nivel de risco invalido");
            }
            area.atualizarNivelRisco(NivelRisco.values()[risco - 1]);
            System.out.println("Risco atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao atualizar risco: " + e.getMessage());
        }
    }

    private void enviarInstrucao() {
        try {
            System.out.println("ID do robo: ");
            Robo robo = central.buscarRobo(lerInt());
            System.out.println("Mensagem: ");
            robo.receberMensagem(sc.nextLine());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    private int lerInt() {
        while (true) {
            try {
                int valor = sc.nextInt();
                sc.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("Digite um numero inteiro valido.");
                sc.nextLine();
            }
        }
    }

    private double lerDouble() {
        while (true) {
            try {
                double valor = sc.nextDouble();
                sc.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("Digite um numero valido.");
                sc.nextLine();
            }
        }
    }

    public void executar() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n=== CENTRAL DE RESGATE ===");
            System.out.println("1 - Listar robos disponiveis");
            System.out.println("2 - Listar missoes pendentes");
            System.out.println("3 - Gerar relatorio");
            System.out.println("4 - Cadastrar area");
            System.out.println("5 - Cadastrar robo");
            System.out.println("6 - Cadastrar missao");
            System.out.println("7 - Atribuir robo a missao");
            System.out.println("8 - Recomendar robo");
            System.out.println("9 - Iniciar missao");
            System.out.println("10 - Executar e concluir missao");
            System.out.println("11 - Cancelar missao");
            System.out.println("12 - Consultar todos os robos");
            System.out.println("13 - Consultar areas");
            System.out.println("14 - Consultar todas as missoes");
            System.out.println("15 - Recarregar robo");
            System.out.println("16 - Salvar relatorio em arquivo");
            System.out.println("17 - Atualizar risco de uma area");
            System.out.println("18 - Enviar mensagem a um robo");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opcao: ");

            opcao = lerInt();

            switch (opcao) {
                case 1:
                    ArrayList<Robo> robosDisponiveis = central.listarRobosDisponiveis();

                    if (robosDisponiveis.isEmpty()) {
                        System.out.println("Nenhum robo disponível.");
                    } else {
                        for (Robo robo : robosDisponiveis) {
                            robo.exibirResumo();
                            System.out.println();
                        }
                    }
                    break;

                case 2:
                    central.listarMissoesPendentes();
                    break;

                case 3:
                    central.gerarRelatorioGeral();
                    break;

                case 4:
                    cadastrarArea();
                    break;

                case 5:
                    cadastrarRobo();
                    break;

                case 6:
                    cadastrarMissao();
                    break;

                case 7:
                    atribuirRobo();
                    break;

                case 8:
                    recomendarRobo();
                    break;

                case 9:
                    iniciarMissao();
                    break;

                case 10:
                    concluirMissao();
                    break;

                case 11:
                    cancelarMissao();
                    break;

                case 12:
                    consultarRobos();
                    break;

                case 13:
                    consultarAreas();
                    break;

                case 14:
                    consultarMissoes();
                    break;

                case 15:
                    recarregarRobo();
                    break;

                case 16:
                    salvarRelatorio();
                    break;

                case 17:
                    atualizarRisco();
                    break;

                case 18:
                    enviarInstrucao();
                    break;

                case 0:
                    System.out.println("Encerrando...");
                    break;

                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }
}
