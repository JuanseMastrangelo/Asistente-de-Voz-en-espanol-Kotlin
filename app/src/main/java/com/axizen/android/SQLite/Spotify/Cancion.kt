package com.axizen.android.SQLite.Spotify

class Cancion {
    var id: Int = 0
    var nombre: String? = null
    var artista: String? = null
    var track: String? = null
    constructor(id: Int, nombre: String, artista: String, track: String) {
        this.id = id
        this.nombre = nombre
        this.artista = artista
        this.track = track
    }
    constructor(nombre: String, artista: String, track: String) {
        this.nombre = nombre
        this.artista = artista
        this.track = track
    }
}