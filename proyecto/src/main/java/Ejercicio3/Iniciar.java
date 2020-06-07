package Ejercicio3;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class Iniciar {

	public static void main(String[] args) {
		
		new Thread(new Runnable() {
		    public void run() {
		    	try {
					Balanceador.main();
				} catch (RemoteException | NotBoundException e) {
					e.printStackTrace();
				}
		    }
		}).start();
		

	}

}
