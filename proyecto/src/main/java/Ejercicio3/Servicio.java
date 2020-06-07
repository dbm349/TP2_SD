package Ejercicio3;


import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Servicio implements Runnable{

	
	private final static Logger log = LoggerFactory.getLogger(Servicio.class);
	private Registry registry;
	private int puerto;
	
	public Servicio(int puerto){
		this.puerto = puerto;
		String packetName = Servicio.class.getSimpleName().toString()+"-"+Thread.currentThread().getId();
		MDC.put("log.packetname",packetName);
		try {
			registry = LocateRegistry.createRegistry(puerto);
			log.info("En escucha, puerto: "+puerto);
		} catch (RemoteException e) {
			log.info(e.getMessage());
		}
		MDC.remove(packetName);
	}
		
	public void run(){
		String packetName = Servicio.class.getSimpleName().toString()+"-"+Thread.currentThread().getId();
		MDC.put("log.packetname",packetName);
		try {
			Remote remote = UnicastRemoteObject.exportObject(new Trabajo() {

			
				public int[] restarVectores (int[] v1,int[] v2) {
					for (int i = 0; i < v1.length; i++) {
						v1[i] = v1[i] - v2[i];
					}
					try {
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						log.info(e.getMessage());
					}
					return v1;
				}

				public int[] sumarVectores (int[] v1,int[] v2) throws RemoteException {
					for (int i = 0; i < v1.length; i++) {
						v1[i] = v1[i] + v2[i];
					}
					try {
						Thread.sleep(20000);
					} catch (InterruptedException e) {
						log.info(e.getMessage());
					}
					return v1;
				}

				
				
			},0);
			
			Remote remoteControl = UnicastRemoteObject.exportObject(new Gestionar() {
				
				public void pararServicio() throws RemoteException {
					try {
						registry.unbind("Trabajo");
						registry.unbind("Gestionando");
						UnicastRemoteObject.unexportObject(registry, true);
						log.info("Se ha detenido el servicio!"+puerto);
					} catch (NotBoundException e) {
						log.info(e.getMessage());
					}
				}
			},0);
			
			registry.bind("Gestionando", remoteControl);
			registry.bind("Trabajo", remote);
		} catch (RemoteException | AlreadyBoundException e) {
			log.info(e.getMessage());
		
		}
		
	}
}
