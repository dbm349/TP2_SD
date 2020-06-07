package Ejercicio1.peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Servidor implements Runnable{
	
	private final static Logger log = LoggerFactory.getLogger(Servidor.class);
	private int intentos=0;
	private int puerto;
	private ArrayList<String> recursos;
	private ServerSocket ss;
	private Socket s;
	private String archivo;
	
	
public Servidor(int puerto,ArrayList<String> recursos, String carpComp){
		iniciar(puerto);
		this.puerto = puerto;
		this.recursos = recursos;
		this.archivo = carpComp;
	}
	
	public void iniciar(int puerto) {
		try {							
			ss = new ServerSocket(puerto);
		} catch (IOException e) {
			intentos++;
			iniciar(++puerto);
			log.info("Error al iniciar el servidor! ");
			log.info("Numero de intentos: "+intentos);
			e.getMessage();
		}
	}
	
	public void run() {
		String packetName = Servidor.class.getSimpleName().toString()+"-";
		MDC.put("log.name",packetName);
		try {
			log.info("Se ha iniciado el servidor en el cliente");
			
			while(true) {
				s = ss.accept();
				ThreadServidorP servidor = new ThreadServidorP(s,recursos,archivo);
				Thread servidorThread = new Thread(servidor);
				servidorThread.start();
			}
	} catch (IOException e) {
		log.info("Puerto en uso");
	}
		MDC.remove(packetName);
	}

}
