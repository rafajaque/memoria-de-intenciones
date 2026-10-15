# PROJECT_STATE.md — Estado del proyecto

## Estado actual

- Fecha: 2026-10-15
- Proyecto Android: esqueleto base creado
- Repositorio Git: inicializado
- Rama actual: `feat/proyecto-base`
- Último commit: `d7edbb9 feat: agregar biblioteca, ajustes y temas`
- Remoto: `https://github.com/rafajaque/memoria-de-intenciones.git`

## Último cambio comprobado

Se implementó la navegación entre biblioteca y ajustes, seis temas visuales y persistencia local del tema con DataStore.

## Archivos actuales

- `.gitignore`
- `AGENTS.md`
- `README.md`
- `PROJECT_STATE.md`
- `docs/DECISIONS.md`

## Verificación realizada

- La carpeta de trabajo estaba vacía antes de inicializar Git.
- `git init -b main` terminó correctamente.
- Se verificó que `local.properties`, `build/`, `.idea/` y `google-services.json` quedan ignorados.
- El entorno solo expone Java 8 (`1.8.0_401`).
- No se detectaron comandos `gradle` ni `adb`, variables `ANDROID_HOME`/`ANDROID_SDK_ROOT`, Android Studio ni Android SDK en las rutas habituales.
- Se instaló Microsoft OpenJDK 17.0.20.1.
- Se instaló el Android SDK con Platform Tools 37.0.1, plataforma Android 36 y Build Tools 36.0.0.
- Se configuraron las variables de usuario `JAVA_HOME` y `ANDROID_SDK_ROOT`.
- `gradlew.bat assembleDebug` terminó con `BUILD SUCCESSFUL`.
- APK generado en `app/build/outputs/apk/debug/app-debug.apk`.
- Se verificó `git diff --check` sin errores de formato.
- Android Studio 2026.2.1.8 quedó instalado.
- Se creó el AVD `memoria-api36` con imagen Android 36 estándar.
- La APK debug se instaló con ADB y `MainActivity` quedó en primer plano en `emulator-5554`.

## Decisiones vigentes

- Aplicación Android nativa con Kotlin y Jetpack Compose.
- Enfoque local-first y privado.
- `main` será la rama estable.
- Las funciones se implementarán en microtareas verificables.

## Bloqueos

- Android Studio no quedó registrado por `winget`; la instalación del SDK por herramientas de línea de comandos sí quedó verificada.
- No hay pruebas automatizadas todavía.
- La ventana de Android Studio no permanece abierta durante el arranque en este entorno; la ejecución verificada se hizo contra el AVD mediante ADB.

## Siguiente acción exacta

Crear pruebas de UI para navegación y selección de tema, o preparar un emulador antes de la siguiente microtarea.
