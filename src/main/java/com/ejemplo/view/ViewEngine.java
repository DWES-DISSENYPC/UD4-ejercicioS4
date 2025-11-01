package com.ejemplo.view;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.io.Writer;

import jakarta.servlet.http.HttpServletResponse;

public class ViewEngine {

    private final TemplateEngine engine;

    public ViewEngine(TemplateEngine engine) {
        this.engine = engine;
    }

    public void render(String template, WebContext ctx, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        try (Writer w = resp.getWriter()) {
            engine.process(template, ctx, w);
        }
    }
}
