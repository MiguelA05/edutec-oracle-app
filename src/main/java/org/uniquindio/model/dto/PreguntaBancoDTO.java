package org.uniquindio.model.dto;

import java.math.BigDecimal;

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

    // Getters (y Setters si son necesarios, aunque los DTOs suelen ser inmutables o solo con getters para mostrar)
    public int getIdPregunta() { return idPregunta; }
    public String getTexto() { return texto; }
    public String getTiempo() { return tiempo; }
    public BigDecimal getPorcentajeDefecto() { return porcentajeDefecto; }
    public String getTipoPreguntaNombre() { return tipoPreguntaNombre; }
    public String getContenidoNombre() { return contenidoNombre; }
    public String getNivelNombre() { return nivelNombre; }
    public String getVisibilidadNombre() { return visibilidadNombre; }

    // Si necesitas la entidad original para pasarla al diálogo de edición
    // public Pregunta getPreguntaOriginal() { return preguntaOriginal; }
    // public void setPreguntaOriginal(Pregunta preguntaOriginal) { this.preguntaOriginal = preguntaOriginal; }
}
