# AGENTS.md — Memoria de Intenciones

## Proyecto

Aplicación Android nativa, privada y local-first para convertir capturas compartidas en pendientes, registrar experiencias de entretenimiento y recomendar contenido según el tiempo disponible.

El proyecto es individual y tiene una duración prevista de ocho semanas. La prioridad es una aplicación funcional, explicable y demostrable.

## Reglas de trabajo

- Trabajar en una tarea pequeña por iteración.
- Antes de editar, revisar `git status`, la rama actual y los archivos relevantes.
- Indicar los archivos y el objetivo antes de modificar código.
- No afirmar que algo funciona sin haberlo comprobado.
- Marcar cada función como MVP, opcional o futura antes de programarla.
- No añadir dependencias sin justificar su función, versión y costo de mantenimiento.
- Registrar las decisiones importantes en `docs/DECISIONS.md`.
- No borrar ni reestructurar trabajo existente sin justificarlo y obtener confirmación.

## Arquitectura prevista

- Kotlin, Jetpack Compose y Material 3.
- Capas simples: `ui`, `domain`, `data`, `navigation` y `theme`.
- MVVM con ViewModel, repositorios y Coroutines/Flow.
- Inyección de dependencias manual.
- Room como fuente de verdad local.
- DataStore para tema y preferencias.
- Retrofit, OkHttp y kotlinx.serialization para consultas externas.
- Coil para imágenes.
- ML Kit para OCR en el dispositivo.
- Versiones centralizadas en `gradle/libs.versions.toml`.

## Seguridad y privacidad

- Los datos del usuario permanecen en el dispositivo en el MVP.
- Las capturas se copian al almacenamiento privado de la aplicación.
- No subir capturas ni datos personales a servidores.
- Guardar claves en `local.properties` o `BuildConfig`, nunca en Git.
- No exponer datos privados en logs.
- Verificar términos, atribución y límites antes de integrar catálogos externos.

## Git

- `main` es la rama estable.
- Cada tarea de código usa una rama corta y descriptiva con prefijo `feat/`, `fix/`, `docs/`, `test/`, `refactor/` o `chore/`.
- Los commits usan mensajes claros en español, por ejemplo `feat: crear biblioteca inicial`.
- No usar `reset --hard`, `clean -fd`, force push ni descartar cambios sin autorización.
- No crear ramas de funcionalidad hasta aprobar el microhito correspondiente.

## Continuidad

- Al iniciar una sesión: leer este archivo y `PROJECT_STATE.md`, revisar Git y consultar el README pertinente.
- Después de cada tarea verificada, actualizar `PROJECT_STATE.md`.
- Si una tarea queda incompleta, documentar el estado real, el bloqueo y el comando para retomarla.
- El código y las pruebas verificadas prevalecen sobre la documentación.

## Definición de terminado

1. La tarea compila cuando corresponda.
2. Las pruebas relevantes fueron ejecutadas y reportadas con su resultado real.
3. `PROJECT_STATE.md` está actualizado.
4. Los cambios están en una rama descriptiva y tienen un commit claro cuando corresponda.
5. Los riesgos y el siguiente paso están documentados.
