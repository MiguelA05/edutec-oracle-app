-- ====================================================================
-- SCRIPT DE CREACIÓN DE ÍNDICES - BASE DE DATOS "EDUTEC"
-- Versión: 2.0 (Corregido)
-- Fecha: 9 de Mayo de 2025
-- Descripción: Crea índices personalizados y para claves foráneas
--              en el tablespace TS_INDICES.
--              NOTA: Se asume que los índices de Clave Primaria ya fueron
--              creados en TS_INDICES durante la creación de tablas (Script v4).
-- ====================================================================

-- --------------------------------------------------------
-- 1. CREACIÓN DE ÍNDICES PERSONALIZADOS EN TABLESPACE TS_INDICES
-- --------------------------------------------------------
-- Estos índices mejoran el rendimiento de consultas comunes.
-- Algunos de estos pueden estar en columnas de Clave Foránea,
-- lo cual es beneficioso.

-- Índice para búsquedas por nombre de curso
CREATE INDEX idx_curso_nombre ON Curso(nombre) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_curso_nombre creado.');

-- Índice para búsquedas por nombre de estudiante
CREATE INDEX idx_estudiante_nombre ON Estudiante(nombre) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_estudiante_nombre creado.');

-- Índice para búsquedas por correo y contraseña de estudiante (para login, por ejemplo)
CREATE INDEX idx_estudiante_correo_contrasena ON Estudiante(correo, contrasena) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_estudiante_correo_contrasena creado.');

-- Índice para búsquedas por nombre de profesor
CREATE INDEX idx_profesor_nombre ON Profesor(nombre) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_profesor_nombre creado.');

-- Índice para búsquedas por correo y contraseña de profesor (para login, por ejemplo)
CREATE INDEX idx_profesor_correo_contrasena ON Profesor(correo, contrasena) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_profesor_correo_contrasena creado.');

-- Índice para búsquedas de exámenes por fecha
CREATE INDEX idx_examen_fecha ON Examen(fecha) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_examen_fecha creado.');

-- Índice para búsquedas de presentaciones de examen por estudiante (FK también)
CREATE INDEX idx_presentacion_estudiante ON PresentacionExamen(estudiante_cedula) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_presentacion_estudiante creado.');

-- Índice para búsquedas de opciones por pregunta (FK también)
CREATE INDEX idx_opcion_pregunta ON OpcionPregunta(pregunta_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_opcion_pregunta creado.');

-- Índice para búsquedas de respuestas por opción seleccionada (FK también)
CREATE INDEX idx_respuesta_opcion ON RespuestaPregunta(opcion_pregunta_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_respuesta_opcion creado.');

COMMIT;

-- --------------------------------------------------------
-- 2. MOVIMIENTO DE ÍNDICES DE CLAVE PRIMARIA A TS_INDICES
-- --------------------------------------------------------
-- ESTA SECCIÓN HA SIDO ELIMINADA.
-- Los índices de Clave Primaria ya se crean en TS_INDICES
-- mediante la cláusula "USING INDEX TABLESPACE TS_INDICES"
-- en las sentencias CREATE TABLE del script de creación de BD v4.
-- --------------------------------------------------------

-- --------------------------------------------------------
-- 3. CREACIÓN DE ÍNDICES PARA CLAVES FORÁNEAS EN TS_INDICES
-- --------------------------------------------------------
-- Se crean índices para columnas de Clave Foránea que no estén
-- ya cubiertas por un índice de PK o un índice personalizado anterior.

-- Índices para claves foráneas en la tabla Curso
CREATE INDEX idx_fk_curso_profesor ON Curso(profesor_cedula) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_curso_profesor creado.');

-- Índices para claves foráneas en Horario_Clase
CREATE INDEX idx_fk_horario_dia_semana ON Horario_Clase(dia_semana_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_horario_dia_semana creado.');

-- Índices para claves foráneas en Unidad
CREATE INDEX idx_fk_unidad_curso ON Unidad(id_curso) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_unidad_curso creado.');

-- Índices para claves foráneas en Contenido
CREATE INDEX idx_fk_contenido_unidad ON Contenido(id_unidad) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_contenido_unidad creado.');

-- Índices para claves foráneas en DetalleEstudianteCurso
-- La PK es (estudiante_cedula, curso_id). Índices individuales pueden ser útiles.
CREATE INDEX idx_fk_detalle_est_cedula ON DetalleEstudianteCurso(estudiante_cedula) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_detalle_est_cedula creado.');
CREATE INDEX idx_fk_detalle_cur_id ON DetalleEstudianteCurso(curso_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_detalle_cur_id creado.');

-- Índices para claves foráneas en DetalleUnidadHorario
-- La PK es (curso_id, horario_clase_id). Índices individuales pueden ser útiles.
CREATE INDEX idx_fk_detalle_unhor_curso ON DetalleUnidadHorario(curso_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_detalle_unhor_curso creado.');
CREATE INDEX idx_fk_detalle_unhor_horario ON DetalleUnidadHorario(horario_clase_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_detalle_unhor_horario creado.');

-- Índices para claves foráneas en Pregunta
CREATE INDEX idx_fk_pregunta_tipo ON Pregunta(tipo_pregunta_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_tipo creado.');
CREATE INDEX idx_fk_pregunta_visibilidad ON Pregunta(visibilidad_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_visibilidad creado.');
CREATE INDEX idx_fk_pregunta_nivel ON Pregunta(nivel_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_nivel creado.');
CREATE INDEX idx_fk_pregunta_padre ON Pregunta(pregunta_padre) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_padre creado.');
CREATE INDEX idx_fk_pregunta_contenido ON Pregunta(contenido_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pregunta_contenido creado.');

-- Índice para clave foránea en OpcionPregunta(pregunta_id)
-- Ya creado como idx_opcion_pregunta en la sección 1. No se repite.

-- Índices para claves foráneas en Examen
CREATE INDEX idx_fk_examen_creacion ON Examen(creacion_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_examen_creacion creado.');
CREATE INDEX idx_fk_examen_categoria ON Examen(categoria_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_examen_categoria creado.');
CREATE INDEX idx_fk_examen_curso ON Examen(curso_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_examen_curso creado.');

-- Índices para claves foráneas en DetallePreguntaExamen
-- La PK es (examen_id, pregunta_id). Índices individuales pueden ser útiles.
CREATE INDEX idx_fk_detallepex_examen ON DetallePreguntaExamen(examen_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_detallepex_examen creado.');
CREATE INDEX idx_fk_detallepex_pregunta ON DetallePreguntaExamen(pregunta_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_detallepex_pregunta creado.');

-- Índices para claves foráneas en PresentacionExamen
CREATE INDEX idx_fk_presentex_examen ON PresentacionExamen(examen_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_presentex_examen creado.');
-- Índice para PresentacionExamen(estudiante_cedula) ya creado como idx_presentacion_estudiante en la sección 1.

-- Índices para claves foráneas en PreguntaExamenEstudiante
CREATE INDEX idx_fk_pexes_presentacion ON PreguntaExamenEstudiante(presentacion_examen_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pexes_presentacion creado.');
CREATE INDEX idx_fk_pexes_detalle_pex ON PreguntaExamenEstudiante(detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_pexes_detalle_pex creado.');

-- Índices para claves foráneas en RespuestaPregunta
-- Índice para RespuestaPregunta(opcion_pregunta_id) ya creado como idx_respuesta_opcion en la sección 1.
CREATE INDEX idx_fk_resp_preg_exam_est ON RespuestaPregunta(preguntaExamenEstudiante_id) TABLESPACE TS_INDICES;
DBMS_OUTPUT.PUT_LINE('Índice idx_fk_resp_preg_exam_est creado.');

COMMIT;

BEGIN
  DBMS_OUTPUT.PUT_LINE('--- Creación de índices para EDUTEC completada ---');
END;
/
