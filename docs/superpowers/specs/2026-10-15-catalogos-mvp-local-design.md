# Diseño: catálogos mínimos y portadas privadas

**Estado:** propuesto para revisión antes de implementar.

## Objetivo

Permitir que una persona busque y elija metadatos de catálogo para libros, películas y series, sin cuenta ni historial remoto. La alta manual continúa disponible cuando no haya red, clave o coincidencia.

## Alcance MVP

- Libros: Google Books como búsqueda inicial; Open Library sólo como respaldo de edición o portada ausente.
- Películas y series: contrato y UI preparados; TMDB no se activa hasta revisar clave, atribución y términos vigentes.
- Máximo cinco resultados, selección humana obligatoria y consulta después de 350 ms sin cambios.
- Se envía únicamente término, categoría y filtro de edición/plataforma cuando aplique. No se envían capturas, notas, identificadores ni historial.

## Datos locales

`CatalogMetadata` separa proveedor, id externo, título, año, autores, editorial, formato, idioma, ISBN y URL de portada de los campos personales. La imagen se descarga sólo después de elegir y se copia a almacenamiento privado; la URL no se vuelve a consultar automáticamente.

Room se migra sólo cuando el contrato quede probado. La UI muestra un marcador local elegante si no hay portada.

## Flujo

1. La persona escribe al menos tres caracteres en el formulario.
2. El repositorio cancela la consulta anterior y busca hasta cinco coincidencias.
3. La pantalla muestra título, campos disponibles y portada remota como previsualización.
4. Elegir una coincidencia rellena la ficha, pero guardar sigue siendo una acción explícita.
5. Al guardar, los metadatos y la portada pasan al dispositivo; al fallar, se conserva el alta manual.

## Proveedores y límites

Google Books admite búsqueda de volúmenes y metadatos públicos; la clave de desarrollo vive en `local.properties` y nunca se confirma en Git. Es apta sólo para desarrollo, no para una APK distribuida. Open Library distingue obras y ediciones y sirve para resolver ISBN/cubierta. TMDB requiere una decisión posterior de atribución y credenciales.

## Pruebas

- Mapeo de respuestas JSON con datos ausentes.
- Límite de cinco, cancelación y fallback manual.
- Copia y eliminación segura de portada privada.
- Recorrido AVD con red y sin red; inspección de que no se transmiten capturas, notas ni identidad.

## Fuera de alcance

Cuentas, Cloudflare, telemetría, videojuegos, sincronización, caché remota y selección automática.
