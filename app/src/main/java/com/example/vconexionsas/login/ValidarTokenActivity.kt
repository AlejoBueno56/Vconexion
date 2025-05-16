package com.example.vconexionsas.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vconexionsas.databinding.ActivityValidarTokenBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ValidarTokenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityValidarTokenBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidarTokenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnValidarToken.setOnClickListener {
            val token = binding.editTextToken.text.toString().trim()

            if (token.isEmpty()) {
                Toast.makeText(this, "Ingresa el código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            validarToken(token)
        }
    }

    private fun validarToken(token: String) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val sede = prefs.getString("sede", "Chitaga")
        val baseUrl = when (sede) {
            "Pamplona" -> "https://login.vconexion.com/"
            "Toledo" -> "https://logint.vconexion.com/"
            "Chitaga" -> "https://loginc.vconexion.com/"
            else -> "https://loginc.vconexion.com/"
        }
        val url = baseUrl + "Api_verificar_token.php"

        val json = JSONObject()
        json.put("token", token)

        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ValidarTokenActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                        Toast.makeText(this@ValidarTokenActivity, "Error al verificar", Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }

                    try {
                        val jsonResponse = JSONObject(responseBody)
                        val status = jsonResponse.optString("status")
                        val mensaje = jsonResponse.optString("mensaje")

                        Toast.makeText(this@ValidarTokenActivity, mensaje, Toast.LENGTH_SHORT).show()

                        if (status == "ok") {
                            val cedula = jsonResponse.optString("cedula")
                            val intent = Intent(this@ValidarTokenActivity, CambiarContrasenaActivity::class.java)
                            intent.putExtra("cedula", cedula)
                            startActivity(intent)
                            finish()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@ValidarTokenActivity, "Respuesta inválida", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}

