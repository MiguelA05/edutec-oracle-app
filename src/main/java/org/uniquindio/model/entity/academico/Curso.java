package org.uniquindio.model.entity.academico;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Curso {
    private int idCurso;
    private String nombre;
    private String descripcion;
    private long profesorCedula; // FK a Profesor.cedula

    public Curso(int idCurso, String nombre, String descripcion, long profesorCedula) {
        this.idCurso = idCurso;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.profesorCedula = profesorCedula;
    }
}
