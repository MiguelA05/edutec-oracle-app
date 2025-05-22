package org.uniquindio.model.dto;

import java.util.List;

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

    // Getters
    public int getPreguntaExamenEstudianteId() {
        return preguntaExamenEstudianteId;
    }

    public String getTextoPregunta() {
        return textoPregunta;
    }

    public String getTipoPreguntaNombre() {
        return tipoPreguntaNombre;
    }

    public String getRespuestaEstudiante() {
        return respuestaEstudiante;
    }

    public List<String> getOpcionesCorrectasTexto() {
        return opcionesCorrectasTexto;
    }

    public List<Integer> getOpcionesCorrectasId() {
        return opcionesCorrectasId;
    }

    public boolean isEsCorrectaLaRespuesta() {
        return esCorrectaLaRespuesta;
    }

    public String getFeedbackEspecifico() {
        return feedbackEspecifico;
    }

    // Setters (opcionales, si se necesita modificar después de la creación)
    public void setPreguntaExamenEstudianteId(int preguntaExamenEstudianteId) {
        this.preguntaExamenEstudianteId = preguntaExamenEstudianteId;
    }

    public void setTextoPregunta(String textoPregunta) {
        this.textoPregunta = textoPregunta;
    }

    public void setTipoPreguntaNombre(String tipoPreguntaNombre) {
        this.tipoPreguntaNombre = tipoPreguntaNombre;
    }

    public void setRespuestaEstudiante(String respuestaEstudiante) {
        this.respuestaEstudiante = respuestaEstudiante;
    }

    public void setOpcionesCorrectasTexto(List<String> opcionesCorrectasTexto) {
        this.opcionesCorrectasTexto = opcionesCorrectasTexto;
    }

    public void setOpcionesCorrectasId(List<Integer> opcionesCorrectasId) {
        this.opcionesCorrectasId = opcionesCorrectasId;
    }

    public void setEsCorrectaLaRespuesta(boolean esCorrectaLaRespuesta) {
        this.esCorrectaLaRespuesta = esCorrectaLaRespuesta;
    }

    public void setFeedbackEspecifico(String feedbackEspecifico) {
        this.feedbackEspecifico = feedbackEspecifico;
    }
}
