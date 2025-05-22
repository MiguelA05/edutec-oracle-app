package org.uniquindio.model.dto;

import java.math.BigDecimal;
import java.util.List;
// Se importa el nuevo DTO
import lombok.Getter;
import lombok.Setter;
import org.uniquindio.model.dto.DetalleRespuestaPreguntaDTO;

@Getter
@Setter
public class ResultadoExamenDTO {
    private BigDecimal calificacionFinal;
    private int respuestasCorrectas;
    private int respuestasIncorrectas;
    private int preguntasRespondidas; // Podría ser diferente al total si hubo tiempo límite
    private int totalPreguntasExamen;
    private String mensajeFeedbackGeneral; // Ej: "¡Felicidades, aprobaste!" o "Necesitas repasar los temas X, Y"
    private String nombreExamen;
    private String fechaPresentacion; // Formateada como String

    // Se descomenta y se define el tipo para la lista de detalles de respuestas
    private List<DetalleRespuestaPreguntaDTO> detalleRespuestas;

    // Constructor actualizado para incluir detalleRespuestas
    public ResultadoExamenDTO(BigDecimal calificacionFinal, int respuestasCorrectas, int respuestasIncorrectas,
                              int preguntasRespondidas, int totalPreguntasExamen,
                              String mensajeFeedbackGeneral, String nombreExamen, String fechaPresentacion,
                              List<DetalleRespuestaPreguntaDTO> detalleRespuestas) {
        this.calificacionFinal = calificacionFinal;
        this.respuestasCorrectas = respuestasCorrectas;
        this.respuestasIncorrectas = respuestasIncorrectas;
        this.preguntasRespondidas = preguntasRespondidas;
        this.totalPreguntasExamen = totalPreguntasExamen;
        this.mensajeFeedbackGeneral = mensajeFeedbackGeneral;
        this.nombreExamen = nombreExamen;
        this.fechaPresentacion = fechaPresentacion;
        this.detalleRespuestas = detalleRespuestas; // Se asigna la lista
    }

    // Constructor anterior (opcional, si quieres mantenerlo por retrocompatibilidad o para casos sin detalle)
    public ResultadoExamenDTO(BigDecimal calificacionFinal, int respuestasCorrectas, int respuestasIncorrectas,
                              int preguntasRespondidas, int totalPreguntasExamen,
                              String mensajeFeedbackGeneral, String nombreExamen, String fechaPresentacion) {
        this(calificacionFinal, respuestasCorrectas, respuestasIncorrectas, preguntasRespondidas,
                totalPreguntasExamen, mensajeFeedbackGeneral, nombreExamen, fechaPresentacion, null); // Llama al constructor principal con detalle null
    }
}
