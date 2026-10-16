# Plan: escáner ISBN para libros

## Ejecución seleccionada

Se ejecutará por fases pequeñas y verificables. Cada fase conserva el alta manual de Libros como alternativa y no añade una operación automática de guardado.

## Fase 1 — Contrato ISBN y modelo bibliográfico

1. Confirmar `domain/Isbn` con pruebas de ISBN-10, ISBN-13, normalización y checksum inválido.
2. Incorporar un valor de dominio para los metadatos bibliográficos opcionales y extender el registro local sin afectar categorías no literarias.
3. Definir el contrato de repositorio de consulta por ISBN, sin red en pruebas.
4. Añadir migración Room y pruebas de persistencia/migración.

**Criterio de salida:** los metadatos opcionales se persisten localmente y las pruebas de dominio y Room pasan.

## Fase 2 — Catálogo de libros

1. Completar el cliente Google Books con búsqueda exacta por ISBN, límite de resultados y timeout.
2. Mapear respuestas completas, incompletas y múltiples a propuestas de libro sin inventar edición.
3. Exponer estados de éxito, vacío y error recuperable para el formulario.
4. Mantener Open Library como respaldo futuro, sin activarlo en esta fase.

**Criterio de salida:** el repositorio entrega propuestas normalizadas y el formulario puede seguir en modo manual si falla la consulta.

## Fase 3 — Escáner en vivo local

1. Centralizar dependencias CameraX y ML Kit Barcode Scanning empaquetado; justificar versión y costo en decisiones.
2. Declarar y solicitar `CAMERA` sólo en la ruta de escaneo.
3. Crear una ruta de cámara con `ImageAnalysis`, limitada a EAN-13, y validar el ISBN en el dispositivo antes de devolverlo.
4. Liberar cámara y analizador al cerrar la pantalla, evitar múltiples detecciones y no persistir fotogramas.
5. Probar detección/rechazo y rutas de permiso concedido/denegado.

**Criterio de salida:** un ISBN válido vuelve al formulario sin capturar ni guardar imagen; la denegación deja disponible el alta manual.

## Fase 4 — Revisión y guardado humano

1. Mostrar “Escanear ISBN” solamente en el alta de Libros.
2. Consultar tras una detección válida y presentar una propuesta editable con origen y estados de carga/error.
3. Mostrar selección explícita ante varias propuestas y un placeholder local si la portada remota falla.
4. Guardar únicamente al pulsar el botón existente de confirmación; editar y eliminar conservan o eliminan metadatos con el título.

**Criterio de salida:** se puede escanear, revisar y guardar una ficha; cancelar o fallar no crea datos.

## Fase 5 — Verificación y documentación

1. Ejecutar `assembleDebug`, `lintDebug` y `connectedDebugAndroidTest`.
2. Recorrido AVD con ISBN real: permiso concedido, revisión/cancelación, permiso denegado y alta manual sin conectividad.
3. Actualizar `docs/DECISIONS.md` y `PROJECT_STATE.md` con resultados reales, dependencias, riesgos y siguiente paso.
4. Crear commits españoles separados por fases verificadas; excluir siempre `Memoria de intenciones.zip`.

## Restricciones permanentes

- Cámara, OCR/lector y fotogramas permanecen en el dispositivo; no existe archivo de foto.
- Sólo se consulta un ISBN validado a Google Books; no se envían identidad, notas, capturas ni historial.
- No se crean cuentas, Cloudflare, telemetría, sincronización ni Open Library en este alcance.
- La edición se conserva vacía si el catálogo no entrega un dato inequívoco.
