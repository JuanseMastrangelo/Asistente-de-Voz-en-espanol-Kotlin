package com.axizen.android.SQLite.Spotify

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.IOException

class ConexionSpotify(context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NOMBRE + " TEXT," + COLUMN_ARTISTA + " TEXT," + COLUMN_TRACK + " TEXT" + ")")


        try {
            db.execSQL(CREATE_PRODUCTS_TABLE)
        }catch (e: IOException){
            Log.d("ERROR", e.toString())
        }


    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }
    fun addName(nombre: Cancion) {
        val values = ContentValues()
        values.put(COLUMN_NOMBRE, nombre.nombre)
        values.put(COLUMN_ARTISTA, nombre.artista)
        values.put(COLUMN_TRACK, nombre.track)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getAllName(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
    fun getCancionAlmacenada(n: String, a: String): Cursor? {
        val db = this.readableDatabase
        val args = arrayOf(n,a)
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE nombre=? AND artista=?", args)
    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Asistente.db"
        val TABLE_NAME = "Spotifycanciones"
        val COLUMN_ID = "_id"
        val COLUMN_NOMBRE = "nombre"
        val COLUMN_ARTISTA = "artista"
        val COLUMN_TRACK = "track"
    }
}