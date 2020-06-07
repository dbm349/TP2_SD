package Ejercicio2.PuntoB;

import java.io.IOException;

import Ejercicio2.PuntoA.Cliente;

public class ClienteExtraccion {

    public static void main(String[] args) {
        System.out.println("Cliente para extraccion iniciado..");
        Cliente client = new Cliente("localhost",7778,"localhost", 7777);
        try {
            client.openSocketExtraccion();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            try {
            	client.extraccion(50d);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
