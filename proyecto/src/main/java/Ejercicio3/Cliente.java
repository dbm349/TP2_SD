package Ejercicio3;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class Cliente {

	public static void main(int numero) throws NotBoundException {
		try {
			
			Random aleatorio = new Random();
			int opcion = aleatorio.nextInt(2);
			int[] v1 = new int[3];
			int[] v2 = new int[3];
			int[] solucion;
			Registry clienteRMI = LocateRegistry.getRegistry("127.0.0.1",9090);
			Trabajo cliStub = (Trabajo) clienteRMI.lookup("Trabajo");
			
			for (int i = 0; i < 3; i++) {
				v1[i] = new Random().nextInt(9);
				v2[i] = new Random().nextInt(9);
			}
			if(opcion == 0) {
					solucion = cliStub.restarVectores(v1, v2);
				System.out.printf("Número de cliente: "+numero+ "Operación resta");
				System.out.printf(" Vector 1=[%d,%d,%d] ",v1[0],v1[1],v1[2]);
				System.out.printf(" Vector 2=[%d,%d,%d] ",v2[0],v2[1],v2[2]);
			}else {
				solucion = cliStub.sumarVectores(v1, v2);
				System.out.printf("Número de cliente: "+numero+ "Operación suma");
				System.out.printf(" Vector 1=[%d,%d,%d] ",v1[0],v1[1],v1[2]);
				System.out.printf(" Vector 2=[%d,%d,%d] ",v2[0],v2[1],v2[2]);
				
			}
			System.out.printf("Número de cliente: "+numero);
			System.out.printf("Resultado: [%d,%d,%d] ",solucion[0],solucion[1],solucion[2]);
			
		} catch (RemoteException e) {
		}
	}
}
			
			