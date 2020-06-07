package Ejercicio3;

import java.rmi.NotBoundException;

public class VariosClientes {

	
	public VariosClientes() {
		for (int i = 0; i < 30; i++) {
			nuevoCliente(i);
		}
	}
	
	public void nuevoCliente(final int n) {
		new Thread(new Runnable() {
		    public void run() {
		    	try {
					Cliente.main(n);
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
		    }
		}).start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("Error!");
		}
	}
	

	public static void main(String[] args) {
		new VariosClientes();

	}

}
