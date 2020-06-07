package Ejercicio1.peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class ThreadServidorP implements Runnable{
	
	private final Logger log = LoggerFactory.getLogger(ThreadServidorP.class);
	private PrintWriter salida;
	private BufferedReader entrada;
	private String mensaje;
	private ArrayList<String> recursosD;
	private String carpComp;
	private Socket s;
	
	
	
	public ThreadServidorP(Socket s,ArrayList<String> recursos, String archivo) {
		this.s = s;
		this.recursosD = recursos;
		this.carpComp= archivo;
		
		try {
			this.salida = new PrintWriter (s.getOutputStream(), true);
			this.entrada = new BufferedReader (new InputStreamReader (s.getInputStream()));
			salida.flush();
		} catch (IOException e) {
			e.getMessage();
		}
		
	};
			
		public String buscar(String recu) {
			int i = 0;
			while(i<recursosD.size()) {
				if(recu.equalsIgnoreCase(recursosD.get(i))) {
					log.info("Recursos: "+recursosD.get(i));
					recu = recursosD.get(i);
					i = recursosD.size();
				}
				i++;
			}
		return recu;
		}
			
		
		public void enviar(){
			try {
				DataOutputStream dataOut = new DataOutputStream(s.getOutputStream());
				String nombreA = this.entrada.readLine();
				nombreA = buscar(nombreA);
				String rutaA = carpComp+System.getProperty("file.separator")+nombreA;
				File archivo = new File(rutaA);
				FileInputStream fileIn = new FileInputStream(rutaA);
				BufferedInputStream bis = new BufferedInputStream(fileIn);
				BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
				
				int tamanioArch = (int) archivo.length();
				dataOut.writeInt(tamanioArch);
			
				byte[] buffer = new byte[tamanioArch];
			
				bis.read(buffer);
			
				for(int i=0;i<buffer.length;i++) {
					bos.write(buffer[i]);
				}
				
				fileIn.close();
				bis.close();
				bos.close();
				
			} catch (IOException e) {
			}
		}
		
		
		public void run() {
				String packetName=ThreadServidorP.class.getSimpleName().toString()+"-"+Thread.currentThread().getId();
				MDC.put("log.name",packetName);
				log.info("Cliente conectado!");
				log.info("Ip: "+s.getInetAddress()+"puerto:."+s.getPort());
				try {
					int option = 0;
					while (option!=3) {
						mensaje = this.entrada.readLine();
						mensaje.trim();
						option = Integer.parseInt(mensaje); 
						
						switch (option) {
							case 1:
								enviar();
								break;
							case 2:
								
								break;
							case 3:
								log.info("El cliente se ha desconectado");
								s.close();
								break;
						}
					};
					
				} catch (Exception e) {
			
				};
				
			};

}
