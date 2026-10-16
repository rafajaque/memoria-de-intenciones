# Diseño — Escáner ISBN y ficha bibliográfica

## Propósito y alcance

**Clasificación: MVP.** Al agregar un libro, la persona podrá abrir un escáner en vivo, leer un ISBN y revisar una ficha bibliográfica precargada antes de guardarla en su biblioteca local. El objetivo es reducir la digitación, no crear registros automáticos sin revisión.

El alcance incluye ISBN-10 e ISBN-13, consulta por ISBN en Google Books y almacenamiento local de los metadatos aceptados. Open Library queda como respaldo futuro. No se implementan cuentas, sincronización, analítica, telemetría, historial remoto ni carga de fotografías.

## Flujo de la persona usuaria

1. En la estantería **Libros**, selecciona “Agregar título” y después “Escanear ISBN”.
2. La aplicación pide el permiso de cámara sólo al abrir el escáner. Muestra una previsualización en vivo con una indicación para alinear el código de barras.
3. El lector local acepta únicamente EAN-13 y valida que el valor corresponda a un ISBN-13. El valor se normaliza y valida en el dispositivo antes de cualquier consulta.
4. Al detectar un ISBN válido, se detiene el análisis y se consulta Google Books con ese ISBN, sin cuenta, notas, captura ni historial.
5. La persona revisa una propuesta editable: ISBN, título, autores, editorial, fecha de publicación, idioma, cantidad de páginas y portada cuando existan. Si hay varias ediciones, elige una propuesta explícitamente.
6. Al confirmar, se guarda la ficha en Room. Cancelar o volver no escribe nada. Puede completar manualmente si el permiso se deniega, no hay red, no existe coincidencia o faltan datos.

La cámara se usa como flujo en vivo; no se captura, almacena ni transmite un archivo de imagen.

## Arquitectura y datos

Se mantiene la arquitectura actual `ui`, `domain` y `data` con inyección manual.

- `domain/Isbn`: normaliza y comprueba checksum ISBN-10/ISBN-13 sin dependencias Android.
- `ui`: una ruta temporal de escáner, basada en `CameraX`, devuelve el ISBN al flujo de alta de Libros. El formulario contiene estado de carga, propuesta, errores recuperables y edición humana.
- `data/catalog`: un repositorio de libros consume el cliente existente de Google Books por ISBN y normaliza respuestas a un contrato de dominio. No registra la consulta ni la respuesta.
- `data/local`: los campos bibliográficos del libro son nullable para preservar registros existentes de otras categorías y permitir una ficha incompleta. La migración Room debe conservar todos los títulos ya guardados.

El registro local mantendrá, cuando estén disponibles y se confirmen: `isbn`, `authors`, `publisher`, `publishedDate`, `language`, `pageCount` y `coverUrl`. El título, categoría, estado, nota y fechas existentes se conservan. “Edición” sólo se muestra o guarda si la fuente aporta un valor inequívoco; Google Books no debe inferirse a partir de la fecha, el ISBN ni el texto libre.

La portada se referencia por URL remota obtenida del catálogo. Coil la descarga al mostrarse y usa el placeholder local si falla. No se descarga de forma preventiva ni se copia al almacenamiento de la aplicación en este hito.

## Dependencias y permisos

- `androidx.camera:camera-core`, `camera-camera2`, `camera-lifecycle` y `camera-view`: previsualización y análisis en vivo con ciclo de vida Android. Se centralizará una versión compatible en `libs.versions.toml`.
- `com.google.mlkit:barcode-scanning`: variante empaquetada. Aumenta aproximadamente 2,4 MB, pero reconoce el código desde el primer uso sin descargar un modelo ni depender de Google Play Services.
- Permiso `android.permission.CAMERA`: solicitado en tiempo de ejecución exclusivamente al abrir el escáner. Sin permiso, el alta manual sigue disponible.

No se añadirá una dependencia para tomar o almacenar fotos. La elección de formatos se restringe a EAN-13 para evitar aceptar códigos irrelevantes; el checksum de ISBN es una segunda defensa local.

## Privacidad y conectividad

La señal de cámara y los fotogramas se procesan localmente y se descartan de inmediato. La única solicitud externa es la búsqueda por ISBN hacia Google Books, después de detectarlo y validarlo. No contiene identidad, correo, notas, capturas ni identificadores propios de la biblioteca.

La aplicación debe comunicar cuando consulta el catálogo y tratar falta de red, respuesta vacía, respuesta inválida y error temporal como errores recuperables. Ningún fallo de catálogo impide crear una ficha manual. Las claves no son necesarias para la búsqueda pública por ISBN y no se incorporan al binario ni a Git.

## Decisiones de interfaz

- El escáner se abre únicamente desde Libros; no aparece en películas, series ni videojuegos.
- La aplicación no guarda automáticamente al detectar un código ni al recibir un resultado remoto.
- Se muestra el ISBN leído y el origen “Google Books” en la revisión, para que la persona pueda corregir o descartar la propuesta.
- Si la fuente entrega varias propuestas, se muestran opciones diferenciadas por título, autores, editorial y fecha, evitando asumir que un ISBN siempre corresponde a una sola presentación comercial.

## Verificación

1. Pruebas unitarias para normalización y checksums ISBN-10/ISBN-13, incluidos casos inválidos.
2. Pruebas de mapeo de respuestas completas, incompletas y múltiples de Google Books.
3. Pruebas de repositorio para migración y persistencia de metadatos nullable sin afectar otras categorías.
4. Prueba instrumentada del analizador con un ISBN válido y rechazo de formatos/códigos inválidos.
5. Compilación `assembleDebug`, `lintDebug` y pruebas instrumentadas en el AVD.
6. Recorrido manual: conceder permiso, escanear un ISBN real, revisar/cancelar; denegar permiso; y completar manualmente sin red.

## Riesgos y límites

- La calidad del enfoque, iluminación y hardware puede impedir la detección; el flujo debe explicar cómo reencuadrar y permitir volver al formulario.
- Los catálogos pueden entregar datos incompletos o inconsistentes, especialmente edición y portada. La revisión humana es obligatoria.
- La disponibilidad y términos de Google Books deben revisarse antes de distribución pública; el uso de este hito es una consulta de metadatos mínima sin autenticación.
- La selección de varias versiones y el respaldo Open Library se implementarán después de comprobar este flujo de extremo a extremo.
