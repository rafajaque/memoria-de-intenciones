# PROJECT_STATE.md — Estado del proyecto

## Estado actual

- Fecha: 2026-10-16
- Proyecto Android: biblioteca navegable con gestión local de títulos
- Repositorio Git: inicializado
- Rama actual: `feat/escanear-isbn`
- Base de esta iteración: `97c517a` — identidad artesanal de la biblioteca
- Último cambio comprobado: escaneo ISBN en Redmi 12 5G y manejo de límite Google Books
- Remoto: `https://github.com/rafajaque/memoria-de-intenciones.git`

## Último cambio comprobado

### Escáner ISBN — Fase 1, 2026-10-16

- Se añadió validación local de ISBN-10 e ISBN-13, incluida la normalización de separadores y el dígito de control `X` para ISBN-10.
- La validación no llama red, no abre cámara y no persiste valores; será la barrera previa al lector en vivo y Google Books.
- `gradlew.bat assembleDebug lintDebug connectedDebugAndroidTest --console=plain`: `BUILD SUCCESSFUL`; 12 pruebas instrumentadas aprobadas en el AVD `memoria-api36`.
- Durante el primer intento no había dispositivo conectado; se inició el AVD y se repitió la verificación completa correctamente.
- Diseño aprobado: `docs/superpowers/specs/2026-10-15-escaner-isbn-libros-design.md` (commit `398a0fc`). Plan por fases: `docs/superpowers/plans/2026-10-16-plan-escaner-isbn-libros.md`.
- Se incorporó `BookMetadata` opcional para Libros: ISBN, autores, editorial, fecha, idioma, páginas, URL de portada y edición. Room migra de 2 a 3 mediante columnas nullable; películas, series y videojuegos mantienen sus fichas sin esos datos.
- La prueba de repositorio comprueba que esos metadatos se persisten para Libros. `assembleDebug`, `lintDebug` y `connectedDebugAndroidTest` terminaron con `BUILD SUCCESSFUL`; 13 pruebas instrumentadas aprobadas en `memoria-api36`.
- Todavía no hay consulta activa a Google Books, permiso/cámara, previsualización, descarga de portada ni exposición de los campos en el formulario. La siguiente microtarea es el contrato de búsqueda por ISBN, sin conectar UI.
- La consulta de Google Books por ISBN ya está aislada en `GoogleBooksBookCatalogRepository`; normaliza el código, limita cinco propuestas y no hace red en sus pruebas. Aún no está conectada a UI ni a cámara.
- `assembleDebug`, `lintDebug` y `connectedDebugAndroidTest` terminaron con `BUILD SUCCESSFUL`; 15 pruebas instrumentadas aprobadas en `memoria-api36`.

### Recorrido manual ISBN — verificado, 2026-10-16

- APK debug instalada en `memoria-api36`; se recorrió Biblioteca → Libros → Agregar título.
- Se concedió el permiso de cámara y la pantalla `Escanear ISBN` abrió correctamente.
- Al salir del escáner se conservó el formulario manual y no se creó ningún registro.
- Se inspeccionó la imagen real `Desktop/Memoria de intenciones/ISBN/www-001006524304570-01.avif`: contiene el ISBN `978-84-663-7497-2` y su código de barras. El original no se modificó.
- El AVD se configuró con una copia PNG de prueba mediante `-camera-back imagefile:<ruta>` y la vista de cámara mostró la portada, pero el encuadre quedó ampliado sobre el texto y dejó el código de barras fuera de pantalla. No se afirma detección ni consulta real todavía.
- Se añadió el fixture centrado `app/src/androidTest/assets/isbn-9788466374972.png` y el procedimiento reproducible en `docs/evaluation/recorrido-isbn-avd.md`; el original AVIF permanece intacto.
- El AVD se reinició con una copia sin espacios y un fixture recortado/desplazado para mejorar el encuadre del EAN-13. La entrada `imagefile` no mostró de forma estable el código en la previsualización y ML Kit no devolvió una lectura después de 6 segundos; no se afirma detección ni consulta Google Books.
- Bloqueo actual: aislar si CameraX entrega a `ImageAnalysis` los mismos fotogramas que `PreviewView` o si el formato/rotación de la entrada virtual impide a ML Kit reconocerlo. El flujo de permisos, navegación y salida manual sí está comprobado.
- Se añadió `IsbnBarcodeFixtureTest`, que carga el PNG directamente en ML Kit sin CameraX y reconoce `9788466374972`. `connectedDebugAndroidTest` terminó con `BUILD SUCCESSFUL` y 16 pruebas aprobadas. El fallo queda aislado en la entrada de `ImageAnalysis` del AVD.
- Se probó temporalmente `InputImage.fromMediaImage(media, 0)` en el APK instalado. El recorrido siguió sin lectura y la previsualización de la cámara virtual no mostró de forma estable el fixture; el cambio fue retirado y se conserva `imageInfo.rotationDegrees`.
- Se probó además una fuente `imagefile` de `640×480` con el EAN-13 reducido y centrado. El AVD alternó entre una previsualización en blanco y un encuadre ampliado que cortaba el código; no hubo lectura. No hay dispositivo físico conectado (`adb devices -l` sólo muestra `emulator-5554`). Siguiente paso bloqueado hasta disponer de cámara física o de otra fuente de frames del emulador.
- Se probó temporalmente salida RGBA de `ImageAnalysis` y conversión a `Bitmap`; compiló, pero el recorrido siguió sin detectar el código. El cambio se retiró por no aportar una mejora comprobada.
- Instrumentación temporal del APK: `ImageAnalysis` recibió frames YUV `640x480`, rotación `90°` y tres planos; ML Kit respondió `codes=[]` de forma consistente. Se retiró el log. El siguiente diagnóstico debe comparar el contenido real del frame analizado con el de `PreviewView`.
- Se capturó un único frame YUV convertido a PNG en debug: confirmó que el AVD recorta asimétricamente la imagen. El fixture desplazado mejora el encuadre, pero la captura diagnóstica interfirió con el ciclo de análisis y se retiró junto con todos los logs; no se cambia producción sin una prueba estable.
- El alta de Libros incluye el acceso al escáner ISBN. CameraX analiza EAN-13 en vivo con ML Kit, valida localmente y retorna el código leído al formulario; no persiste imágenes. La respuesta del catálogo se convierte en campos editables y se guarda como `BookMetadata` al confirmar.
- En el Redmi 12 5G el escáner leyó un ISBN real y volvió al formulario sin la pantalla blanca reproducida previamente. Se añadió una barrera de lectura única y se detienen el analizador y el cliente ML Kit al desmontar la vista, evitando múltiples `popBackStack()`.
- Google Books respondió `HTTP 429` durante la consulta real. El ISBN permanece en el formulario y ahora el error se transforma en un mensaje recuperable para completar el título manualmente; no se agregan reintentos automáticos.
- Se añadió Open Library como respaldo automático para respuestas 429, fallos temporales o resultados vacíos de Google Books. El respaldo mapea título, autores, editorial, año, idioma, páginas y portada al mismo formulario editable. La consulta real de Open Library devolvió datos para `9780306406157`; `9788466374972` no tiene registro allí.
- Verificación manual final en Xiaomi Redmi 12 5G: se escaneó un ISBN real, se rellenaron automáticamente los datos del libro usando el respaldo de catálogo y se guardó el registro en la estantería Libros.
- Cuando ninguno de los catálogos encuentra el ISBN, el formulario conserva el código y muestra: `No encontramos este ISBN en nuestros catálogos. Completa los datos manualmente.`
- Verificación manual del caso negativo en Xiaomi Redmi 12 5G: el mensaje aparece, el ISBN permanece editable, y el título se puede guardar y editar correctamente.
- `assembleDebug lintDebug compileDebugAndroidTestKotlin --console=plain`: `BUILD SUCCESSFUL`. La suite conectada posterior no pudo instalar el APK de pruebas en MIUI por `INSTALL_FAILED_USER_RESTRICTED`; la suite anterior en el Redmi había aprobado 17 pruebas antes del cambio de manejo 429.

### Autocompletado Google Books — Fase 4 parcial, 2026-10-16

- El ISBN leído vuelve al formulario y dispara una consulta de Google Books con timeout de 8 segundos.
- Una propuesta se aplica automáticamente; varias se muestran para selección explícita; cero resultados y errores dejan disponible el formulario manual.
- Título, ISBN, autores, editorial, fecha, idioma, páginas y URL de portada quedan editables y sólo se guardan al confirmar el formulario.
- La portada todavía se conserva como URL de metadato; la descarga y el placeholder visual quedan pendientes de la siguiente microtarea.
- Google Books conserva prioridad y Open Library se consulta automáticamente sólo como respaldo cuando el primer catálogo está limitado, falla temporalmente o no devuelve propuestas. Se añadieron repositorio, servicio y pruebas de mapeo/fallback; no se añadió una clave ni se envían datos personales.
- `assembleDebug lintDebug connectedDebugAndroidTest --console=plain`: `BUILD SUCCESSFUL`; 15 pruebas instrumentadas aprobadas en `memoria-api36`.

### Portadas visuales — Fase 4, 2026-10-16

- Coil 2.7.0 carga la URL de portada en la propuesta, el formulario y las fichas de Libros; el fallo o ausencia de URL muestra un placeholder local.
- No se guarda ningún archivo de imagen propio: la URL sigue siendo el único dato persistido y Coil gestiona su caché de ejecución.
- `assembleDebug`, `lintDebug` y `connectedDebugAndroidTest` terminaron con `BUILD SUCCESSFUL`; 15 pruebas instrumentadas aprobadas en `memoria-api36`.

Se implementó el spike técnico de Share Sheet + OCR local. La aplicación recibe una imagen compartida, la valida como JPEG/PNG/WebP, la copia a `filesDir/shared-captures` con límite de 15 MiB y usa el modelo latino empaquetado de ML Kit para leer texto en el dispositivo. La pantalla explica el alcance y muestra el resultado sólo de forma temporal: no crea un título, no escribe en Room, no registra texto y elimina la copia al cerrar, abandonar o fallar.

El resultado OCR ahora puede abrir una confirmación explícita: propone un título editable, permite escoger categoría y sólo guarda en Room tras pulsar `Confirmar y guardar`. Descartar conserva la biblioteca sin cambios. La propuesta es una heurística local de líneas cortas; no sustituye la revisión humana ni llama catálogos.

Se añadió el contrato normalizado y cliente inicial de Google Books con Retrofit/Kotlin serialization. No existe aún búsqueda conectada a UI, clave configurada, migración Room ni descarga de portada.

La nueva dependencia `com.google.mlkit:text-recognition:16.0.1` aporta OCR disponible sin descarga ni red, a cambio de aproximadamente 4 MB por arquitectura. No hay permisos de almacenamiento ni de internet añadidos. El filtro del manifiesto acepta `ACTION_SEND` con `image/*`; el importador resuelve el MIME concreto desde el proveedor antes de validar.

La prueba manual con una captura técnica creada en el emulador verificó que una intención `ACTION_SEND` abre la ficha de lectura y que una URI sin permiso se comunica como fallo recuperable sin exponer la ruta o URI en la interfaz. Además, una prueba instrumentada generó el texto sintético `MEMORIA OCR` como PNG, lo copió al directorio privado y comprobó su reconocimiento exitoso por ML Kit. No existe aún una prueba manual exitosa con una aplicación emisora real: `adb shell` no puede otorgar al receptor el permiso temporal que entrega el Share Sheet. Tampoco se han usado ni contabilizado cinco capturas autorizadas; esa evidencia sigue pendiente según el protocolo.

### Verificación realizada

- Prueba roja: `compileDebugAndroidTestKotlin` falló inicialmente porque no existían `PrivateImageCopier` ni `SharedImageImportException`.
- Prueba verde: `compileDebugAndroidTestKotlin` terminó con `BUILD SUCCESSFUL` tras incorporar el copiador privado.
- `assembleDebug` y `lintDebug`: terminaron sin errores. El informe local de lint queda en `app/build/reports/lint-results-debug.html` (ignorado por Git).
- `connectedDebugAndroidTest`: 8 pruebas aprobadas en `memoria-api36` (API 36), incluida la prueba de propuesta OCR que cubre la unión de líneas cortas para revisión.
- Recorrido ADB: una `ACTION_SEND` con URI de contenido abrió la ficha `Lectura local`; la URI generada por la shell no tuvo permiso de lectura para el paquete, y la aplicación mostró el error genérico recuperable esperado. No se considera una lectura OCR exitosa.

## Último cambio comprobado anterior

Se implementó la gestión de títulos desde las cuatro estanterías. El formulario guarda y edita título, estado, duración opcional y nota opcional en Room; al editar se conservan identidad, categoría y fecha de creación. Las fichas de cada estante abren el formulario precargado. Eliminar exige una confirmación que nombra el título y, tras aceptarla, vuelve al estante actualizado.

La base Room subió de versión 1 a 2 mediante una migración que añade `durationMinutes` sin borrar registros existentes. No se añadieron dependencias.

## Ubicación del trabajo

- Pantallas y materiales: `app/src/main/java/cl/rafit/memoriadeintenciones/`.
- Decoración reutilizable: `app/src/main/java/cl/rafit/memoriadeintenciones/ui/LibraryDecor.kt`.
- Alta, edición y eliminación de contenido: `app/src/main/java/cl/rafit/memoriadeintenciones/AddItemScreen.kt`.
- Persistencia y pruebas existentes: `app/src/main/java/cl/rafit/memoriadeintenciones/data/` y `app/src/androidTest/`.
- Decisiones: `docs/DECISIONS.md`.
- Spike de importación y OCR local: `app/src/main/java/cl/rafit/memoriadeintenciones/data/shared/` y `SharedCaptureScreen.kt`.

## Verificación realizada

### Edición y eliminación confirmada — 2026-10-15

- Prueba roja: `compileDebugAndroidTestKotlin` falló antes de añadir `LibraryRepository.edit(...)`, porque la operación aún no existía.
- `assembleDebug`: `BUILD SUCCESSFUL` después de integrar la ruta de edición, el formulario precargado y la confirmación.
- `connectedDebugAndroidTest`: 4 pruebas instrumentadas aprobadas en `memoria-api36`; la nueva prueba verifica que editar conserva `id`, categoría y fecha de creación, y que eliminar retira el título observado.
- Recorrido ADB: se creó `DuneEditar`, se abrió su ficha, se cambió de `PENDIENTE` a `TERMINADO` y el estante mostró el estado actualizado. También se abrió el diálogo de eliminación, que incluye el título, y se ejerció la ruta Cancelar. El AVD se desconectó durante la confirmación final, por lo que esa última comprobación visual queda respaldada por la prueba instrumentada, no por un segundo recorrido manual.
- No se añadieron dependencias. La eliminación no tiene deshacer en este MVP.

### Contenido real — 2026-10-15

- Refinamiento visual posterior: se retiraron los números locales delante de Películas, Series, Libros y Videojuegos; se conserva la referencia general `01 — 04` en el encabezado de la colección.
- Prueba roja: `compileDebugAndroidTestKotlin` falló inicialmente porque no existían `LibraryRepository.add(...)` ni `durationMinutes`.
- `connectedDebugAndroidTest`: 3 pruebas instrumentadas aprobadas en `memoria-api36`, incluida la nueva prueba de repositorio que normaliza título y nota, guarda duración y filtra por categoría.
- `assembleDebug lintDebug`: `BUILD SUCCESSFUL`. Lint conserva advertencias de configuración preexistentes, sin errores.
- Recorrido ADB en la APK: desde Películas se agregó `DunePrueba`, se comprobó la ficha, el contador `1 título guardado` y la biblioteca principal con `1 título`; al relanzar la aplicación el título seguía disponible.
- Captura local ignorada: `app/build/library-with-content.png`.
- La comprobación cubre alta y lectura. La migración 1→2 se incluye en el binario; falta una prueba automática específica de actualización desde un archivo de base de datos v1.

### Rediseño artesanal — 2026-10-15

- `gradlew.bat assembleDebug lintDebug --rerun-tasks`: `BUILD SUCCESSFUL`; lint informa 0 errores y 12 advertencias previas de versiones/API, reglas de respaldo e icono de aplicación.
- Se corrigió el escape de `:` en la ruta del SDK dentro de `local.properties` (archivo local ignorado por Git). Fue necesario repetir lint sin caché para renovar su informe.
- `gradlew.bat connectedDebugAndroidTest`: 2 pruebas instrumentadas Room, ambas aprobadas en `memoria-api36` (API 36).
- Prueba de interfaz con ADB: abrir Películas, Series, Libros y Videojuegos, comprobar sus estados vacíos y volver a la biblioteca.
- Se seleccionaron y revisaron los seis temas; Azul marino conservó la selección tras forzar el cierre y volver a abrir la aplicación.
- Se revisaron capturas de biblioteca y estado vacío con texto normal, y de biblioteca y ajustes con texto al 150 %. Se verificó la selección de tema con texto ampliado y se restauraron Madera cálida y la escala de texto original.
- La suite instrumentada retiró su instalación al terminar; se reinstaló la APK final y se abrió la biblioteca en el emulador.
- El primer intento del script local de revisión no encontraba una opción fuera de pantalla; se corrigió su desplazamiento y se completaron las comprobaciones restantes.
- Capturas e informes locales en `app/build/` (ignorados): `library-artesanal.png`, `library-empty-shelf.png`, `library-celeste.png`, `library-marino.png`, `library-large-text.png`, `library-settings-large-text.png` y `reports/lint-results-debug.html`.
- La revisión visual cubre el AVD actual en orientación vertical; no equivale a probar todos los dispositivos, TalkBack ni todas las escalas de texto.

### Entorno y microhitos anteriores

- La carpeta de trabajo estaba vacía antes de inicializar Git.
- `git init -b main` terminó correctamente y el remoto privado está configurado.
- Se verificó que `local.properties`, `build/`, `.idea/` y `google-services.json` quedan ignorados.
- Microsoft OpenJDK 17.0.20.1 está instalado y `JAVA_HOME` queda configurado de forma persistente.
- Android SDK configurado en `C:\Users\rafit\AppData\Local\Android\Sdk` con Platform Tools 37.0.1, plataforma Android 36, Build Tools 36.0.0 y Emulator 37.2.12.
- `ANDROID_SDK_ROOT` y `ANDROID_HOME` quedan configurados de forma persistente; `PATH` incluye Java, Platform Tools y `sdkmanager`.
- El Gradle Wrapper usa Gradle 9.4.1 y `gradlew.bat assembleDebug` terminó con `BUILD SUCCESSFUL`.
- APK generado y verificado en `app/build/outputs/apk/debug/app-debug.apk`.
- Se creó el AVD `memoria-api36` con imagen Android 36 estándar; ADB lo detectó como `emulator-5554`.
- La APK debug se instaló con éxito y `MainActivity` quedó en primer plano en el emulador.
- Android Studio 2026.2.1.8 está instalado y el proceso fue iniciado apuntando al proyecto.
- La pantalla principal se verificó visualmente en el AVD mediante una captura después de instalar la APK actualizada.
- Se verificó con ADB que Películas abre su propia vista y que el botón Atrás devuelve a la biblioteca.
- La navegación por estanterías está clasificada como funcionalidad MVP; el contenido real queda para la siguiente microtarea.
- Room 2.8.5 con KSP 2.3.10 está configurado; la entidad, DAO, base, repositorio y ViewModel están preparados para la UI.
- `connectedDebugAndroidTest` pasó 2 pruebas instrumentadas de inserción, filtrado, actualización y eliminación en una base Room en memoria.

## Decisiones vigentes

- Aplicación Android nativa con Kotlin y Jetpack Compose.
- Enfoque local-first y privado.
- `main` será la rama estable.
- Las funciones se implementarán en microtareas verificables.

## Notas

- Hay tres pruebas instrumentadas de Room y una revisión ADB del flujo de alta; todavía no existe una suite automatizada de pruebas Compose.

### Share Sheet: ISBN desde código de barras — 2026-10-16

- El flujo de captura compartida intenta primero leer un EAN-13 con ML Kit Barcode Scanning sobre la imagen completa y un recorte ampliado de la zona inferior; sólo después usa el ISBN encontrado por OCR textual.
- Cuando se obtiene un ISBN válido, se consulta el catálogo existente y su propuesta bibliográfica tiene prioridad sobre heurísticas de líneas OCR como nombres editoriales.
- La lectura del código y la consulta mantienen el comportamiento local-first del flujo: no se guarda la imagen compartida ni se registra el texto OCR; sólo se conserva la ficha al confirmar.
- Se añadió una prueba instrumentada sobre `isbn-9788466374972.png` para validar la lectura directa de `9788466374972`, además de una prueba de extracción desde texto.
- Verificación actual: `assembleDebug lintDebug compileDebugAndroidTestKotlin --console=plain` terminó correctamente y `git diff --check` no reportó errores. Falta el recorrido manual final en el Redmi 12 5G con la captura compartida para confirmar la propuesta visible de catálogo.
- Corrección posterior: si el ISBN se detecta pero ambos catálogos no entregan una ficha, la propuesta ya no reutiliza una línea OCR de editorial como título; deja el título vacío y solicita completarlo manualmente.
- Verificación manual posterior en Redmi 12 5G: un segundo libro con ISBN presente en Google Books/Open Library completó correctamente título, autor y editorial, y quedó listo para guardar. El caso `9788466374972` permanece como limitación de cobertura del catálogo, no como fallo de lectura del código.
- Idea futura registrada: permitir contribuciones bibliográficas opt-in de metadatos manuales cuando falte un ISBN, separadas de la biblioteca personal y sujetas a deduplicación, procedencia, moderación y protección de datos. No implementado en el MVP.

### Portadas privadas — 2026-10-16

- Las portadas bibliográficas se descargan al confirmar el libro en `filesDir/book-covers`, con límite de 10 MiB, sólo desde URLs HTTPS y validando que el contenido sea una imagen.
- Room pasa de versión 3 a 4 con la columna nullable `bookCoverLocalPath`; los registros existentes conservan su URL y continúan siendo compatibles.
- La interfaz usa primero la copia local, luego la URL remota y finalmente el placeholder. Al reemplazar o eliminar un libro se intenta limpiar la copia local anterior.
- Se añadieron pruebas instrumentadas para descarga válida, rechazo de HTTP/imagen inválida y eliminación. Verificación de compilación principal y `git diff --check` sin errores; falta el recorrido manual en Redmi.
- Verificación manual en Redmi 12 5G: un libro con portada se guardó, la portada permaneció visible al reabrir la aplicación y no se observaron problemas.

### Experiencias personales — 2026-10-16

- Se añadió `ItemExperience` como registro local opcional uno a uno con el título: plataforma/soporte, valoración de 1 a 5, comentario y fechas.
- La experiencia se ofrece desde la ficha cuando el estado es `TERMINADO`; no es obligatoria y no se mezcla con la nota personal del título.
- Room pasa de versión 4 a 5 con la tabla `item_experiences`; eliminar un título elimina también su experiencia.
- Se añadieron pruebas instrumentadas de creación, edición y eliminación separadas del título.
- `assembleDebug`, `lintDebug` y `compileDebugAndroidTestKotlin` terminaron con `BUILD SUCCESSFUL`; falta el recorrido manual en Redmi.
- Pendientes de configuración anteriores al rediseño: icono de aplicación y reglas explícitas de extracción/respaldo para Android 12+. No se modificaron en esta tarea visual.
- Las actualizaciones de SDK y dependencias deben planificarse por separado; no se cambian versiones solo para silenciar advertencias.
- Android Studio se verificó ejecutándose con el proyecto como argumento; los archivos temporales de arranques anteriores no afectan la ejecución actual.

## Siguiente acción exacta

Repetir el recorrido con una etiqueta EAN-13/ISBN visible para el emulador; comprobar propuesta, portada, cancelación y consulta sin conectividad.
