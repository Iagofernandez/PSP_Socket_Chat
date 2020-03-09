package Chat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Iago Fernandez
 */
public class ServidorChat extends Thread {
    /*
    Aqui se crean las variables globales
    */
    
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private DataInputStream entrada;
    private DataOutputStream salidaCli = null;
    private static ArrayList<Socket> lClientes = new ArrayList<Socket>();
    

    public ServidorChat() {
        

    }

    public ServidorChat(Socket socket, ArrayList<Socket> lClientes) {
        this.entrada = null;
        clientSocket = socket;
        this.lClientes = lClientes;
        lClientes.add(clientSocket);
    }

    public void run() {
    
        try {
            
            boolean conectado = true;
            System.out.println("Iniciando");

           
            entrada = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            salidaCli = new DataOutputStream(clientSocket.getOutputStream());
           
            String usuario = entrada.readUTF();
            System.out.println(usuario + " se ha conectado.");

            String conecta = entrada.readUTF();
            broadcastStatus(conecta);

            while (conectado) {
               
                String mensaje = entrada.readUTF();
                broadcast(mensaje, usuario);
               
                if (!mensaje.equals("/bye")) {
                   
                    System.out.println(usuario + " : " + mensaje);
                } else {
                   
                    conectado = false;
                }
          
                    
                
            }

           
            conecta = entrada.readUTF();
            broadcastStatus(conecta);
            
            System.out.println(usuario + " se ha desconectado");
            entrada.close();

        } catch (IOException ex) {
            Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }


    public static void main(String[] args) {

        try {
            
            System.out.println("Creando socket servidor");

            ServerSocket serverSocket = new ServerSocket();

            System.out.println("Realizando el bind");

           
            int port = selecPuerto();

            InetSocketAddress addr = new InetSocketAddress("localhost", port);
            serverSocket.bind(addr);
            System.out.println("Inicializando servicio en: localhost");
            System.out.println("Inicializando servicio en: " + port);

            System.out.println("Aceptando conexiones");

            while (serverSocket != null) {

               
                Socket newSocket = serverSocket.accept();
                System.out.println("Conexión recibida");
                System.out.println("cliente: " + newSocket);

                ServidorChat hilo = new ServidorChat(newSocket, lClientes);
                hilo.start();
            }
            System.out.println("Conexion recibida");
        } catch (IOException ex) {
            Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

   
    public static int selecPuerto() {
        String puerto = JOptionPane.showInputDialog("Indique puerto donde alojar su servidor");
        int port = 0;
        if (puerto.length() >= 2 && puerto.matches("[0-9]+")) {
            port = Integer.parseInt(puerto);
        } else {
            port = selecPuerto();
        }
        return port;

    }

   
    public void broadcast(String mensaje, String usuario) {

        try {
            for (Socket cliente : lClientes) {
                String mensaje2 = usuario + " : " + mensaje;
                salidaCli = new DataOutputStream(cliente.getOutputStream());
                salidaCli.writeUTF(mensaje2);
            }
            salidaCli.flush();

        } catch (IOException ex) {
            Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void broadcastStatus(String mensaje) {

        try {
            for (Socket cliente : lClientes) {
                salidaCli = new DataOutputStream(cliente.getOutputStream());
                salidaCli.writeUTF(mensaje);
            }
            salidaCli.flush();

        } catch (IOException ex) {
            Logger.getLogger(ServidorChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
