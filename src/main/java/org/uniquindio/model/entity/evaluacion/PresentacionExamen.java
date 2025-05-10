package org.uniquindio.model.entity.evaluacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PresentacionExamen {
    private int id;
    private BigDecimal calificacion; // NUMBER(5,2)
    private Integer respuestasCorrectas;
    private Integer respuestasIncorrectas;
    private Integer tiempo; // Tiempo empleado en segundos, por ejemplo
    private String direccionIp;
    private Integer examenId; // FK
    private Long estudianteCedula; // FK

    public PresentacionExamen(int id, BigDecimal calificacion, Integer respuestasCorrectas,
                              Integer respuestasIncorrectas, Integer tiempo, String direccionIp,
                              Integer examenId, Long estudianteCedula) {
        this.id = id;
        this.calificacion = calificacion;
        this.respuestasCorrectas = respuestasCorrectas;
        this.respuestasIncorrectas = respuestasIncorrectas;
        this.tiempo = tiempo;
        this.direccionIp = direccionIp;
        this.examenId = examenId;
        this.estudianteCedula = estudianteCedula;
    }

}
