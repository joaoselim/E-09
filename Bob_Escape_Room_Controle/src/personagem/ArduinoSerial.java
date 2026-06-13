package personagem;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;

public class ArduinoSerial extends Thread {

    private final Controle controle;
    private final SerialPort porta;

    public ArduinoSerial(Controle controle, String nomePorta) {

        this.controle = controle;

        porta = SerialPort.getCommPort(nomePorta);

        porta.setBaudRate(9600);
        porta.setNumDataBits(8);
        porta.setNumStopBits(SerialPort.ONE_STOP_BIT);
        porta.setParity(SerialPort.NO_PARITY);

        // Evita travamentos na leitura
        porta.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING,
                100,
                0
        );
    }

    @Override
    public void run() {
        try {
            if (!porta.openPort()) {
                System.out.println("Erro ao abrir a porta serial: " + porta.getSystemPortName());
                return;
            }

            System.out.println("Porta serial aberta com sucesso: " + porta.getSystemPortName());
            InputStream entrada = porta.getInputStream();

            int ultimoEstado = -1;

            while (!Thread.currentThread().isInterrupted()) {
                // Verifica se o Arduino enviou algum dado
                if (porta.bytesAvailable() > 0) {

                    int bytesNoBuffer = porta.bytesAvailable();

                    // Se o buffer acumular muitos bytes antigos por lentidão do jogo,
                    // descarta os antigos e lê apenas o comando mais recente (tempo real)
                    if (bytesNoBuffer > 1) {
                        entrada.skip(bytesNoBuffer - 1);
                    }

                    int estadoAtual = entrada.read();

                    // Garante que o byte é válido e que o estado realmente mudou
                    if (estadoAtual >= 0 && estadoAtual != ultimoEstado) {
                        System.out.println("Controle detectado - Estado: " + estadoAtual);
                        controle.atualizarEstado(estadoAtual);
                        ultimoEstado = estadoAtual;
                    }
                }

                // Pausa de 10ms para não sobrecarregar o processador do PC
                Thread.sleep(10);
            }

        } catch (Exception e) {
            System.out.println("Erro na comunicação serial com o Arduino:");
            e.printStackTrace();
        } finally {
            if (porta != null && porta.isOpen()) {
                porta.closePort();
                System.out.println("Porta serial fechada.");
            }
        }
    }
}