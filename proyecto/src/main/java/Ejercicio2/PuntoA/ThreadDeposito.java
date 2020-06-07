package Ejercicio2.PuntoA;

import com.google.gson.Gson;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;

public class ThreadDeposito implements Runnable{
    private String Archivo;
    private Logger log;
    private Socket cliente;
    private Gson gson;

    ThreadDeposito(String archivo, Logger log, Gson gson, Socket cliente){
        this.Archivo = archivo;
        this.log = log;
        this.cliente = cliente;
        this.gson = gson;
    }

    public void run() {
        try {
            BufferedReader inputChannel = new BufferedReader(new InputStreamReader(this.cliente.getInputStream()));
            PrintWriter outputChannel = new PrintWriter(this.cliente.getOutputStream(), true);
            log.info("Esperando un deposito...");
            String dep;
            while ((dep = inputChannel.readLine()) != null){
                Double montoDeposito = gson.fromJson(dep, Double.class);
                log.info("~~ Nuevo Deposito por: "+montoDeposito+"$");
                BufferedReader brFile = new BufferedReader(new FileReader(Archivo));
                //leo saldo del archivo
                Double saldo = new Double(brFile.readLine());
                log.info("~~ Saldo Antes del Deposito:" + saldo + ", Monto:" + montoDeposito);
                saldo += montoDeposito;
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //actualizo saldo en el archivo
                FileWriter writer = new FileWriter(Archivo);
                writer.write(String.valueOf(saldo), 0, String.valueOf(saldo).length());
                String json = gson.toJson("Deposito Exitoso, el saldo actual es: "+saldo);
                outputChannel.print(json);
                log.info("~~ El deposito fue realizado con exito");
                writer.close();
                log.info("~~ Saldo Despues de la Extraccion:" + saldo + ", Monto:" + montoDeposito);
                log.info("Esperando un deposito...");
                brFile.close();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
