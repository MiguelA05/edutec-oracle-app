package org.uniquindio.model.dto;

import java.math.BigDecimal;
import java.util.List;
// Se importa el nuevo DTO
import org.uniquindio.model.dto.DetalleRespuestaPreguntaDTO;

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


    // Getters
    public BigDecimal getCalificacionFinal() {
        return calificacionFinal;
    }

    public int getRespuestasCorrectas() {
        return respuestasCorrectas;
    }

    public int getRespuestasIncorrectas() {
        return respuestasIncorrectas;
    }

    public int getPreguntasRespondidas() {
        return preguntasRespondidas;
    }

    public int getTotalPreguntasExamen() {
        return totalPreguntasExamen;
    }

    public String getMensajeFeedbackGeneral() {
        return mensajeFeedbackGeneral;
    }

    public String getNombreExamen() {
        return nombreExamen;
    }

    public String getFechaPresentacion() {
        return fechaPresentacion;
    }

    // Getter para la lista de detalles
    public List<DetalleRespuestaPreguntaDTO> getDetalleRespuestas() {
        return detalleRespuestas;
    }

    // Setters (si son necesarios)
    public void setCalificacionFinal(BigDecimal calificacionFinal) {
        this.calificacionFinal = calificacionFinal;
    }

    public void setRespuestasCorrectas(int respuestasCorrectas) {
        this.respuestasCorrectas = respuestasCorrectas;
    }

    public void setRespuestasIncorrectas(int respuestasIncorrectas) {
        this.respuestasIncorrectas = respuestasIncorrectas;
    }

    public void setPreguntasRespondidas(int preguntasRespondidas) {
        this.preguntasRespondidas = preguntasRespondidas;
    }

    public void setTotalPreguntasExamen(int totalPreguntasExamen) {
        this.totalPreguntasExamen = totalPreguntasExamen;
    }

    public void setMensajeFeedbackGeneral(String mensajeFeedbackGeneral) {
        this.mensajeFeedbackGeneral = mensajeFeedbackGeneral;
    }

    public void setNombreExamen(String nombreExamen) {
        this.nombreExamen = nombreExamen;
    }

    public void setFechaPresentacion(String fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    // Setter para la lista de detalles
    public void setDetalleRespuestas(List<DetalleRespuestaPreguntaDTO> detalleRespuestas) {
        this.detalleRespuestas = detalleRespuestas;
    }
}
