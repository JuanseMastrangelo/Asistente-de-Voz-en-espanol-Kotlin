<p align="center">
  <img src="https://img.shields.io/github/forks/JuanseMastrangelo/Asistente-de-Voz-en-espanol-Kotlin"><br><br>
  <img src="https://cdn.canstarblue.co.nz/wp-content/uploads/banner-with-robot-coffee.jpg">
</p>

- - -

# ASISTENTE DE VOZ :speak_no_evil:
Asistente de voz con la opción de reconocer una palabra. Esto lo hace mediante el speech de Google que estará escuchando en todo momento.

## Desarrolladores
Podes llamar al asistente con la palabra **ejecutar comando** aunque esto lo puedes cambiar desde la variable en **MainActivity.java > ACTIVATION_KEYWORD** (default: 'ejecutar comando').

## Comandos Disponibles
Ejecuta comando diciendo: '**ejecutar comando**', luego dí:
* **reproducir** `cancion` **del artista** `artista` : Reproduce una cancion de Spotify (Ver solapa Tutorial)
* **canciones** : Mostrará las listas de canciones agregadas y que puedes decirle al asistente


## Tutorial
`1.0: ` En esta versión es capaz de reconocer el comando "**reproducir** `cancion` **del artista** `artista`" y reproducir esta canción con *Spotify*. Si la canción no se encuentra este nos dará un error.<br>
  Para agregar una canción manualmente tendrás que ir a la canción, click en **compartir** y seleccionar **axizen**, te aparecerá un dialog con el nombre de la canción y su autor. Al dar click en guardar este se almacenará para que lo puedas reproducír con el asistente de voz.
  
## Tecnologías utilizadas
* Android Studio (**Kotlin**)
* SQLite
* WebService (proximamente)


