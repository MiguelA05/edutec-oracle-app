package org.uniquindio.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.uniquindio.model.entity.evaluacion.Examen;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime; // Importar LocalDateTime
import java.time.format.DateTimeFormatter; // Importar DateTimeFormatter
import java.util.Date;

@Getter
@Setter
public class ExamenDisponibleDTO {
    private int idExamen;
    private String nombreExamen;
    private String nombreCurso;
    private String fechaPresentacion;
    private String horaPresentacion; // Este se formateará desde LocalDateTime
    private Integer tiempoDuracion;
    private Integer numeroPreguntasAMostrar;
    private Examen examenOriginal;

    public ExamenDisponibleDTO(int idExamen, String nombreExamen, String nombreCurso,
                               Date fecha, // java.util.Date para la fecha
                               LocalDateTime hora, // java.time.LocalDateTime para la hora
                               Integer tiempoDuracion, Integer numeroPreguntasAMostrar,
                               Examen examenOriginal) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        // Usar DateTimeFormatter para LocalDateTime
        DateTimeFormatter timeFormatterForDTO = DateTimeFormatter.ofPattern("HH:mm");

        this.idExamen = idExamen;
        this.nombreExamen = nombreExamen;
        this.nombreCurso = nombreCurso;
        this.fechaPresentacion = (fecha != null) ? dateFormat.format(fecha) : "N/A"; // Correcto para java.util.Date

        // Corrección para formatear LocalDateTime
        this.horaPresentacion = (hora != null) ? hora.format(timeFormatterForDTO) : "N/A";

        this.tiempoDuracion = tiempoDuracion;
        this.numeroPreguntasAMostrar = numeroPreguntasAMostrar;
        this.examenOriginal = examenOriginal;
    }
}
