package org.uniquindio.model.entity.academico;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Unidad {
    private int idUnidad;
    private String nombre;
    private int idCurso; // FK a Curso.id_curso

    public Unidad(int idUnidad, String nombre, int idCurso) {
        this.idUnidad = idUnidad;
        this.nombre = nombre;
        this.idCurso = idCurso;
    }
}
