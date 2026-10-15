# Diseño — Persistencia local de la biblioteca con Room

## Estado

Propuesta aprobada conversacionalmente para revisión escrita antes de implementar.

## Objetivo

Convertir las estanterías actuales, que todavía muestran estados vacíos de demostración, en vistas preparadas para trabajar con contenido local real. Room será la fuente de verdad de los elementos de biblioteca dentro del MVP; Compose observará el estado a través de un ViewModel y un repositorio.

## Alcance del microhito

Incluye:

- dependencia y configuración de Room para Android;
- entidad local `LibraryItemEntity`;
- categorías y estados persistidos como valores controlados;
- DAO para observar, insertar, actualizar y eliminar elementos;
- base de datos local `MemoriaDatabase`;
- repositorio y ViewModel preparados para la UI;
- pruebas unitarias del mapeo y consultas principales;
- documentación del dominio y de la decisión técnica.

No incluye:

- formulario de alta o edición;
- OCR, Share Sheet, catálogos externos o imágenes remotas;
- sincronización, cuentas o backend;
- datos ficticios insertados automáticamente;
- migraciones desde una versión anterior, porque todavía no existe una base instalada del producto.

## Modelo de dominio

### LibraryItem

Un `LibraryItem` es una intención personal de recordar o consumir una obra. Pertenece a una única categoría y conserva el estado actual que el usuario le asigna.

Campos del MVP:

| Campo | Tipo de dominio | Persistencia | Regla |
|---|---|---|---|
| `id` | identificador local | `Long` autogenerado | no cambia durante la vida del registro |
| `title` | texto | `String` no nulo | debe contener texto no vacío |
| `category` | categoría de biblioteca | `String` controlado | `MOVIE`, `SERIES`, `BOOK` o `GAME` |
| `status` | estado de consumo | `String` controlado | `TO_CONSUME`, `IN_PROGRESS`, `COMPLETED` o `ABANDONED` |
| `notes` | nota personal | `String` nullable | opcional; no se registra en logs |
| `createdAt` | fecha de creación | `Long` epoch millis | se asigna al insertar |
| `updatedAt` | fecha de modificación | `Long` epoch millis | se actualiza al modificar |

La categoría no se duplicará en distintos mapas de UI: el mismo conjunto de valores será la fuente para las etiquetas visuales y las consultas de datos. Los textos localizados y colores seguirán perteneciendo a la capa de presentación.

## Arquitectura

La implementación seguirá las capas simples previstas por `AGENTS.md`:

- `data/local/LibraryItemEntity.kt`: representación Room.
- `data/local/LibraryItemDao.kt`: operaciones SQL y `Flow` de lectura.
- `data/local/MemoriaDatabase.kt`: instancia Room singleton por aplicación.
- `data/LibraryRepository.kt`: interfaz de acceso usada por el ViewModel.
- `domain/LibraryItem.kt`: modelo sin anotaciones de Room y enums de categoría/estado.
- `ui/LibraryViewModel.kt`: estado observable y operaciones de la biblioteca.

La inyección será manual. El ViewModel recibirá el repositorio; la Activity creará la base y el repositorio en el punto de composición existente hasta que el proyecto necesite un contenedor más formal.

## Flujo de datos

```text
Room DAO -> Repository -> ViewModel -> StateFlow -> Compose
Compose event -> ViewModel -> Repository -> Room DAO
```

Las pantallas no accederán directamente a Room. Las estanterías observarán una consulta filtrada por categoría y mostrarán una lista vacía cuando no existan registros.

## Consultas mínimas

- observar todos los elementos ordenados por `updatedAt` descendente;
- observar elementos de una categoría;
- insertar un elemento validado;
- actualizar título, estado o nota;
- eliminar por `id`.

Las consultas devolverán `Flow<List<LibraryItemEntity>>` para que los cambios locales se reflejen automáticamente en Compose.

## Validación y errores

- El título vacío se rechaza antes de llegar al DAO.
- Categorías y estados desconocidos se convierten en un error de dominio controlado, no en una excepción expuesta a la UI.
- La UI mostrará estado vacío y estado de error separados.
- No se registrarán títulos, notas ni otros datos del usuario en logs.

## Migraciones y privacidad

La base comenzará en versión 1. Si el esquema cambia en el futuro, se agregará una migración explícita y una prueba de migración. No se usará `fallbackToDestructiveMigration` en producción. La base permanecerá en el almacenamiento privado de la aplicación y no habrá sincronización de red.

## Verificación

- `assembleDebug` debe terminar correctamente.
- Las pruebas deben cubrir inserción, filtrado por categoría, actualización y eliminación.
- El emulador debe mostrar una estantería vacía cuando la base no contenga elementos.
- El estado de Git y `PROJECT_STATE.md` se actualizarán al terminar el microhito.
