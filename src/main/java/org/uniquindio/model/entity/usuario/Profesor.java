package org.uniquindio.model.entity.usuario;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Profesor {
    private long cedula; // NUMBER(10) puede exceder int
    private String nombre;
    private String correo;
    private String contrasena;

    public Profesor(long cedula, String nombre, String correo, String contrasena) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }
}
