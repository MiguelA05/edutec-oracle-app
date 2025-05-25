package org.uniquindio.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DetalleRespuestaPreguntaDTO {
    private int preguntaExamenEstudianteId;
    private String textoPregunta;
    private String tipoPreguntaNombre;
    private String respuestaEstudiante;
    private List<String> opcionesCorrectasTexto;
    private List<Integer> opcionesCorrectasId;
    private boolean esCorrectaLaRespuesta;
    private String feedbackEspecifico;
    private Integer idPreguntaPadreOriginal; // Nuevo campo

    // Constructor completo
    public DetalleRespuestaPreguntaDTO(int preguntaExamenEstudianteId, String textoPregunta, String tipoPreguntaNombre,
                                       String respuestaEstudiante, List<String> opcionesCorrectasTexto,
                                       List<Integer> opcionesCorrectasId, boolean esCorrectaLaRespuesta,
                                       String feedbackEspecifico, Integer idPreguntaPadreOriginal) { // Añadido al constructor
        this.preguntaExamenEstudianteId = preguntaExamenEstudianteId;
        this.textoPregunta = textoPregunta;
        this.tipoPreguntaNombre = tipoPreguntaNombre;
        this.respuestaEstudiante = respuestaEstudiante;
        this.opcionesCorrectasTexto = opcionesCorrectasTexto;
        this.opcionesCorrectasId = opcionesCorrectasId;
        this.esCorrectaLaRespuesta = esCorrectaLaRespuesta;
        this.feedbackEspecifico = feedbackEspecifico;
        this.idPreguntaPadreOriginal = idPreguntaPadreOriginal; // Asignar nuevo campo
    }

    public DetalleRespuestaPreguntaDTO(int peeId, String textoP, String tipoP, String respEst, List<String> optCorrectasTexto, List<Integer> optCorrectasId, boolean esCorrecta, String feedback) {
        this.preguntaExamenEstudianteId = peeId;
        this.textoPregunta = textoP;
        this.tipoPreguntaNombre = tipoP;
        this.respuestaEstudiante = respEst;
        this.opcionesCorrectasTexto = optCorrectasTexto;
        this.opcionesCorrectasId = optCorrectasId;
        this.esCorrectaLaRespuesta = esCorrecta;
        this.feedbackEspecifico = feedback;
        this.idPreguntaPadreOriginal = null; // Inicializar a null por defecto
    }
}
