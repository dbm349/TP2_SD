package Ejercicio4.Distribuido;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITarea extends Remote {
    public Imagen convertirImg(Imagen img) throws RemoteException;
}
