package com.ejemplo.services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class GestionFicherosService {

    public static boolean toMayus(HttpServletRequest req) {

        try {

            Part fichero = req.getPart("fichero");
            Path directorioBase = Paths.get("E:\\Mis Documentos\\Documentos\\ESTUDIOS DAW 2\\DESARROLLO WEB ENTORNO SERVIDOR\\1º EVALUACION\\UNIDAD 4\\ficheros");
            String nombreUsuario = req.getParameter("usuario");
            Path directorioUsuario = directorioBase.resolve(nombreUsuario);

            if(!Files.isDirectory(directorioUsuario)) Files.createDirectories(directorioUsuario);

            String nombreFichero = Paths.get(fichero.getSubmittedFileName()).getFileName().toString();
            if(!nombreFichero.endsWith(".txt")) return false;
            int posPunto = nombreFichero.lastIndexOf(".");
            String mombreFinalFichero = nombreFichero.substring(0, posPunto) + "_mayus" + nombreFichero.substring(posPunto);
            Path rutaFinal = directorioUsuario.resolve(mombreFinalFichero);

            if ( !Files.isRegularFile(rutaFinal)) Files.createFile(rutaFinal);

            BufferedWriter bw = new BufferedWriter(new FileWriter(rutaFinal.toString()));
            BufferedReader br = new BufferedReader(new InputStreamReader(fichero.getInputStream()));

            String linea;
            while((linea = br.readLine())!=null ) {
                bw.write(linea.toUpperCase());
                bw.newLine();
            }
            bw.close();
            br.close();

        } catch(IOException e){

            System.out.println("Error de lectura/escritura");
            return false;

        } catch (ServletException e) {

            System.out.println("Error del servidor");
            return false;
        }

        return true;

    }
    
}
