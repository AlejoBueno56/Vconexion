package com.example.vconexionsas.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.vconexionsas.R
import com.example.vconexionsas.home.HomeActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val exp = prefs.getLong("expiracion", 0L)
        val ahora = System.currentTimeMillis() / 1000

        Log.d("INICIO_SESION", "Token leído: $token")
        Log.d("INICIO_SESION", "Expiración: $exp vs ahora: $ahora")
        Log.d("INICIO_SESION", "Sesión válida: ${isSesionValida(this)}")

        // ✅ Verificar si ya hay sesión guardada
        if (isSesionValida(this)) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        // 🔓 Si no hay sesión, mostrar pantalla de login
        setContentView(R.layout.loginvisual)

        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        forgotPassword.setOnClickListener {
            // ✅ Lanzar la nueva actividad (no el fragmento)
            val intent = Intent(this, OlvideContrasenaActivity::class.java)
            startActivity(intent)
        }

        val inputUsuario = findViewById<TextInputEditText>(R.id.input_usuario)
        val inputContrasena = findViewById<TextInputEditText>(R.id.input_contrasena)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        btnLogin.setOnClickListener {
            val usuario = inputUsuario.text.toString().trim()
            val contrasena = inputContrasena.text.toString().trim()

            if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                loginUser(usuario, contrasena, this) { success ->
                    runOnUiThread {
                        if (success) {
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Usuario o contraseña incorrectos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese todos los datos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // 🔔 Permisos de notificación (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "🔑 Token: $token")
            }
        }
    }

    fun loginUser(
        cedula: String,
        contrasena: String,
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        val client = OkHttpClient()
        val url = "https://loginc.vconexion.com/apiclient.php"

        val json = JSONObject().apply {
            put("cedula", cedula)
            put("contrasena", contrasena)
        }

        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("LOGIN_ERROR", "Falló la conexión con el servidor: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                }
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                Log.d("LOGIN_RESPONSE_CODE", response.code.toString())
                Log.d("LOGIN_RAW", "Cuerpo crudo: $responseBody")

                Handler(Looper.getMainLooper()).post {
                    if (!response.isSuccessful) {
                        try {
                            val jsonError = JSONObject(responseBody ?: "{}")
                            val errorMsg = jsonError.optString("error", "Error desconocido")
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error de conexion", Toast.LENGTH_SHORT).show()
                        }
                        callback(false)
                        return@post
                    }

                    if (responseBody.isNullOrBlank()) {
                        Toast.makeText(context, "Respuesta vacía del servidor", Toast.LENGTH_SHORT).show()
                        callback(false)
                        return@post
                    }

                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val status = jsonResponse.optString("status", "")
                        val data = jsonResponse.optJSONObject("data")

                        if (data == null) {
                            Toast.makeText(context, "Datos de respuesta inválidos", Toast.LENGTH_SHORT).show()
                            callback(false)
                            return@post
                        }

                        val codigoUsuario = data.optString("codigo_usuario", "")
                        val nombre = data.optString("nombre", "Usuario")
                        val correo = data.optString("correo", "correo@ejemplo.com")
                        val necesitaCambio = data.optBoolean("necesita_cambio", false)

                        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        sharedPref.edit().apply {
                            putString("codigo_usuario", codigoUsuario)
                            putString("nombre_usuario", nombre)
                            putString("correo_usuario", correo)
                            apply()
                        }

                        if (status == "temporal" || necesitaCambio) {
                            Toast.makeText(context, "Debe cambiar su contraseña temporal", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, CambiarContrasenaActivity::class.java)
                            intent.putExtra("cedula", cedula)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            callback(false)
                            return@post
                        }

                        val token = data.optString("token", "")
                        val expiracion = data.optLong("expiracion", 0)

                        sharedPref.edit().apply {
                            putString("token", token)
                            putLong("expiracion", expiracion)
                            apply()
                        }

                        Toast.makeText(context, "¡Bienvenido, $nombre!", Toast.LENGTH_SHORT).show()
                        callback(true)

                    } catch (e: Exception) {
                        Log.e("LOGIN_ERROR", "Error al parsear JSON: ${e.message}")
                        Toast.makeText(context, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                        callback(false)
                    }
                }
            }
        })
    }
}

fun isSesionValida(context: Context): Boolean {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("token", null)
    val codigoUsuario = prefs.getString("codigo_usuario", null)
    val expiracion = prefs.getLong("expiracion", 0L)
    val ahora = System.currentTimeMillis() / 1000
    return !token.isNullOrEmpty() && !codigoUsuario.isNullOrEmpty() && expiracion > ahora
}


