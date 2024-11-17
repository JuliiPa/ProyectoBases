package org.example.controller;

import org.example.model.Usuario;
import java.sql.SQLException;

public class UsuarioController {
    private Usuario usuarioActual; // Usuario autenticado actualmente

    public boolean login(String nombreUsuario, int documento) throws SQLException {
        usuarioActual = Usuario.obtenerUsuario(nombreUsuario, documento);
        return usuarioActual != null;
    }

    public int getEstudianteId() {
        return usuarioActual != null ? usuarioActual.getEstudianteId() : -1;
    }
}
