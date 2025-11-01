package com.ejemplo.web;

import com.ejemplo.config.ThymeleafConfig;
import com.ejemplo.modelo.Usuario;
import com.ejemplo.services.GestionFicherosService;
import com.ejemplo.view.ViewEngine;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@MultipartConfig
@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private transient JakartaServletWebApplication app;
    private transient ViewEngine view;

    @Override
    public void init() throws ServletException {
        this.app = JakartaServletWebApplication.buildApplication(getServletContext());
        TemplateEngine engine = ThymeleafConfig.engine(app);
        this.view = new ViewEngine(engine);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        Usuario u = new Usuario(
                req.getParameter("usuario"),
                req.getParameter("contraseña")
               
        );

        IWebExchange exchange = this.app.buildExchange(req, resp);
        WebContext ctx = new WebContext(exchange, exchange.getLocale());
        ctx.setVariable("usuario", u);
 
        String mensaje = "";
        
        boolean fichero_ok = GestionFicherosService.toMayus(req); 

        mensaje = fichero_ok ? "Se ha enviado el fichero correctamente" : "Error con el fichero";
        ctx.setVariable("msj", mensaje);

        if(fichero_ok) this.view.render("registro-ok", ctx, resp);
        else           this.view.render("registro-not-ok", ctx, resp);
        
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {        
        // Acceso directo: redirigimos al formulario
        resp.sendRedirect(req.getContextPath() + "/index.html");
    }
    
}
