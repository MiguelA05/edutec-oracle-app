package org.uniquindio.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class ExamenProfesorDTO {
    private String id;
    private String nombre;
    private String idCurso;
    private String nombreCurso;
    private Date fecha;
    private LocalTime hora;
    private int duracion;

    public ExamenProfesorDTO(String id, String nombre, String idCurso, String nombreCurso, Date fecha, LocalTime hora, int duracion) {
        this.id = id;
        this.nombre = nombre;
        this.idCurso = idCurso;
        this.nombreCurso = nombreCurso;
        this.fecha = fecha;
        this.hora = hora;
        this.duracion = duracion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getIdCurso() { return idCurso; }
    public void setIdCurso(String idCurso) { this.idCurso = idCurso; }
    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public String getFechaFormateada() {
        return fecha != null
                ? new java.sql.Date(fecha.getTime()).toLocalDate()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "N/A";
    }
    public String getHoraFormateada() {
        return hora != null ? hora.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
    }
    @Override
    public String toString() { return nombre + " (" + nombreCurso + ")"; }
}
