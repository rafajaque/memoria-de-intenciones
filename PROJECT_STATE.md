# PROJECT_STATE.md — Estado del proyecto

## Estado actual

- Fecha: 2026-10-15
- Proyecto Android: biblioteca visual navegable; integración de registros con la UI pendiente
- Repositorio Git: inicializado
- Rama actual: `feat/estanteria-artesanal`
- Base de esta iteración: `3201aa4` — persistencia local con Room
- Último cambio comprobado: rediseño de biblioteca artesanal (MVP)
- Remoto: `https://github.com/rafajaque/memoria-de-intenciones.git`

## Último cambio comprobado

Se rediseñaron biblioteca, estanterías vacías y ajustes con madera, encuadernaciones, papel, fichas de catálogo y tipografía editorial. Las ilustraciones se dibujan con Canvas y no representan registros guardados. Se mantienen las cuatro categorías, los seis temas (incluidos Celeste cielo y Azul marino), las rutas existentes y la preferencia local con DataStore.

La infraestructura Room del microhito anterior continúa disponible, pero todavía no alimenta las pantallas ni los contadores. Esta iteración es exclusivamente visual y no añade dependencias.

## Ubicación del trabajo

- Pantallas y materiales: `app/src/main/java/cl/rafit/memoriadeintenciones/`.
- Decoración reutilizable: `app/src/main/java/cl/rafit/memoriadeintenciones/ui/LibraryDecor.kt`.
- Persistencia y pruebas existentes: `app/src/main/java/cl/rafit/memoriadeintenciones/data/` y `app/src/androidTest/`.
- Decisiones: `docs/DECISIONS.md`.

## Verificación realizada

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

- Hay dos pruebas instrumentadas de Room; la revisión visual actual usa ADB y capturas, no una suite automatizada de pruebas Compose.
- Pendientes de configuración anteriores al rediseño: icono de aplicación y reglas explícitas de extracción/respaldo para Android 12+. No se modificaron en esta tarea visual.
- Las actualizaciones de SDK y dependencias deben planificarse por separado; no se cambian versiones solo para silenciar advertencias.
- Android Studio se verificó ejecutándose con el proyecto como argumento; los archivos temporales de arranques anteriores no afectan la ejecución actual.

## Siguiente acción exacta

Crear el formulario MVP para agregar un `LibraryItem` y conectar la primera estantería con datos reales.
