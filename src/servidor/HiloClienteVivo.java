package servidor;

import java.io.IOException;
import java.net.Socket;

public class HiloClienteVivo extends Thread {

    Socket sk; //

    public HiloClienteVivo(Socket sk) {
        this.sk = sk;
    }

    @Override
    public void run() {

        try {

            while (true) {
                Thread.sleep(2000);
                if (!sk.isConnected() && sk.isClosed()) {
                    System.err.println("\nConexión perdida con el cliente");
                    Thread.currentThread().interrupt(); //interrumpimos/matamos al hilo ya que no está funcionando
                    return; //para salir del bucle infinito
                }
            }

        } catch (InterruptedException ex) {
            System.err.println("\nConexión perdida");
            try {
                sk.close();
            } catch (IOException ex1) {
            }

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }
}
