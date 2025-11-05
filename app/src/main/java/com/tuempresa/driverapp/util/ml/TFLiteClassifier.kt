package com.tuempresa.driverapp.util.ml

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.random.Random

class TFLiteClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null

    init {
        try {
            interpreter = Interpreter(loadModelFile("driver_model.tflite"))
            Log.d("TFLiteClassifier", "✅ Modelo cargado correctamente.")
        } catch (e: Exception) {
            Log.e("TFLiteClassifier", "⚠️ No se pudo cargar el modelo real, usando dummy.", e)
        }
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        FileInputStream(fileDescriptor.fileDescriptor).use { input ->
            val channel = input.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }

    fun predict(inputData: FloatArray): FloatArray {
        return try {
            val output = Array(1) { FloatArray(6) } // Ejemplo: 6 clases
            interpreter?.run(arrayOf(inputData), output)
            output[0]
        } catch (e: Exception) {
            Log.w("TFLiteClassifier", "⚙️ Usando predicción simulada (dummy).")
            predictDummy()
        }
    }

    private fun predictDummy(): FloatArray {
        // Devuelve valores aleatorios simulando un modelo real
        return FloatArray(6) { Random.nextFloat() }
    }

    fun close() {
        interpreter?.close()
    }
}
