package tela;
import personagem.Inventario.Inventario;
import personagem.Inventario.Itens.Item;
import personagem.Jogador;

import javax.swing.JOptionPane;

public class Cofre {
    private boolean destrancado = false;
    private String senha = "91815";
    private Jogador jogador;

    public Cofre(Jogador jogador) {
        this.jogador = jogador;
    }

    private void Destrancar(){ destrancado = true; }

    public boolean analisarSenha(String senhaInserida){

        if (senhaInserida == null){
            return false;
        }
        if (senhaInserida.equals(senha)){
            Item chaveHotel = new Item();
            chaveHotel.setChave_hotel(true);
            jogador.adicionarItem(chaveHotel);
            JOptionPane.showMessageDialog(null, "Você encontrou a Chave do Hotel!", "Item Adquirido", JOptionPane.INFORMATION_MESSAGE);
            Destrancar();

            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Senha errada!", "", JOptionPane.INFORMATION_MESSAGE);

            return false;
        }
    }

    public boolean isDestrancado() {
        return destrancado;
    }
}
