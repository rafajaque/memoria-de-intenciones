# Recorrido manual del escáner ISBN en AVD

## Fixture

El fixture `app/src/androidTest/assets/isbn-9788466374972.png` es una copia recortada de la imagen compartida desde `Desktop/Memoria de intenciones/ISBN`. El original AVIF no forma parte del repositorio y no se modifica.

ISBN esperado: `978-84-663-7497-2`.

El recorte deja el EAN-13 centrado, con margen suficiente para que la cámara virtual no lo pierda al adaptar la relación de aspecto.

## Preparar la cámara virtual

El emulador debe recibir una ruta sin espacios. Por ejemplo, en PowerShell:

```powershell
$fixture = "C:\\Temp\\memoria-isbn-9788466374972.png"
Copy-Item "app\\src\\androidTest\\assets\\isbn-9788466374972.png" $fixture -Force

$emulator = "$env:LOCALAPPDATA\\Android\\Sdk\\emulator\\emulator.exe"
Start-Process $emulator -ArgumentList @(
  "-avd", "memoria-api36",
  "-camera-back", "imagefile:$fixture"
)
```

Si ya existe una instancia del AVD, hay que cerrarla completamente antes de arrancar esta configuración; cambiar la imagen mientras el proceso sigue vivo no recarga el fotograma.

## Recorrido

1. Abrir Biblioteca → Libros → Agregar título.
2. Pulsar Escanear ISBN y conceder permiso de cámara si Android lo solicita.
3. Confirmar visualmente que el EAN-13 está dentro del cuadro.
4. Esperar la detección y comprobar que el formulario recibe `978-84-663-7497-2`.
5. Comprobar que la consulta de Google Books muestra la propuesta y la portada cuando hay conectividad.
6. Salir sin guardar y comprobar que no se crea ningún registro.

## Resultado de la sesión 2026-10-16

La navegación, el permiso y la pantalla del escáner funcionaron. La instancia administrada del AVD conservó un fotograma anterior y no permitió verificar todavía la detección con el fixture centrado; por eso el resultado queda marcado como pendiente y no como recorrido completado.
