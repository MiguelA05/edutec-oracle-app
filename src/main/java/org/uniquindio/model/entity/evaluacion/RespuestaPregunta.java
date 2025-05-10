package org.uniquindio.model.entity.evaluacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RespuestaPregunta {
    private int id;
    private String respuestaDada;
    private Integer opcionPreguntaId; // FK, puede ser null si es respuesta abierta
    private int preguntaExamenEstudianteId; // FK

    public RespuestaPregunta(int id, String respuestaDada, Integer opcionPreguntaId, int preguntaExamenEstudianteId) {
        this.id = id;
        this.respuestaDada = respuestaDada;
        this.opcionPreguntaId = opcionPreguntaId;
        this.preguntaExamenEstudianteId = preguntaExamenEstudianteId;
    }
}
