package Ejercicio1.peer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.gson.Gson;


public class NodoP {
	
	private final static Logger log = LoggerFactory.getLogger(NodoP.class);
	public static BufferedReader entrada;
	public static PrintWriter salida;
	private static ObjectOutputStream Object;
	private String mensaje;
	private Scanner miScanner = new Scanner(System.in);
	private static ArrayList<String> recursos;
	private String carpComp;
	private Gson gson;
	private static ArrayList<String> listaS;
	private static Socket s;
	private static int puertoS;

		public NodoP(ArrayList<String> listaS) {
			this.listaS = listaS;
			int thread = (int) Thread.currentThread().getId();
			String packetName = NodoP.class.getSimpleName().toString()+thread;
			MDC.put("log.name",packetName);
		}
			
		
		public static boolean conectarse(int idS) {
			
			int puerto;
			String IpS;
			boolean conectado = false;
			while((conectado!=true) && (idS<=listaS.size()-1)) {
				try {
					
					String division[] = listaS.get(idS).split(":");
					IpS = division[0];
					puerto = Integer.parseInt(division[1]);
					s = new Socket(IpS,puerto);
					log.info("Conectandose al server master");
					entrada = new BufferedReader (new InputStreamReader (s.getInputStream()));
					salida = new PrintWriter (s.getOutputStream(), true);
					Object = new ObjectOutputStream(s.getOutputStream());
					s.setSoTimeout(1000);
					conectado = true;
						}catch (IOException e){
							log.info("Intentos de conexion "+idS);	
							++idS;
					}
			}
		return conectado;
	}
		
		
		public ArrayList<String> listaRecursos(String ruta){
			carpComp = ruta;
			File dir = new File(ruta);
			ArrayList<String> recursos = new ArrayList<String>();
			String[] ficheros = dir.list();
			if(ficheros==null) {
				System.out.println("Sin ficheros");
			}else {
				for (String recu : ficheros) {
					File dir2 = new File(ruta+"/"+recu);
						if(!dir2.isDirectory()) {
							recursos.add(recu);
						}	
				}
			}
		return recursos;
		}

		public static void notificar() throws InterruptedException{
			salida.println("1");
			salida.println(puertoS);
				try {
						Thread.sleep(2000);
						Object.writeObject(recursos);
						Object.flush();
						log.info("El envio de los recursos fue exitoso");
				} catch (IOException e) {
					log.info("Ha ocurrido un error en el envio de los recursos");
					conectarse(0);
		}
	}

		public void preguntarPorR() {
			System.out.println("Ingrese el recurso(nombre)");
			String nameR = miScanner.nextLine().trim();
			try {
				nameR = nameR.trim().toLowerCase();
				salida.println("2");
				salida.println(nameR);
				String locali = entrada.readLine();
				if(!locali.equals("0")) {
					
					String division[] = locali.split(":");
					descargar(division[0], Integer.parseInt(division[1]), nameR);
				}else {
					System.out.println("No se ha encontrado el recurso solicitado");
				}	
			} catch (IOException | NullPointerException e) {
				log.info("Ha ocurrido un error en la conexion");
				if(conectarse(0)) {
					log.info("Se ha establecido la conexion nuevamente");
				}else {
					log.info("Ha finalizado la conexion");
				}
			}
		}
		
		
		public void descargar(String ip, int puerto, String nombreA){
			Descarga d = new Descarga(ip, puerto,nombreA);
			if(d.varActivo()) {
				Thread desc = new Thread(d);
				desc.start();
			}else
				log.info("Error, no se a podido descargar el archivo(No se pudo conectar)");
		}
		
				
		public void inicio(int IdS) throws InterruptedException {
		
			if (conectarse(IdS)){
			
				String division[] = listaS.get(listaS.size()-1).split(":");
				Random aleatorio = new Random();
				int r=aleatorio.nextInt(100);
				puertoS =  Integer.parseInt(division[1])+r;
				System.out.println("");
				System.out.println("El nodo ha sido iniciado!");
				System.out.println("Indique la ruta a la carpeta que contiene los recursos compartidos: ");
				mensaje = miScanner.nextLine();
				ArrayList<String> recurso = listaRecursos(mensaje);
				
				if(!recurso.isEmpty()) {
					System.out.print("Recursos Disponibles: ");
					for (String string : recurso) {
						System.out.print(string+", ");
					}
					System.out.println("");
					recursos = recurso;
					notificar();
				}
				iniciarServerSide(puertoS,recursos);
				iniciarConexion();
				menuCliente();
			}else {
				log.info("Error, la conexion con el master se ha caido");
			}
		}
		
		public static void iniciarConexion() {
			Conexion c = new Conexion();
			Thread cThread = new Thread(c);
			cThread.start();
		}
		
		public void menuCliente() {
			int opcion = 0;
			
			while(opcion != 6) {
				System.out.println("***********Menu Principal*********");
				System.out.println("1. Descargar archivo             ");
				System.out.println("2. Mostrar archivos compartidos     ");
				System.out.println("6. Abandonar                        ");
				System.out.println("------------------------------------");
				System.out.println("Opcion======>");
				try {
					opcion = Integer.parseInt(miScanner.nextLine().trim());
					
					switch (opcion) {
					case 1:
						preguntarPorR();
					break;
					
					case 2:
						System.out.println(recursos);
						break;
					case 6:
						salida.println("3");
						break;
					default:
						System.out.println("Opcion Invalida!");
						break;
					}
				} catch (NumberFormatException e) {
					System.out.println("Error, intente nuevamente");
				}
			}
		}
		
		public void iniciarServerSide(int puerto,ArrayList<String> recursos) {
			Servidor servidor = new Servidor(puerto, recursos, carpComp);
			Thread servidorThread = new Thread(servidor);
			servidorThread.start();
		}
		
	}
