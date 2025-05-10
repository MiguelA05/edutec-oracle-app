package org.uniquindio.model.entity.evaluacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PreguntaExamenEstudiante {
    private int id;
    private Integer tiempo; // Tiempo empleado en la pregunta
    private Integer presentacionExamenId; // FK
    private Integer detallePreguntaExamenExamenId; // Parte de FK compuesta
    private Integer detallePreguntaExamenPreguntaId; // Parte de FK compuesta

    public PreguntaExamenEstudiante(int id, Integer tiempo, Integer presentacionExamenId,
                                    Integer detallePreguntaExamenExamenId, Integer detallePreguntaExamenPreguntaId) {
        this.id = id;
        this.tiempo = tiempo;
        this.presentacionExamenId = presentacionExamenId;
        this.detallePreguntaExamenExamenId = detallePreguntaExamenExamenId;
        this.detallePreguntaExamenPreguntaId = detallePreguntaExamenPreguntaId;
    }
}
