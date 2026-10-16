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

## 2026-10-15 — Navegación, temas y preferencias

- **Decisión:** usar Navigation Compose 2.9.6 y DataStore Preferences 1.2.1.
- **Motivo:** Navigation 2.10.2 y Compose más recientes requieren `compileSdk 37`, que no está instalado; estas versiones permiten verificar el proyecto con API 36.
- **Decisión visual:** conservar Madera cálida, Bosque, Universo y Papel antiguo, y añadir Celeste cielo y Azul marino.
- **Implementación:** tokens de color centralizados en `Theme.kt`; el nombre del tema se guarda localmente con DataStore.
- **Verificación:** `gradlew.bat assembleDebug` terminó con `BUILD SUCCESSFUL`; la APK se instaló y se abrió en el AVD `emulator-5554`.

## 2026-10-15 — Biblioteca digital con lenguaje de estantería

- **Decisión:** representar la pantalla principal como una biblioteca personal digital, con un resumen destacado y estanterías por categoría.
- **Motivo:** acercar la experiencia a una biblioteca real sin introducir imágenes externas ni dependencias adicionales en esta etapa.
- **Implementación:** tarjetas con lomos de color para Películas, Series, Libros y Videojuegos; el contenido continúa siendo explícitamente de demostración.
- **Consecuencia:** la siguiente iteración puede conectar cada estantería con datos reales sin rehacer la jerarquía visual principal.

## 2026-10-15 — Vistas navegables por estantería

- **Clasificación:** MVP.
- **Decisión:** cada categoría tendrá una ruta y una pantalla propia antes de conectar el almacenamiento real.
- **Motivo:** validar la navegación y la jerarquía de la biblioteca sin inventar contenido persistente.
- **Implementación:** las cuatro tarjetas abren `ShelfScreen` con estado vacío contextual y navegación de regreso.
- **Consecuencia:** el modelo de datos podrá incorporarse por categoría sin cambiar las rutas principales.

## 2026-10-15 — Persistencia local con Room

- **Clasificación:** MVP.
- **Decisión:** usar Room 2.8.5 como fuente de verdad local, con KSP 2.3.10 para generar el código del compilador.
- **Motivo:** la biblioteca necesita conservar elementos en el dispositivo y observar cambios mediante `Flow`, sin introducir backend ni sincronización.
- **Implementación:** entidad `LibraryItemEntity`, DAO, `MemoriaDatabase`, repositorio `RoomLibraryRepository` y `LibraryViewModel`; categorías y estados se almacenan como valores controlados.
- **Verificación:** `connectedDebugAndroidTest` ejecutó 2 pruebas en el AVD y terminó con `BUILD SUCCESSFUL`.

## 2026-10-15 — Identidad de biblioteca artesanal

- **Clasificación:** mejora visual del MVP; sin nuevas funciones de datos.
- **Decisión:** sustituir la composición genérica de tarjetas por estantes ilustrados con profundidad, madera, encuadernaciones, papel y fichas de catálogo. Usar titulares serif y etiquetas monoespaciadas.
- **Motivo:** el usuario solicitó una identidad más cercana a una biblioteca real antes de continuar las funciones del MVP.
- **Implementación:** ilustraciones nativas con Canvas y materiales centralizados. Las cuatro categorías comparten un lenguaje visual, pero tienen composiciones distintas. No se añaden dependencias, imágenes remotas ni permisos.
- **Compatibilidad:** se mantienen los seis temas y sus identificadores guardados en DataStore, incluidos Celeste cielo y Azul marino. El tema modifica también madera y papel, no solo el acento.
- **Accesibilidad:** contenido desplazable, márgenes de barras del sistema, títulos semánticos, filas de tema seleccionables y decoración excluida del árbol accesible. Se revisa el tamaño de texto al 150 %.
- **Límite:** los libros dibujados son decorativos, identificados como tales en la pantalla. Las estanterías interiores siguen vacías hasta integrar Room con la UI; no se simulan registros del usuario.

## 2026-10-15 — Alta y consulta de títulos locales

- **Clasificación:** MVP.
- **Decisión:** agregar títulos desde la estantería correspondiente y precargar su categoría, en vez de ofrecer un selector que pueda guardar el registro en otra estantería por error.
- **Campos:** título obligatorio; estado, duración aproximada en minutos y nota personal opcionales. El repositorio normaliza espacios y rechaza títulos vacíos o duraciones no positivas.
- **Flujo:** `LibraryViewModel` comparte una sola observación de Room con biblioteca, estanterías y formulario. Los contadores y listas se derivan de esa colección; no se guardan contadores estáticos.
- **Persistencia:** elevar Room a versión 2 y migrar de 1 a 2 con una columna nullable `durationMinutes`, preservando los registros de instalaciones existentes.
- **Límite:** no se implementan edición, eliminación, imágenes de portada ni catálogos remotos. Las portadas decorativas solo se muestran en estanterías sin contenido real.

## 2026-10-15 — Edición y eliminación confirmada de títulos

- **Clasificación:** MVP.
- **Decisión:** reutilizar el formulario de alta como ficha de edición, abierta al tocar una ficha del estante. La categoría permanece inmutable en esta pantalla para evitar mover un título por accidente; se conservan también `id` y fecha de creación.
- **Eliminación:** solicitar confirmación explícita que nombra el título y volver al estante al confirmar. Cancelar conserva la ficha sin cambios. No se incorpora deshacer en esta iteración.
- **Motivo:** mantiene el flujo breve, evita una segunda pantalla y reduce operaciones irreversibles accidentales sin añadir dependencias ni complejidad de sincronización.
- **Verificación:** prueba instrumentada de repositorio para invariantes de edición y borrado; build debug y recorrido ADB de creación, edición y apertura/cancelación del diálogo.

## 2026-10-15 — Distribución futura con catálogos y cuentas

- **Clasificación:** arquitectura futura; excluida del MVP académico y no implementada.
- **Decisión:** preparar una capa Cloudflare Worker para autenticar cuentas, proteger las claves de catálogos y normalizar resultados. La biblioteca, notas y portadas descargadas permanecen en el dispositivo hasta que se apruebe una sincronización separada.
- **Cuentas y privacidad:** correo y contraseña con verificación, recuperación y sesiones revocables. El lanzamiento inicial se limita a Chile y a mayores de 18 años. Telemetría y errores son opcionales, separados de la cuenta y revocables.
- **Catálogos:** TMDB para películas/series, Google Books con Open Library de respaldo para libros, e IGDB para videojuegos por plataforma.
- **Cumplimiento:** orientar el diseño a ISO/IEC 27001:2022, ISO/IEC 27701:2025 y Ley 21.719; revisar jurídicamente proveedores, transferencias, textos y operación antes de publicar.
- **Referencia:** `docs/superpowers/specs/2026-10-15-catalogos-cuentas-privacidad-design.md`.
- **Límite del título:** no crear cuentas, no recolectar correo ni telemetría y no desplegar Cloudflare. Si se habilitan consultas de catálogo, solo se enviarán términos mínimos de búsqueda sin identidad ni historial remoto; las claves de desarrollo quedarán fuera de Git y se documentará que no son una solución de distribución.

## 2026-10-15 — Evaluación y entrega del título

- **Clasificación:** planificación académica del MVP.
- **Decisión:** separar una versión de evaluación, etiquetada al cierre de noviembre, de una versión final de diciembre. Las métricas se obtienen sobre la primera; la segunda corrige defectos y mejora la entrega sin cambiar los flujos medidos.
- **Metodología:** OCR sobre capturas autorizadas y anonimizadas; ranking local probado con casos controlados; MovieLens únicamente como análisis offline; usabilidad SUS con muestra de 5 a 8 participantes; inspección de tráfico y rendimiento.
- **Rigor:** reportar solo resultados reales y especificar versión, conjunto, procedimiento y métrica. Cuentas, Cloudflare y telemetría permanecen como trabajo futuro.
- **Referencia:** `docs/superpowers/specs/2026-10-15-memoria-y-evaluacion-design.md`.

## 2026-10-15 — Ejecución por microhitos

- **Clasificación:** planificación operativa del MVP.
- **Decisión:** ejecutar primero un hito documental de protocolo y luego un spike acotado de Share Sheet + OCR local antes de comprometer el resto del flujo. Cada hito requiere evidencia y reevaluación de alcance.
- **Motivo:** la captura compartida y OCR son el principal riesgo técnico y evaluativo del título; validarlos temprano evita construir sobre una premisa no comprobada.
- **Referencia:** `docs/superpowers/plans/2026-10-15-plan-mvp-y-evaluacion.md`.

## 2026-10-15 — Protocolo previo a evaluación

- **Clasificación:** MVP documental.
- **Decisión:** preparar protocolos y plantillas sin datos reales antes de incorporar OCR o reclutar participantes. Las capturas, consentimientos firmados y resultados se mantienen fuera de Git.
- **Motivo:** preservar privacidad, definir la verdad de referencia antes de medir y evitar resultados no reproducibles o datos personales versionados.
- **Referencia:** `docs/evaluation/`.

## 2026-10-15 — Spike de Share Sheet y OCR local

- **Clasificación:** MVP técnico; no crea ni persiste títulos todavía.
- **Decisión:** aceptar imágenes JPEG, PNG y WebP mediante `ACTION_SEND`, copiarlas inmediatamente a `filesDir/shared-captures` y ejecutar OCR con el modelo latino empaquetado de ML Kit 16.0.1.
- **Privacidad:** la imagen no sale del dispositivo. La copia temporal se elimina al cerrar o abandonar la pantalla y también ante un error. El texto se muestra sólo en memoria y no se guarda en Room ni en registros.
- **Límite:** cada captura se limita a 15 MiB para controlar el uso del almacenamiento privado; se rechazan tipos no compatibles y copias incompletas.
- **Dependencia:** `com.google.mlkit:text-recognition:16.0.1`. Se eligió la variante empaquetada porque permite OCR local desde el primer uso, sin descarga ni servicios de Google; su costo aproximado es 4 MB por arquitectura. No se agregó `kotlinx-coroutines-play-services` porque la espera de `Task` se adapta con coroutines estándar.
- **Verificación:** tres pruebas instrumentadas cubren copia privada, rechazo de tipo/tamaño, limpieza de archivos y una lectura OCR exitosa sobre una imagen sintética privada. La suite completa aprobó 7 pruebas en `memoria-api36`.
- **Límite evaluativo:** la comprobación manual usó una captura técnica local. Las cinco capturas autorizadas y anonimizadas, junto con su verdad de referencia, siguen pendientes y no se sustituyen con este smoke test.

## 2026-10-15 — Confirmación humana de propuesta OCR

- **Clasificación:** MVP.
- **Decisión:** el OCR entrega una propuesta local editable, no un título automático. La persona revisa título y categoría y debe pulsar `Confirmar y guardar`; `Descartar captura` no persiste nada.
- **Motivo:** el OCR de portadas puede devolver texto parcial o erróneo, como la variante `FUEGO SNGRE` observada durante la prueba manual.
- **Límite:** la heurística usa líneas cortas del final del texto y parte en Libros; no infiere metadatos, no consulta catálogos y no garantiza exactitud.
