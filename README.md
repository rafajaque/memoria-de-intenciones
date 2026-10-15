# Memoria de Intenciones

Aplicación Android nativa, privada y local-first para:

- convertir capturas compartidas en intenciones pendientes;
- registrar experiencias de películas, series, anime, libros y videojuegos;
- recomendar qué consumir según el tiempo libre disponible.

## Estado

La aplicación incluye cuatro estanterías navegables, una identidad visual de biblioteca artesanal y seis ambientes: Madera cálida, Bosque, Universo, Papel antiguo, Celeste cielo y Azul marino. La elección se guarda con DataStore.

La biblioteca permite agregar títulos desde cada estantería con estado, duración aproximada y nota opcional. Los registros se guardan en Room, actualizan el contador y sustituyen la ilustración decorativa por lomos y fichas reales. Aún faltan edición, eliminación, portadas y búsqueda externa.

El estado de la rama de trabajo y las comprobaciones recientes se registran en `PROJECT_STATE.md`.

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
.\gradlew.bat lintDebug
# Con un emulador Android iniciado:
.\gradlew.bat connectedDebugAndroidTest
```

## Privacidad

El MVP será local-first: los datos del usuario permanecerán en el dispositivo. Las capturas importadas se copiarán al almacenamiento privado de la aplicación y no se subirán a servidores.

## Documentación

- `AGENTS.md`: reglas estables de trabajo.
- `PROJECT_STATE.md`: estado operativo y siguiente acción.
- `docs/DECISIONS.md`: decisiones técnicas y sus motivos.
