# Diseño académico: memoria, evaluación y entrega

**Fecha:** 2026-10-15

**Estado:** diseño aprobado; pendiente de revisión y plan de ejecución

## Objetivo

Preparar una memoria de título sustentada en evidencia del MVP Android local-first. La memoria debe responder si el prototipo convierte capturas compartidas en pendientes confiables con mínima intervención y si puede sugerir contenido acorde al tiempo disponible, sin presentar como implementadas funciones futuras.

El documento se construirá continuamente desde octubre hasta diciembre. Solo se reportarán resultados obtenidos con una versión identificable del software, conjuntos de datos definidos y procedimientos reproducibles.

## Alcance académico

### MVP que se evalúa

- Biblioteca local de entretenimiento por categorías, persistida con Room.
- Alta, edición y eliminación de títulos; estados, duración y notas locales.
- Importación mediante Android Share Sheet, copia privada de captura, OCR en el dispositivo, candidatos de título y confirmación/corrección humana.
- Consulta de catálogo mínima, si se implementa, sin cuenta, correo, telemetría ni historial remoto; las consultas contienen solo términos mínimos y categoría.
- Ficha de contenido, experiencias personales, valoración, comentario y plataforma/soporte.
- Recomendación explicable “Tengo tiempo libre” basada en duración, afinidad, prioridad, antigüedad y retroalimentación local.
- Funcionamiento sin conexión para el contenido ya guardado y las portadas almacenadas localmente.

### Fuera de evaluación y trabajo futuro

- Cloudflare, cuentas, correo, telemetría, sincronización y respaldo remoto.
- Detección automática de capturas fuera de Share Sheet.
- Catálogos de videojuegos o anime, salvo que no comprometan los hitos centrales.
- Cambio manual avanzado de portadas, rutinas, notificaciones y disponibilidad en servicios de streaming.
- Recomendación colaborativa dentro de la aplicación.

MovieLens se usará, si alcanza el tiempo, en un análisis offline separado. No describe ni valida un recomendador colaborativo integrado en la app.

## Estructura de la memoria

1. **Introducción y problema.** Contexto, motivación, pregunta de investigación, objetivo general y objetivos específicos.
2. **Estado del arte y diferenciador.** Referentes como Letterboxd, Goodreads, Trakt y Backloggd; diferenciación por privacidad local-first, capturas y recomendación explicable.
3. **Requisitos y diseño del prototipo.** Alcance MVP, casos de uso, requisitos funcionales/no funcionales, arquitectura Android, modelo local, privacidad, riesgos y limitaciones.
4. **Implementación.** Biblioteca, persistencia, formularios, OCR/Share Sheet, catálogos mínimos, portadas, experiencias y ranking contextual. Cada elemento se describe solo si existe en la versión correspondiente.
5. **Metodología de evaluación.** Conjuntos de prueba, instrumentos, métricas, procedimientos, controles de privacidad y amenazas a la validez.
6. **Resultados y discusión.** Mediciones reales, interpretación, comparación con objetivos y limitaciones. Ninguna tabla se completa con datos estimados.
7. **Conclusiones y trabajo futuro.** Respuesta a la pregunta de investigación, contribuciones, recortes y arquitectura futura de distribución.

Anexos: consentimiento informado, guion de tareas, cuestionario SUS, definición de verdad de referencia del OCR, resultados detallados, configuración de experimentos, Gantt real, decisiones y capturas anonimizadas autorizadas.

## Metodología de evaluación

### OCR, candidatos y catálogo

- Usar 30 a 50 capturas propias o autorizadas y anonimizadas.
- Definir antes de medir una verdad de referencia por captura: título esperado, categoría y variantes aceptables.
- Medir acierto top-1, proporción que necesita corrección manual y tiempo desde compartir hasta guardar.
- Registrar las causas de fallo: OCR, generación de candidatos, catálogo, ambigüedad o corrección humana.

### Recomendación

- Probar dentro de la app las reglas del ranking con pruebas unitarias y casos controlados.
- Si se realiza análisis con MovieLens: documentar la versión del conjunto, división entrenamiento/prueba, semilla, valores de K, métrica elegida (Hit Rate@K y/o NDCG@K) y línea base de popularidad.
- Presentar MovieLens como experimento offline; no atribuir sus resultados al uso real de la aplicación.

### Usabilidad

- Reclutar una muestra de 5 a 8 participantes, no un grupo de control.
- Obtener consentimiento informado antes de las pruebas.
- Aplicar tareas equivalentes, cuestionario SUS y preguntas abiertas breves.
- Reportar tamaño, método de selección, resultados individuales anonimizados y limitaciones de generalización.

### Privacidad y rendimiento

- Inspeccionar tráfico de red durante los flujos evaluados para comprobar que no salen capturas, notas, identificadores personales ni contenido de la biblioteca; si existe una consulta de catálogo, verificar que solo viajan términos mínimos.
- Medir tiempo de OCR y tiempo de apertura de la biblioteca con una cantidad de entradas indicada en el protocolo.
- Conservar evidencias reproducibles: comandos, configuración de dispositivo, versión de APK y fecha de prueba.

## Ética, privacidad y marco legal

- Usar únicamente capturas propias o con autorización; anonimizar nombres de usuario, mensajes, imágenes de personas y datos no necesarios.
- Explicar consentimiento, voluntariedad, retiro y resguardo de los datos de participantes antes de ejecutar usabilidad.
- Durante el desarrollo, referenciar la Ley 19.628 como marco vigente; registrar que la Ley 21.719 entra en vigencia el 1 de diciembre de 2026 y requiere revisión jurídica antes de una publicación posterior. La memoria no afirmará cumplimiento certificado.
- Describir la orientación hacia ISO/IEC 27001 e ISO/IEC 27701 como criterio de diseño, no como certificación.

## Carta Gantt por bloques

| Periodo | Entregables y evidencia |
| --- | --- |
| Octubre | Consolidar proyecto Android, biblioteca local, temas, Room y gestión de títulos ya iniciados. Preparar el conjunto anonimizado de capturas, protocolo OCR y experimento Share Sheet + OCR local. Redactar capítulos 1 a 3 como borrador vivo. |
| Noviembre — bloque 1 | Implementar Share Sheet, copia privada, OCR local, candidatos y corrección manual. Añadir pruebas y registrar decisiones. |
| Noviembre — bloque 2 | Implementar ficha, experiencias, valoración, comentario, plataforma y catálogos/portadas mínimos con fallback manual y caché local. |
| Noviembre — bloque 3 | Implementar “Tengo tiempo libre”, ranking explicable, retroalimentación y pruebas unitarias. Preparar versión de evaluación y etiquetarla en Git. |
| Diciembre — bloque 1 | Ejecutar evaluación de OCR, usabilidad SUS, privacidad/tráfico, rendimiento y, si procede, MovieLens offline. Redactar resultados con los datos obtenidos. |
| Diciembre — bloque 2 | Corregir defectos priorizados, actualizar memoria, discusión, conclusiones, anexos, demo y defensa. No ampliar el alcance académico. |

## Dos versiones controladas

### Versión de evaluación

Se entrega al cierre de noviembre. Incluye los flujos indispensables del MVP: biblioteca local, Share Sheet + OCR, corrección, ficha, experiencias, portadas/catálogos mínimos, recomendación explicable y privacidad local-first. Se etiqueta en Git y se usa sin cambios funcionales durante la recolección de evidencia.

### Versión final

Se construye durante diciembre. Corrige defectos detectados, mejora accesibilidad, rendimiento, memoria y demo. Puede incorporar mejoras de bajo riesgo que no cambien las métricas ni los flujos ya evaluados. No introduce cuentas, nube ni funciones que invaliden la evaluación de la versión anterior.

## Regla de evidencia

Cada resultado debe enlazarse a: versión o commit evaluado, dispositivo/configuración, fecha, conjunto de datos, procedimiento, métrica y resultado observado. Cuando falte evidencia, el texto debe decir “no evaluado” o “trabajo futuro”, nunca inferir un resultado.

## Próximo paso

Revisar este diseño y convertirlo en un plan por microhitos de implementación y documentación. El primer microhito será validar Share Sheet + OCR local con un conjunto pequeño de capturas autorizadas.
