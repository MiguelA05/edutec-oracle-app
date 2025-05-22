package org.uniquindio.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DetalleRespuestaPreguntaDTO {
    private int preguntaExamenEstudianteId; // ID de la tabla PREGUNTAEXAMENESTUDIANTE
    private String textoPregunta;
    private String tipoPreguntaNombre; // Ej: "Opción Múltiple", "Verdadero/Falso"
    private String respuestaEstudiante; // Lo que el estudiante respondió (texto o ID de opción)
    private List<String> opcionesCorrectasTexto; // Texto de la(s) opción(es) correcta(s)
    private List<Integer> opcionesCorrectasId; // IDs de la(s) opción(es) correcta(s) (si aplica)
    private boolean esCorrectaLaRespuesta;
    private String feedbackEspecifico; // Retroalimentación específica para esta pregunta, si la hay

    // Constructor completo
    public DetalleRespuestaPreguntaDTO(int preguntaExamenEstudianteId, String textoPregunta, String tipoPreguntaNombre,
                                       String respuestaEstudiante, List<String> opcionesCorrectasTexto,
                                       List<Integer> opcionesCorrectasId, boolean esCorrectaLaRespuesta,
                                       String feedbackEspecifico) {
        this.preguntaExamenEstudianteId = preguntaExamenEstudianteId;
        this.textoPregunta = textoPregunta;
        this.tipoPreguntaNombre = tipoPreguntaNombre;
        this.respuestaEstudiante = respuestaEstudiante;
        this.opcionesCorrectasTexto = opcionesCorrectasTexto;
        this.opcionesCorrectasId = opcionesCorrectasId;
        this.esCorrectaLaRespuesta = esCorrectaLaRespuesta;
        this.feedbackEspecifico = feedbackEspecifico;
    }

}
