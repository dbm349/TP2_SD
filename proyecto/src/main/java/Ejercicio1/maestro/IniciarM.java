package Ejercicio1.maestro;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class IniciarM {
	private static Gson gson;
	private static	NodoM mst;
	private static HashMap<String, ArrayList<String>> config;
	
	public static void main(String[] args) throws JsonSyntaxException, FileNotFoundException{
			try {
				gson=new Gson();
				config = gson.fromJson(new FileReader("conexion.json"), HashMap.class);
				ArrayList<String> listaS = config.get("servidores");
					if (listaS.size()>=2) {
						mst = new NodoM(listaS);
						mst.iniciar(0);
					}else {
							System.out.println("Error, es necesario contar con dos servidores o mas!");
						}
			} catch (JsonIOException e) {
				System.out.println("Error al cargar el archivo ");
			} 
	}
}
