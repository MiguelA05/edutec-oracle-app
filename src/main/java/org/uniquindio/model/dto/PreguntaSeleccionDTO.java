// En org.uniquindio.model.dto.PreguntaSeleccionDTO.java
package org.uniquindio.model.dto;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class PreguntaSeleccionDTO {
    private final SimpleLongProperty idPregunta;
    private final SimpleStringProperty textoPregunta;
    private final SimpleStringProperty tipoPreguntaNombre; // Nombre del tipo
    private final SimpleStringProperty nivelNombre;      // Nombre del nivel
    private final BooleanProperty seleccionado;
    private final DoubleProperty porcentajeEnExamen; // Porcentaje que tendrá ESTA pregunta EN ESTE examen

    public PreguntaSeleccionDTO(long idPregunta, String textoPregunta, String tipoPreguntaNombre, String nivelNombre, double porcentajeInicial) {
        this.idPregunta = new SimpleLongProperty(idPregunta);
        this.textoPregunta = new SimpleStringProperty(textoPregunta);
        this.tipoPreguntaNombre = new SimpleStringProperty(tipoPreguntaNombre);
        this.nivelNombre = new SimpleStringProperty(nivelNombre);
        this.seleccionado = new SimpleBooleanProperty(false);
        this.porcentajeEnExamen = new SimpleDoubleProperty(porcentajeInicial);
    }

    public long getIdPregunta() { return idPregunta.get(); }
    public SimpleLongProperty idPreguntaProperty() { return idPregunta; }

    public String getTextoPregunta() { return textoPregunta.get(); }
    public SimpleStringProperty textoPreguntaProperty() { return textoPregunta; }

    public String getTipoPreguntaNombre() { return tipoPreguntaNombre.get(); }
    public SimpleStringProperty tipoPreguntaNombreProperty() { return tipoPreguntaNombre; }

    public String getNivelNombre() { return nivelNombre.get(); }
    public SimpleStringProperty nivelNombreProperty() { return nivelNombre; }

    public boolean isSeleccionado() { return seleccionado.get(); }
    public BooleanProperty seleccionadoProperty() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado.set(seleccionado); }

    public double getPorcentajeEnExamen() { return porcentajeEnExamen.get(); }
    public DoubleProperty porcentajeEnExamenProperty() { return porcentajeEnExamen; }
    public void setPorcentajeEnExamen(double porcentajeEnExamen) { this.porcentajeEnExamen.set(porcentajeEnExamen); }
}