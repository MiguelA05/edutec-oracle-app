package org.uniquindio.model.entity.relacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetalleCursoHorario {
    private int cursoId;           // FK a Curso.id_curso
    private int horarioClaseId;    // FK a Horario_Clase.id_horario

    public DetalleCursoHorario(int cursoId, int horarioClaseId) {
        this.cursoId = cursoId;
        this.horarioClaseId = horarioClaseId;
    }
}
