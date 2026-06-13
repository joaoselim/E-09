package tela;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import java.io.*;

import personagem.ArduinoSerial;
import personagem.Controle;
import personagem.Jogador;

public class PainelJogo extends JPanel implements Runnable {

    final int screenWidth = 1365;
    final int screenHeight = 562;

    // VARIÁVEIS DO MENU
    String estado = "MENU"; // "MENU" ou "JOGO"
    Rectangle botaoNovoJogo;
    Rectangle botaoContinuar;

    // ARQUIVO DE SAVE
    private static final String SAVE_FILE = "savegame.dat";

    Thread gameThread;

    Controle controle = new Controle();
    ArduinoSerial arduino =
            new ArduinoSerial(controle, "COM10");

    Jogador jogador;
    Cenario cenario;

    BufferedImage background;
    BufferedImage imagemEnigma;

    boolean mostrarEnigma = false;

    String cenarioAtual;

    public PainelJogo() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);

        this.addKeyListener(controle);
        this.setFocusable(true);

        // CONFIGURAR BOTÕES COM AS COORDENADAS
        botaoNovoJogo = new Rectangle(851, 431, 177, 91);
        botaoContinuar = new Rectangle(1047, 431, 177, 91);


        arduino.start();

        // LISTENER DO MOUSE
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point pos = e.getPoint();

                if (estado.equals("MENU")) {
                    if (botaoNovoJogo.contains(pos)) {
                        System.out.println("NOVO JOGO clicado!");
                        iniciarNovoJogo();
                    } else if (botaoContinuar.contains(pos)) {
                        System.out.println("CONTINUAR clicado!");
                        carregarJogo();
                    }
                } else if (estado.equals("JOGO")) {
                    if (cenarioAtual != null && cenarioAtual.equals("CofreFechado")) {
                        inputSenhaCofre();
                        requestFocusInWindow();
                    }
                }
            }
        });

        // TECLA S PARA SALVAR
        this.getInputMap().put(KeyStroke.getKeyStroke("S"), "salvar");
        this.getActionMap().put("salvar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (estado.equals("JOGO") && jogador != null) {
                    salvarJogo();
                }
            }
        });

        // COMEÇA NO MENU
        estado = "MENU";
        carregarImagemEnigma();
        carregarCenario("Inicio");
    }

    // MÉTODO PARA INICIAR NOVO JOGO
    private void iniciarNovoJogo() {
        jogador = new Jogador(controle);
        cenario = new Cenario(jogador);
        cenarioAtual = "Entrada";
        carregarCenario(cenarioAtual);
        estado = "JOGO";

        if (gameThread == null) {
            startGameThread();
        }

        repaint();
        requestFocusInWindow();
    }

    // MÉTODO PARA SALVAR JOGO
    private void salvarJogo() {
        try {
            FileOutputStream fileOut = new FileOutputStream(SAVE_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(jogador);
            out.writeObject(cenarioAtual);

            out.close();
            fileOut.close();

            JOptionPane.showMessageDialog(this, "Jogo salvo com sucesso!");
            System.out.println("Jogo salvo em: " + new File(SAVE_FILE).getAbsolutePath());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // MÉTODO PARA CARREGAR JOGO
    private void carregarJogo() {
        File saveFile = new File(SAVE_FILE);

        if (!saveFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum save encontrado! Comece um novo jogo.",
                    "Arquivo não encontrado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            FileInputStream fileIn = new FileInputStream(SAVE_FILE);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            jogador = (Jogador) in.readObject();
            cenarioAtual = (String) in.readObject();

            in.close();
            fileIn.close();

            // Reconecta o controle e recarrega as imagens
            jogador.setControle(controle);
            jogador.recarregarImagens();

            cenario = new Cenario(jogador);
            carregarCenario(cenarioAtual);
            estado = "JOGO";

            if (gameThread == null) {
                startGameThread();
            }

            JOptionPane.showMessageDialog(this, "Jogo carregado com sucesso!");

            repaint();
            requestFocusInWindow();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) {
            if (estado.equals("JOGO") && cenario != null) {
                update();
            }
            repaint();

            try {
                Thread.sleep(16);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (cenario.permiteMovimentoJogador(cenarioAtual)) {
            jogador.update(cenarioAtual);
        }

        String novoCenario = cenario.updateCenario(cenarioAtual, jogador, screenWidth);

        if (novoCenario.equals("Fim")) {
            cenarioAtual = "Fim";
            carregarCenario(cenarioAtual);
            JOptionPane.showMessageDialog(this, "Parabéns! Você completou o jogo!", "Vitória!", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!novoCenario.equals(cenarioAtual)) {
            cenarioAtual = novoCenario;
            carregarCenario(cenarioAtual);
        }

        if (controle.consumirInventario()) {
            mostrarEnigma = !mostrarEnigma;
        }
    }

    private void analisarClique(int mouseX, int mouseY) {

        if (cenarioAtual.equals("CofreFechado")) {

            inputSenhaCofre();

            this.requestFocusInWindow();
        }
    }

    private void inputSenhaCofre() {
        String senhaInserida = JOptionPane.showInputDialog(null, "Digite a senha:");
        if (senhaInserida != null && cenario != null) {
            boolean abriu = cenario.getCofre().analisarSenha(senhaInserida);

            if (abriu) {
                cenarioAtual = "CofreAberto";
                carregarCenario(cenarioAtual);
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Senha incorreta!");
            }
        }
    }

    private void carregarImagemEnigma() {

        String caminhoImagem = "/personagem/Inventario/Itens/ObjetosInterativos/Enigma_kakatua.png";

        try {

            InputStream stream = getClass().getResourceAsStream(caminhoImagem);

            if (stream == null) {
                throw new RuntimeException("Imagem do enigma não encontrada: " + caminhoImagem);
            }

            imagemEnigma = ImageIO.read(stream);

        } catch (Exception e) {

            System.out.println("ERRO AO CARREGAR IMAGEM DO ENIGMA");
            e.printStackTrace();
        }
    }

    private void carregarCenario(String nomeCenario) {
        String caminhoImagem = "";

        if (nomeCenario.equals("Inicio")) {
            caminhoImagem = "/tela/Cenarios/inicio.jpg";
        } else if (nomeCenario.equals("Entrada")) {
            caminhoImagem = "/tela/Cenarios/entrada.png";
        } else if (nomeCenario.equals("Corredor")) {
            caminhoImagem = "/tela/Cenarios/corredor.png";
        } else if (nomeCenario.equals("Bar")) {
            caminhoImagem = "/tela/Cenarios/bar.png";
        } else if (nomeCenario.equals("Escritorio")) {
            caminhoImagem = "/tela/Cenarios/escritorio.png";
        } else if (nomeCenario.equals("Quarto")) {
            caminhoImagem = "/tela/Cenarios/quarto.png";
        } else if (nomeCenario.equals("Prateleira")) {
            caminhoImagem = "/tela/Cenarios/prateleira.png";
        } else if (nomeCenario.equals("CofreFechado")) {
            caminhoImagem = "/tela/Cenarios/cofre_fechado.png";
        } else if (nomeCenario.equals("CofreAberto")) {
            caminhoImagem = "/tela/Cenarios/cofre_aberto.png";
        } else if (nomeCenario.equals("Fim")) {
            caminhoImagem = "/tela/Cenarios/conclusão.jpg";
        } else {
            System.out.println("CENARIO DESCONHECIDO: " + nomeCenario);
            return;
        }

        try {
            InputStream stream = getClass().getResourceAsStream(caminhoImagem);
            if (stream == null) {
                throw new RuntimeException("Cenário não encontrado: " + caminhoImagem);
            }
            background = ImageIO.read(stream);
        } catch (Exception e) {
            System.out.println("ERRO AO CARREGAR CENARIO: " + caminhoImagem);
            e.printStackTrace();
        }
    }

    private void desenharEnigma(Graphics2D g2) {

        if (imagemEnigma == null) {
            return;
        }

        int larguraOriginal = imagemEnigma.getWidth();
        int alturaOriginal = imagemEnigma.getHeight();

        int larguraMaxima = (int) (screenWidth * 0.85);
        int alturaMaxima = (int) (screenHeight * 0.85);

        double escalaLargura = (double) larguraMaxima / larguraOriginal;
        double escalaAltura = (double) alturaMaxima / alturaOriginal;
        double escala = Math.min(escalaLargura, escalaAltura);

        if (escala > 1) {
            escala = 1;
        }

        int larguraFinal = (int) (larguraOriginal * escala);
        int alturaFinal = (int) (alturaOriginal * escala);

        int x = (screenWidth - larguraFinal) / 2;
        int y = (screenHeight - alturaFinal) / 2;

        g2.drawImage(imagemEnigma, x, y, larguraFinal, alturaFinal, null);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (background != null) {
            g2.drawImage(background, 0, 0, screenWidth, screenHeight, null);
        }

        if (estado.equals("JOGO")) {
            if (cenario != null && cenario.deveDesenharJogador(cenarioAtual) && jogador != null) {
                jogador.draw(g2);
            }
        }

        if (mostrarEnigma && jogador.possuiItem("papel")) {
            desenharEnigma(g2);
        }

        g2.dispose();
    }
}