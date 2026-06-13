package personagem.Inventario.Itens;

import java.io.Serializable;

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean chave_escritorio;
    private boolean chave_quarto;
    private boolean chave_hotel;
    private boolean dollar;
    private boolean papel;

    private String nome;  // ← NOVO: para facilitar a identificação do item

    // Construtor vazio (necessário para serialização)
    public Item() {}

    // Construtor com nome (opcional)
    public Item(String nome) {
        this.nome = nome;
    }

    // GETTERS E SETTERS
    public boolean isChave_Escritorio() {
        return chave_escritorio;
    }

    public void setChave_Escritorio(boolean chave_escritorio) {
        this.chave_escritorio = chave_escritorio;
        if (chave_escritorio) this.nome = "chave_escritorio";
    }

    public boolean isChave_quarto() {
        return chave_quarto;
    }

    public void setChave_quarto(boolean chave_quarto) {
        this.chave_quarto = chave_quarto;
        if (chave_quarto) this.nome = "chave_quarto";
    }

    public boolean isChave_hotel() {
        return chave_hotel;
    }

    public void setChave_hotel(boolean chave_hotel) {
        this.chave_hotel = chave_hotel;
        if (chave_hotel) this.nome = "chave_hotel";
    }

    public boolean isDollar() {
        return dollar;
    }

    public void setDollar(boolean dollar) {
        this.dollar = dollar;
        if (dollar) this.nome = "dollar";
    }

    public boolean isPapel() {
        return papel;
    }

    public void setPapel(boolean papel) {
        this.papel = papel;
        if (papel) this.nome = "papel";
    }

    // Getter e Setter para nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}