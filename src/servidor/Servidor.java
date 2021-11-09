package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    static ServerSocket servidor;
    static Socket sc; //
    static DataInputStream in;
    static DataOutputStream out;

    public static void main(String[] args) {
        final int PORT = 40080;

        try {

            servidor = new ServerSocket(PORT);
            servidor.setReuseAddress(true);

            System.out.println("Servidor Iniciado");

            while (true) {
                sc = servidor.accept(); //se queda aquí hasta que sea aceptada la petición
                //creamos la comunicación de entrada y salida
                in = new DataInputStream(sc.getInputStream()); //para recibir los mensajes del cliente
                out = new DataOutputStream(sc.getOutputStream()); //para enviar mensaje al cliente

                HiloParaAntenderUnCliente hilo = new HiloParaAntenderUnCliente(in, out);
                hilo.start();

                System.out.println("Creada la conexión con el cliente ");

                //cuando creamos la conexión creamos un hilovivo para saber si el socket con el cliente esta conectado
                HiloClienteVivo hiloVivo = new HiloClienteVivo(sc);
                hiloVivo.start();

                //terminamos el hilo principal solo cuando sea el hilo interrumpido
                try {
                    hilo.join();
                } catch (InterruptedException ex) {
                }

                System.err.println("Cerrada la conexión con el cliente ");
                System.out.println("Servidor a la espera de nuevas conexiones...");
                sc.close();

            }
        } catch (IOException ex) {
            System.err.println("Error servidor: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

}
