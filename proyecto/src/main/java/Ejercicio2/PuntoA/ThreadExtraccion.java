package Ejercicio2.PuntoA;

import com.google.gson.Gson;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;

public class ThreadExtraccion implements Runnable{
    private String Archivo;
    private Logger log;
    private Socket cliente;
    private Gson gson;

    ThreadExtraccion(String archivo, Logger log, Gson gson, Socket cliente){
        this.Archivo = archivo;
        this.log = log;
        this.cliente = cliente;
        this.gson = gson;
    }

    public void run() {
        try {
            PrintWriter outputChannel = new PrintWriter(this.cliente.getOutputStream(), true);
            BufferedReader inputChannel = new BufferedReader(new InputStreamReader(this.cliente.getInputStream()));
            log.info("Esperando una extraccion...");
            String ext;
            while ((ext = inputChannel.readLine())!=null){
                Double montoExtraccion = gson.fromJson(ext, Double.class);
                log.info("~~ Nueva Extraccion por: "+montoExtraccion+"$");

                BufferedReader brFile = new BufferedReader(new FileReader(Archivo));
                //leo saldo del archivo
                Double saldo = new Double(brFile.readLine());
                log.info("~~ Saldo Antes de la Extraccion:" + saldo + ", Monto:" + montoExtraccion);
                if (saldo >= montoExtraccion) {
                    saldo -= montoExtraccion;
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //actualizo saldo en el archivo
                    FileWriter writer = new FileWriter(Archivo);
                    writer.write(String.valueOf(saldo), 0, String.valueOf(saldo).length());
                    String json = gson.toJson("Extraccion Exitosa, el saldo actual es: "+saldo);
                    outputChannel.print(json);
                    log.info("~~ La extraccion fue realizada con exito");
                    writer.close();
                }else{
                    //En caso de no ser suficiente el saldo en el archivo
                    String json = gson.toJson("La extraccion fue rechazada, el saldo actual es: "+saldo);
                    outputChannel.print(json);
                    log.info("~~ La extraccion fue rechazada, el saldo no es suficiente");
                }

                log.info("~~ Saldo Despues de la Extraccion:" + saldo + ", Monto:" + montoExtraccion);
                log.info("Esperando una extraccion...");
                brFile.close();
            }


        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
