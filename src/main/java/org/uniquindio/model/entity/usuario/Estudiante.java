package org.uniquindio.model.entity.usuario;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Estudiante {
    private long cedula; // NUMBER(10)
    private String nombre;
    private String correo;
    private String contrasena;

    public Estudiante(long cedula, String nombre, String correo, String contrasena) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }
}
