package org.uniquindio.model.entity.evaluacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Examen {
    private int id;
    private Integer tiempo; // Tiempo en minutos, por ejemplo
    private Integer numeroPreguntas;
    private Date fecha;
    private Date hora; // Similar a HorarioClase.hora_inicio
    private BigDecimal calificacionMinAprobatoria; // NUMBER(5,2)
    private BigDecimal pesoCurso; // NUMBER(5,2)
    private String descripcion;
    private Integer creacionId; // FK
    private Integer categoriaId; // FK
    private Integer cursoId; // FK

    public Examen(int id, Integer tiempo, Integer numeroPreguntas, Date fecha, Date hora,
                  BigDecimal calificacionMinAprobatoria, BigDecimal pesoCurso, String descripcion,
                  Integer creacionId, Integer categoriaId, Integer cursoId) {
        this.id = id;
        this.tiempo = tiempo;
        this.numeroPreguntas = numeroPreguntas;
        this.fecha = fecha;
        this.hora = hora;
        this.calificacionMinAprobatoria = calificacionMinAprobatoria;
        this.pesoCurso = pesoCurso;
        this.descripcion = descripcion;
        this.creacionId = creacionId;
        this.categoriaId = categoriaId;
        this.cursoId = cursoId;
    }
}