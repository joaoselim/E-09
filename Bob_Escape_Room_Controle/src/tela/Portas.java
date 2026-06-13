package tela;
import javax.swing.JOptionPane;
import personagem.Jogador;

import javax.swing.*;

public class Portas {

    private boolean portaEscritorio = false;
    private boolean portaQuarto = false;
    private boolean portaHotel = false;

    public String tentarUsarPorta(String cenarioAtual, Jogador jogador) {

        // PORTA DO HOTEL
        if (cenarioAtual.equals("Entrada") && jogador.x <= 195 && jogador.possuiItem("chave_hotel")) {

            if (!portaHotel) {
                JOptionPane.showMessageDialog(null, "Você destrancou o Hotel!", "", JOptionPane.INFORMATION_MESSAGE);
                portaHotel = true;
                return cenarioAtual = "Fim";
            }

            JOptionPane.showMessageDialog(null, "Concluiu o jogo, encerrando", "", JOptionPane.INFORMATION_MESSAGE);
            return cenarioAtual;
        }

        // PORTA DO ESCRITÓRIO
        if (cenarioAtual.equals("Entrada") && jogador.x >= 910 && jogador.x <= 1065 && jogador.possuiItem("chave_escritorio")) {

            if (!portaEscritorio) {
                portaEscritorio = true;
                JOptionPane.showMessageDialog(null, "Você destrancou o Escritório!", "", JOptionPane.INFORMATION_MESSAGE);
                return cenarioAtual;
            }

            return "Escritorio";
        }

        // SAIR DO ESCRITÓRIO
        if (cenarioAtual.equals("Escritorio") && jogador.x >= 1100) {
            return "Entrada";
        }

        // PORTA DO QUARTO
        if (cenarioAtual.equals("Corredor") && jogador.x >= 360 && jogador.x <= 510 && jogador.possuiItem("chave_quarto")) {

            if (!portaQuarto) {
                portaQuarto = true;
                JOptionPane.showMessageDialog(null, "Você destrancou o Quarto!", "", JOptionPane.INFORMATION_MESSAGE);
                return cenarioAtual;
            }

            return "Quarto";
        }

        // SAIR DO QUARTO
        if (cenarioAtual.equals("Quarto") && jogador.x >= 1065) {
            return "Corredor";
        }

        return null;
    }

    public boolean isPortaEscritorio() {
        return portaEscritorio;
    }

    public boolean isPortaQuarto() {
        return portaQuarto;
    }

    public boolean isPortaHotel() {
        return portaHotel;
    }
}