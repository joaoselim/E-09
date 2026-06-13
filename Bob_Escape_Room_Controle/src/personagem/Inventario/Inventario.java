package personagem.Inventario;

import java.util.ArrayList;
import java.io.Serializable;
import personagem.Inventario.Itens.Item;

public class Inventario implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Item> itens;

    public Inventario() {
        itens = new ArrayList<>();
    }

    public void adicionarItem(Item item) {
        itens.add(item);
    }

    public boolean possuiItem(String tipoItem) {
        for(Item item : itens) {
            switch(tipoItem) {
                case "chave_escritorio":
                    if(item.isChave_Escritorio()) return true;
                    break;
                case "chave_quarto":
                    if(item.isChave_quarto()) return true;
                    break;
                case "chave_hotel":
                    if(item.isChave_hotel()) return true;
                    break;
                case "dollar":
                    if(item.isDollar()) return true;
                    break;
                case "papel":
                    if(item.isPapel()) return true;
                    break;
            }
        }
        return false;
    }

    public ArrayList<Item> getItens() {
        return itens;
    }
}