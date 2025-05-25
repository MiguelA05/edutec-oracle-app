package org.uniquindio.model.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PreguntaPresentacionDTO {
    private int preguntaExamenEstudianteId;
    private int idPreguntaOriginal; // Nuevo campo: ID original de la tabla PREGUNTA
    private String textoPregunta;
    private String tipoPreguntaNombre;
    private Integer tipoPreguntaId;
    private List<OpcionPresentacionDTO> opciones;
    private String tiempoSugerido;
    private Integer idPreguntaPadreOriginal;

    // Constructor actualizado
    public PreguntaPresentacionDTO(int preguntaExamenEstudianteId,
                                   int idPreguntaOriginal, // Añadido al constructor
                                   String textoPregunta,
                                   String tipoPreguntaNombre, Integer tipoPreguntaId,
                                   List<OpcionPresentacionDTO> opciones, String tiempoSugerido,
                                   Integer idPreguntaPadreOriginal) {
        this.preguntaExamenEstudianteId = preguntaExamenEstudianteId;
        this.idPreguntaOriginal = idPreguntaOriginal; // Asignar nuevo campo
        this.textoPregunta = textoPregunta;
        this.tipoPreguntaNombre = tipoPreguntaNombre;
        this.tipoPreguntaId = tipoPreguntaId;
        this.opciones = opciones;
        this.tiempoSugerido = tiempoSugerido;
        this.idPreguntaPadreOriginal = idPreguntaPadreOriginal;
    }

    // Getters y Setters son generados por Lombok

    public static class OpcionPresentacionDTO {
        private int idOpcion;
        private String textoOpcion;

        public OpcionPresentacionDTO(int idOpcion, String textoOpcion) {
            this.idOpcion = idOpcion;
            this.textoOpcion = textoOpcion;
        }

        public int getIdOpcion() { return idOpcion; }
        public String getTextoOpcion() { return textoOpcion; }
        public void setIdOpcion(int idOpcion) { this.idOpcion = idOpcion; }
        public void setTextoOpcion(String textoOpcion) { this.textoOpcion = textoOpcion; }
    }
}
