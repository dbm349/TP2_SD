package Ejercicio2.PuntoB;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorDeposito {
    private static final Gson gson = new Gson();
    private static final Logger log = LoggerFactory.getLogger(ServidorDeposito.class);
    private static String Archivo = "src/main/java/Ejercicio2/PuntoB/saldo.txt";


    public static void main(String[] args){

        int thread = (int) Thread.currentThread().getId();
        String packetName=ServidorDeposito.class.getSimpleName().toString()+"-"+thread;
        System.setProperty("log.name",packetName);
        int puerto = 7777;
        
        try {
            ServerSocket ss = new ServerSocket (puerto);
            System.out.println(" Servidor de Deposito iniciado en el puerto: "+puerto);
            while (true){
                Socket cliente = ss.accept();
                ThreadDeposito td = new ThreadDeposito(Archivo, log, gson, cliente);
                Thread tdThread = new Thread(td);
                tdThread.start();
            }
        } catch (IOException e){
            System.out.println("El puerto se encuentra en uso");
        }

    }


}
