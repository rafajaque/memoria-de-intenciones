# Protocolo del conjunto de capturas para OCR

## Propósito

Evaluar el flujo local de Share Sheet, OCR y confirmación humana con 30 a 50 capturas que representen contenido de entretenimiento. Este documento define qué puede incorporarse al conjunto y cómo se medirá; no contiene capturas ni resultados.

## Criterios de inclusión

- La captura pertenece a quien realiza el estudio o cuenta con autorización documentada de su titular.
- Representa una película, serie, libro, videojuego o anime identificable.
- Tiene un título esperado definido antes de ejecutar el flujo.
- Puede incluir ruido visual realista: menús, hashtags, fechas, botones o texto adicional.

## Criterios de exclusión

- Mensajes privados, datos de contacto, rostros, nombres de usuario o contenido de terceros sin autorización.
- Capturas con información sensible o que no puedan anonimizarse sin afectar el objetivo.
- Imágenes modificadas después de fijar la verdad de referencia.

## Anonimización y almacenamiento

1. Crear una copia de trabajo fuera del repositorio.
2. Eliminar o cubrir nombres de usuario, avatares, mensajes, identificadores, ubicación y otros datos no necesarios.
3. Asignar un código no identificable, por ejemplo `OCR-001`; no usar nombre de persona, servicio ni archivo original como identificador.
4. Guardar el conjunto únicamente en almacenamiento local protegido del investigador y no subirlo a Git, servicios públicos ni herramientas de telemetría.
5. Mantener el consentimiento o la prueba de titularidad fuera del repositorio, asociado al código de muestra.

## Verdad de referencia previa

Antes de medir, cada muestra debe registrar la siguiente información en una planilla local no versionada:

| Código | Categoría | Título esperado | Variantes aceptables | Fuente/autorización | Estado |
| --- | --- | --- | --- | --- | --- |

No se ejecuta el flujo OCR ni se modifica el título esperado después de registrar esta fila.

## Procedimiento por muestra

1. Iniciar la versión identificada de la aplicación en el dispositivo definido.
2. Compartir la imagen mediante Android Share Sheet.
3. Registrar tiempo desde compartir hasta que el borrador queda listo para confirmar.
4. Registrar el candidato top-1 mostrado, si existe.
5. Indicar si el candidato coincide con la verdad de referencia, requiere corrección o no produce candidato útil.
6. Registrar una causa principal de fallo cuando corresponda: OCR, ruido, candidato, catálogo, ambigüedad o interacción.
7. Eliminar la copia de evaluación del dispositivo al terminar la sesión, si no es necesaria para reproducir el resultado autorizado.

## Métricas

- **Acierto top-1:** proporción de muestras cuyo primer candidato coincide con el título esperado o una variante aceptada.
- **Corrección manual:** proporción de muestras en que la persona modifica o reemplaza el candidato antes de guardar.
- **Tiempo de guardado:** segundos desde la acción Compartir hasta que el borrador confirmado se guarda.
- **Cobertura:** número de muestras ejecutadas por categoría y total válido.

Los resultados se reportan con numerador, denominador y limitaciones. Un resultado ausente se declara como no evaluado.
