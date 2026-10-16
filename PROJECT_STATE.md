# PROJECT_STATE.md — Estado del proyecto

## Estado actual

- Fecha: 2026-10-15
- Proyecto Android: biblioteca navegable con gestión local de títulos
- Repositorio Git: inicializado
- Rama actual: `feat/share-ocr-local`
- Base de esta iteración: `97c517a` — identidad artesanal de la biblioteca
- Último cambio comprobado: confirmación humana de propuesta OCR (MVP)
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
- El alta de Libros incluye el acceso al escáner ISBN. CameraX analiza EAN-13 en vivo con ML Kit, valida localmente y retorna el código leído al formulario; no persiste imágenes. Aún falta convertir la respuesta de Google Books en campos editables y guardarla como `BookMetadata`.

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
- Pendientes de configuración anteriores al rediseño: icono de aplicación y reglas explícitas de extracción/respaldo para Android 12+. No se modificaron en esta tarea visual.
- Las actualizaciones de SDK y dependencias deben planificarse por separado; no se cambian versiones solo para silenciar advertencias.
- Android Studio se verificó ejecutándose con el proyecto como argumento; los archivos temporales de arranques anteriores no afectan la ejecución actual.

## Siguiente acción exacta

Completar la ejecución controlada de las cinco capturas autorizadas mediante el Share Sheet real, registrar métricas agregadas fuera de Git y revisar la nueva confirmación. Después, iniciar Hito 3: consulta de catálogos y portadas sin identidad.
