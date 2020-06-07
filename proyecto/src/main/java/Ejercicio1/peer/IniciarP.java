package Ejercicio1.peer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class IniciarP {
	
	private static NodoP peer;
	private static Gson gson;
	private static HashMap<String, ArrayList<String>> config;
	
	public static void main(String[] args) throws JsonSyntaxException, FileNotFoundException, InterruptedException {
	
		try {
			gson = new Gson();
			config = gson.fromJson(new FileReader("conexion.json"), HashMap.class);
			ArrayList<String> listaS = config.get("servidores");
				if(listaS.size()>0) {
					peer = new NodoP(listaS);
					peer.inicio(0);
				}
				else {
					System.out.println("No hay ningun servidor configurado");
					System.out.println("chequee el archivo de configuracion");
				}
			
		} catch (JsonIOException e) {
			e.printStackTrace();
		} 
		System.exit(0);
	}

}
