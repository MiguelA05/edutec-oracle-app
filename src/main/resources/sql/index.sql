-- ====================================================================
-- SCRIPT DE CREACIÓN DE ÍNDICES - BASE DE DATOS "EDUTEC"
-- Versión: 2.2 (Corregido para SQL*Plus y Complementado)
-- Fecha: 22 de Mayo de 2025
-- Descripción: Crea índices personalizados y para claves foráneas
--              en el tablespace TS_INDICES.
--              NOTA: Se asume que los índices de Clave Primaria ya fueron
--              creados en TS_INDICES durante la creación de tablas.
-- ====================================================================

-- Habilitar salida del servidor para ver los mensajes
BEGIN
    DBMS_OUTPUT.ENABLE(buffer_size => NULL);
END;
/

-- --------------------------------------------------------
-- 1. CREACIÓN DE ÍNDICES PERSONALIZADOS EN TABLESPACE TS_INDICES
-- --------------------------------------------------------
-- Estos índices mejoran el rendimiento de consultas comunes.

-- Índice para búsquedas por nombre de curso
CREATE INDEX idx_curso_nombre ON Curso(nombre) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_curso_nombre creado.');
END;
/

-- Índice para búsquedas por nombre de estudiante
CREATE INDEX idx_estudiante_nombre ON Estudiante(nombre) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_estudiante_nombre creado.');
END;
/

-- Índice para búsquedas por correo y contraseña de estudiante (para login)
CREATE INDEX idx_estudiante_login ON Estudiante(correo, contrasena) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_estudiante_login creado.');
END;
/

-- Índice para búsquedas por nombre de profesor
CREATE INDEX idx_profesor_nombre ON Profesor(nombre) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_profesor_nombre creado.');
END;
/

-- Índice para búsquedas por correo y contraseña de profesor (para login)
CREATE INDEX idx_profesor_login ON Profesor(correo, contrasena) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_profesor_login creado.');
END;
/

-- Índice para búsquedas de exámenes por fecha
CREATE INDEX idx_examen_fecha ON Examen(fecha) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_examen_fecha creado.');
END;
/

-- Índice para búsquedas de presentaciones de examen por estudiante (cubre FK)
CREATE INDEX idx_presentacion_estudiante ON PresentacionExamen(estudiante_cedula) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_presentacion_estudiante creado.');
END;
/

-- Índice para búsquedas de opciones por pregunta (cubre FK)
CREATE INDEX idx_opcion_pregunta ON OpcionPregunta(pregunta_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_opcion_pregunta creado.');
END;
/

-- Índice para búsquedas de respuestas por opción seleccionada (cubre FK)
CREATE INDEX idx_respuesta_opcion ON RespuestaPregunta(opcion_pregunta_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_respuesta_opcion creado.');
END;
/

COMMIT;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Sección 1: Índices Personalizados Completada ---');
END;
/

-- --------------------------------------------------------
-- 2. MOVIMIENTO DE ÍNDICES DE CLAVE PRIMARIA A TS_INDICES
-- --------------------------------------------------------
-- ESTA SECCIÓN SE MANTIENE COMENTADA.
-- Los índices de Clave Primaria ya se crean en TS_INDICES
-- mediante la cláusula "USING INDEX TABLESPACE TS_INDICES"
-- en las sentencias CREATE TABLE.
-- --------------------------------------------------------

-- --------------------------------------------------------
-- 3. CREACIÓN DE ÍNDICES PARA CLAVES FORÁNEAS EN TS_INDICES
-- --------------------------------------------------------
-- Se crean índices para columnas de Clave Foránea que no estén
-- ya cubiertas por un índice de PK o un índice personalizado anterior.

-- Tabla: Curso
CREATE INDEX idx_fk_curso_profesor ON Curso(profesor_cedula) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_curso_profesor creado.');
END;
/

-- Tabla: Horario_Clase
CREATE INDEX idx_fk_horario_dia_semana ON Horario_Clase(dia_semana_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_horario_dia_semana creado.');
END;
/

-- Tabla: Unidad
CREATE INDEX idx_fk_unidad_curso ON Unidad(id_curso) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_unidad_curso creado.');
END;
/

-- Tabla: Contenido
CREATE INDEX idx_fk_contenido_unidad ON Contenido(id_unidad) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_contenido_unidad creado.');
END;
/

-- Tabla: DetalleEstudianteCurso
-- PK es (estudiante_cedula, curso_id). Índices individuales para FKs.
CREATE INDEX idx_fk_dec_estudiante_cedula ON DetalleEstudianteCurso(estudiante_cedula) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_dec_estudiante_cedula creado.');
END;
/
CREATE INDEX idx_fk_dec_curso_id ON DetalleEstudianteCurso(curso_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_dec_curso_id creado.');
END;
/

-- Tabla: DetalleCursoHorario (CORREGIDO de DetalleUnidadHorario)
-- PK es (curso_id, horario_clase_id). Índices individuales para FKs.
CREATE INDEX idx_fk_dch_curso_id ON DetalleCursoHorario(curso_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_dch_curso_id creado.');
END;
/
CREATE INDEX idx_fk_dch_horario_clase_id ON DetalleCursoHorario(horario_clase_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_dch_horario_clase_id creado.');
END;
/

-- Tabla: Pregunta
CREATE INDEX idx_fk_pregunta_tipo ON Pregunta(tipo_pregunta_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_tipo creado.');
END;
/
CREATE INDEX idx_fk_pregunta_visibilidad ON Pregunta(visibilidad_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_visibilidad creado.');
END;
/
CREATE INDEX idx_fk_pregunta_nivel ON Pregunta(nivel_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_nivel creado.');
END;
/
CREATE INDEX idx_fk_pregunta_padre ON Pregunta(pregunta_padre) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_padre creado.');
END;
/
CREATE INDEX idx_fk_pregunta_contenido ON Pregunta(contenido_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_contenido creado.');
END;
/
-- Índice AÑADIDO para la FK creador_cedula_profesor
CREATE INDEX idx_fk_pregunta_prof_creador ON Pregunta(creador_cedula_profesor) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_prof_creador creado.');
END;
/


-- Tabla: OpcionPregunta
-- Índice para OpcionPregunta(pregunta_id) ya creado como idx_opcion_pregunta en la sección 1.

-- Tabla: Examen
CREATE INDEX idx_fk_examen_creacion ON Examen(creacion_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_examen_creacion creado.');
END;
/
CREATE INDEX idx_fk_examen_categoria ON Examen(categoria_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_examen_categoria creado.');
END;
/
CREATE INDEX idx_fk_examen_curso ON Examen(curso_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_examen_curso creado.');
END;
/

-- Tabla: DetallePreguntaExamen
-- PK es (examen_id, pregunta_id). Índices individuales para FKs.
CREATE INDEX idx_fk_dpe_examen_id ON DetallePreguntaExamen(examen_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_dpe_examen_id creado.');
END;
/
CREATE INDEX idx_fk_dpe_pregunta_id ON DetallePreguntaExamen(pregunta_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_dpe_pregunta_id creado.');
END;
/

-- Tabla: PresentacionExamen
CREATE INDEX idx_fk_presentex_examen_id ON PresentacionExamen(examen_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_presentex_examen_id creado.');
END;
/
-- Índice para PresentacionExamen(estudiante_cedula) ya creado como idx_presentacion_estudiante en la sección 1.

-- Tabla: PreguntaExamenEstudiante
CREATE INDEX idx_fk_pexes_presentacion_id ON PreguntaExamenEstudiante(presentacion_examen_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pexes_presentacion_id creado.');
END;
/
-- Para la FK compuesta (detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id)
CREATE INDEX idx_fk_pexes_detalle_pex ON PreguntaExamenEstudiante(detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pexes_detalle_pex creado.');
END;
/

-- Tabla: RespuestaPregunta
-- Índice para RespuestaPregunta(opcion_pregunta_id) ya creado como idx_respuesta_opcion en la sección 1.
CREATE INDEX idx_fk_resp_preg_exam_est_id ON RespuestaPregunta(preguntaExamenEstudiante_id) TABLESPACE TS_INDICES;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Índice idx_fk_resp_preg_exam_est_id creado.');
END;
/

COMMIT;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Sección 3: Índices para Claves Foráneas Completada ---');
END;
/

BEGIN
    DBMS_OUTPUT.PUT_LINE('=========================================================');
    DBMS_OUTPUT.PUT_LINE('SCRIPT DE CREACIÓN DE ÍNDICES PARA EDUTEC COMPLETADO.');
    DBMS_OUTPUT.PUT_LINE('=========================================================');
END;
/
