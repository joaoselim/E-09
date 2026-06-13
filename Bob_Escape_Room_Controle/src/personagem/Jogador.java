package personagem;
import personagem.Inventario.*;
import personagem.Inventario.Itens.Item;
import java.io.Serializable;
import java.io.InputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
public class Jogador implements Serializable {
    private static final long serialVersionUID = 1L;

    transient Controle controle;  // ← Adicionar "transient" aqui
    Inventario inventario;
    public int x = 0;

    public final int tamanhoPersonagem = 185;

    final int chaoY = 527;

    int y = chaoY - tamanhoPersonagem;

    int velocidade = 5;

    boolean olhandoPraDireita = true;

    double velocidadeY = 0;
    double gravidade = 0.6;

    boolean pulando = false;

    transient BufferedImage[] framesAndando;
    transient BufferedImage[] framesParado;

    transient BufferedImage framePulando;

    int controleSprite = 0;
    int spriteNum = 0;

    public Jogador(Controle controle) {
        this.inventario = new Inventario();
        this.controle = controle;

        try {

            BufferedImage recorteAndando =
                    carregarImagem("/personagem/animacoes/BobAndando.png");

            BufferedImage recorteParado =
                    carregarImagem("/personagem/animacoes/BobParado.png");

            BufferedImage recortePulo =
                    carregarImagem("/personagem/animacoes/BobPulando.png");

            framesAndando = cortandoFrames(recorteAndando, 3, 3, 8);
            framesParado = cortandoFrames(recorteParado, 5, 5, 25);
            framePulando = recortePulo;

        } catch (Exception e) {

            System.out.println("ERRO AO CARREGAR SPRITES");
            e.printStackTrace();
        }
    }

    private BufferedImage carregarImagem(String caminho) throws Exception {

        InputStream stream = getClass().getResourceAsStream(caminho);

        if (stream == null) {
            throw new RuntimeException("Imagem não encontrada: " + caminho);
        }

        return ImageIO.read(stream);
    }

    private BufferedImage[] cortandoFrames(
            BufferedImage recorte,
            int colunas,
            int linhas,
            int numFrames
    ) {

        BufferedImage[] frames = new BufferedImage[numFrames];

        int larguraFrame = recorte.getWidth() / colunas;
        int alturaFrame = recorte.getHeight() / linhas;

        int frame = 0;

        for (int linha = 0; linha < linhas; linha++) {

            for (int coluna = 0; coluna < colunas; coluna++) {

                if (frame < numFrames) {

                    frames[frame] = recorte.getSubimage(
                            coluna * larguraFrame,
                            linha * alturaFrame,
                            larguraFrame,
                            alturaFrame
                    );

                    frame++;
                }
            }
        }

        return frames;
    }

    public void update(String cenarioAtual) {

        boolean podeIrDireita = false;
        boolean podeIrEsquerda = false;

        if (cenarioAtual.equals("Entrada") || cenarioAtual.equals("Corredor")) {
            podeIrDireita = true;
        }

        if (cenarioAtual.equals("Corredor") || cenarioAtual.equals("Bar")) {
            podeIrEsquerda = true;
        }

        if (controle.esquerdaAcionado) {

            if (!podeIrEsquerda) {

                if (x > -55) {
                    x -= velocidade;
                }

            } else {

                x -= velocidade;
            }

            olhandoPraDireita = false;
        }

        if (controle.direitaAcionado) {

            if (!podeIrDireita) {

                if (x < 1210) {
                    x += velocidade;
                }

            } else {

                x += velocidade;
            }

            olhandoPraDireita = true;
        }

        controleSprite++;

        if (controleSprite > 10) {

            spriteNum++;

            int maxFrames;

            if (pulando) {
                maxFrames = 1;
            } else if (controle.direitaAcionado || controle.esquerdaAcionado) {
                maxFrames = framesAndando.length;
            } else {
                maxFrames = framesParado.length;
            }

            if (spriteNum >= maxFrames) {
                spriteNum = 0;
            }

            controleSprite = 0;
        }

        if (controle.puloAcionado && !pulando) {
            velocidadeY = -13;
            pulando = true;
        }

        velocidadeY += gravidade;

        y += velocidadeY;

        int alturaChao = chaoY - tamanhoPersonagem;

        if (y >= alturaChao) {

            y = alturaChao;

            velocidadeY = 0;

            pulando = false;
        }
    }

    public boolean consumirInteracao() {
        return controle.consumirInteracao();
    }

    public void draw(Graphics2D g2) {

        BufferedImage frameAtual;

        if (pulando) {

            frameAtual = framePulando;

        } else if (controle.esquerdaAcionado || controle.direitaAcionado) {

            int frameAux = spriteNum % framesAndando.length;
            frameAtual = framesAndando[frameAux];

        } else {

            int frameAux = spriteNum % framesParado.length;
            frameAtual = framesParado[frameAux];
        }

        if (olhandoPraDireita) {
            g2.drawImage(frameAtual, x, y, tamanhoPersonagem, tamanhoPersonagem,null);

        } else {
            g2.drawImage(frameAtual,x + tamanhoPersonagem, y, -tamanhoPersonagem, tamanhoPersonagem,null);
        }
    }

    public void adicionarItem(Item item) {
        this.inventario.adicionarItem(item);
    }

    public boolean possuiItem(String nomeItem) {
        return inventario.possuiItem(nomeItem);
    }

    // 🆕 MÉTODO ADICIONADO: Para reconectar o controle depois de carregar o jogo
    public void setControle(Controle controle) {
        this.controle = controle;
    }

    // 🆕 MÉTODO ADICIONADO: Para acessar os itens do inventário (opcional)
    public ArrayList<Item> getItens() {
        return inventario.getItens();
    }
    public void recarregarImagens() {
        try {
            BufferedImage recorteAndando =
                    carregarImagem("/personagem/animacoes/BobAndando.png");

            BufferedImage recorteParado =
                    carregarImagem("/personagem/animacoes/BobParado.png");

            BufferedImage recortePulo =
                    carregarImagem("/personagem/animacoes/BobPulando.png");

            framesAndando = cortandoFrames(recorteAndando, 3, 3, 8);
            framesParado = cortandoFrames(recorteParado, 5, 5, 25);
            framePulando = recortePulo;

        } catch (Exception e) {
            System.out.println("ERRO AO RECARREGAR SPRITES");
            e.printStackTrace();
        }
    }
}