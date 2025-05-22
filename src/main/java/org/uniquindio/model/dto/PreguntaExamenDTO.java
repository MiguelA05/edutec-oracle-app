package org.uniquindio.model.dto;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
public class PreguntaExamenDTO {
    private final SimpleLongProperty idPregunta;
    private final SimpleStringProperty textoPregunta;
    private final SimpleStringProperty tipoPregunta; // Podría ser un objeto TipoPregunta si lo tienes
    private final SimpleDoubleProperty porcentaje;

    public PreguntaExamenDTO(Long idPregunta, String textoPregunta, String tipoPregunta, Double porcentaje) {
        this.idPregunta = new SimpleLongProperty(idPregunta);
        this.textoPregunta = new SimpleStringProperty(textoPregunta);
        this.tipoPregunta = new SimpleStringProperty(tipoPregunta);
        this.porcentaje = new SimpleDoubleProperty(porcentaje);
    }

    public long getIdPregunta() { return idPregunta.get(); }
    public SimpleLongProperty idPreguntaProperty() { return idPregunta; }
    public void setIdPregunta(long idPregunta) { this.idPregunta.set(idPregunta); }

    public String getTextoPregunta() { return textoPregunta.get(); }
    public SimpleStringProperty textoPreguntaProperty() { return textoPregunta; }
    public void setTextoPregunta(String textoPregunta) { this.textoPregunta.set(textoPregunta); }

    public String getTipoPregunta() { return tipoPregunta.get(); }
    public SimpleStringProperty tipoPreguntaProperty() { return tipoPregunta; }
    public void setTipoPregunta(String tipoPregunta) { this.tipoPregunta.set(tipoPregunta); }

    public double getPorcentaje() { return porcentaje.get(); }
    public SimpleDoubleProperty porcentajeProperty() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje.set(porcentaje); }
}