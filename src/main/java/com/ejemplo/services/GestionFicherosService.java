package com.ejemplo.services;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class GestionFicherosService {

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final String CREDENCIALES_PATH = "/users.txt";

    /** Carga usuarios autorizados del archivo de recursos */
    public static Map<String, String> cargarCredenciales() {

        System.out.println("CREDENCIALES");

        Map<String, String> credenciales = new HashMap<>();
        try (InputStream in = GestionFicherosService.class.getResourceAsStream(CREDENCIALES_PATH);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length == 2)
                    credenciales.put(partes[0].trim(), partes[1].trim());

            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error cargando credenciales: " + e.getMessage());
        }
        return credenciales;
    }

    /** Comprueba usuario y contraseña */
    public static boolean autenticarUsuario(String usuario, String contrasena) {
        System.out.println("AUTENTIFICACION");

        Map<String, String> credenciales = cargarCredenciales();
        return credenciales.containsKey(usuario)
                && credenciales.get(usuario).equals(contrasena);
    }

    /** Obtiene el nombre del fichero original */
    private static String getNombreFichero(Part fichero) {
        return Paths.get(fichero.getSubmittedFileName()).getFileName().toString();
    }

    /** Valida extensión */
    private static boolean validarExtension(String nombre, String extEsperada) {
        return nombre != null && nombre.toLowerCase().endsWith(extEsperada.toLowerCase());
    }

    /** Valida tamaño leyendo el flujo (segura con cualquier contenedor) */
    private static boolean validarTamanyo(Part fichero) {
        long total = 0;
        try (InputStream in = fichero.getInputStream()) {
            byte[] buf = new byte[8192];
            int leidos;
            while ((leidos = in.read(buf)) != -1) {
                total += leidos;
                if (total > MAX_FILE_SIZE) {
                    System.out.println("Archivo demasiado grande (" + total + " bytes)");
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo fichero: " + e.getMessage());
            return false;
        }
        return true;
    }

    private static String leerCampoTexto(HttpServletRequest req, String nombreCampo)
            throws IOException, ServletException {
        Part parte = req.getPart(nombreCampo);
        if (parte == null) {
            System.out.println("Campo '" + nombreCampo + "' no encontrado en la petición multipart.");
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(parte.getInputStream()))) {
            String valor = reader.readLine();
            return valor != null ? valor.trim() : null;
        }
    }

    /** Procesa fichero: lo guarda en mayúsculas con nombre único */
    public static boolean procesarFichero(HttpServletRequest req, String extEsperada) {

        try {

            System.err.println("=== ENTRANDO A procesarFichero ===");

            String usuario = leerCampoTexto(req, "usuario");
            String contrasenya = leerCampoTexto(req, "contrasenya");

            System.out.println("Usuario: " + usuario);
            System.out.println("Contraseña: " + contrasenya);

            if (!autenticarUsuario(usuario, contrasenya)) {
                System.err.println("Usuario no autorizado.");
                return false;
            }

            Part fichero = req.getPart("fichero");
            if (fichero == null) {
                System.err.println("No se recibió fichero.");
                return false;
            }

            String nombreFichero = getNombreFichero(fichero);

            if (!validarExtension(nombreFichero, extEsperada)) {
                System.err.println("Extensión no válida.");
                return false;
            }

            if (!validarTamanyo(fichero)) {
                System.err.println("Fichero excede tamaño máximo.");
                return false;
            }

            // Ruta base
            Path salidaBase = Paths.get("E:", "web_file", "procesados");
            Files.createDirectories(salidaBase);

            String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String nombreFinal = nombreFichero.replace(extEsperada, "_" + fecha + extEsperada);
            Path rutaDestino = salidaBase.resolve(nombreFinal);

            // Releer el inputStream (nuevo flujo)
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fichero.getInputStream()));
                    BufferedWriter bw = Files.newBufferedWriter(rutaDestino)) {

                String linea;
                while ((linea = br.readLine()) != null) {
                    //System.out.println(linea); // mostrar en consola
                    bw.write(linea.toUpperCase());
                    bw.newLine();
                }
            }

            System.out.println("Archivo procesado OK: " + rutaDestino);
            return true;

        } catch (IOException | ServletException e) {
            System.err.println("Error en procesarFichero: " + e.getMessage());
            return false;
        }
    }
}
