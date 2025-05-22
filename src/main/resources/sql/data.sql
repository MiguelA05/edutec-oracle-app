-- ====================================================================
-- SCRIPT DE POBLADO - BASE DE DATOS "EDUTEC"
-- Versión: 2.2 (Corregido y Ajustado al Esquema)
-- Fecha: 22 de Mayo de 2025
-- ====================================================================

-- 1. TABLAS CATÁLOGO
-- Dia_Semana
INSERT INTO Dia_Semana (id, nombre, descripcion) VALUES (1, 'Lunes',     'Primer día laboral');
INSERT INTO Dia_Semana (id, nombre, descripcion) VALUES (2, 'Martes',    'Segundo día');
INSERT INTO Dia_Semana (id, nombre, descripcion) VALUES (3, 'Miércoles', 'Mitad de semana');
INSERT INTO Dia_Semana (id, nombre, descripcion) VALUES (4, 'Jueves',    'Preparación fin de semana');
INSERT INTO Dia_Semana (id, nombre, descripcion) VALUES (5, 'Viernes',   'Último día hábil');
INSERT INTO Dia_Semana (id, nombre, descripcion) VALUES (6, 'Sábado',    'Clases especiales');
INSERT INTO Dia_Semana (id, nombre, descripcion) VALUES (7, 'Domingo',   'Descanso');
COMMIT;

-- Visibilidad
INSERT INTO Visibilidad (id, nombre, descripcion) VALUES (1, 'Pública', 'Visible para todos');
INSERT INTO Visibilidad (id, nombre, descripcion) VALUES (2, 'Privada', 'Solo creador');
COMMIT;

-- TipoPregunta
INSERT INTO TipoPregunta (id, nombre, descripcion) VALUES (1, 'Selección múltiple', 'Una respuesta correcta');
INSERT INTO TipoPregunta (id, nombre, descripcion) VALUES (2, 'Verdadero/Falso',    'Evaluar afirmación');
INSERT INTO TipoPregunta (id, nombre, descripcion) VALUES (3, 'Ordenar conceptos',    'Texto breve');
INSERT INTO TipoPregunta (id, nombre, descripcion) VALUES (4, 'Relacionar conceptos','Emparejar elementos');
INSERT INTO TipoPregunta (id, nombre, descripcion) VALUES (5, 'Seleccion Unica',    'Valor numérico exacto o única opción');
COMMIT;

-- Nivel
INSERT INTO Nivel (id, nombre, descripcion) VALUES (1, 'Básico',     'Fundamentos');
INSERT INTO Nivel (id, nombre, descripcion) VALUES (2, 'Intermedio', 'Aplicación práctica');
INSERT INTO Nivel (id, nombre, descripcion) VALUES (3, 'Avanzado',   'Análisis complejo');
COMMIT;

-- Creacion
INSERT INTO Creacion (id, nombre, descripcion) VALUES (1, 'Manual',     'Creado por profesor');
INSERT INTO Creacion (id, nombre, descripcion) VALUES (2, 'Automático', 'Generado por sistema');
COMMIT;

-- Categoria
INSERT INTO Categoria (id, nombre, descripcion) VALUES (1,  'Parcial',       'Evaluación parcial');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (2,  'Final',         'Examen final');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (3,  'Quiz',          'Evaluación rápida');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (4,  'Taller',        'Actividad práctica');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (5,  'Laboratorio',   'Práctica experimental');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (6,  'Proyecto',      'Trabajo aplicado');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (7,  'Diagnóstico',   'Evaluación inicial');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (8,  'Simulacro',     'Preparación examen');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (9,  'Seguimiento',   'Evaluación continua');
INSERT INTO Categoria (id, nombre, descripcion) VALUES (10, 'Competencia',   'Desafío entre estudiantes');
COMMIT;

-- 2. USUARIOS
-- Profesores
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000001, 'Ana García', 'a.garcia@edutec.edu', 'Prof24#ABC123');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000002, 'Carlos Rodríguez', 'c.rodriguez@edutec.edu', 'Prof24#DEF456');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000003, 'Elena Martínez', 'e.martinez@edutec.edu', 'Prof24#GHI789');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000004, 'David López', 'd.lopez@edutec.edu', 'Prof24#JKL012');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000005, 'Lucía Pérez', 'l.perez@edutec.edu', 'Prof24#MNO345');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000006, 'Miguel Gómez', 'm.gomez@edutec.edu', 'Prof24#PQR678');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000007, 'Laura Sánchez', 'l.sanchez@edutec.edu', 'Prof24#STU901');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000008, 'Javier Fernández', 'j.fernandez@edutec.edu', 'Prof24#VWX234');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000009, 'María Torres', 'm.torres@edutec.edu', 'Prof24#YZA567');
INSERT INTO Profesor (cedula, nombre, correo, contrasena) VALUES (1000000010, 'José Ramírez', 'j.ramirez@edutec.edu', 'Prof24#BCD890');
COMMIT;

-- Estudiantes
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000001, 'Juan Gómez Pérez', 'j.gom1@edutec.edu', 'Estu24#ABC123');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000002, 'María Rodríguez López', 'm.rod2@edutec.edu', 'Estu24#DEF456');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000003, 'Carlos Martínez García', 'c.mar3@edutec.edu', 'Estu24#GHI789');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000004, 'Ana Hernández Sánchez', 'a.her4@edutec.edu', 'Estu24#JKL012');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000005, 'Luis López Ramírez', 'l.lop5@edutec.edu', 'Estu24#MNO345');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000006, 'Laura Pérez Torres', 'l.per6@edutec.edu', 'Estu24#PQR678');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000007, 'Pedro Sánchez Vargas', 'p.san7@edutec.edu', 'Estu24#STU901');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000008, 'Sofía Gómez Morales', 's.gom8@edutec.edu', 'Estu24#VWX234');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000009, 'Diego Rodríguez Ortiz', 'd.rod9@edutec.edu', 'Estu24#YZA567');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000010, 'Valentina Martínez Castro', 'v.mar10@edutec.edu', 'Estu24#BCD890');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000011, 'Andrés Jiménez Ruiz', 'a.jim11@edutec.edu', 'Estu24#EFG111');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000012, 'Camila Vargas Moreno', 'c.var12@edutec.edu', 'Estu24#HIJ222');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000013, 'Ricardo Castro Silva', 'r.cas13@edutec.edu', 'Estu24#KLM333');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000014, 'Isabella Mora Ortiz', 'i.mor14@edutec.edu', 'Estu24#NOP444');
INSERT INTO Estudiante (cedula, nombre, correo, contrasena) VALUES (2000000015, 'Mateo Rojas Peña', 'm.roj15@edutec.edu', 'Estu24#QRS555');
COMMIT;

-- 3. ESTRUCTURA ACADÉMICA
-- Cursos
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (1, 'Matematicas Basicas', 'Fundamentos de matemáticas', 1000000001);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (2, 'Programación I', 'Introducción a la programación', 1000000002);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (3, 'Física General', 'Conceptos básicos de física', 1000000003);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (4, 'Bases de Datos', 'Diseño y manejo de bases de datos', 1000000004);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (5, 'Comunicación', 'Habilidades de comunicación', 1000000005);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (6, 'Cálculo Diferencial', 'Cálculo de una variable', 1000000006);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (7, 'Álgebra Lineal', 'Vectores y matrices', 1000000007);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (8, 'Estructuras de Datos', 'Organización de datos en memoria', 1000000008);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (9, 'Prog. Avanzada', 'Técnicas avanzadas de desarrollo', 1000000009);
INSERT INTO Curso (id_curso, nombre, descripcion, profesor_cedula) VALUES (10, 'Sistemas Operativos', 'Gestión de recursos computacionales', 1000000010);
COMMIT;

-- Horario_Clase
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (1, TO_DATE('07:00','HH24:MI'), TO_DATE('08:30','HH24:MI'), 1);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (2, TO_DATE('08:30','HH24:MI'), TO_DATE('10:00','HH24:MI'), 1);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (3, TO_DATE('10:00','HH24:MI'), TO_DATE('11:30','HH24:MI'), 2);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (4, TO_DATE('11:30','HH24:MI'), TO_DATE('13:00','HH24:MI'), 3);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (5, TO_DATE('14:00','HH24:MI'), TO_DATE('15:30','HH24:MI'), 4);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (6, TO_DATE('15:30','HH24:MI'), TO_DATE('17:00','HH24:MI'), 5);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (7, TO_DATE('17:00','HH24:MI'), TO_DATE('18:30','HH24:MI'), 6);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (8, TO_DATE('18:30','HH24:MI'), TO_DATE('20:00','HH24:MI'), 7);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (9, TO_DATE('20:00','HH24:MI'), TO_DATE('21:30','HH24:MI'), 1);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (10, TO_DATE('08:00','HH24:MI'), TO_DATE('10:00','HH24:MI'), 6);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (11, TO_DATE('10:00','HH24:MI'), TO_DATE('12:00','HH24:MI'), 6);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (12, TO_DATE('14:00','HH24:MI'), TO_DATE('16:00','HH24:MI'), 7);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (13, TO_DATE('16:00','HH24:MI'), TO_DATE('18:00','HH24:MI'), 7);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (14, TO_DATE('09:00','HH24:MI'), TO_DATE('11:00','HH24:MI'), 6);
INSERT INTO Horario_Clase (id_horario, hora_inicio, hora_fin, dia_semana_id) VALUES (15, TO_DATE('11:00','HH24:MI'), TO_DATE('13:00','HH24:MI'), 7);
COMMIT;

-- Unidades
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (1, 'Aritmética/Álgebra', 1);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (2, 'Intro Programación', 2);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (3, 'Mecánica Newtoniana', 3);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (4, 'Modelado/Normaliz.', 4);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (5, 'Comunicación Oral', 5);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (6, 'Límites/Continuidad', 6);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (7, 'Ecuaciones Lineales', 7);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (8, 'Estructuras Lineales', 8);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (9, 'Prog. Orientada Obj.', 9);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (10, 'Arquitectura de SO', 10);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (11, 'Geometria Analitica', 1);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (12, 'Estructuras Control', 2);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (13, 'Termodinámica', 3);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (14, 'Lenguaje SQL', 4);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (15, 'Comunicacion Escrita', 5);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (16, 'Derivadas/Aplicac.', 6);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (17, 'Espacios Vectoriales', 7);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (18, 'Estruc. No Lineales', 8);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (19, 'Patrones de Diseño', 9);
INSERT INTO Unidad (id_unidad, nombre, id_curso) VALUES (20, 'Gestión de Procesos', 10);
COMMIT;

-- Contenidos
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (1, 'Op. Números Reales', 1);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (2, 'Resol. Ecuac. Lin.', 1);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (3, 'Conceptos/Pseudocod.', 2);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (4, 'Variables/Tipos Dato', 2);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (5, 'Leyes de Newton', 3);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (6, 'Cinematica/Dinamica', 3);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (7, 'Modelo Entidad-Rel.', 4);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (8, 'Normalizacion Tablas', 4);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (9, 'Redaccion Academica', 15);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (10, 'Técnicas Expr. Oral', 5);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (11, 'Concepto de límite', 6);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (12, 'Funciones continuas', 6);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (13, 'Método Gauss-Jordan', 7);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (14, 'Matrices/Determinant', 7);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (15, 'Pilas y colas', 8);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (16, 'Listas enlazadas', 8);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (17, 'Clases y objetos', 9);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (18, 'Herencia/Polimorf.', 9);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (19, 'Componentes de un SO', 10);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (20, 'Llamadas al sistema', 10);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (21, 'Distancia Puntos', 11);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (22, 'Ecuación Recta', 11);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (23, 'Ciclos For y While', 12);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (24, 'Sentencias If-Else', 12);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (25, 'Calor y Temperatura', 13);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (26, 'Leyes Termodinámica', 13);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (27, 'Consultas SELECT', 14);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (28, 'JOINs', 14);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (29, 'Ensayo argumentativo', 15);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (30, 'Normas APA', 15);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (31, 'Reglas Derivación', 16);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (32, 'Optimización', 16);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (33, 'Bases y dimensión', 17);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (34, 'Transf. Lineales', 17);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (35, 'Árboles binarios', 18);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (36, 'Grafos', 18);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (37, 'Singleton y Factory', 19);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (38, 'Observer y Strategy', 19);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (39, 'Estados Proceso', 20);
INSERT INTO Contenido (id_contenido, nombre, id_unidad) VALUES (40, 'Planificación CPU', 20);
COMMIT;

-- 4. EVALUACIONES
-- Preguntas
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (1, '¿Cuál es la solución de la ecuación 2x + 5 = 15?', 5, 0.25, 1, 1, 1, NULL, 2, 1000000001);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (2, 'Explica la diferencia entre un ciclo for y un ciclo while', 10, 0.25, 3, 1, 2, NULL, 23, 1000000002);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (3, '¿La primera ley de Newton se aplica en un vacío?', 2, 0.25, 2, 1, 1, NULL, 5, 1000000003);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (4, '¿Qué es la normalización en bases de datos?', 8, 0.25, 3, 1, 2, NULL, 8, 1000000004);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (5, 'Menciona tres técnicas para mejorar la comunicación oral', 6, 0.25, 3, 1, 1, NULL, 10, 1000000005);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (6, 'Calcula el límite cuando x tiende a 2 de (x²-4)/(x-2)', 5, 0.25, 5, 1, 2, NULL, 11, 1000000006);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (7, '¿Qué es una matriz invertible?', 3, 0.25, 3, 1, 2, NULL, 14, 1000000007);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (8, '¿Cuál es la diferencia entre pila y cola?', 4, 0.25, 3, 1, 2, NULL, 15, 1000000008);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (9, '¿Qué es el polimorfismo en POO?', 5, 0.25, 3, 1, 3, NULL, 18, 1000000009);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (10, '¿Qué son las llamadas al sistema?', 4, 0.25, 3, 1, 2, NULL, 20, 1000000010);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (11, '¿Cuál es la fórmula de la distancia entre dos puntos (x1, y1) y (x2, y2)?', 3, 0.15, 3, 1, 1, NULL, 21, 1000000001);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (12, 'La sentencia IF-ELSE se usa para definir bucles. ¿Verdadero o Falso?', 2, 0.10, 2, 1, 1, NULL, 24, 1000000002);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (13, '¿Qué ley de la termodinámica establece que la energía no se crea ni se destruye?', 5, 0.20, 1, 1, 2, NULL, 26, 1000000003);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (14, 'Escribe una consulta SQL para seleccionar todos los campos de una tabla llamada "Clientes".', 7, 0.25, 3, 1, 1, NULL, 27, 1000000004);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (15, '¿Cuál es la derivada de f(x) = x^3?', 3, 0.15, 5, 1, 1, NULL, 31, 1000000006);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (16, 'Un árbol binario siempre está balanceado. ¿Verdadero o Falso?', 2, 0.10, 2, 1, 2, NULL, 35, 1000000008);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (17, '¿Qué patrón de diseño garantiza que una clase tenga una sola instancia?', 5, 0.20, 1, 1, 3, NULL, 37, 1000000009);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (18, 'Enumera los 3 estados básicos de un proceso.', 6, 0.25, 3, 1, 2, NULL, 39, 1000000010);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (19, 'Resolver el sistema: x+y=5, x-y=1', 5, 0.20, 5, 1, 1, NULL, 13, 1000000007);
INSERT INTO Pregunta (id_pregunta, texto, tiempo_estimado, porcentaje, tipo_pregunta_id, visibilidad_id, nivel_id, pregunta_padre, contenido_id, creador_cedula_profesor)
VALUES (20, '¿Qué significa FIFO?', 2, 0.10, 3, 1, 1, NULL, 15, 1000000008);
COMMIT;

-- OpcionesPregunta
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (1, 'x = 5', 'S', 1);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (2, 'x = 10', 'N', 1);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (3, 'x = 7.5', 'N', 1);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (4, 'x = 15', 'N', 1);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (5, 'Verdadero', 'S', 3);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (6, 'Falso', 'N', 3);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (7, '4', 'S', 6);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (8, '0', 'N', 6);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (9, 'No existe', 'N', 6);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (10, '2', 'N', 6);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (11, 'Verdadero', 'N', 12);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (12, 'Falso', 'S', 12);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (13, 'Primera Ley', 'S', 13);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (14, 'Segunda Ley', 'N', 13);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (15, 'Tercera Ley', 'N', 13);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (16, 'Ley Cero', 'N', 13);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (17, '3x^2', 'S', 15);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (18, 'x^2', 'N', 15);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (19, '3x', 'N', 15);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (20, 'Verdadero', 'N', 16);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (21, 'Falso', 'S', 16);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (22, 'Singleton', 'S', 17);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (23, 'Factory', 'N', 17);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (24, 'Observer', 'N', 17);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (25, 'Strategy', 'N', 17);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (26, 'x=3, y=2', 'S', 19);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (27, 'x=2, y=3', 'N', 19);
INSERT INTO OpcionPregunta (id, respuesta, es_correcta, pregunta_id) VALUES (28, 'x=4, y=1', 'N', 19);
COMMIT;

-- Exámenes
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (1, 'Parcial 1 Matematicas', 90, 4, TO_DATE('2025-02-28', 'YYYY-MM-DD'), TO_DATE('09:00','HH24:MI'), 5.0, 0.20, 'Parcial 1 - Matemáticas Básicas', 1, 1, 1);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (2, 'Final Programacion I', 120, 5, TO_DATE('2025-03-30', 'YYYY-MM-DD'), TO_DATE('14:00','HH24:MI'), 5.0, 0.25, 'Final - Programación I', 1, 2, 2);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (3, 'Quiz Fisica General', 30, 2, TO_DATE('2025-04-14', 'YYYY-MM-DD'), TO_DATE('16:00','HH24:MI'), 3.0, 0.10, 'Quiz - Física General', 2, 3, 3);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (4, 'Parcial 1 BD', 90, 3, TO_DATE('2025-04-19', 'YYYY-MM-DD'), TO_DATE('10:00','HH24:MI'), 5.0, 0.20, 'Parcial 1 - Bases de Datos', 1, 1, 4);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (5, 'Final Comunicacion', 120, 2, TO_DATE('2025-04-24', 'YYYY-MM-DD'), TO_DATE('13:00','HH24:MI'), 5.0, 0.25, 'Final - Comunicación', 1, 2, 5);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (6, 'Quiz Calculo Dif', 60, 3, TO_DATE('2025-05-10', 'YYYY-MM-DD'), TO_DATE('11:00','HH24:MI'), 4.0, 0.15, 'Quiz - Cálculo Diferencial', 2, 3, 6);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (7, 'Parcial 1 Algebra', 90, 3, TO_DATE('2025-05-15', 'YYYY-MM-DD'), TO_DATE('08:00','HH24:MI'), 5.0, 0.20, 'Parcial 1 - Álgebra Lineal', 1, 1, 7);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (8, 'Final Estructuras', 120, 4, TO_DATE('2025-05-20', 'YYYY-MM-DD'), TO_DATE('15:00','HH24:MI'), 5.0, 0.25, 'Final - Estructuras de Datos', 1, 2, 8);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (9, 'Quiz Prog Avanzada', 45, 3, TO_DATE('2025-05-25', 'YYYY-MM-DD'), TO_DATE('10:00','HH24:MI'), 3.0, 0.10, 'Quiz - Prog. Avanzada', 2, 3, 9);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (10, 'Parcial 1 Sist Op', 90, 3, TO_DATE('2025-05-30', 'YYYY-MM-DD'), TO_DATE('14:00','HH24:MI'), 5.0, 0.20, 'Parcial 1 - Sistemas Operativos', 1, 1, 10);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (11, 'Final Matematicas', 120, 4, TO_DATE('2025-06-05', 'YYYY-MM-DD'), TO_DATE('09:00','HH24:MI'), 5.0, 0.25, 'Final - Matemáticas Básicas', 1, 2, 1);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (12, 'Quiz Programacion I', 30, 2, TO_DATE('2025-06-10', 'YYYY-MM-DD'), TO_DATE('16:00','HH24:MI'), 3.0, 0.10, 'Quiz - Programación I', 2, 3, 2);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (13, 'Parcial 1 Fisica', 90, 3, TO_DATE('2025-06-15', 'YYYY-MM-DD'), TO_DATE('10:00','HH24:MI'), 5.0, 0.20, 'Parcial 1 - Física General', 1, 1, 3);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (14, 'Final Bases de Datos', 120, 4, TO_DATE('2025-06-20', 'YYYY-MM-DD'), TO_DATE('13:00','HH24:MI'), 5.0, 0.25, 'Final - Bases de Datos', 1, 2, 4);
INSERT INTO Examen (id, nombre, tiempo, numero_preguntas, fecha, hora, calificacion_min_aprobatoria, peso_curso, descripcion, creacion_id, categoria_id, curso_id)
VALUES (15, 'Quiz Comunicacion', 60, 3, TO_DATE('2025-06-25', 'YYYY-MM-DD'), TO_DATE('11:00','HH24:MI'), 4.0, 0.15, 'Quiz - Comunicación', 2, 3, 5);
COMMIT;

-- 5. TABLAS DE DETALLE

-- DetalleEstudianteCurso
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000001, 1);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000001, 2);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000002, 1);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000002, 3);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000003, 2);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000003, 4);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000004, 3);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000004, 5);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000005, 4);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000005, 6);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000006, 5);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000006, 7);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000007, 6);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000007, 8);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000008, 7);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000008, 9);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000009, 8);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000009, 10);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000010, 9);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000010, 1);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000011, 1);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000011, 4);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000012, 2);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000012, 8);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000013, 3);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000013, 6);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000014, 5);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000014, 10);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000015, 7);
INSERT INTO DetalleEstudianteCurso (estudiante_cedula, curso_id) VALUES (2000000015, 9);
COMMIT;

-- DetalleCursoHorario (CORREGIDO: Nombre de tabla)
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (1, 1);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (2, 2);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (3, 3);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (4, 4);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (5, 5);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (6, 6);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (7, 7);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (8, 8);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (9, 9);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (10, 10);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (1, 11);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (2, 12);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (3, 13);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (4, 14);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (5, 15);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (6, 1);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (7, 2);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (8, 3);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (9, 4);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (10, 5);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (1, 3);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (2, 4);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (3, 5);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (4, 6);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (5, 7);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (6, 8);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (7, 9);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (8, 10);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (9, 11);
INSERT INTO DetalleCursoHorario (curso_id, horario_clase_id) VALUES (10, 12);
COMMIT;

-- DetallePreguntaExamen
-- Examen 1 (Matematicas Basicas - Parcial 1) - 4 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (1, 1, 0.30);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (1, 11, 0.20);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (1, 19, 0.30);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (1, 7, 0.20); -- Relacionado con álgebra
-- Examen 2 (Programacion I - Final) - 5 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (2, 2, 0.25);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (2, 12, 0.15);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (2, 8, 0.20); -- Introducción a estructuras
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (2, 20, 0.15); -- FIFO
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (2, 16, 0.25); -- Concepto básico árbol
-- Examen 3 (Fisica - Quiz) - 2 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (3, 3, 0.50);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (3, 13, 0.50);
-- Examen 4 (Bases de Datos - Parcial 1) - 3 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (4, 4, 0.40);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (4, 14, 0.30);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (4, 7, 0.30); -- Concepto de matriz (relacionado a tablas)
-- Examen 5 (Comunicacion - Final) - 2 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (5, 5, 0.60);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (5, 9, 0.40); -- Concepto avanzado (Polimorfismo)
-- Examen 6 (Calculo - Quiz) - 3 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (6, 6, 0.40);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (6, 15, 0.30);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (6, 11, 0.30); -- Geometría relacionada
-- Examen 7 (Algebra Lineal - Parcial 1) - 3 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (7, 7, 0.40);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (7, 19, 0.30);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (7, 13, 0.30); -- Pregunta de otro tema para variar
-- Examen 8 (Estructuras Datos - Final) - 4 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (8, 8, 0.25);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (8, 16, 0.25);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (8, 20, 0.25);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (8, 2, 0.25); -- Relacionado a algoritmos
-- Examen 9 (Prog Avanzada - Quiz) - 3 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (9, 9, 0.40);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (9, 17, 0.30);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (9, 12, 0.30); -- Base
-- Examen 10 (Sist Operativos - Parcial 1) - 3 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (10, 10, 0.40);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (10, 18, 0.30);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (10, 3, 0.30); -- Pregunta aleatoria
-- Examen 11 (Matematicas Basicas - Final) - 4 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (11, 1, 0.25);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (11, 11, 0.25);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (11, 19, 0.25);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (11, 7, 0.25);
-- Examen 12 (Programacion I - Quiz) - 2 Preguntas
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (12, 2, 0.50);
INSERT INTO DetallePreguntaExamen (examen_id, pregunta_id, porcentaje) VALUES (12, 12, 0.50);
COMMIT;


-- 6. HISTÓRICO

-- PresentacionExamen
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (1, 7.5, 3, 1, 5200, TO_DATE('2025-02-28 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-02-28 09:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5200/86400, '192.168.1.10', 1, 2000000001);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (2, 8.0, 4, 1, 6800, TO_DATE('2025-03-30 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-03-30 14:00:00', 'YYYY-MM-DD HH24:MI:SS') + 6800/86400, '10.0.0.5', 2, 2000000002);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (3, 6.0, 1, 1, 1750, TO_DATE('2025-04-14 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-04-14 16:00:00', 'YYYY-MM-DD HH24:MI:SS') + 1750/86400, '172.16.5.20', 3, 2000000003);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (4, 9.0, 3, 0, 5000, TO_DATE('2025-04-19 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-04-19 10:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5000/86400, '192.168.1.11', 4, 2000000004);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (5, 7.0, 1, 1, 6500, TO_DATE('2025-04-24 13:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-04-24 13:00:00', 'YYYY-MM-DD HH24:MI:SS') + 6500/86400, '10.0.1.8', 5, 2000000005);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (6, 8.5, 3, 0, 3500, TO_DATE('2025-05-10 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-10 11:00:00', 'YYYY-MM-DD HH24:MI:SS') + 3500/86400, '172.16.10.3', 6, 2000000006);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (7, 6.5, 2, 1, 5100, TO_DATE('2025-05-15 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-15 08:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5100/86400, '192.168.1.12', 7, 2000000007);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (8, 9.5, 4, 0, 7000, TO_DATE('2025-05-20 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-20 15:00:00', 'YYYY-MM-DD HH24:MI:SS') + 7000/86400, '10.0.2.15', 8, 2000000008);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (9, 5.0, 1, 2, 2600, TO_DATE('2025-05-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS') + 2600/86400, '172.16.15.4', 9, 2000000009);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (10, 7.0, 2, 1, 5300, TO_DATE('2025-05-30 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-30 14:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5300/86400, '192.168.1.13', 10, 2000000010);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (11, 8.0, 3, 1, 6900, TO_DATE('2025-06-05 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-06-05 09:00:00', 'YYYY-MM-DD HH24:MI:SS') + 6900/86400, '10.0.3.1', 11, 2000000001);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (12, 9.0, 2, 0, 1700, TO_DATE('2025-06-10 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-06-10 16:00:00', 'YYYY-MM-DD HH24:MI:SS') + 1700/86400, '172.16.20.5', 12, 2000000002);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (13, 6.0, 2, 1, 5200, TO_DATE('2025-06-15 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-06-15 10:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5200/86400, '192.168.1.14', 13, 2000000003);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (14, 7.5, 3, 1, 7100, TO_DATE('2025-06-20 13:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-06-20 13:00:00', 'YYYY-MM-DD HH24:MI:SS') + 7100/86400, '10.0.4.9', 14, 2000000004);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (15, 8.5, 3, 0, 3400, TO_DATE('2025-06-25 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-06-25 11:00:00', 'YYYY-MM-DD HH24:MI:SS') + 3400/86400, '172.16.25.6', 15, 2000000005);
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (16, 4.0, 1, 2, 5400, TO_DATE('2025-02-28 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-02-28 09:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5400/86400, '192.168.1.15', 1, 2000000011); -- Examen 1
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (17, 9.5, 5, 0, 6700, TO_DATE('2025-03-30 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-03-30 14:00:00', 'YYYY-MM-DD HH24:MI:SS') + 6700/86400, '10.0.5.11', 2, 2000000012); -- Examen 2
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (18, 7.0, 1, 1, 1650, TO_DATE('2025-04-14 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-04-14 16:00:00', 'YYYY-MM-DD HH24:MI:SS') + 1650/86400, '172.16.30.7', 3, 2000000013); -- Examen 3
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (19, 8.0, 2, 1, 5150, TO_DATE('2025-04-19 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-04-19 10:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5150/86400, '192.168.1.16', 4, 2000000014); -- Examen 4
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (20, 6.0, 1, 1, 6600, TO_DATE('2025-04-24 13:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-04-24 13:00:00', 'YYYY-MM-DD HH24:MI:SS') + 6600/86400, '10.0.6.13', 5, 2000000015); -- Examen 5
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (21, 9.0, 3, 0, 3550, TO_DATE('2025-05-10 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-10 11:00:00', 'YYYY-MM-DD HH24:MI:SS') + 3550/86400, '172.16.35.8', 6, 2000000001); -- Examen 6
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (22, 5.5, 1, 2, 5250, TO_DATE('2025-05-15 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-15 08:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5250/86400, '192.168.1.17', 7, 2000000002); -- Examen 7
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (23, 8.5, 3, 1, 7050, TO_DATE('2025-05-20 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-20 15:00:00', 'YYYY-MM-DD HH24:MI:SS') + 7050/86400, '10.0.7.14', 8, 2000000003); -- Examen 8
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (24, 7.0, 2, 1, 2650, TO_DATE('2025-05-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS') + 2650/86400, '172.16.40.9', 9, 2000000004); -- Examen 9
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (25, 9.5, 3, 0, 5350, TO_DATE('2025-05-30 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-05-30 14:00:00', 'YYYY-MM-DD HH24:MI:SS') + 5350/86400, '192.168.1.18', 10, 2000000005); -- Examen 10
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (26, 6.0, 2, 2, 6850, TO_DATE('2025-06-05 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-06-05 09:00:00', 'YYYY-MM-DD HH24:MI:SS') + 6850/86400, '10.0.8.16', 11, 2000000006); -- Examen 11
INSERT INTO PresentacionExamen (id, calificacion, respuestas_correctas, respuestas_incorrectas, tiempo, hora_inicio, hora_fin, direccion_ip, examen_id, estudiante_cedula)
VALUES (27, 8.0, 2, 0, 1750, TO_DATE('2025-06-10 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2025-06-10 16:00:00', 'YYYY-MM-DD HH24:MI:SS') + 1750/86400, '172.16.45.10', 12, 2000000007); -- Examen 12
COMMIT;

-- PreguntaExamenEstudiante
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (1, 120, 1, 1, 1);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (2, 150, 1, 1, 11);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (3, 130, 1, 1, 19);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (4, 120, 1, 1, 7);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (5, 180, 2, 2, 2);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (6, 100, 2, 2, 12);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (7, 140, 2, 2, 8);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (8, 90, 2, 2, 20);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (9, 170, 2, 2, 16);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (10, 80, 3, 3, 3);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (11, 100, 3, 3, 13);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (12, 110, 16, 1, 1);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (13, 160, 16, 1, 11);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (14, 140, 16, 1, 19);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (15, 130, 16, 1, 7);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (16, 190, 17, 2, 2);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (17, 110, 17, 2, 12);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (18, 150, 17, 2, 8);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (19, 80, 17, 2, 20);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (20, 160, 17, 2, 16);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (21, 100, 21, 6, 6);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (22, 120, 21, 6, 15);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (23, 110, 21, 6, 11);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (24, 150, 25, 10, 10);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (25, 130, 25, 10, 18);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (26, 100, 25, 10, 3);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (27, 70, 27, 12, 2);
INSERT INTO PreguntaExamenEstudiante (id, tiempo, presentacion_examen_id, detalle_pregunta_examen_examen_id, detalle_pregunta_examen_pregunta_id) VALUES (28, 90, 27, 12, 12);
COMMIT;

-- RespuestaPregunta
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (1, 'x = 5', 1, 1);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (2, 'sqrt((x2-x1)^2 + (y2-y1)^2)', NULL, 2);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (3, 'x=3, y=2', 26, 3);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (4, 'Una matriz con determinante diferente de cero', NULL, 4);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (5, 'For itera N veces, While hasta condición', NULL, 5);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (6, 'Falso', 12, 6);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (7, 'Pila es LIFO, Cola es FIFO', NULL, 7);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (8, 'First In First Out', NULL, 8);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (9, 'Falso', 21, 9);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (10, 'Verdadero', 5, 10);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (11, 'Primera Ley', 13, 11);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (12, 'x = 5', 1, 12);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (13, 'Formula...', NULL, 13);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (14, 'x=3, y=2', 26, 14);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (15, '...', NULL, 15);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (16, '...', NULL, 16);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (17, 'Falso', 12, 17);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (18, '...', NULL, 18);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (19, '...', NULL, 19);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (20, 'Falso', 21, 20);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (21, '4', 7, 21);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (22, '3x^2', 17, 22);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (23, '...', NULL, 23);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (24, 'Interfaz entre app y kernel', NULL, 24);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (25, 'Listo, Ejecutando, Bloqueado', NULL, 25);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (26, 'Verdadero', 5, 26);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (27, 'For y While', NULL, 27);
INSERT INTO RespuestaPregunta (id, respuesta_dada, opcion_pregunta_id, preguntaExamenEstudiante_id) VALUES (28, 'Falso', 12, 28);
COMMIT;

BEGIN
    DBMS_OUTPUT.PUT_LINE('Script de poblado EDUTEC (corregido) ejecutado.');
END;
/
