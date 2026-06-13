package personagem;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controle implements KeyListener {

    public boolean interagirAcionado;
    public boolean inventarioAcionado;
    public boolean esquerdaAcionado;
    public boolean direitaAcionado;
    public boolean puloAcionado;

    private boolean interagirConsumido = false;
    private boolean inventarioConsumido = false;

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            interagirAcionado = true;
        }

        if (code == KeyEvent.VK_E) {
            inventarioAcionado = true;
        }

        if (code == KeyEvent.VK_A) {
            esquerdaAcionado = true;
        }

        if (code == KeyEvent.VK_D) {
            direitaAcionado = true;
        }

        if (code == KeyEvent.VK_SPACE) {
            puloAcionado = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            interagirAcionado = false;
            interagirConsumido = false;
        }

        if (code == KeyEvent.VK_E) {
            inventarioAcionado = false;
            inventarioConsumido = false;
        }

        if (code == KeyEvent.VK_A) {
            esquerdaAcionado = false;
        }

        if (code == KeyEvent.VK_D) {
            direitaAcionado = false;
        }

        if (code == KeyEvent.VK_SPACE) {
            puloAcionado = false;
        }
    }

    public void atualizarEstado(int estado) {

        esquerdaAcionado   = (estado & (1 << 0)) != 0;
        direitaAcionado    = (estado & (1 << 1)) != 0;
        puloAcionado       = (estado & (1 << 2)) != 0;
        interagirAcionado  = (estado & (1 << 3)) != 0;
        inventarioAcionado = (estado & (1 << 4)) != 0;
    }

    public boolean consumirInteracao() {

        if (interagirAcionado && !interagirConsumido) {
            interagirConsumido = true;
            return true;
        }

        return false;
    }

    public boolean consumirInventario() {

        if (inventarioAcionado && !inventarioConsumido) {
            inventarioConsumido = true;
            return true;
        }

        return false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}