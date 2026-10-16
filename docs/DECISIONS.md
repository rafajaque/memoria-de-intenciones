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

## 2026-10-15 — Contrato inicial de Google Books

- **Clasificación:** preparación MVP; sin UI ni consulta activada todavía.
- **Decisión:** normalizar resultados públicos con Retrofit y Kotlin serialization, con máximo de cinco resultados previsto en el servicio.
- **Seguridad:** no se añade clave al repositorio; la búsqueda pública no incorpora cuenta, captura, nota ni historial.

## 2026-10-16 — Lectura única y límite del catálogo ISBN

- **Decisión:** el escáner acepta sólo la primera lectura válida por apertura y libera CameraX/ML Kit al salir; los errores HTTP 429 de Google Books se presentan como una limitación temporal y dejan el ISBN editable.
- **Motivo:** una cámara real entrega varios frames del mismo código y las lecturas duplicadas podían ejecutar la navegación varias veces, dejando una pantalla blanca. Reintentar automáticamente ante 429 puede aumentar el límite y no debe impedir el alta manual local.
- **Consecuencia:** el escaneo queda validado en el Redmi 12 5G; la ficha puede completarse manualmente cuando Google Books no esté disponible.

## 2026-10-16 — Respaldo automático del catálogo ISBN

- **Decisión:** consultar Open Library por ISBN cuando Google Books responda 429, falle temporalmente o devuelva cero propuestas; Google Books mantiene prioridad.
- **Motivo:** el objetivo del escaneo es completar la ficha automáticamente y un límite temporal de un proveedor no debe degradar inmediatamente el flujo a edición manual.
- **Privacidad:** sólo se envía el ISBN normalizado; no se envían capturas, notas, identidad ni historial.
- **Límite:** la cobertura depende de los registros públicos de cada catálogo; si ambos no encuentran la edición, el ISBN queda editable para completar manualmente.

## 2026-10-16 — Validación local de ISBN

- **Clasificación:** MVP; primera microtarea del escáner ISBN.
- **Decisión:** normalizar ISBN-10 e ISBN-13 eliminando separadores y validar su checksum localmente antes de que una consulta de catálogo pueda ejecutarse.
- **Motivo:** evita enviar códigos mal formados y permite probar la regla de negocio sin cámara, red ni datos personales.
- **Implementación:** `domain/Isbn` acepta el dígito de control `X` de ISBN-10 y aplica las fórmulas ISO correspondientes. No guarda ni transmite el valor.
- **Verificación:** `gradlew.bat assembleDebug lintDebug connectedDebugAndroidTest --console=plain` terminó con `BUILD SUCCESSFUL`; 12 pruebas instrumentadas aprobadas en `memoria-api36`.

## 2026-10-16 — Metadatos bibliográficos locales

- **Clasificación:** MVP; segunda microtarea del escáner ISBN.
- **Decisión:** añadir `BookMetadata` opcional a un título y persistirlo en columnas nullable exclusivas de libros: ISBN, autores, editorial, fecha, idioma, páginas, URL de portada y edición.
- **Motivo:** permite que una consulta futura precargue información sin obligar a películas, series o videojuegos a usar un esquema ajeno. La edición se conserva sólo como texto confirmado, sin inferencias.
- **Persistencia:** Room pasa de versión 2 a 3 con `MIGRATION_2_3` aditiva; conserva todos los registros existentes. Los autores se serializan localmente como JSON y un valor dañado se interpreta como lista vacía, no como un cierre de la biblioteca.
- **Restricción:** el repositorio rechaza metadatos bibliográficos para categorías distintas de Libros. No se guarda imagen: `coverUrl` es sólo una referencia aún no utilizada por UI.
- **Verificación:** `gradlew.bat assembleDebug lintDebug connectedDebugAndroidTest --console=plain` terminó con `BUILD SUCCESSFUL`; 13 pruebas instrumentadas aprobadas en `memoria-api36`.

## 2026-10-16 — Consulta de catálogo por ISBN

- **Clasificación:** MVP; capa de datos sin interfaz.
- **Decisión:** `GoogleBooksBookCatalogRepository` consulta sólo `isbn:<ISBN normalizado>`, limita a cinco resultados y entrega propuestas editables sin crear registros locales.
- **Privacidad:** se rechaza un ISBN inválido antes de tocar el servicio; las pruebas usan un doble local, sin tráfico de red.
- **Verificación:** `assembleDebug`, `lintDebug` y `connectedDebugAndroidTest` finalizaron correctamente con 15 pruebas instrumentadas en `memoria-api36`.

## 2026-10-16 — Escáner ISBN en vivo

- **Clasificación:** MVP.
- **Decisión:** usar CameraX 1.5.3 y ML Kit Barcode Scanning 17.3.0 empaquetado, restringido a EAN-13 y validado como ISBN antes de devolverlo al formulario.
- **Privacidad:** no se toma, guarda ni transmite una foto; cada fotograma se usa sólo en memoria y se cierra tras analizarse. El permiso se pide al entrar al escáner.
- **Distribución:** `android.hardware.camera` se declara opcional, manteniendo instalación posible en equipos sin cámara. El modelo empaquetado añade aproximadamente 2,4 MB pero no exige descarga inicial.
- **Verificación:** `assembleDebug`, `lintDebug` y `connectedDebugAndroidTest` terminaron con `BUILD SUCCESSFUL`; 15 pruebas instrumentadas aprobadas.

## 2026-10-16 — ISBN en capturas compartidas: código de barras antes que OCR

- **Clasificación:** MVP; mejora del flujo OCR compartido.
- **Decisión:** intentar primero la lectura directa del EAN-13 con ML Kit sobre la imagen compartida, incluyendo un recorte ampliado de la franja inferior, y usar la extracción de ISBN desde OCR como respaldo.
- **Motivo:** en contraportadas el OCR puede reconocer con más claridad la editorial que los dígitos pequeños del ISBN. El código de barras contiene una señal más específica para identificar la edición.
- **Comportamiento:** un ISBN válido dispara el catálogo ya existente; la propuesta de Google Books/Open Library reemplaza la heurística OCR de título y sigue siendo editable antes de guardar.
- **Privacidad:** la imagen y el texto permanecen en memoria/almacenamiento temporal privado; no se incorporan nuevos datos enviados al catálogo aparte del ISBN normalizado.
- **Verificación:** compilación de APK, lint y compilación de tests instrumentados correctas; prueba instrumentada añadida para leer `9788466374972` desde el fixture de código de barras. El recorrido en Redmi 12 5G queda pendiente para validar la propuesta visible.

## Futuro — Contribuciones bibliográficas opt-in

- **Clasificación:** futura; fuera del MVP actual.
- **Idea:** cuando un ISBN no tenga ficha, permitir completar el libro manualmente y ofrecer, de forma explícita y opcional, compartir sólo los metadatos bibliográficos mínimos para alimentar un catálogo reutilizable por otros usuarios.
- **Condiciones:** conservar el guardado local aunque la persona no contribuya; deduplicar por ISBN y edición; registrar procedencia y fecha; validar o moderar cambios; excluir notas personales, capturas, identidad e historial.
- **Escalabilidad:** separar el catálogo comunitario de la biblioteca personal y diseñar una API/proxy con autenticación, límites y protección contra abuso antes de habilitar sincronización remota.

## 2026-10-16 — Disciplina de alcance del MVP

- **Decisión:** continuar sólo con el orden y los criterios del plan aprobado. Las ideas nuevas que impliquen trabajo amplio, cuentas, backend, sincronización o catálogo comunitario se registran como escalabilidad futura y no alteran el siguiente microhito.
- **Siguiente paso planificado:** completar la gestión local de portadas del catálogo mínimo, incluyendo descarga privada y fallback visual, antes de pasar al hito de ficha y experiencias personales.

## 2026-10-16 — Copia privada de portadas

- **Clasificación:** MVP.
- **Decisión:** descargar la portada HTTPS al confirmar un libro y guardar sólo una ruta privada opcional junto a la URL de catálogo. La UI prioriza archivo local, URL remota y placeholder.
- **Límites:** máximo 10 MiB, contenido validado como imagen, sin descarga durante la consulta ni para propuestas no confirmadas. No se añaden dependencias.
- **Limpieza:** al reemplazar una portada o eliminar el libro se intenta borrar la copia anterior; los fallos de limpieza no impiden mantener la biblioteca.
- **Migración:** Room 3→4 añade `bookCoverLocalPath` nullable y no altera registros existentes.

## 2026-10-16 — Experiencia personal opcional por título

- **Clasificación:** MVP.
- **Decisión:** guardar una experiencia local separada por título, con plataforma/soporte, valoración de 1–5 y comentario. Se puede editar o eliminar desde la ficha cuando el estado sea `TERMINADO`.
- **Motivo:** separar la valoración de la nota breve de biblioteca y permitir que marcar un título como terminado no obligue a escribir una reseña.
- **Persistencia:** Room 4→5 crea `item_experiences` con `itemId` como clave primaria. Al eliminar el título se elimina su experiencia.
- **Límite:** una sola experiencia por título en el MVP; no hay recomendaciones, sincronización, cuentas ni publicación remota.

## 2026-10-16 — Fechas y listas controladas en experiencias

- **Decisión:** para Libros registrar fecha de inicio y fecha de término mediante calendario, en lugar de pedir minutos de lectura. Películas, series y videojuegos mantienen la duración aproximada del título.
- **Soporte:** reemplazar texto libre por listas específicas: formatos de libro, medios de película/serie y plataformas de videojuego, con opción `Otro`.
- **Validación:** el término no puede ser anterior al inicio; ambas fechas son opcionales porque una persona puede no querer registrar el recorrido completo.
- **Persistencia:** Room 5→6 añade dos columnas nullable en `item_experiences`; las experiencias existentes no pierden información.

## 2026-10-16 — Fecha de término obligatoria para libros terminados

- **Clasificación:** MVP; validación de formulario.
- **Decisión:** un Libro marcado como `TERMINADO` debe tener fecha de término antes de poder guardarse. La fecha de inicio sigue siendo opcional.
- **Motivo:** el estado terminado debe representar un recorrido de lectura cerrado y no dejar una ficha incompleta en el historial.
- **Alta y edición:** la sección de experiencia aparece también al crear un libro terminado; si se confirma una fecha, la experiencia se guarda usando el ID recién creado. Las demás categorías no reciben esta obligación.
- **Compatibilidad:** al editar libros terminados existentes sin fecha, la misma validación solicita completar la fecha antes de guardar.

## 2026-10-16 — Autocompletado editable desde Google Books

- **Clasificación:** MVP; integración de UI y consulta, sin guardado automático.
- **Decisión:** al volver del escáner, consultar el ISBN validado; aplicar una única propuesta automáticamente y pedir selección cuando haya varias. Los estados vacío y error conservan el formulario manual.
- **Privacidad:** sólo se envía el ISBN normalizado; no se incluyen notas, capturas, identidad ni historial. El cliente usa timeouts de 8 segundos para que la consulta no bloquee indefinidamente el alta.
- **Persistencia:** el título y los metadatos se mantienen editables y sólo se escriben en Room mediante la confirmación existente. La portada se conserva como URL; su descarga local queda para una iteración posterior.
- **Verificación:** `assembleDebug lintDebug connectedDebugAndroidTest --console=plain` terminó con `BUILD SUCCESSFUL`; 15 pruebas instrumentadas aprobadas en `memoria-api36`.

## 2026-10-16 — Visualización de portadas

- **Clasificación:** MVP; presentación visual sin almacenamiento propio de imágenes.
- **Decisión:** usar Coil 2.7.0 (`coil-compose`) para cargar URLs HTTPS de Google Books en la selección, el formulario y las fichas de Libros. La versión se mantiene en `gradle/libs.versions.toml`.
- **Motivo:** integra carga asíncrona y caché de ejecución con poco código, sin introducir un repositorio de archivos ni una migración Room. El costo de mantenimiento es una dependencia adicional y su caché temporal.
- **Fallback:** una URL ausente o fallida muestra un placeholder local y no impide guardar la ficha.
- **Privacidad:** no se suben imágenes del usuario; sólo se solicita al proveedor la URL de portada ya entregada por Google Books.
- **Verificación:** `assembleDebug`, `lintDebug` y `connectedDebugAndroidTest` terminaron con `BUILD SUCCESSFUL`; 15 pruebas instrumentadas aprobadas en `memoria-api36`.
