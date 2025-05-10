package org.uniquindio.model.entity.academico;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class HorarioClase {
    private int idHorario;
    private Date horaInicio; // DATE en Oracle, puede ser java.time.LocalTime si solo importa la hora
    private Date horaFin;    // y se maneja la conversión, o java.time.LocalDateTime
    private int diaSemanaId; // FK a Dia_Semana.id

    public HorarioClase(int idHorario, Date horaInicio, Date horaFin, int diaSemanaId) {
        this.idHorario = idHorario;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.diaSemanaId = diaSemanaId;
    }

}
