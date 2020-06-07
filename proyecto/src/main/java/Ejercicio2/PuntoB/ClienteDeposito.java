package Ejercicio2.PuntoB;

import java.io.IOException;

import Ejercicio2.PuntoA.Cliente;

public class ClienteDeposito {

    public static void main(String[] args) {
        System.out.println("Cliente para deposito iniciado..");
        Cliente client = new Cliente("localhost",7778,"localhost", 7777);
        try {
            client.openSocketDeposito();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            try {
            	client.deposito(100d);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
