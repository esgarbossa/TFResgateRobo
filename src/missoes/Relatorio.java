package missoes;

import enums.StatusMissao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Relatorio {
    private LocalDateTime dataGeracao;
    private int totalRobos;
    private int totalMissoes;
    private long missoesConcluidas;
    private long missoesPendentes;

    public Relatorio(int totalRobos, List<Missao> missoes) {
        this.dataGeracao = LocalDateTime.now();
        this.totalRobos = totalRobos;
        this.totalMissoes = missoes.size();
        this.missoesConcluidas = missoes.stream().filter(m -> m.getStatus() == StatusMissao.CONCLUIDA).count();
    }

    public String gerarResumo() {
        String resumo = "RELATORIO DA CENTRAL\n";
        resumo += "Data: " + dataGeracao + "\n";
        resumo += "Total de robos: " + totalRobos + "\n";
        resumo += "Total de missoes: " + totalMissoes + "\n";
        resumo += "Missoes concluidas: " + missoesConcluidas + "\n";
        resumo += "Missoes pendentes: " + missoesPendentes + "\n";
        return resumo;
    }

    public void salvarEmArquivo(String nomeArquivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write(gerarResumo());
        }
    }

}
