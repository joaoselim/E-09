package tela;
import personagem.Inventario.Itens.Item;
import personagem.Inventario.*;
import personagem.Jogador;
import javax.swing.JOptionPane;
public class Cenario {

    private Cofre cofre;
    public Portas portas;

    public Cenario(Jogador jogador) {
        this.portas = new Portas();
        this.cofre = new Cofre(jogador);
    }

    public Cofre getCofre() {
        return cofre;
    }


    public String updateCenario(String cenarioAtual, Jogador jogador, int screenWidth) {

        if (jogador.consumirInteracao()) {

            // Se estiver em uma tela de foco, W volta para o escritório
            if (cenarioAtual.equals("Prateleira")) {
                jogador.x = 80;
                return "Escritorio";
            }

            if (cenarioAtual.equals("CofreFechado")) {
                jogador.x = 590;
                return "Escritorio";
            }

            if (cenarioAtual.equals("CofreAberto")) {
                jogador.x = 590;
                return "Escritorio";
            }

            // Primeiro tenta usar portas
            String destinoPorta = portas.tentarUsarPorta(cenarioAtual, jogador);

            if (destinoPorta != null) {

                if (!destinoPorta.equals(cenarioAtual)) {
                    reposicionarJogadorDepoisDaPorta(cenarioAtual, destinoPorta, jogador, screenWidth);
                }

                return destinoPorta;
            }

            // Se não era porta, tenta interações normais do mapa
            String destinoInteracao = verificarInteracoesDoMapa(cenarioAtual, jogador);

            if (!destinoInteracao.equals(cenarioAtual)) {
                return destinoInteracao;
            }
        }

        // TROCA DE CENÁRIO AO SAIR DA TELA

        // Entrada -> Corredor
        if (cenarioAtual.equals("Entrada") && jogador.x >= 1260) {

            jogador.x = -50;
            return "Corredor";
        }

        // Corredor -> Entrada
        if (cenarioAtual.equals("Corredor") && jogador.x <= -100) {

            jogador.x = screenWidth - jogador.tamanhoPersonagem;
            return "Entrada";
        }

        // Corredor -> Bar
        if (cenarioAtual.equals("Corredor") && jogador.x >= 1260) {

            jogador.x = -50;
            return "Bar";
        }

        // Bar -> Corredor
        if (cenarioAtual.equals("Bar") && jogador.x <= -100) {

            jogador.x = screenWidth - jogador.tamanhoPersonagem;
            return "Corredor";
        }

        return cenarioAtual;
    }

    private String verificarInteracoesDoMapa(String cenarioAtual, Jogador jogador) {

        // MESA DA ENTRADA
        if (cenarioAtual.equals("Entrada") && jogador.x >= 450 && jogador.x <= 610) {
            // Verifica se já pegou a chave
            if (!jogador.possuiItem("chave_escritorio")) {
                // Cria e adiciona a chave ao inventário
                Item chaveEscritorio = new Item();
                chaveEscritorio.setChave_Escritorio(true);
                jogador.adicionarItem(chaveEscritorio);
                JOptionPane.showMessageDialog(null, "Você encontrou a Chave do Escritório!", "Item Adquirido", JOptionPane.INFORMATION_MESSAGE);
            }
            return cenarioAtual;
        }

        // MÁQUINA DE SALGADINHO
        if (cenarioAtual.equals("Bar") && jogador.x >= 1050 && jogador.possuiItem("dollar"))  {
            if(!jogador.possuiItem("papel"))
            {
                Item papel = new Item();
                papel.setPapel(true);
                jogador.adicionarItem(papel);JOptionPane.showMessageDialog(null, "Você encontrou um Enigma!", "Item Adquirido", JOptionPane.INFORMATION_MESSAGE);System.out.println("Papel Cifrado");
            }
            return cenarioAtual;
        }


        // MESINHA DO QUARTO
        if (cenarioAtual.equals("Quarto") && jogador.x >= 600 && jogador.x <= 730) {
            if (!jogador.possuiItem("dollar")) {
                Item dollar = new Item();
                dollar.setDollar(true);
                jogador.adicionarItem(dollar);
                JOptionPane.showMessageDialog(null, "Você encontrou uma nota de Dollar!", "Item Adquirido", JOptionPane.INFORMATION_MESSAGE);
            }

            return cenarioAtual;
        }

        // LIXEIRA DO ESCRITÓRIO
        if (cenarioAtual.equals("Escritorio") && jogador.x >= 125 && jogador.x <= 250) {
            if(!jogador.possuiItem("chave_quarto"))
            {
                Item chaveQuarto = new Item();
                chaveQuarto.setChave_quarto(true);
                jogador.adicionarItem(chaveQuarto);
                JOptionPane.showMessageDialog(null, "Você encontrou a Chave do Quarto!", "Item Adquirido", JOptionPane.INFORMATION_MESSAGE);
            }
            return cenarioAtual;
        }

        // PRATELEIRA
        if (cenarioAtual.equals("Escritorio") && jogador.x <= 125) {
            return "Prateleira";
        }

        // COFRE
        if (cenarioAtual.equals("Escritorio") && jogador.x >= 400 && jogador.x <= 780) {
            if (cofre.isDestrancado()){
                Item chaveEscritorio = new Item();
                chaveEscritorio.setChave_hotel(true);
                jogador.adicionarItem(chaveEscritorio);
                return "CofreAberto";
            }
            else {
                return "CofreFechado";
            }
        }
        if (cenarioAtual.equals("CofreFechado") && cofre.isDestrancado()){
            return "CofreAberto";
        }

        return cenarioAtual;
    }

    private void reposicionarJogadorDepoisDaPorta(
            String cenarioAntigo,
            String cenarioNovo,
            Jogador jogador,
            int screenWidth
    ) {

        // Entrada -> Escritório
        if (cenarioAntigo.equals("Entrada") && cenarioNovo.equals("Escritorio")) {
            jogador.x = 1000;
        }

        // Escritório -> Entrada
        else if (cenarioAntigo.equals("Escritorio") && cenarioNovo.equals("Entrada")) {
            jogador.x = 980;
        }

        // Corredor -> Quarto
        else if (cenarioAntigo.equals("Corredor") && cenarioNovo.equals("Quarto")) {
            jogador.x = 1000;
        }

        // Quarto -> Corredor
        else if (cenarioAntigo.equals("Quarto") && cenarioNovo.equals("Corredor")) {
            jogador.x = 430;
        }
    }

    public boolean deveDesenharJogador(String cenarioAtual) {

        if (cenarioAtual.equals("Prateleira")) {
            return false;
        }

        if (cenarioAtual.equals("CofreFechado")) {
            return false;
        }

        if (cenarioAtual.equals("CofreAberto")) {
            return false;
        }

        return true;
    }

    public boolean permiteMovimentoJogador(String cenarioAtual) {

        if (cenarioAtual.equals("Prateleira")) {
            return false;
        }

        if (cenarioAtual.equals("CofreFechado")) {
            return false;
        }

        if (cenarioAtual.equals("CofreAberto")) {
            return false;
        }

        return true;
    }
}