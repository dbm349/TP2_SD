package Ejercicio1.maestro;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class NodoM{ 
	private int puerto;
	private ArrayList<String> listaS;
	private HashMap<String, String> recursos = new HashMap<String, String>();
	private Socket s;
	private final Logger log = LoggerFactory.getLogger(NodoM.class);
	private ServerSocket ss;
	
	public NodoM(ArrayList<String> lista){
		this.listaS = lista;
		int thread = (int) Thread.currentThread().getId();
		String packetName = NodoM.class.getSimpleName().toString()+"-"+thread;
		MDC.put("log.name",packetName);
	}
		
	public void iniciar(int nro) {
		int nroS = nro;
		try {
			if(nroS<=listaS.size()) {
				String parts[] = listaS.get(nroS).split(":");
				this.puerto = Integer.parseInt(parts[1]);
				ss = new ServerSocket(this.puerto);
				log.info("Servidor corriendo en el puerto: "+this.puerto);
			
			while(true) {
				s = ss.accept();
				ThreadServidor hilo = new ThreadServidor(s,listaS,puerto,recursos);
				Thread sThread = new Thread(hilo);
				sThread.start();
				
				}
			}
			
			}catch (Exception e) {
				iniciar(++nroS);
				log.info("Puerto en uso!");
		}
	}}
