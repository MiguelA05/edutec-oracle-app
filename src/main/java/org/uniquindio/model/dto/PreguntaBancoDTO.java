package org.uniquindio.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PreguntaBancoDTO {
    private int idPregunta;
    private String texto;
    private String tiempo; // VARCHAR2(100) en DB
    private BigDecimal porcentajeDefecto; // Porcentaje por defecto de la pregunta
    private String tipoPreguntaNombre;
    private String contenidoNombre; // Tema
    private String nivelNombre;
    private String visibilidadNombre;
    // Puedes añadir el ID original de la pregunta si necesitas referenciar la entidad completa
    // private Pregunta preguntaOriginal;


    public PreguntaBancoDTO(int idPregunta, String texto, String tiempo, BigDecimal porcentajeDefecto,
                            String tipoPreguntaNombre, String contenidoNombre, String nivelNombre, String visibilidadNombre) {
        this.idPregunta = idPregunta;
        this.texto = texto;
        this.tiempo = tiempo;
        this.porcentajeDefecto = porcentajeDefecto;
        this.tipoPreguntaNombre = tipoPreguntaNombre;
        this.contenidoNombre = contenidoNombre;
        this.nivelNombre = nivelNombre;
        this.visibilidadNombre = visibilidadNombre;
    }

    // Si necesitas la entidad original para pasarla al diálogo de edición
    // public Pregunta getPreguntaOriginal() { return preguntaOriginal; }
    // public void setPreguntaOriginal(Pregunta preguntaOriginal) { this.preguntaOriginal = preguntaOriginal; }
}
