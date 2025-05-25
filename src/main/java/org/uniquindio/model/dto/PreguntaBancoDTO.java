package org.uniquindio.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PreguntaBancoDTO {
    private int idPregunta;
    private String texto;
    private String tiempo;
    private BigDecimal porcentajeDefecto;
    private String tipoPreguntaNombre;
    private String contenidoNombre;
    private String nivelNombre;
    private String visibilidadNombre;
    private Integer idPreguntaPadre; // Nuevo campo

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
        this.idPreguntaPadre = null; // Inicializar a null por defecto
    }

    // Constructor que incluye idPreguntaPadre (opcional, si la consulta lo devuelve directamente)
    public PreguntaBancoDTO(int idPregunta, String texto, String tiempo, BigDecimal porcentajeDefecto,
                            String tipoPreguntaNombre, String contenidoNombre, String nivelNombre,
                            String visibilidadNombre, Integer idPreguntaPadre) {
        this(idPregunta, texto, tiempo, porcentajeDefecto, tipoPreguntaNombre, contenidoNombre, nivelNombre, visibilidadNombre);
        this.idPreguntaPadre = idPreguntaPadre;
    }
}
