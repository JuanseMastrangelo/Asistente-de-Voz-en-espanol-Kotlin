package com.axizen.android

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.github.stephenvinouze.core.interfaces.RecognitionCallback
import com.github.stephenvinouze.core.managers.KontinuousRecognitionManager
import com.github.stephenvinouze.core.models.RecognitionStatus
import java.util.*
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.widget.Toast
import com.axizen.android.SQLite.Spotify.Cancion
import com.axizen.android.SQLite.Spotify.ConexionSpotify
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.guardar_cancion_spotify.view.*
import java.io.IOException


class MainActivity : AppCompatActivity(), RecognitionCallback {

    // SQLITE
    var dbHandler: ConexionSpotify ?= null
    // Texto a Voz
    lateinit var mTTS: TextToSpeech
    // Voz a texto
    companion object {
        private const val ACTIVATION_KEYWORD = "ejecutar comando"
        private const val RECORD_AUDIO_REQUEST_CODE = 101
    }
    private val recognitionManager: KontinuousRecognitionManager by lazy {
        KontinuousRecognitionManager(this, activationKeyword = ACTIVATION_KEYWORD, callback = this)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Fullscreen*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
            setContentView(R.layout.activity_main)

        // Voz a texto
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        }


        // Tomar valor enviado desde otra app al dar al boton 'compartir'
        val intent = intent
        val urlCompartido = intent.getStringExtra(Intent.EXTRA_TEXT)
        if(urlCompartido != null) DatosSpotify(urlCompartido)

        // SQLITE
        dbHandler = ConexionSpotify(this,null)
        ReproducirVoz("Asistente escuchando")

    }
    // Reproducir voz
    private fun ReproducirVoz(s: String) {
        mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if(status != TextToSpeech.ERROR){
                val locSpanish = Locale("spa", "ARG")
                mTTS.language = locSpanish
            }
            val toSpeak = s
            mTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
        })

    }

    // Voz a texto
    private fun startRecognition() {
        recognitionManager.startRecognition()
    }

    private fun stopRecognition() {
        recognitionManager.stopRecognition()
    }
    private fun getErrorText(errorCode: Int): String = when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No match"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
        SpeechRecognizer.ERROR_SERVER -> "Error from server"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
        else -> "Didn't understand, please try again."
    }

    override fun onBeginningOfSpeech() {
        Log.i("Recognition","onBeginningOfSpeech")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i("Recognition", "onBufferReceived: $buffer")
    }

    override fun onEndOfSpeech() {
        Log.i("Recognition","onEndOfSpeech")
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.i("Recognition","onError: $errorMessage")
    }




    override fun onEvent(eventType: Int, params: Bundle) {
        Log.i("Recognition","onEvent")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReadyForSpeech(params: Bundle) {
        Log.i("Recognition","onReadyForSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
    }
    override fun onPrepared(status: RecognitionStatus) {
        when (status) {
            RecognitionStatus.SUCCESS -> {
                Log.i("Recognition","onPrepared: Success")
            }
            RecognitionStatus.FAILURE,
            RecognitionStatus.UNAVAILABLE -> {
                Log.i("Recognition", "onPrepared: Failure or unavailable")
                AlertDialog.Builder(this)
                    .setTitle("Speech Recognizer unavailable")
                    .setMessage("Your device does not support Speech Recognition. Sorry!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onKeywordDetected() {
        Log.i("Recognition","keyword detected !!!")
        mTTS = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if(status != TextToSpeech.ERROR){
                ReproducirVoz("Dime")
            }

        })
    }




    override fun onPartialResults(results: List<String>) {}

    override fun onResults(results: List<String>, scores: FloatArray?) {
        val text = results.joinToString(separator = "\n")
        Log.i("Recognition","onResults : $text")
        DetectorAcciones(text)
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecognition()
                }
            }
        }
    }


    // Estados de la app
    override fun onDestroy() {
        recognitionManager.destroyRecognizer()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecognition()
        }
    }





    override fun onPause() {
        stopRecognition()
        super.onPause()
    }






    fun DetectorAcciones(s: String){
        // SPLIT PRINCIPAL PARA SABER LA ACCION =>> CORTAMOS accion[0] QUE NOS DIRÁ QUE HACER
        val accion = s.split(" ")

        // *********************************************** ||
        //  -> ALGORITMO REPRODUCCIR CANCION DE SPOTIFY    ||
        // *********************************************** ||
            // REPRODUCIR 'sed' DE 'callejeros'    =>> SPLIT para saber el nombre de la canción
            val cancionYartista = s.split(" artista ")



            // ********************************** ||
            //  -> ALGORITMO PRINCIPAL INTERNO    ||
            // ********************************** ||
            if(accion[0].equals("reproducir")){ // ALGORITMO INTERIOR DE ACCION 'REPRODUCIR'
                val nombreCancionEnviada = cancionYartista[0].replace("reproducir ","") // Tomamos el nombre de la cancion
                val respuesta = verificarExistenciaCancion(nombreCancionEnviada, cancionYartista[1])

                // Validamos si existe la cancion
                if(respuesta != "Error"){
                    // EXISTE! var uri = "spotify:track:231GRlgtbKSCjzFixplhAv"
                    var uri = "spotify:track:"+respuesta
                    val launcher = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    startActivity(launcher)
                    stopRecognition()
                }else{
                    Snackbar.make(
                        mainActivityLayout, // view / layout
                        "La cancion no existe",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            }else if(accion[0].equals("canciones")){
                MostrarCancion()
            }
    }

    fun MostrarCancion(){
        val cursor = dbHandler?.getAllName()
        cursor!!.moveToFirst()
        if(cursor.getCount() > 0) {

            while(cursor.moveToNext()){
                Toast.makeText(this, cursor.getString(cursor.getColumnIndex(ConexionSpotify.COLUMN_TRACK)), Toast.LENGTH_SHORT).show()
                Toast.makeText(this, cursor.getString(cursor.getColumnIndex(ConexionSpotify.COLUMN_ARTISTA)), Toast.LENGTH_SHORT).show()
                Toast.makeText(this, cursor.getString(cursor.getColumnIndex(ConexionSpotify.COLUMN_NOMBRE)), Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(this, "No existen canciones guardadas", Toast.LENGTH_SHORT).show()
        }

        cursor.close()

    }

    fun verificarExistenciaCancion(n: String, a: String) : String {

        // VERIFICAMOS DATOS SQLITE TABLA = 'Spotifycanciones'
        val cursor = dbHandler?.getCancionAlmacenada(n,a)
        var respuesta: String
        cursor!!.moveToFirst()
        if(cursor.getCount() > 0) {
            val trackURL = (cursor.getString(cursor.getColumnIndex(ConexionSpotify.COLUMN_TRACK)))
            // ENVIAMOS EL TRACK
            respuesta = trackURL
        }else{
            respuesta = "Error"
        }

        cursor.close()
        return respuesta

    }



    // Datos recibidos de SPOTIFY
    fun DatosSpotify(s: String){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.guardar_cancion_spotify,null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Guardar cancion")

        val  mAlertDialog = mBuilder.show()


        mDialogView.btn_Guardar.setOnClickListener {
            mAlertDialog.dismiss()
            val cancion = mDialogView.et_cancion.text.toString()
            val artista = mDialogView.et_artista.text.toString()
            GuardarCancionEnDDBB(cancion,artista,s)
        }
        mDialogView.btn_Cancelar.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }


    fun GuardarCancionEnDDBB (c: String, a: String, t: String){
        // c = Cancion      a = Artista

        //Filtramos el valor tomado de spotify quitando https://open.spotify.com/track/
        val urlSinUrl = t.replace("https://open.spotify.com/track/", "")
        //Filtramos el valor urlSinUrl quitando ?si=oGa2234aGasdasd
        val track = urlSinUrl.split("?si=")

        // Enviamos el Track, cancion, artista limpio
        try {
            dbHandler?.addName(Cancion(c.toLowerCase(),a.toLowerCase(),track[0]))
            Snackbar.make(
                mainActivityLayout, // view / layout
                c+"-"+a+" ya esta disponible",
                Snackbar.LENGTH_SHORT
            ).show()

        }catch (e: IOException){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }

    }

}
