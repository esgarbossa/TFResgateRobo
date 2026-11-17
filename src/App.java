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
            int id = sc.nextInt();
            sc.nextLine();

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

            int opcaoTerreno = sc.nextInt();
            sc.nextLine();

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

            int opcaoRisco = sc.nextInt();
            sc.nextLine();

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
            int id = sc.nextInt();
            sc.nextLine();

            System.out.println("Nome: ");
            String nome = sc.nextLine();

            System.out.println("Nivel de bateria: ");
            double bateria = sc.nextDouble();
            sc.nextLine();

            System.out.println("Tipo de robo: ");
            System.out.println("1 - Drone");
            System.out.println("2 - Robo terrestre;");

            int tipo = sc.nextInt();
            sc.nextLine();

            Robo robo;

            if (tipo == 1) {
                System.out.println("Alcance maximo: ");
                double alcance = sc.nextDouble();
                sc.nextLine();

                robo = new Drone(id, 0, nome, bateria, StatusRobo.DISPONIVEL, alcance);
            } else if (tipo == 2) {
                System.out.println("Capacidade de carga: ");
                double capacidade = sc.nextDouble();
                sc.nextLine();

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
            int id = sc.nextInt();
            sc.nextLine();

            System.out.println("Descricao: ");
            String descricao = sc.nextLine();

            System.out.println("ID da area: ");
            int idArea = sc.nextInt();
            sc.nextLine();

            AreaOperacao area = central.buscarArea(idArea);

            System.out.println("Tipo de missao: ");
            System.out.println("1 - Reconhecimento");
            System.out.println("2 - Localizacao de pessoas");
            System.out.println("3 = Transporte de suprimentos");

            int opcaoTipo = sc.nextInt();
            sc.nextLine();

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

            int opcaoPrioridade = sc.nextInt();
            sc.nextLine();

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

                default:
                    throw new IllegalArgumentException("Prioridade invalida.");
            }

            Missao missao = new Missao(opcaoPrioridade, descricao, tipo, area, prioridade);
            central.registrarMissao(missao);
            System.out.println("Missao cadastrada com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro em cadastrar missao: " + e.getMessage());
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
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opcao: ");

            opcao = sc.nextInt();
            sc.nextLine();

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

                case 0:
                    System.out.println("Encerrando...");
                    break;

                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }
}
