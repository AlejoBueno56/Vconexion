package com.example.vconexionsas.home.plan

import android.content.Context
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.vconexionsas.BuildConfig
import com.example.vconexionsas.model.PlanData
import org.json.JSONException

fun obtenerPlanes(
    context: Context,
    onSuccess: (List<PlanData>) -> Unit,
    onError: (() -> Unit)? = null
) {
    val masterKeyAlias = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_user_prefs",
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val sede = prefs.getString("sede", "Chitaga")
    val baseUrl = when (sede) {
        "Pamplona" -> BuildConfig.URL_PAMPLONA
        "Toledo" -> BuildConfig.URL_TOLEDO
        "Chitaga" -> BuildConfig.URL_CHITAGA
        else -> BuildConfig.URL_CHITAGA
    }

    val url = baseUrl + "apipag.php?accion=listar_planes"

    val queue = Volley.newRequestQueue(context)

    val request = JsonObjectRequest(
        com.android.volley.Request.Method.GET, url, null,
        { response ->
            try {
                val planes = mutableListOf<PlanData>()
                val jsonArray = response.getJSONArray("planes")
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val nombrePlan = obj.getString("nombre_plan")
                    val megas = obj.getInt("megas")
                    val valor = obj.getInt("valor")

                    planes.add(
                        PlanData(
                            title = nombrePlan,
                            speed = "$megas MB",
                            price = "$${String.format("%,d", valor)}"
                        )
                    )
                }
                onSuccess(planes)
            } catch (e: JSONException) {
                e.printStackTrace()
                onError?.invoke()
            }
        },
        { error ->
            error.printStackTrace()
            Toast.makeText(context, "Error al conectar", Toast.LENGTH_SHORT).show()
            onError?.invoke()
        }
    )

    queue.add(request)
}

