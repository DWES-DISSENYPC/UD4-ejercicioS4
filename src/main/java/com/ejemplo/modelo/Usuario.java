package com.ejemplo.modelo;

public class Usuario {
    private final String usuario;
    private final String contraseña;

    public Usuario (String usuario, String contraseña) {

        this.usuario = usuario;
        this.contraseña = contraseña;

    }

    public String getNombre() { return usuario; }
    public String getContraseña() { return contraseña;}
}
