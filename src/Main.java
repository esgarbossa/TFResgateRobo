import java.io.IOException;
import java.io.FileNotFoundException;

import controle.CentralResgate;
import persistencia.PersistenciaArquivo;

public class Main {
    public static void main(String args[]) {
        PersistenciaArquivo persistencia = new PersistenciaArquivo("central.dat");
        CentralResgate central;

        try {
            central = persistencia.carregar();
            System.out.println("Dados carregados com sucesso.");
        } catch (FileNotFoundException e) {
            central = new CentralResgate("Central de Resgate");
            System.out.println("Nenhum arquivo encontrado. Nova central criada.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar dados salvos.");
            central = new CentralResgate("Central de Resgate");
        }

        App app = new App(central, persistencia);
        app.executar();

        try {
            persistencia.salvar(central);
            System.out.println("Dados salvos com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados.");
        }
    }
}
