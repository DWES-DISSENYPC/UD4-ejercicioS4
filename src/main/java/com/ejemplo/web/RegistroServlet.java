package com.ejemplo.web;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import com.ejemplo.services.GestionFicherosService;

@WebServlet("/registro")
@MultipartConfig
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        boolean ok = GestionFicherosService.procesarFichero(req, ".txt");

        if (ok) {
            resp.sendRedirect("registro-ok.html");
        } else {
            resp.sendRedirect("registro-not-ok.html");
        }
    }
}
