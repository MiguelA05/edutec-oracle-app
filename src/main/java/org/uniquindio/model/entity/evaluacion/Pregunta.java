package org.uniquindio.model.entity.evaluacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Pregunta {
    private int idPregunta;
    private String texto;
    private String tiempo; // VARCHAR2(100) en DB
    private BigDecimal porcentaje; // NUMBER(5,2)
    private Integer tipoPreguntaId; // FK, puede ser null
    private Integer visibilidadId;  // FK, puede ser null
    private Integer nivelId;        // FK, puede ser null
    private Integer preguntaPadre;  // FK a Pregunta.id_pregunta (auto-referencia), puede ser null
    private Integer contenidoId;    // FK, puede ser null

    public Pregunta(int idPregunta, String texto, String tiempo, BigDecimal porcentaje,
                    Integer tipoPreguntaId, Integer visibilidadId, Integer nivelId,
                    Integer preguntaPadre, Integer contenidoId) {
        this.idPregunta = idPregunta;
        this.texto = texto;
        this.tiempo = tiempo;
        this.porcentaje = porcentaje;
        this.tipoPreguntaId = tipoPreguntaId;
        this.visibilidadId = visibilidadId;
        this.nivelId = nivelId;
        this.preguntaPadre = preguntaPadre;
        this.contenidoId = contenidoId;
    }
}
