# Plan: “Tengo tiempo libre”

## Ejecución seleccionada

Se implementará en fases pequeñas, manteniendo la biblioteca y el alta manual funcionales en cada fase. La lógica de ranking se completará antes de conectarla a Compose para evitar que la UI defina reglas de negocio.

## Fase 1 — Modelo local y migración

1. Añadir `RecommendationPriority` al dominio y una prioridad nullable/default-compatible al registro local.
2. Crear el modelo y DAO de feedback por título: aceptar, posponer y descartar, con actualización idempotente.
3. Subir Room mediante migraciones aditivas y cubrir valores por defecto, persistencia y eliminación junto al título.
4. Exponer prioridad y feedback en `LibraryItem` y `LibraryRepository` sin cambiar las reglas de experiencias.

**Criterio de salida:** una base existente conserva sus títulos; los registros antiguos aparecen con prioridad normal y feedback neutral; borrar un título no deja feedback huérfano.

## Fase 2 — Ranking puro y razones

1. Crear los tipos de entrada, preferencias, resultado y razón calculada en `domain`.
2. Implementar el ranking puro con ajuste temporal, prioridad, antigüedad, afinidad, feedback, penalización por duración desconocida y desempates definidos.
3. Generar razones desde los mismos factores y umbrales usados en la puntuación.
4. Añadir pruebas deterministas para tiempos menor/igual/mayor, duración desconocida, prioridad, antigüedad, feedback y todos los desempates.

**Criterio de salida:** la misma entrada produce siempre el mismo orden y las razones no describen factores que no afectaron el resultado.

## Fase 3 — Preferencias y ViewModel

1. Crear un almacén DataStore para tiempo reciente, afinidad por categoría y pesos mínimos, con defaults neutrales.
2. Extender `LibraryViewModel` con estado de recomendación, cálculo desde la biblioteca local y acciones de feedback.
3. Mantener errores de persistencia recuperables y no introducir red ni logs con datos privados.

**Criterio de salida:** el ViewModel recalcula al cambiar tiempo, biblioteca o feedback, sin depender de un orden accidental de Room.

## Fase 4 — Pantalla y navegación

1. Añadir la entrada `Tengo tiempo libre` a la biblioteca y una ruta Compose dedicada.
2. Crear tarjetas con título, categoría, duración, razones visibles y acciones aceptar/posponer/descartar/realizar.
3. Hacer que realizar abra la ficha existente y conserve las validaciones actuales, incluida la valoración y las reglas de libros.
4. Cubrir validación de tiempo, estado vacío, duración desconocida y accesibilidad básica de acciones.

**Criterio de salida:** un caso controlado permite pedir recomendaciones, entender cada razón y ejecutar las cuatro acciones sin salir del alcance local.

## Fase 5 — Verificación y continuidad

1. Ejecutar `assembleDebug`, `lintDebug`, `connectedDebugAndroidTest` y `git diff --check`.
2. Ejecutar un recorrido AVD con títulos de las cuatro categorías y duraciones menor, igual, mayor y desconocida.
3. Actualizar `PROJECT_STATE.md` con resultados reales, riesgos y siguiente acción.
4. Crear commits claros en español y subir cada commit a GitHub.

## Restricciones

- No añadir dependencias.
- No consultar catálogos ni enviar datos fuera del dispositivo.
- No añadir géneros, plataformas, capítulos, sinopsis ni aprendizaje automático.
- No cambiar las reglas de alta, edición y experiencias salvo para reutilizar la ficha al realizar una recomendación.
