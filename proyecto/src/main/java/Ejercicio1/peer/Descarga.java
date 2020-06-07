package Ejercicio1.peer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Descarga implements Runnable{
	
		private static Logger log = LoggerFactory.getLogger(Descarga.class);	
		private String ip;
		private int puerto;
		private Socket s;
		private String nombreA;
			
			public Descarga(String ip, int puerto, String nombreA) {
				this.ip = ip;
				this.puerto = puerto;
				this.nombreA = nombreA;
				conectar();
			}
			
			
			public int conectar() {
				int conectado = 0;
				try {
					s = new Socket(ip,puerto);
					conectado = 1;
				} catch (IOException e) {
					e.getMessage();
				} 
				return conectado;
			}
			
			
			public boolean varActivo() {
				return s.isConnected();
			}

	public void run() {
		String packetName = Descarga.class.getSimpleName().toString()+"-";
		MDC.put("log.name",packetName);
		
		try {
			DataInputStream dataEntrada = new DataInputStream(s.getInputStream());
			PrintWriter salida = new PrintWriter(s.getOutputStream(),true);
			
			salida.println("1");
			salida.println(nombreA);
			
			int tamaño = dataEntrada.readInt();
			log.info("Descargando...: Archivo: "+nombreA+" ,tamaño: "+tamaño);
			File ubicacion = new File("recibidos"+Thread.currentThread().getId());
			ubicacion.mkdir();
			FileOutputStream fos = new FileOutputStream(ubicacion.getName()+System.getProperty("file.separator")+nombreA);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
			
			byte[] buffer = new byte[tamaño];
			
			for(int i=0; i<buffer.length;i++) {
				buffer[i] = (byte)bis.read();
			}
			bos.write(buffer);
			salida.println(3);
			bos.flush();
			bis.close();
			bos.close();
			s.close();
			log.info(" El archivo se ha descargado correctamente y se encuentra en: "+ubicacion.getPath());
		} catch (IOException e) {
			log.info("Ha ocurrido un error en la descarga del archivo");
		}
	}


}
