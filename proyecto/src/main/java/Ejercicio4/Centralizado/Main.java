package Ejercicio4.Centralizado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

public class Main {
    Logger log = LoggerFactory.getLogger(Main.class);

    public void singleThConCortes(String fileSource) {
        try {
            int CANT_CORTES = 10;
            LocalTime initTime = LocalTime.now();
            BufferedImage imagen;
            File file = null;
            file = new File(fileSource);
            imagen = ImageIO.read(file);
            log.info("Calculando imagen (Con cortes): "+file.getName());

            ManipularImagen imgHandler = new ManipularImagen(imagen,CANT_CORTES);
            ArrayList<BufferedImage> arrayImg = imgHandler.cortar();
            ArrayList<BufferedImage> arrayImgConFiltro = new ArrayList<BufferedImage>();

            for (BufferedImage img : arrayImg) {
                FiltroSobel filtro = new FiltroSobel(img);
                arrayImgConFiltro.add(filtro.aplicarFiltro());
            }

            BufferedImage resultado = imgHandler.unirImagen(arrayImgConFiltro);

            LocalTime endTime = LocalTime.now();
            log.info("Init time:"+initTime.toString());
            log.info("End time:"+endTime.toString());
            log.info("Delta time:"+ Duration.between(initTime, endTime));

            // write image
            try {
                file = new File("image_singleThread_conCorte_sobel.png");
                ImageIO.write(resultado, "png", file);
            } catch (IOException e) {
                System.err.println(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void singleThSinCortes(String fileSource) {
        try {
            LocalTime initTime = LocalTime.now();
            BufferedImage imagen;
            File file = null;
            file = new File(fileSource);
            imagen = ImageIO.read(file);
            log.info("Calculando imagen (Sin cortes): "+file.getName());

            FiltroSobel filtro = new FiltroSobel(imagen);

            BufferedImage resultado = filtro.aplicarFiltro();

            LocalTime endTime = LocalTime.now();
            log.info("Init time:"+initTime.toString());
            log.info("End time:"+endTime.toString());
            log.info("Delta time:"+Duration.between(initTime, endTime));

            // write image
            try {
                file = new File("image_singleThread_sinCorte_sobel.png");
                ImageIO.write(resultado, "png", file);
            } catch (IOException e) {
                System.err.println(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Main m = new Main();
        //uso imagen cargada en el repositorio
        m.singleThConCortes("ejemplo.png");
        m.singleThSinCortes("ejemplo.png");
    }

}

