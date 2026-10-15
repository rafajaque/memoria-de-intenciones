# Decisiones técnicas — Memoria de Intenciones

## 2026-10-14 — Repositorio inicial

- **Decisión:** usar Git con `main` como rama estable.
- **Motivo:** mantener un historial revisable y separar futuras tareas pequeñas mediante ramas descriptivas.
- **Consecuencia:** no se crearán ramas de funcionalidad hasta definir y aprobar el primer microhito.

## 2026-10-14 — Enfoque local-first

- **Decisión:** el MVP almacenará los datos del usuario en el dispositivo.
- **Motivo:** priorizar privacidad, funcionamiento sin internet y una implementación demostrable en ocho semanas.
- **Consecuencia:** cualquier integración con catálogos externos se limitará a consultas necesarias y deberá documentar sus términos y límites.

## 2026-10-14 — Stack previsto

- **Decisión:** Kotlin, Jetpack Compose, Material 3, MVVM, repositorios, Room y DataStore.
- **Motivo:** son tecnologías adecuadas para una aplicación Android nativa clara y explicable.
- **Consecuencia:** las versiones concretas, `minSdk` y `targetSdk` quedan pendientes de verificación antes de crear el proyecto.

## 2026-10-14 — Verificación inicial del entorno de desarrollo

- **Resultado:** el entorno disponible solo tiene Java 8 (`1.8.0_401`); no se detectaron Gradle, ADB, Android SDK ni Android Studio en las rutas habituales.
- **Referencia técnica:** la documentación oficial del Android Gradle Plugin 9.2 indica JDK 17, Gradle 9.4.1 y Build Tools 36.0.0 como configuración de compatibilidad; la documentación de Compose indica usar el BOM vigente y el plugin de Compose.
- **Decisión:** no crear todavía el proyecto Android hasta disponer de la herramienta de compilación.
- **Motivo:** un proyecto no verificable incumpliría la regla de reportar solo builds comprobados.
- **Consecuencia:** el siguiente paso requiere autorización para instalar o habilitar JDK 17, Android Studio y el Android SDK.

## 2026-10-15 — Preparación del entorno Android

- **Resultado:** se instaló Microsoft OpenJDK 17.0.20.1, Android SDK Platform Tools 37.0.1, Android SDK Platform 36 y Build Tools 36.0.0.
- **Configuración:** `JAVA_HOME` apunta al JDK 17 y `ANDROID_SDK_ROOT` apunta al SDK local del usuario.
- **Decisión:** usar API 36 como `compileSdk` y `targetSdk`, y API 26 como `minSdk` inicial.
- **Motivo:** son las herramientas instaladas y verificadas localmente; API 26 ofrece una base amplia para el MVP sin añadir compatibilidad innecesaria.
- **Consecuencia:** la compatibilidad real queda respaldada por el build debug ejecutado.

## 2026-10-15 — Proyecto Android base

- **Decisión:** usar AGP 9.2.0, Gradle 9.4.1, Kotlin 2.2.10, Compose BOM 2025.08.00 y JDK 17.
- **Motivo:** AGP y Gradle fueron verificados localmente; el BOM inicialmente previsto 2026.08.00 exigía `compileSdk 37`, cuya plataforma no está disponible en el SDK instalado.
- **Consecuencia:** se usa Compose BOM 2025.08.00, compatible con `compileSdk 36`, y se deja documentado el motivo del ajuste.

## 2026-10-15 — Primera verificación de build

- **Comando:** `gradlew.bat assembleDebug`
- **Resultado:** `BUILD SUCCESSFUL`.
- **Artefacto:** `app/build/outputs/apk/debug/app-debug.apk`.
