package com.example.vconexionsas.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.vconexionsas.BuildConfig
import com.example.vconexionsas.R
import com.example.vconexionsas.home.HomeActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class CambiarContrasenaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_contrasena)

        val cedula = intent.getStringExtra("cedula")
        val inputNuevaContrasena = findViewById<EditText>(R.id.inputNuevaContrasena)
        val inputConfirmarContrasena = findViewById<EditText>(R.id.inputConfirmarContrasena)
        val btnCambiar = findViewById<Button>(R.id.btnCambiar)

        btnCambiar.setOnClickListener {
            val nuevaContrasena = inputNuevaContrasena.text.toString()
            val confirmarContrasena = inputConfirmarContrasena.text.toString()

            if (nuevaContrasena.length < 6) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (nuevaContrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!cedula.isNullOrBlank()) {
                cambiarContrasena(cedula, nuevaContrasena)
            } else {
                Toast.makeText(this, "No se recibió la cédula del usuario", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun cambiarContrasena(cedula: String, nuevaContrasena: String) {
        val client = OkHttpClient()

        val masterKeyAlias = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            this,
            "secure_user_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val sede = prefs.getString("sede", "Chitaga") // default
        val baseUrl = when (sede) {
            "Pamplona" -> BuildConfig.URL_PAMPLONA
            "Toledo" -> BuildConfig.URL_TOLEDO
            "Chitaga" -> BuildConfig.URL_CHITAGA
            else -> BuildConfig.URL_CHITAGA
        }

        val url = baseUrl + "apiclient.php"

        val json = JSONObject().apply {
            put("cedula", cedula)
            put("nueva_contrasena", nuevaContrasena)
        }

        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@CambiarContrasenaActivity,
                        "Error de conexión: ${e.message}", Toast.LENGTH_LONG
                    ).show()
                    Log.e("CAMBIO_ERROR", "Falló la conexión: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("CAMBIO_RESPONSE", "Código: ${response.code}, Cuerpo: $responseBody")

                runOnUiThread {
                    if (response.isSuccessful) {
                        try {
                            val jsonResponse = JSONObject(responseBody ?: "{}")
                            val status = jsonResponse.optString("status", "")
                            val mensaje =
                                jsonResponse.optString("message", "Contraseña actualizada")

                            if (status == "success") {
                                Toast.makeText(
                                    this@CambiarContrasenaActivity,
                                    mensaje,
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Limpiar token
                                prefs.edit().remove("token").remove("expiracion").apply()

                                // Redirigir al login
                                val intent =
                                    Intent(this@CambiarContrasenaActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                val error = jsonResponse.optString("error", "Error desconocido")
                                Toast.makeText(
                                    this@CambiarContrasenaActivity,
                                    error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@CambiarContrasenaActivity,
                                "Error al procesar la respuesta", Toast.LENGTH_SHORT
                            ).show()
                            Log.e("CAMBIO_ERROR", "Error al parsear JSON: ${e.message}")
                        }
                    } else {
                        try {
                            val jsonError = JSONObject(responseBody ?: "{}")
                            val errorMsg =
                                jsonError.optString("error", "Error al cambiar contraseña")
                            Toast.makeText(
                                this@CambiarContrasenaActivity,
                                errorMsg,
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@CambiarContrasenaActivity,
                                "Error en el servidor: ${response.code}", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }
}
