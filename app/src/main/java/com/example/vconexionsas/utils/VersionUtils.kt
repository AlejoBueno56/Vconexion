package com.example.vconexionsas.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object VersionUtils {
    fun getAppVersion(context: Context): String {
        return try {
            val inputStream = context.assets.open("VERSION.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val version = reader.readLine()
            reader.close()
            version ?: "Desconocida"
        } catch (e: Exception) {
            "Desconocida"
        }
    }
}
