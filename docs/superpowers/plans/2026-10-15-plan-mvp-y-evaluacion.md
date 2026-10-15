# Plan de ejecución: MVP local-first y evaluación

**Fecha:** 2026-10-15

**Base:** `docs/superpowers/specs/2026-10-15-memoria-y-evaluacion-design.md`

**Regla de alcance:** cada microhito se desarrolla en una rama corta, se verifica, actualiza `PROJECT_STATE.md` y se documenta antes de pasar al siguiente. No se implementan cuentas, Cloudflare, telemetría ni sincronización durante este plan.

## Hito 0 — Línea base y protocolo de evidencia

**Objetivo:** fijar qué versión existe, qué se medirá y qué datos pueden usarse antes de incorporar nuevos flujos.

- Crear protocolo del conjunto de capturas: autorización, anonimización, categoría, título esperado y variantes aceptables.
- Crear borradores de consentimiento de usabilidad, guion de tareas y hoja de resultados; no reclutar participantes aún.
- Registrar versión de APK, AVD/dispositivo, comandos de build y pruebas actuales.
- Clasificación: MVP documental.

**Verificación:** revisión de que el protocolo no contiene capturas reales ni datos personales; `git diff --check`.

## Hito 1 — Spike de Share Sheet y OCR local

**Objetivo:** comprobar con cinco capturas autorizadas que Android puede recibir una imagen compartida, copiarla al espacio privado y extraer texto localmente.

- Añadir el receptor `ACTION_SEND` limitado a `image/*` y copiar el URI recibido al almacenamiento privado.
- Integrar ML Kit Text Recognition en el dispositivo, justificando versión, tamaño y mantenimiento de la dependencia.
- Mostrar el texto OCR como borrador temporal; no guardar automáticamente una entrada ni enviar la imagen a la red.
- Añadir una prueba de la lógica de copia/validación que no requiera una imagen externa y una comprobación manual en el AVD.
- Clasificación: MVP técnico; el resultado es una decisión de viabilidad, no una medición final.

**Salida:** evidencia de cinco recorridos y decisión documentada: continuar, ajustar o recortar OCR. Si el flujo falla, se conserva el alta manual y se registra el bloqueo.

## Hito 2 — Candidatos y confirmación humana

**Objetivo:** transformar el texto OCR en candidatos editables sin prometer exactitud automática.

- Crear una función pura que elimine ruido evidente y priorice líneas plausibles; mantener reglas simples y explicables.
- Mostrar de uno a tres candidatos y un campo manual; la persona siempre confirma o corrige antes de guardar.
- Incorporar origen `MANUAL` o `SHARE` y la referencia privada de la captura al modelo local, sin subirla.
- Crear pruebas unitarias para reglas de candidatos y pruebas Room para el origen y la conservación de datos.
- Clasificación: MVP.

**Verificación:** casos de ruido definidos en pruebas; alta confirmada desde ambos orígenes; inspección de logs sin texto OCR ni URI privada.

## Hito 3 — Metadatos y portadas locales desde catálogos mínimos

**Objetivo:** asociar una selección explícita de catálogo a películas/series y libros, conservando alta manual si no hay red o coincidencia.

- Antes de programar, revisar términos, atribución, límites y método de credenciales de TMDB, Google Books y Open Library; decidir por proveedor qué se puede usar en el MVP.
- Definir modelo local para proveedor, identificador externo, año, portada remota y archivo de portada privado.
- Buscar con términos mínimos sin cuenta ni historial remoto; presentar hasta cinco sugerencias y requerir selección humana.
- Para libros, mostrar autor, editorial, fecha, formato/idioma e ISBN cuando existan; para películas/series, título, año y póster.
- Descargar la portada elegida al almacenamiento privado, mostrar marcador elegante ante fallo y mantener consulta manual sin portada.
- Registrar cualquier clave solo en `local.properties`; documentar que no sirve para una distribución pública.
- Clasificación: MVP condicionado a términos y prueba técnica.

**Verificación:** pruebas de mapeo de respuestas y de fallback; recorrido AVD con y sin red; atribución visible cuando un proveedor lo exija; inspección de tráfico para comprobar que no salen capturas, notas ni identidad.

## Hito 4 — Ficha y experiencias personales

**Objetivo:** completar el ciclo local de contenido: detalle, plataforma/soporte, experiencia, estrellas y comentario.

- Separar datos de catálogo de los campos escritos por la persona para que una edición personal no reemplace la fuente ni portada elegida.
- Añadir entidades y migración Room solo cuando el modelo quede validado; incluir exportación de esquema y pruebas de migración.
- Ofrecer crear una experiencia al marcar un contenido como completado, sin obligarla.
- Añadir consulta y edición/eliminación de experiencias.
- Clasificación: MVP.

**Verificación:** pruebas de DAO/repositorio para alta, edición y borrado; recorrido AVD de una experiencia completa; build, lint y pruebas instrumentadas.

## Hito 5 — “Tengo tiempo libre” y ranking explicable

**Objetivo:** recomendar pendientes locales según tiempo disponible y mostrar razones calculadas.

- Definir un ranking puro con duración, prioridad, antigüedad, afinidad local y retroalimentación; aplicar penalizaciones suaves a duración desconocida y pospuestos.
- Guardar preferencias y pesos mínimos en DataStore; no entrenar modelos ni usar datos de otras personas.
- Implementar acciones aceptar, posponer, descartar y realizar; cada acción actualiza solo el ranking local.
- Mostrar factores realmente usados en cada recomendación, sin frases no calculadas.
- Clasificación: MVP.

**Verificación:** pruebas unitarias deterministas de orden, desempate, duración desconocida y efecto de feedback; demostración AVD con casos controlados.

## Hito 6 — Versión de evaluación

**Objetivo:** cerrar una versión medible al término de noviembre.

- Ejecutar build, lint, pruebas unitarias, instrumentadas y recorridos manuales del MVP.
- Preparar APK y etiqueta Git de versión de evaluación con notas de cambios y lista de limitaciones.
- Congelar los flujos evaluados: OCR/Share Sheet, alta/corrección, ficha, experiencias, catálogo mínimo, ranking y privacidad local-first.
- Completar protocolos de OCR, usabilidad, tráfico y rendimiento antes de recoger resultados.
- Clasificación: MVP de evaluación.

**Verificación:** todas las pruebas disponibles en verde, evidencia de versión/commit/dispositivo y un checklist reproducible de cada flujo.

## Hito 7 — Evaluación y memoria

**Objetivo:** producir evidencia real sobre la versión de evaluación.

- Ejecutar OCR sobre 30 a 50 capturas autorizadas; registrar top-1, corrección manual y tiempos.
- Realizar prueba SUS con 5 a 8 participantes consentidos y tareas equivalentes.
- Inspeccionar tráfico de red y medir OCR/apertura de biblioteca con protocolo fijo.
- Ejecutar MovieLens offline solo si no desplaza la evaluación central; registrar conjunto, split, semilla, K y línea base.
- Redactar capítulos de resultados y discusión a partir de los datos, incluidos resultados negativos y limitaciones.
- Clasificación: evaluación del título.

**Verificación:** planillas anonimizadas, evidencias de red, resultados reproducibles y trazabilidad hacia el commit de evaluación.

## Hito 8 — Versión final y defensa

**Objetivo:** entregar una aplicación y memoria coherentes con la evidencia recolectada.

- Corregir defectos y problemas de accesibilidad/rendimiento observados sin modificar los flujos ni métricas de la versión de evaluación.
- Actualizar capítulos de implementación, resultados, limitaciones, conclusiones, anexos y demo.
- Mantener cuentas, nube, telemetría, sincronización y expansión de catálogos como trabajo futuro.
- Clasificación: MVP final.

**Verificación:** build y pruebas relevantes de la versión final; revisión de que la memoria identifica ambas versiones y no atribuye resultados posteriores a la evaluación.

## Orden inmediato

1. Ejecutar el Hito 0 en una tarea documental corta.
2. Abrir una tarea separada para el Hito 1; no iniciar Hito 2 hasta registrar el resultado del spike.
3. Revaluar alcance y tiempo al terminar cada hito; los recortes se deciden en este orden: MovieLens, catálogos secundarios, cambio de portada, detección automática de capturas. No recortar persistencia local, confirmación humana ni ranking explicable.
