package org.uniquindio.model.entity.relacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetalleEstudianteCurso {
    private long estudianteCedula; // FK a Estudiante.cedula
    private int cursoId;           // FK a Curso.id_curso

    public DetalleEstudianteCurso(long estudianteCedula, int cursoId) {
        this.estudianteCedula = estudianteCedula;
        this.cursoId = cursoId;
    }

}
