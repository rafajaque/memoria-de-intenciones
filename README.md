# Memoria de Intenciones

Aplicación Android nativa, privada y local-first para:

- convertir capturas compartidas en intenciones pendientes;
- registrar experiencias de películas, series, anime, libros y videojuegos;
- recomendar qué consumir según el tiempo libre disponible.

## Estado

Existe un proyecto Android base en la rama `feat/proyecto-base`. Incluye una pantalla Compose mínima y genera un APK debug.

## Stack previsto

- Kotlin
- Jetpack Compose y Material 3
- MVVM, repositorios y Coroutines/Flow
- Room para datos locales
- DataStore para preferencias
- ML Kit para OCR local

Las versiones verificadas están en `gradle/libs.versions.toml` y las decisiones en `docs/DECISIONS.md`.

## Comandos

En Windows, con JDK 17 y Android SDK configurados:

```powershell
.\gradlew.bat assembleDebug
```

## Privacidad

El MVP será local-first: los datos del usuario permanecerán en el dispositivo. Las capturas importadas se copiarán al almacenamiento privado de la aplicación y no se subirán a servidores.

## Documentación

- `AGENTS.md`: reglas estables de trabajo.
- `PROJECT_STATE.md`: estado operativo y siguiente acción.
- `docs/DECISIONS.md`: decisiones técnicas y sus motivos.
