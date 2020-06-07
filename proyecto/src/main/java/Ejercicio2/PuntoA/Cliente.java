package Ejercicio2.PuntoA;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class Cliente{
    Gson gson = new Gson();
    private String ipE;
    private int puertoE;
    private String ipD;
    private int puertoD;
    Socket socketE;
    Socket socketD;
    BufferedReader inputChannel;
    PrintWriter outputChannel;
    BufferedReader inputChannel2 ;
    PrintWriter outputChannel2;

    public Cliente (String ipE, int puertoE,String ipD, int puertoD) {
        this.ipE = ipE;
        this.puertoE = puertoE;
        this.ipD = ipD;
        this.puertoD = puertoD;
    }

    public void openSocketExtraccion() throws IOException {
        socketE = new Socket (ipE, puertoE);
        inputChannel = new BufferedReader (new InputStreamReader(socketE.getInputStream()));
        outputChannel = new PrintWriter (socketE.getOutputStream(),true);
    }

    public void openSocketDeposito() throws IOException {
        socketD = new Socket (ipD, puertoD);
        inputChannel2 = new BufferedReader (new InputStreamReader (socketD.getInputStream()));
        outputChannel2 = new PrintWriter (socketD.getOutputStream(),true);
    }

    public void extraccion(Double monto){
        System.out.println("Conexion con cliente exitosa");
        String json = gson.toJson(monto);
        System.out.println(json);
        outputChannel.println(json);
    }

    public void deposito(Double monto){
        System.out.println("Conexion con cliente exitosa");
        String json = gson.toJson(monto);
        System.out.println(json);
        outputChannel2.println(json);
    }
}
