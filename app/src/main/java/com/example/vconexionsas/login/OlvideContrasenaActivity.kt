package com.example.vconexionsas.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.vconexionsas.databinding.ActivityOlvideContrasenaBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class OlvideContrasenaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOlvideContrasenaBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOlvideContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEnviarToken.setOnClickListener {
            val correo = binding.editCorreo.text.toString().trim()

            if (correo.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            enviarToken(correo)
        }
    }

    private fun enviarToken(correo: String) {
        binding.progressBar.visibility = View.VISIBLE

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

        val sede = prefs.getString("sede", "Chitaga")
        val baseUrl = when (sede) {
            "Pamplona" -> "https://login.vconexion.com/"
            "Toledo" -> "https://logint.vconexion.com/"
            "Chitaga" -> "https://loginc.vconexion.com/"
            else -> "https://loginc.vconexion.com/"
        }

        val url = baseUrl + "Api_enviar_token.php"

        val json = JSONObject().apply {
            put("correo", correo)
        }
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@OlvideContrasenaActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                }

                if (!response.isSuccessful || body.isNullOrEmpty()) {
                    runOnUiThread {
                        Toast.makeText(this@OlvideContrasenaActivity, "No se pudo enviar el token", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val json = JSONObject(body)
                val status = json.optString("status")
                val mensaje = json.optString("mensaje")

                runOnUiThread {
                    Toast.makeText(this@OlvideContrasenaActivity, mensaje, Toast.LENGTH_SHORT).show()
                    if (status == "ok") {
                        val intent = Intent(this@OlvideContrasenaActivity, ValidarTokenActivity::class.java)
                        intent.putExtra("correo", correo)
                        startActivity(intent)
                    }
                }
            }
        })
    }
}


