package Ejercicio3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Balanceador {
	
	private static ArrayList<String> Nodos;
	private static HashMap<String, Integer> info_nodos = new LinkedHashMap<String, Integer>();
	private static Logger log = LoggerFactory.getLogger(Balanceador.class);
	private static Gson gson;
	private static int estado_sinCarga = 10;
	private static int estado_normal = 30;
	private static int estado_alerta = 60;
	private static int estado_critico = 80;
	private static int puerto = 9090;
	
	
	public static HashMap<String, Integer> ordenarPorCarga(Map<String, Integer> mapa) {
		
	    List<Map.Entry<String, Integer>> listaNodos = new LinkedList<Map.Entry<String, Integer>>(mapa.entrySet());
	    Collections.sort(listaNodos, new Comparator<Map.Entry<String, Integer>>() {
	       
	    	public int compare(Map.Entry<String, Integer> map1, Map.Entry<String, Integer> map2) {
	            return (map1.getValue()).compareTo(map2.getValue());
	        }
	    });
	    
	    HashMap<String, Integer> solucion = new LinkedHashMap<String, Integer>();
	    for (Map.Entry<String, Integer> entry : listaNodos) {
	        solucion.put(entry.getKey(), entry.getValue());
	    }
	    return solucion;
	}
	
	
	

	public static int cargarNodos() {
		gson = new Gson();
		int longitud;
		HashMap<String, ArrayList<String>> config;
		try {
			config = gson.fromJson(new FileReader("nodos.json"), HashMap.class);
			Nodos = config.get("nodos");
			log.info("Configuracion Exitosa");
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			log.info("Error!! Chequee el archivo de configuracion");
		} 
		longitud=Nodos.size();
		
		return longitud;
	}

	
	public static boolean crearNodo() {
		boolean creado = false;
		String localizacion;
		if(info_nodos.size()<Nodos.size()) {
				localizacion = Nodos.get(info_nodos.size());
			
			int puerto = dividir(localizacion);
			Servicio servicio = new Servicio(puerto);
			Thread hilo = new Thread(servicio);
			hilo.start();
			info_nodos.put(localizacion,estado_sinCarga);
			
			MDC.put("log.packetname",Balanceador.class.getSimpleName().toString());
			log.info("Se ha creado un nuevo nodo y su puerto es: "+puerto);
			creado = true;
		}
		return creado;
	}

	
	public static int dividir(String localizacion) {//retorna puerto
		String[] puerto = localizacion.split(":");
		
		int obtenemos = Integer.parseInt(puerto[1]);
		return obtenemos;
	}
	
	public int dividirIp(String localizacion) {//retorna ip
		String[] ip = localizacion.split(":");
		
		int obtenemos = Integer.parseInt(ip[0]);
		return obtenemos;
	}
	

	public synchronized static void finalizarTrabajo(String localizacion){
		int trabajo;
		synchronized(info_nodos) {
			trabajo = info_nodos.get(localizacion);
		}
		//log.info("Trabajo terminado"+trabajo);
		trabajo--;
		info_nodos.put(localizacion, trabajo);
		log.info("El nodo que termino la tarea es: "+localizacion);
		if((trabajo==0)&&(info_nodos.size()>2)) {
			parar(localizacion);
		}
	}
	
	public static void parar(String localizacion) {
		String[] division = localizacion.split(":");
		try {
			String div1=division[0];
			int div2=Integer.parseInt(division[1]);
			Registry registro = LocateRegistry.getRegistry(div1, div2);
			Gestionar gest=(Gestionar) registro.lookup("Gestionando");
			Gestionar controlNodo = gest;
			controlNodo.pararServicio();
			synchronized (info_nodos) {
				info_nodos.remove(localizacion);				
			}
		} catch (NumberFormatException | RemoteException | NotBoundException e) {
			log.info("Error al intertar parar el nodo: "+localizacion);
		}
	}
	
		
	public synchronized static String asignarTrabajo() {
		MDC.put("log.packetname",Balanceador.class.getSimpleName().toString());
		info_nodos = ordenarPorCarga(info_nodos);
		Map.Entry<String, Integer>entrada = info_nodos.entrySet().iterator().next();
		String localiz = entrada.getKey();
		int trabajo = info_nodos.get(localiz);
		if(trabajo<=estado_normal) {
			trabajo = info_nodos.get(localiz);
			trabajo++;
			info_nodos.put(localiz, trabajo);
			log.info("Se le asigno el trabajo: "+trabajo+ "al nodo:"+localiz);
		}else if((trabajo>=estado_alerta) && (trabajo<=estado_critico)) {
			boolean crearNodo = crearNodo();
			if(crearNodo) {
				localiz = asignarTrabajo();
			}else if(trabajo<estado_critico){
				trabajo++;
				info_nodos.put(localiz, trabajo);
				log.info("Se le asigno el trabajo: "+trabajo+ "al nodo:"+localiz);
			}else {
				localiz = "127.0.0.1"+puerto;
			}
		}
		return localiz;
	}
		
	
	
	public static String atenderCliente() {
		String localiz = null;
		try {
			localiz = asignarTrabajo();
			int port = dividir(localiz);
			if(port == puerto)
				log.info("No hay ningun nodo disponible, usted debe esperar a que alguno se desocupe");
			
			while (port==puerto) {
				Thread.sleep(5000);
				localiz = asignarTrabajo();
				port = dividir(localiz);
			}
		} catch (InterruptedException e) {
			log.info(e.getMessage());
		}
		return localiz;
	}

	
	
	public static void main() throws RemoteException, NotBoundException{

		Remote remote = UnicastRemoteObject.exportObject(new Trabajo() {
			
			public int[] restarVectores(int[] v1, int[] v2) throws RemoteException{
				Registry registry;
				int[] solucion = null;
				try {
					String direccion = atenderCliente();
					String[] division = direccion.split(":");
					String div1=division[0];
					int div2=Integer.parseInt(division[1]);
					registry = LocateRegistry.getRegistry(div1,div2);
					Trabajo nodo = (Trabajo) registry.lookup("Trabajo");
					solucion = nodo.restarVectores(v1, v2);
					finalizarTrabajo(direccion);
				} catch (NotBoundException | RemoteException e) {
					log.info(e.getMessage());
				}
				return solucion;
			}

			public int[] sumarVectores(int[] v1, int[] v2) throws RemoteException{
				Registry registry;
				int[] solucion = null;
				try {
					String direccion = atenderCliente();
					String[] division = direccion.split(":");
					String div1=division[0];
					int div2=Integer.parseInt(division[1]);
					registry = LocateRegistry.getRegistry(div1,div2);
					Trabajo nodo = (Trabajo) registry.lookup("Trabajo");
					solucion = nodo.sumarVectores(v1, v2);
					finalizarTrabajo(direccion);
				} catch (NotBoundException | RemoteException e) {
					log.info(e.getMessage());
				}
				return solucion;
			}

			
		},0);
		
		if(cargarNodos()>=2) {
			crearNodo();
			crearNodo();
			Registry registrar = LocateRegistry.createRegistry(puerto);
			registrar.rebind("Trabajo", remote);
			log.info("¡El balanceador se encuentra en funcionamiento!");
		};
	}	
}
