package org.uniquindio.model.entity.academico;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Contenido {
    private int idContenido;
    private String nombre;
    private int idUnidad; // FK a Unidad.id_unidad

    public Contenido(int idContenido, String nombre, int idUnidad) {
        this.idContenido = idContenido;
        this.nombre = nombre;
        this.idUnidad = idUnidad;
    }
}
