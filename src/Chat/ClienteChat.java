package Chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static Chat.ServidorChat.selecPuerto;

/**
 *
 * @author Iago Fernandez
 */
public class ClienteChat {
/*
    Variables globales
    */
    private static DataInputStream entradaServ = null;
    private static DataOutputStream salidaServ = null;
    private Socket clienteSocket = null;

    public static void main(String args[]) {

        ServidorChat serv = new ServidorChat(); 

        try {
            //Aqui se crea el SOcket 
            System.out.println("Creando socket cliente");
            Socket clienteSocket = new Socket();
            System.out.println("Estableciendo la conexion");
             
            /*
            Aqui se le pide al cliente que nos indique a que server se quiere conectar
            */
            String ip = JOptionPane.showInputDialog("IP del servidor");
            int port = selecPuerto();

            InetSocketAddress addr = new InetSocketAddress(ip, port);
            clienteSocket.connect(addr);
            /*
            Nombre del usuario que quieres tener en el chat
            */
            String usuario = JOptionPane.showInputDialog("Cual es tu nombre de usuario");
            boolean conectado = true;
            entradaServ = new DataInputStream(clienteSocket.getInputStream());
            salidaServ = new DataOutputStream(clienteSocket.getOutputStream());

            salidaServ.writeUTF(usuario);
            salidaServ.flush();
             
            // Aqui muestra por pantalla si la conexión ha sido exitosa
            String conecta = usuario + " se ha conectado.";
            salidaServ.writeUTF(conecta);
            salidaServ.flush();
            
            // Si el usuario escrbe en el cuadro de texto bye, sale del chat
            String mensaje = JOptionPane.showInputDialog("");
            while (conectado) {
                while (!mensaje.equals("/bye")) {
                    try {
                       

                        String mensaje2 = entradaServ.readUTF();
                        System.out.println(mensaje2);

                        
                    } catch (IOException ioe) {
                        System.out.println("Enviando error: " + ioe.getMessage());
                    }
                }
                conectado = false;
            }
            salidaServ.writeUTF(mensaje);
            salidaServ.flush();
            
            
            conecta = usuario + " se ha desconectado.";
            salidaServ.writeUTF(conecta);
            salidaServ.flush();

            clienteSocket.close();
            entradaServ.close();
            salidaServ.close();
        } catch (IOException ex) {
            Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
