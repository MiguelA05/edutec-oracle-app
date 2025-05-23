package org.uniquindio.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.uniquindio.model.entity.evaluacion.OpcionPregunta; // Asumiendo que OpcionPregunta es tu entidad para las opciones

import java.util.List;

@Getter
@Setter
public class PreguntaPresentacionDTO {
    private int preguntaExamenEstudianteId; // ID de la tabla PREGUNTAEXAMENESTUDIANTE (clave para registrar la respuesta)
    private String textoPregunta;
    private String tipoPreguntaNombre; // Ej: "Opción Múltiple", "Verdadero/Falso"
    private Integer tipoPreguntaId; // El ID del tipo de pregunta, útil para la lógica de la UI
    private List<OpcionPresentacionDTO> opciones; // Lista de opciones para la pregunta
    private String tiempoSugerido; // Tiempo sugerido para esta pregunta (si aplica)

    // Constructor
    public PreguntaPresentacionDTO(int preguntaExamenEstudianteId, String textoPregunta, String tipoPreguntaNombre, Integer tipoPreguntaId, List<OpcionPresentacionDTO> opciones, String tiempoSugerido) {
        this.preguntaExamenEstudianteId = preguntaExamenEstudianteId;
        this.textoPregunta = textoPregunta;
        this.tipoPreguntaNombre = tipoPreguntaNombre;
        this.tipoPreguntaId = tipoPreguntaId;
        this.opciones = opciones;
        this.tiempoSugerido = tiempoSugerido;
    }

    // Podrías necesitar un DTO interno para las opciones también, para no exponer la entidad completa
    // o para simplificar la información que se envía a la UI.
    public static class OpcionPresentacionDTO {
        private int idOpcion; // ID de la OpcionPregunta
        private String textoOpcion;
        // No incluir 'esCorrecta' aquí, ya que el estudiante no debería verla durante el examen.

        public OpcionPresentacionDTO(int idOpcion, String textoOpcion) {
            this.idOpcion = idOpcion;
            this.textoOpcion = textoOpcion;
        }

        public int getIdOpcion() {
            return idOpcion;
        }

        public String getTextoOpcion() {
            return textoOpcion;
        }

        public void setIdOpcion(int idOpcion) {
            this.idOpcion = idOpcion;
        }

        public void setTextoOpcion(String textoOpcion) {
            this.textoOpcion = textoOpcion;
        }
    }
}
