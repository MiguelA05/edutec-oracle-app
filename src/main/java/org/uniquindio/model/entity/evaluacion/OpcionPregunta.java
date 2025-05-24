package org.uniquindio.model.entity.evaluacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpcionPregunta {
    private int id;
    private String respuesta;
    private char esCorrecta; // CHAR(1) 'S' o 'N'
    private int preguntaId;  // FK a Pregunta.id_pregunta
    private Integer secuencia; // NUEVO CAMPO: Para el orden en "Ordenar Conceptos"

    // Constructor original
    public OpcionPregunta(int id, String respuesta, char esCorrecta, int preguntaId) {
        this.id = id;
        this.respuesta = respuesta;
        this.esCorrecta = esCorrecta;
        this.preguntaId = preguntaId;
        this.secuencia = null; // Por defecto null si no se especifica
    }

    // Nuevo constructor que incluye secuencia
    public OpcionPregunta(int id, String respuesta, char esCorrecta, int preguntaId, Integer secuencia) {
        this.id = id;
        this.respuesta = respuesta;
        this.esCorrecta = esCorrecta;
        this.preguntaId = preguntaId;
        this.secuencia = secuencia;
    }
}