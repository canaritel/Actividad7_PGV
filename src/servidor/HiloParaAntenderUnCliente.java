package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class HiloParaAntenderUnCliente extends Thread {

    private final DataInputStream in; //
    private final DataOutputStream out;
    static String ENCRYPT_KEY = "clavecompartida1"; //la clave debe tenerla el servidor y clientes

    public HiloParaAntenderUnCliente(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {

        String linea;

        try {

            Scanner entrada = new Scanner(System.in);
            entrada.useDelimiter("\n");

            while (true) {
                System.out.println("Esperando algo");
                linea = in.readUTF();

                System.out.println("Mensaje Cliente: " + decrypt(linea));
                // System.out.println("Mensaje Cliente: " + linea);

                System.out.print("(Si se cierra conexión con el cliente y se queda"
                        + " en esta fila por favor pulse ENTER para reinicar la conexión) \nEscribe respuesta: ");
                String respuesta = entrada.nextLine();

                out.writeUTF(encript(respuesta));
                //out.writeUTF(respuesta);
            }

        } catch (IOException ex) {
            System.err.println("Error Servidor " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Error Servidor claves " + ex.getMessage());
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
