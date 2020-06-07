package Ejercicio1.maestro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ThreadServidor implements Runnable{
	
	private PrintWriter salida;
	private BufferedReader entrada;
	private ObjectInputStream object;
	private Socket s;
	private String mensaje;
	private final Logger log = LoggerFactory.getLogger(ThreadServidor.class);
	private ArrayList<String> listaS; 
	private HashMap<String, String> recursos;
	private int puerto;
	private String direcCli;
	
	public ThreadServidor(Socket s,ArrayList<String> listaS,int puerto,HashMap<String, String> lista) {
		this.puerto = puerto;
		this.s = s;
		this.listaS = listaS;
		this.recursos = lista;
		try {
			this.salida = new PrintWriter (s.getOutputStream(), true);
			this.entrada = new BufferedReader (new InputStreamReader (s.getInputStream()));
			salida.flush();
			this.object = new ObjectInputStream(s.getInputStream());
		} catch (Exception e) {
		
		}
	}
	
	public void cargarArchivos() throws InterruptedException{
		String ip = s.getInetAddress().toString();
		String puerto;
		ip = ip.replace("/", "");
			try {
				try {
					puerto = this.entrada.readLine();
					ArrayList<String> arr = (ArrayList<String>) this.object.readObject();
							for (String recurso : arr) {
								recursos.put(recurso.trim().toLowerCase(), ip+":"+puerto);						}//Borro espacios y paso a minusc.
							log.info("Recursos: "+recursos);
							direcCli = ip+":"+puerto;
							replicar();
					} catch (IOException e) {}
				} catch (ClassNotFoundException e) {e.printStackTrace();}
	}
	
	public void replicar() throws InterruptedException {
		if(listaS.size()>=2) {
			String division[] = listaS.get(1).split(":");
			if((Integer.parseInt(division[1]))!=puerto) 
				try {
					Socket s = new Socket(division[0],Integer.parseInt(division[1]));
					
					PrintWriter salida = new PrintWriter (s.getOutputStream(), true);
					ObjectOutputStream Ob = new ObjectOutputStream(s.getOutputStream());
					
					salida.println("3");
					Thread.sleep(3000);
					Ob.writeObject(recursos);
					Ob.flush();
					log.info(" Los recursos fueron replicados: "+division[0]+":"+division[1]);
				} catch (IOException e) {
					log.info("Ha ocurrido un error en la replicacion"+e.getMessage());
				}
			}else{log.info("No hay servidores de replica");}
			
		}
	
	public void almacenarReplica() throws ClassNotFoundException {
		try {
			log.info("Adquiriendo la replica");
			HashMap<String, String> listaR = (HashMap<String, String>)this.object.readObject();
				if(listaR.size()<recursos.size()) {
					recursos.clear();
				}
			listaR.forEach((k,v)->recursos.put(k, v));
			log.info("La lista se ha replicado");
		} catch (IOException e) {
			log.info("Error en la obtencion de la informacion");
		} 
	}
	
	public void agarrarPeticion() {
		try {
			String nameR = entrada.readLine();
			if(recursos.containsKey(nameR)) {
				salida.println(recursos.get(nameR));
			}else {
				salida.println("0");
			}
		} catch (IOException e) {
			log.info("No hay respuesta del cliente");
		}
	}
	
	public void borrarArchivos() throws InterruptedException {
		Iterator it = recursos.entrySet().iterator();
		while(recursos.containsValue(direcCli) && it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			if(m.getValue().equals(direcCli)) {
				it.remove();
			}
		}
		replicar();
	}
	
	public void run() {
		int thread = (int) Thread.currentThread().getId();
		String packetName = ThreadServidor.class.getSimpleName().toString()+"-"+"puerto:"+this.puerto;
		MDC.put("log.name",packetName);
		log.info("Cliente conectado! ");
		log.info(s.getInetAddress()+":"+s.getPort());

		try {
			int option = 0;
				while(option!=5) {
					mensaje = this.entrada.readLine();
					mensaje.trim();
					option = Integer.parseInt(mensaje); 

					switch (option) {
						case 1:
							cargarArchivos();
							break;
						case 2:
							agarrarPeticion();
							break;
						case 3:
							almacenarReplica();
							break;
						case 4:
							this.salida.println("OK");
							break;
						case 5:
							borrarArchivos();
							log.info("Cliente desconectado:"+s.getInetAddress()+":"+s.getPort());
							s.close();
						break;
					}
				}
			
		} catch (Exception e) {
	
		}
		
	}


}
