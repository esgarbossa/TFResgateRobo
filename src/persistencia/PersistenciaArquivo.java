package persistencia;

import controle.CentralResgate;
import java.io.*;
import java.nio.file.*;

public class PersistenciaArquivo {
    private final Path arquivo;

    public PersistenciaArquivo(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new IllegalArgumentException("Nome de arquivo obrigatorio");
        }
        arquivo = Path.of(nomeArquivo).toAbsolutePath();
    }

    public void salvar(CentralResgate central) throws IOException {
        if (central == null) {
            throw new IllegalArgumentException("Central obrigatoria");
        }
        Path temporario = Files.createTempFile(arquivo.getParent(), "central-", ".tmp");
        try {
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(central);
            }
            if (Files.exists(arquivo)) {
                Files.copy(arquivo, arquivo.resolveSibling(arquivo.getFileName() + ".bak"),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    public CentralResgate carregar() throws IOException, ClassNotFoundException {
        // FileInputStream permite ao Main distinguir a primeira execucao.
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(arquivo.toFile()))) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof CentralResgate)) {
                throw new IOException("O arquivo nao contem uma central de resgate");
            }
            return (CentralResgate) objeto;
        }
    }
}
