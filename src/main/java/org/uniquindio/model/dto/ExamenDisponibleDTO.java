package org.uniquindio.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.uniquindio.model.entity.evaluacion.Examen; // Para tener una referencia al examen completo si es necesario

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class ExamenDisponibleDTO {
    private int idExamen;
    private String nombreExamen; // O descripción si se usa como título
    private String nombreCurso; // Nombre del curso al que pertenece
    private String fechaPresentacion; // Formateada
    private String horaPresentacion; // Formateada
    private Integer tiempoDuracion; // En minutos
    private Integer numeroPreguntasAMostrar;
    private Examen examenOriginal; // Referencia al objeto Examen completo para pasarlo al iniciar

    public ExamenDisponibleDTO(int idExamen, String nombreExamen, String nombreCurso,
                               Date fecha, Date hora, Integer tiempoDuracion, Integer numeroPreguntasAMostrar,
                               Examen examenOriginal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        this.idExamen = idExamen;
        this.nombreExamen = nombreExamen;
        this.nombreCurso = nombreCurso;
        this.fechaPresentacion = (fecha != null) ? dateFormat.format(fecha) : "N/A";
        this.horaPresentacion = (hora != null) ? timeFormat.format(hora) : "N/A";
        this.tiempoDuracion = tiempoDuracion;
        this.numeroPreguntasAMostrar = numeroPreguntasAMostrar;
        this.examenOriginal = examenOriginal;
    }
}
