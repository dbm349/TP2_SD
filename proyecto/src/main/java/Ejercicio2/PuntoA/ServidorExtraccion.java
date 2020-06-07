package Ejercicio2.PuntoA;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorExtraccion {
    private static final Gson gson = new Gson();
    private static final Logger log = LoggerFactory.getLogger(ServidorExtraccion.class);
    private static String Archivo = "src/main/java/Ejercicio2/PuntoA/saldo.txt";


    public static void main(String[] args) {
        int thread = (int) Thread.currentThread().getId();
        String packetName = ServidorExtraccion.class.getSimpleName().toString() + "-" + thread;
        System.setProperty("log.name", packetName);

        try {
            int puerto = 7778;
            ServerSocket ss = new ServerSocket(puerto);
            System.out.println(" Servidor de Extraccion iniciado en el puerto: " + puerto);
            while (true) {
                Socket cliente = ss.accept();
                ThreadExtraccion te = new ThreadExtraccion(Archivo, log, gson, cliente);
                Thread teThread = new Thread(te);
                teThread.start();
            }
        } catch (IOException e){
            System.out.println("El puerto se encuentra en uso");
        }
    }

}
