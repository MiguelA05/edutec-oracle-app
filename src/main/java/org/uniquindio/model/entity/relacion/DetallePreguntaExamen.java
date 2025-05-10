package org.uniquindio.model.entity.relacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class DetallePreguntaExamen {
    private int examenId;    // FK a Examen.id
    private int preguntaId;  // FK a Pregunta.id_pregunta
    private BigDecimal porcentaje; // NUMBER(5,2)

    public DetallePreguntaExamen(int examenId, int preguntaId, BigDecimal porcentaje) {
        this.examenId = examenId;
        this.preguntaId = preguntaId;
        this.porcentaje = porcentaje;
    }
}
