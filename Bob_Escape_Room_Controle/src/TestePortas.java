import com.fazecast.jSerialComm.SerialPort;

public class TestePortas {

    public static void main(String[] args) {

        SerialPort[] portas = SerialPort.getCommPorts();

        System.out.println("Portas encontradas:");

        for (SerialPort porta : portas) {

            System.out.println(
                    porta.getSystemPortName()
                            + " -> "
                            + porta.getDescriptivePortName()
            );
        }
    }
}