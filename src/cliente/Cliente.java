package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Cliente {

    final static int PORT = 40080;
    final static String HOST = "localhost";
    static DataInputStream in;
    static DataOutputStream out;
    static Socket sc; //
    static String ENCRYPT_KEY = "clavecompartida1"; //la clave debe tenerla el servidor y clientes

    public static void main(String[] args) {

        try {
            Scanner entrada = new Scanner(System.in);
            entrada.useDelimiter("\n");

            sc = new Socket(HOST, PORT);

            in = new DataInputStream(sc.getInputStream()); //para recibir los mensajes del servidor
            out = new DataOutputStream(sc.getOutputStream()); //para enviar mensaje al servidor

            enviarMensajesAlServidor(in, out, entrada);
            System.out.println("Cerramos socket cliente");

            in.close();
            out.close();
            sc.close();

        } catch (IOException ex) {
            System.err.println("Error en cliente " + ex.getLocalizedMessage());
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }

    }

    private static void enviarMensajesAlServidor(DataInputStream in, DataOutputStream out, Scanner entrada) {

        String linea = "";

        while (true) {
            try {
                System.out.print("Escribe algo: ");
                linea = entrada.nextLine();

                out.writeUTF(encript(linea));
                //out.writeUTF(linea);
                System.out.println("Esperando mensaje del servidor...");
                String respuesta = in.readUTF();

                System.out.println("El servidor dice: " + decrypt(respuesta));
                //System.out.println("El servidor dice: " + respuesta);

            } catch (IOException ex) {
                System.err.println("Error de conexión " + ex.getMessage());
                System.err.println("Aplicación cliente cerrada");

            } catch (Exception ex) {
                System.err.println("Error Cliente claves " + ex.getMessage());
            }
        }
    }

    private static String encript(String text) throws Exception {
        Key aesKey = new SecretKeySpec(ENCRYPT_KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] encrypted = cipher.doFinal(text.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    private static String decrypt(String encrypted) throws Exception {
        Key aesKey = new SecretKeySpec(ENCRYPT_KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        byte[] encryptedBytes = Base64.getDecoder().decode(encrypted.replace("\n", ""));
        String decrypted = new String(cipher.doFinal(encryptedBytes), "UTF-8");

        return decrypted;
    }

}
