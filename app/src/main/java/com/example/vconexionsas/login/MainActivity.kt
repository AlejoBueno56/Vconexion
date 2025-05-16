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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vconexionsas.R
import com.example.vconexionsas.home.HomeActivity
import com.example.vconexionsas.utils.VersionUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val exp = prefs.getLong("expiracion", 0L)
        val sede = prefs.getString("sede", null)
        val ahora = System.currentTimeMillis() / 1000
        val version = VersionUtils.getAppVersion(this)


        if (!token.isNullOrEmpty() && exp > ahora) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        if (sede.isNullOrEmpty()) {
            setContentView(R.layout.activity_sede)

            findViewById<Button>(R.id.btn_pamplona).setOnClickListener {
                prefs.edit().putString("sede", "Pamplona").apply()
                restartMain()
            }
            findViewById<Button>(R.id.btn_chitaga).setOnClickListener {
                prefs.edit().putString("sede", "Chitaga").apply()
                restartMain()
            }
            findViewById<Button>(R.id.btn_toledo).setOnClickListener {
                prefs.edit().putString("sede", "Toledo").apply()
                restartMain()
            }
            return
        }

        setContentView(R.layout.loginvisual)

        val btnCambiarSede = findViewById<Button>(R.id.btnCambiarSede)
        btnCambiarSede.setOnClickListener {
            prefs.edit().remove("sede").apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val forgotPassword = findViewById<TextView>(R.id.forgot_password)
        forgotPassword.setOnClickListener {
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
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, ingrese todos los datos", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
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

            val versionTextView = findViewById<TextView>(R.id.version_app)
            versionTextView.text = "Versión: $version"
        }
    }

    private fun restartMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun loginUser(
        cedula: String,
        contrasena: String,
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        val client = OkHttpClient()

        val sede = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getString("sede", "Chitaga")

        val baseUrl = when (sede) {
            "Pamplona" -> "https://login.vconexion.com/"
            "Toledo" -> "https://logint.vconexion.com/"
            "Chitaga" -> "https://loginc.vconexion.com/"
            else -> "https://loginc.vconexion.com/"
        }

        val url = baseUrl + "apiclient.php"

        val json = JSONObject().apply {
            put("cedula", cedula)
            put("contrasena", contrasena)
        }

        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("LOGIN_ERROR", "Falló conexión: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Handler(Looper.getMainLooper()).post {
                    if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                        Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        callback(false)
                        return@post
                    }

                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val status = jsonResponse.optString("status", "")
                        val data = jsonResponse.optJSONObject("data") ?: run {
                            Toast.makeText(context, "Respuesta inválida", Toast.LENGTH_SHORT).show()
                            callback(false)
                            return@post
                        }

                        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        sharedPref.edit().apply {
                            putString("codigo_usuario", data.optString("codigo_usuario", ""))
                            putString("nombre_usuario", data.optString("nombre", "Usuario"))
                            putString("correo_usuario", data.optString("correo", "correo@ejemplo.com"))
                            putString("token", data.optString("token", ""))
                            putLong("expiracion", data.optLong("expiracion", 0))
                            apply()
                        }

                        val necesitaCambio = data.optBoolean("necesita_cambio", false)
                        if (status == "temporal" || necesitaCambio) {
                            Toast.makeText(context, "Debe cambiar su contraseña temporal", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, CambiarContrasenaActivity::class.java)
                            intent.putExtra("cedula", cedula)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            callback(false)
                            return@post
                        }

                        Toast.makeText(context, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                        callback(true)
                    } catch (e: Exception) {
                        Log.e("LOGIN_ERROR", "Error al parsear: ${e.message}")
                        Toast.makeText(context, "Error al procesar respuesta", Toast.LENGTH_SHORT).show()
                        callback(false)
                    }
                }
            }
        })
    }
}



