package org.uniquindio.model.entity.evaluacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Pregunta {
    private int idPregunta;
    private String texto;
    private Integer tiempoEstimado;
    private BigDecimal porcentaje;
    private Integer tipoPreguntaId; // FK, puede ser null
    private Integer visibilidadId;  // FK, puede ser null
    private Integer nivelId;        // FK, puede ser null
    private Integer preguntaPadre;  // FK a Pregunta.id_pregunta (auto-referencia), puede ser null
    private Integer contenidoId;    // FK, puede ser null
    private int creadorCedulaProfesor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private String usuarioModificacion;

    public Pregunta(int idPregunta, String texto, Integer tiempo, BigDecimal porcentaje,
                    Integer tipoPreguntaId, Integer visibilidadId, Integer nivelId,
                    Integer preguntaPadre, Integer contenidoId, int creadorCedulaProfesor, LocalDateTime fechaCreacion, LocalDateTime fechaModificacion, String usuarioModificacion) {
        this.idPregunta = idPregunta;
        this.texto = texto;
        this.tiempoEstimado = tiempo;
        this.porcentaje = porcentaje;
        this.tipoPreguntaId = tipoPreguntaId;
        this.visibilidadId = visibilidadId;
        this.nivelId = nivelId;
        this.preguntaPadre = preguntaPadre;
        this.contenidoId = contenidoId;
        this.creadorCedulaProfesor = creadorCedulaProfesor;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        this.usuarioModificacion = usuarioModificacion;
    }
}
