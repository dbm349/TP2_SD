package Ejercicio1.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conexion implements Runnable{
	
		
		private BufferedReader entrada;
		private PrintWriter salida;
		
		private final static Logger log = LoggerFactory.getLogger(Conexion.class);
			
			public void run() {
				String aux;
				while (true) {
					try {
						Thread.sleep(10000);
						NodoP.salida.println("4");
						aux = NodoP.entrada.readLine();
						
							if(aux==null) {
								log.info("Error, se ha caido la conexion con el server master");
								if(NodoP.conectarse(0)) {
									NodoP.notificar();
								}
							}
					} catch (IOException e) {
						NodoP.conectarse(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	
			}

}
