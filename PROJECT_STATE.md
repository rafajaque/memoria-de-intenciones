# PROJECT_STATE.md — Estado del proyecto

## Estado actual

- Fecha: 2026-10-15
- Proyecto Android: esqueleto base creado
- Repositorio Git: inicializado
- Rama actual: `feat/persistencia-room`
- Último cambio comprobado: pendiente de commit del microhito Room
- Remoto: `https://github.com/rafajaque/memoria-de-intenciones.git`

## Último cambio comprobado

Se implementó la navegación entre biblioteca y ajustes, seis temas visuales, persistencia local del tema con DataStore, la primera mejora visual de la biblioteca digital, vistas propias para cada estantería y la capa inicial de persistencia Room.

## Archivos actuales

- `.gitignore`
- `AGENTS.md`
- `README.md`
- `PROJECT_STATE.md`
- `docs/DECISIONS.md`

## Verificación realizada

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

- No hay pruebas automatizadas todavía.
- Android Studio se verificó ejecutándose con el proyecto como argumento; los archivos temporales de arranques anteriores no afectan la ejecución actual.

## Siguiente acción exacta

Crear el formulario MVP para agregar un `LibraryItem` y conectar la primera estantería con datos reales.
