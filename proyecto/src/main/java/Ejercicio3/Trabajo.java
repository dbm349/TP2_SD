package Ejercicio3;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Trabajo extends Remote{
	
	public int[] sumarVectores (int[] v1, int[] v2)throws RemoteException;
	public int[] restarVectores (int[] v1, int[] v2)throws RemoteException;
}
