package persistencia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import controle.CentralResgate;

public class PersistenciaArquivo {

    private String nomeArquivo;

    public PersistenciaArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void salvar(CentralResgate central) throws IOException {
        try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            saida.writeObject(central);
        }
    }

    public CentralResgate carregar() throws IOException, ClassNotFoundException {

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            return (CentralResgate) entrada.readObject();
        }
    }
}
