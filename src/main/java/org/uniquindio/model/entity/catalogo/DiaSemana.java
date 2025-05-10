package org.uniquindio.model.entity.catalogo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiaSemana {
    private int id;
    private String nombre;
    private String descripcion;

    public DiaSemana(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

}