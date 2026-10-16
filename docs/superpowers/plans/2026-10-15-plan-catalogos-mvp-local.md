# Plan: catálogos mínimos locales

1. Crear contrato `CatalogSearchRepository` y modelos normalizados, sin red en pruebas.
2. Agregar cliente Google Books con URL, tiempo de espera, máximo cinco resultados y clave leída sólo desde `local.properties`.
3. Implementar mapeo de respuesta y fallback manual; dejar Open Library como adaptador posterior para edición/cubierta ausente.
4. Añadir migración Room para metadatos elegidos y referencia de archivo de portada privado.
5. Incorporar búsqueda cancelable y selección explícita en el formulario de libros; preparar las rutas de película/serie sin activar TMDB.
6. Descargar portada únicamente tras selección a almacenamiento privado y mostrar marcador ante fallo.
7. Probar mapeo, límite, fallback y copia de archivo; comprobar AVD con y sin red, build, lint y pruebas instrumentadas.

## Restricciones

- Ninguna clave, captura, nota o historial se confirma en Git.
- No se activa TMDB, Cloudflare, cuentas ni telemetría.
- Si falta clave o red, el flujo manual permanece funcional.
