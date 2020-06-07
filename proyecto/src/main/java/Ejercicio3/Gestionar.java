package Ejercicio3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Gestionar extends Remote {
	public void pararServicio() throws RemoteException;

}
