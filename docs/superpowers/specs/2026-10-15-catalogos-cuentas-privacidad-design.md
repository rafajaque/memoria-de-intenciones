# Diseño: catálogos, cuentas y privacidad para distribución

**Fecha:** 2026-10-15

**Estado:** diseño aprobado; pendiente de plan de implementación
**Clasificación:** arquitectura para distribución; el contenido local existente sigue siendo MVP

## Propósito y alcance

La aplicación se distribuirá inicialmente solo en Chile para personas mayores de 18 años. Mantendrá la biblioteca, notas y portadas descargadas en el dispositivo. Incorporará cuentas de correo y contraseña, búsqueda automática de metadatos y portadas, y telemetría o informes de error únicamente con consentimiento independiente.

La finalidad de la cuenta no es sincronizar la biblioteca en esta fase: protege las consultas a catálogos, permite gestionar consentimiento y habilita soporte técnico limitado.

Quedan fuera de esta fase: sincronizar títulos o notas, perfiles infantiles, inicio de sesión social, pagos, recomendaciones basadas en datos remotos y analítica de comportamiento.

## Decisiones aprobadas

- La fuente de portadas es un catálogo externo; las imágenes elegidas se copian después al almacenamiento privado de la aplicación.
- La búsqueda comienza automáticamente tras tres caracteres y una pausa breve; presenta como máximo cinco sugerencias y nunca selecciona una sin intervención.
- Películas y series se buscan de forma genérica; libros se distinguen por edición; videojuegos se filtran por consola o plataforma elegida.
- Las cuentas usan correo y contraseña, con verificación de correo y restablecimiento desde la primera versión distribuida.
- El lanzamiento inicial es para Chile y mayores de 18 años, declarado por la persona durante el registro.
- La telemetría y los informes automáticos de errores están desactivados inicialmente, se aceptan por separado y se pueden revocar.
- Se usará Cloudflare como intermediario para catálogos y cuentas. La aplicación no contiene credenciales de catálogos.

## Arquitectura

```text
Aplicación Android
  ├─ Room: biblioteca, notas, metadatos elegidos y archivos de portada privados
  ├─ DataStore: sesión segura y preferencias de consentimiento
  └─ API HTTPS propia
          │
          ▼
Cloudflare Worker
  ├─ autenticación y límites de uso
  ├─ búsqueda y normalización de catálogos
  ├─ caché temporal de respuestas públicas
  ├─ envío de correo transaccional
  ├─ recepción de telemetría consentida y errores saneados
  └─ D1: cuenta, consentimientos, sesiones revocables y eventos mínimos
          │
          ▼
TMDB · Google Books · Open Library · IGDB · proveedor de correo
```

El Worker es el único componente que puede leer secretos de proveedores. Se aplican límites por cuenta, sesión y origen, validación de entradas, límites de tamaño y tiempos de espera. La respuesta expone únicamente el modelo normalizado que la app necesita; nunca reenvía credenciales ni datos de cuenta a catálogos.

Cloudflare D1 no contendrá títulos, notas, capturas, portadas, historial de búsqueda ni el contenido de la biblioteca. Las portadas se descargan solo después de que la persona elija una sugerencia y se guardan localmente.

## Cuentas y sesiones

### Registro y acceso

1. La persona declara ser mayor de 18 años, ingresa correo y contraseña, y acepta la versión vigente de Términos y Política de Privacidad.
2. El servidor crea una cuenta pendiente, almacena un verificador de contraseña con un algoritmo resistente y envía un enlace de verificación de un solo uso.
3. Solo una cuenta verificada puede usar el catálogo distribuido.
4. El inicio de sesión entrega una sesión de corta duración y un mecanismo de renovación rotativo y revocable. Los valores de sesión se almacenan con almacenamiento seguro del sistema operativo, no en Room ni en logs.
5. La recuperación solicita un correo sin revelar si existe una cuenta; el enlace de restablecimiento es temporal, de un uso y revoca sesiones activas al cambiar la contraseña.

### Datos remotos de cuenta

| Dato | Finalidad | Retención propuesta |
| --- | --- | --- |
| Correo verificado | acceso, verificación y recuperación | hasta eliminar la cuenta |
| Verificador de contraseña | autenticación | hasta eliminar la cuenta o cambiar contraseña |
| Estado/versión de consentimientos | acreditar decisiones de privacidad | hasta eliminar la cuenta, salvo obligación legal documentada |
| Identificadores y estado de sesiones | revocación y seguridad | hasta expirar o revocar, máximo 90 días |
| Eventos mínimos de seguridad | investigar abuso y proteger cuentas | 90 días |

La eliminación de cuenta revoca sesiones y elimina estos datos. La biblioteca ya almacenada en el teléfono no se borra ni se sube como consecuencia de esta operación; la aplicación lo explicará antes de confirmar.

## Catálogos y modelo normalizado

### Proveedores

- **Películas y series:** TMDB. Debe incorporarse la atribución exigida en Acerca de o Créditos antes de publicar. TMDB expone búsqueda de películas, series e imágenes. [Documentación](https://developer.themoviedb.org/docs/finding-data) · [atribución](https://developer.themoviedb.org/docs/faq)
- **Libros principal:** Google Books, por su búsqueda por título, autor, editorial o ISBN y por sus metadatos de volumen. [Documentación](https://developers.google.com/books/docs/v1/using)
- **Libros de respaldo:** Open Library para resolver ediciones o cubiertas ausentes. Distingue una obra de sus ediciones y permite recuperar cubiertas por ISBN u otros identificadores. [API](https://openlibrary.org/dev/docs/api/books) · [cubiertas](https://openlibrary.org/dev/docs/api/covers)
- **Videojuegos:** IGDB, con búsqueda, plataformas y referencia de portada. [Documentación](https://api-docs.igdb.com/)

Antes de producción se revisarán los términos, límites y requisitos de atribución vigentes de cada proveedor y se documentarán sus cambios de versión.

### Consultas y selección

- La app inicia una búsqueda después de tres caracteres y 350 ms sin cambios; cancela la solicitud anterior al seguir escribiendo.
- El Worker devuelve un máximo de cinco resultados por solicitud y conserva en caché solo metadatos públicos durante un plazo corto definido en configuración.
- La persona debe seleccionar un resultado. Si no hay red, expira la sesión o falla el proveedor, puede conservar un registro manual sin portada y reintentar más tarde.

| Categoría | Criterios y campos de sugerencia |
| --- | --- |
| Película | título, año, póster y duración si existe |
| Serie | título, año inicial, póster y temporadas si existen |
| Libro | título, autor, editorial, fecha, formato/idioma e ISBN si existen; la edición elegida fija portada y metadatos |
| Videojuego | título, año, plataformas y carátula; la consola elegida se conserva junto a la selección |

Cada título local diferencia los datos escritos por la persona de los datos de catálogo. Editar un título personal no altera la fuente, edición, plataforma ni portada seleccionada. Si un catálogo solo ofrece una carátula genérica para varias consolas, la interfaz lo indicará.

## Privacidad, telemetría y errores

La Ley chilena 21.719 entra en vigencia el 1 de diciembre de 2026 y exige, entre otros, finalidad, transparencia, seguridad y mecanismos para ejercer derechos. [BCN](https://www.bcn.cl/leychile/Navegar/imprimir?idNorma=1209272) Este diseño busca facilitar su cumplimiento, pero no sustituye asesoría jurídica ni una revisión previa al lanzamiento.

El diseño se alinea con un sistema de gestión de seguridad conforme a ISO/IEC 27001:2022 y con gestión de privacidad conforme a ISO/IEC 27701:2025. La certificación formal depende de un alcance organizacional y auditoría independientes, no solo de la implementación técnica. [ISO 27001](https://www.iso.org/standard/27001) · [ISO 27701](https://www.iso.org/cms/live/live/en/sites/isoorg/contents/data/publication/20/02/PUB200277.html)

### Consentimiento

- El consentimiento para Términos/Política y el consentimiento opcional de telemetría son acciones separadas, informadas y versionadas.
- Telemetría y errores están apagados por defecto y se revocan desde Ajustes sin cerrar la cuenta.
- La aplicación ofrece exportar los datos remotos de cuenta y solicitar su eliminación. Debe incluir un correo o formulario de contacto para solicitudes de derechos.

### Datos opcionales y límites

| Tipo | Permitido | Prohibido |
| --- | --- | --- |
| Métricas | versión de app/Android, idioma, pantallas y eventos técnicos agregados | títulos, notas, consultas, correo, IP, identificador publicitario, capturas |
| Errores | tipo de excepción, versión, modelo general, traza saneada | tokens, contraseñas, correo, URL completa, texto de formularios o contenido de Room |

Un seudónimo técnico rotativo se usa solo mientras exista consentimiento y se regenera al revocarlo. Errores detallados se retienen 30 días; métricas agregadas, 12 meses; eventos de seguridad, 90 días. Cualquier excepción requiere finalidad, justificación, plazo y documentación.

## Operación y cumplimiento

- Identificar al responsable del tratamiento como persona natural en Chile mediante nombre legal y canal de contacto, que se completarán fuera del repositorio antes de publicar.
- Mantener inventario de datos, finalidades, bases de licitud, proveedores, transferencias internacionales y plazos de retención.
- Publicar Términos, Política de Privacidad y política de retención versionadas y accesibles antes del registro.
- Aplicar mínimo privilegio, MFA para accesos administrativos, secretos fuera de Git, rotación y trazabilidad administrativa mínima.
- Mantener un procedimiento de incidentes, evaluación de riesgos, revisión de proveedores y evaluación jurídica antes de producción.
- Validar contratos o términos de Cloudflare, correo y cada catálogo, incluida la documentación de transferencias internacionales que resulte aplicable.

## Pruebas y criterios de aceptación

Antes de distribuir una versión con este sistema se comprobará:

1. Registro, verificación, inicio, recuperación, cierre y revocación de sesión.
2. Protección frente a enumeración de correos, fuerza bruta, reutilización de enlaces y acceso sin verificar.
3. Que ninguna clave de catálogo, contraseña, token, título, nota o captura aparezca en el APK, repositorio, telemetría o logs.
4. Búsqueda cancelable, máximo de cinco sugerencias, selección explícita y funcionamiento manual sin red.
5. Resolución de edición de libro y plataforma de videojuego, con portada privada tras selección.
6. Consentimiento inicial desactivado, revocación efectiva, saneamiento de errores y plazos de retención configurables.
7. Exportación y eliminación de datos remotos de cuenta, sin alterar la biblioteca local.
8. Atribuciones visibles y términos/límites revisados para cada catálogo.
9. Revisión de amenazas, pruebas de API y revisión jurídica de los textos y tratamientos antes del lanzamiento.

## Riesgos y mitigaciones

| Riesgo | Mitigación |
| --- | --- |
| Exposición de claves en Android | el Worker conserva secretos; controles de tasa y rotación |
| Abuso de la API pública | cuenta verificada, límites, validación, observabilidad mínima y revocación |
| Metadatos o portada incorrectos | sugerencias cortas y selección explícita; edición/plataforma visibles |
| Falta de cobertura de un proveedor | registro manual funcional, reintento y fuente de respaldo para libros |
| Tratamiento excesivo de datos | biblioteca local, consentimiento opcional, minimización, retención y derechos |
| Cumplimiento incompleto | inventario, revisión de proveedores, pruebas y asesoría jurídica antes de producción |

## Siguiente paso

Convertir este diseño en un plan por microhitos. El primer microhito será preparar el contrato de API y el modelo local de metadatos sin desplegar ni habilitar cuentas públicas.
