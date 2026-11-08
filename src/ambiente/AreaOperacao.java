package ambiente;

import enums.TipoTerreno;
import enums.NivelRisco;

public class AreaOperacao {
    private int id;
    private String nome;
    private String localizacao;
    private TipoTerreno tipoTerreno;
    private NivelRisco nivelRisco;
    private String descricao;

    public AreaOperacao(String descricao, int id, NivelRisco nivelRisco,
                        String localizacao, String nome, TipoTerreno tipoTerreno) {
        this.id = id;
        setDescricao(descricao);
        setNivelRisco(nivelRisco);
        setLocalizacao(localizacao);
        setNome(nome);
        setTipoTerreno(tipoTerreno);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        if (localizacao == null || localizacao.isBlank()) {
            throw new IllegalArgumentException(
                    "A area precisa de uma localizacao"
            );
        }

        this.localizacao = localizacao;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(NivelRisco nivelRisco) {
        if (nivelRisco == null) {
            throw new IllegalArgumentException(
                    "O nivel de risco nao pode ser nulo"
            );
        }

        this.nivelRisco = nivelRisco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "A area precisa de um nome"
            );
        }

        this.nome = nome;
    }

    public TipoTerreno getTipoTerreno() {
        return tipoTerreno;
    }

    public void setTipoTerreno(TipoTerreno tipoTerreno) {
        if (tipoTerreno == null) {
            throw new IllegalArgumentException(
                    "A area precisa de um tipo de terreno"
            );
        }

        this.tipoTerreno = tipoTerreno;
    }

    public void atualizarNivelRisco(NivelRisco novoRisco) {
        setNivelRisco(novoRisco);
    }

    public void exibirInformacoes() {
        System.out.println("ID: " + id);
        System.out.println("Nome: " + nome);
        System.out.println("Descricao: " + descricao);
        System.out.println("Localizacao: " + localizacao);
        System.out.println("Terreno: " + tipoTerreno);
        System.out.println("Risco: " + nivelRisco);
    }
}