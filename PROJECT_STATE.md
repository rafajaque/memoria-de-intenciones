# PROJECT_STATE.md — Estado del proyecto

## Estado actual

- Fecha: 2026-10-15
- Proyecto Android: esqueleto base creado
- Repositorio Git: inicializado
- Rama actual: `feat/proyecto-base`
- Último commit: `c2fa200 chore: crear proyecto Android base`
- Remoto: `https://github.com/rafajaque/memoria-de-intenciones.git`

## Último cambio comprobado

Se creó el proyecto Android base con Kotlin integrado por AGP, Compose, manifest, actividad inicial y Gradle Wrapper; se publicó como respaldo privado en GitHub.

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

## Decisiones vigentes

- Aplicación Android nativa con Kotlin y Jetpack Compose.
- Enfoque local-first y privado.
- `main` será la rama estable.
- Las funciones se implementarán en microtareas verificables.

## Bloqueos

- Android Studio no quedó registrado por `winget`; la instalación del SDK por herramientas de línea de comandos sí quedó verificada.
- No hay pruebas automatizadas todavía.

## Siguiente acción exacta

Crear la primera pantalla navegable de «Mi biblioteca» y su pantalla de ajustes en una microtarea separada.
