# Hito 5 — “Tengo tiempo libre”: diseño

## Propósito

Recomendar títulos pendientes de la biblioteca local según el tiempo disponible y explicar cada resultado con factores que realmente hayan intervenido en el cálculo. El flujo no consulta catálogos, no usa cuentas ni envía datos fuera del dispositivo.

## Alcance MVP

- Una pantalla local `Tengo tiempo libre` recibe un tiempo disponible en minutos.
- Sólo participan títulos con estado `TO_CONSUME` (`PENDIENTE`).
- Películas, series y videojuegos usan la duración aproximada ya existente. En series, esa duración representa el bloque que la persona desea ver, no necesariamente la serie completa.
- Libros no necesitan nuevos metadatos para este hito: su recomendación usa la duración sólo si existe; las fechas de lectura y el soporte siguen perteneciendo a la experiencia personal.
- La pantalla muestra una lista ordenada, puntuación no necesariamente visible y razones legibles derivadas de los factores usados.
- No se incorporan géneros, plataformas, reparto, sinopsis, capítulos ni información remota.

## Entradas locales

Cada candidato usa:

- categoría;
- duración aproximada nullable;
- fecha de creación;
- prioridad manual local, con valores `BAJA`, `NORMAL` y `ALTA`;
- feedback local de recomendaciones anteriores;
- preferencias locales de peso y afinidad por categoría.

La prioridad y el feedback son datos de la biblioteca, no inferencias remotas. La afinidad parte neutral y puede ajustarse mediante acciones locales; no se entrena un modelo.

## Ranking determinista

El dominio expondrá una función pura equivalente a:

```text
rank(pendientes, tiempoDisponible, preferencias) -> recomendaciones
```

La función rechaza tiempos no positivos y nunca modifica entradas ni persiste datos. Para cada título calcula una puntuación con pesos configurables y límites fijos:

1. **Ajuste temporal:** bonificación máxima cuando la duración conocida cabe en el tiempo y es cercana a él; duración conocida que excede el tiempo recibe una penalización suave. Duración desconocida recibe una bonificación neutra reducida y la razón `Duración no indicada`.
2. **Prioridad:** alta, normal y baja aportan una bonificación ordenada.
3. **Antigüedad:** los títulos más antiguos reciben una bonificación acotada, evitando que domine al ajuste temporal.
4. **Afinidad:** peso local por categoría, neutral por defecto.
5. **Feedback:** aceptar mejora levemente, posponer penaliza suavemente y descartar excluye el título hasta que la persona lo vuelva a dejar pendiente desde su ficha.

Los pesos por defecto se fijan en el dominio y los mínimos configurables se guardan en DataStore. Si dos resultados empatan, se desempata en este orden: duración que cabe en el tiempo, duración conocida, fecha de creación más antigua e `id` menor. El resultado no depende del orden de Room, del reloj durante el cálculo ni de valores aleatorios.

## Feedback y acciones

- **Aceptar:** registra una señal positiva local y mantiene el título pendiente.
- **Posponer:** registra una señal negativa suave y mantiene el título pendiente.
- **Descartar:** registra una exclusión local; no borra el título y deja de mostrarlo hasta que vuelva a quedar disponible desde la ficha.
- **Realizar:** abre la ficha existente para que la persona complete el flujo normal. El título sólo deja de ser candidato cuando se guarda con el estado correspondiente; no se salta la valoración ni las validaciones actuales de libros.

Las acciones deben ser idempotentes para una misma interacción visible y actualizar sólo el estado local de recomendación. No se guardan eventos con texto libre ni se envía feedback a terceros.

## Persistencia

Room añadirá únicamente los campos/tablas necesarios para prioridad y feedback local, con migración aditiva y compatibilidad con títulos existentes. DataStore almacenará el tiempo/preferencias de ranking y pesos mínimos. La ausencia de registros nuevos equivale a prioridad normal, feedback neutral y afinidad neutral.

El repositorio expondrá observación local de pendientes y operaciones de feedback. La lógica de orden quedará fuera de Room para poder probarla sin Android ni base de datos.

## Interfaz y razones

La biblioteca ofrecerá una entrada a `Tengo tiempo libre`. La pantalla tendrá:

- selector o campo de minutos disponibles;
- estado vacío cuando no haya pendientes;
- tarjetas ordenadas con título, categoría y duración;
- una o más razones calculadas, por ejemplo `Cabe en tus 45 min`, `Prioridad alta` o `Lleva más tiempo pendiente`;
- acciones aceptar, posponer, descartar y realizar.

Las razones se construirán a partir de los mismos factores y umbrales que usa el ranking. No se mostrará una razón si su factor no participó o si el dato necesario estaba ausente.

## Errores y límites

- Tiempo vacío, cero o negativo: se muestra validación y no se calcula.
- Duración ausente: no bloquea la recomendación; se explica como duración desconocida.
- Fallo de DataStore o Room: se mantiene el estado en memoria de la pantalla y se muestra un mensaje recuperable; no se inventan preferencias.
- Sin candidatos: se comunica que no hay títulos pendientes disponibles.

## Verificación

- Pruebas de dominio para ajuste temporal, prioridad, antigüedad, afinidad, duración desconocida, feedback y todos los desempates.
- Pruebas de repositorio/migración para valores por defecto y feedback.
- Prueba de UI o instrumentada para razones visibles y acciones locales.
- Recorrido AVD con casos controlados: duración menor, igual, mayor, desconocida, prioridades empatadas y título pospuesto.
- `assembleDebug`, `lintDebug`, pruebas instrumentadas y `git diff --check`.

## Fuera de alcance

Catálogos remotos, perfiles, recomendaciones colaborativas, aprendizaje automático, géneros o plataformas inferidos, sincronización, telemetría y cambios a las reglas de experiencias personales.
