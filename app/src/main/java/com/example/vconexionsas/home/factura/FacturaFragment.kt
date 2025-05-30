package com.example.vconexionsas.home.factura

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.vconexionsas.BuildConfig
import com.example.vconexionsas.R
import com.example.vconexionsas.databinding.FragmentFacturaBinding
import okhttp3.*
import okio.buffer
import okio.sink
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

class FacturaFragment : Fragment() {

    private lateinit var binding: FragmentFacturaBinding
    private var nombreArchivoPDF: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacturaBinding.inflate(inflater, container, false)

        val prefs = getSecurePrefs(requireContext())
        val codigoUsuario = prefs.getString("codigo_usuario", "") ?: ""

        if (codigoUsuario.isNotEmpty()) {
            obtenerFactura()
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }

        binding.descargarFacturaButton.setOnClickListener {
            val codigo = obtenerCodigoDesdePrefs()
            obtenerUrlFacturaSegura(codigo) { urlSeguro ->
                urlSeguro?.let {
                    descargarArchivoDirecto(requireContext(), it)
                }
            }
        }

        binding.detalleUltimaFacturaButton.setOnClickListener {
            val bottomSheet = FacturaFiltroBottomSheet { mes, anio ->
                val codigo = obtenerCodigoDesdePrefs()
                obtenerUrlFacturaSegura(codigo, mes, anio) { urlSeguro ->
                    urlSeguro?.let {
                        descargarArchivoDirecto(requireContext(), it)
                    }
                }
            }
            bottomSheet.show(parentFragmentManager, "FiltroFactura")
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        binding.pagoPlanButton.setOnClickListener {
            findNavController().navigate(R.id.action_actualFragment_to_sesionPagosFragment)
        }

        return binding.root
    }

    private fun getSecurePrefs(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_user_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun obtenerBaseUrlPorSede(): String {
        val prefs = getSecurePrefs(requireContext())
        val sede = prefs.getString("sede", "Chitaga")

        return when (sede) {
                "Pamplona" -> BuildConfig.URL_PAMPLONA
                "Toledo" -> BuildConfig.URL_TOLEDO
                "Chitaga" -> BuildConfig.URL_CHITAGA
                else -> BuildConfig.URL_CHITAGA
            }
    }

    private fun obtenerFactura() {
        val prefs = getSecurePrefs(requireContext())
        val codigoUsuario = prefs.getString("codigo_usuario", null)
        val token = prefs.getString("token", null)

        if (codigoUsuario.isNullOrEmpty() || token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isAdded) return
        activity?.runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
        }

        val url = obtenerBaseUrlPorSede() + "apipag.php?codigo_usuario=$codigoUsuario"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!isAdded) return
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (!isAdded) return

                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                }

                if (body.isNullOrBlank()) {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Respuesta erronea", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                try {
                    val json = JSONObject(body)
                    activity?.runOnUiThread {
                        if (json.has("error")) {
                            Toast.makeText(requireContext(), json.getString("error"), Toast.LENGTH_SHORT).show()
                        } else {
                            val plan = json.optString("nombre_plan", "No disponible")
                            val valor = json.optDouble("valor", 0.0)
                            val fecha = json.optString("fecha_proxima_carga", "00-00-0000")
                            val estado = json.optString("estado_pago", "Desconocido")
                            val megas = json.optString("megas", "0")

                            binding.planActual.text = "Plan Actual: $plan ($megas MB)"
                            binding.valorPagar.text = "$${"%,.0f".format(valor)}"
                            binding.fechaPago.text = "Próxima carga: $fecha"
                            binding.estadoPago.text = "Estado: $estado"

                            val color = when (estado.lowercase()) {
                                "pagado" -> android.R.color.holo_green_dark
                                "pendiente" -> android.R.color.holo_red_dark
                                else -> android.R.color.darker_gray
                            }
                            binding.estadoPago.setTextColor(ContextCompat.getColor(requireContext(), color))
                        }
                    }
                } catch (e: JSONException) {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Respuesta no completada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun obtenerUrlFacturaSegura(
        codigoUsuario: String,
        mes: String? = null,
        anio: String? = null,
        callback: (String?) -> Unit
    ) {
        if (!isAdded) return
        activity?.runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
        }

        val baseUrl = obtenerBaseUrlPorSede() + "api_fact.php"
        val token = "Bearer vconexion2025"

        val urlBuilder = StringBuilder("$baseUrl?codigo_usuario=$codigoUsuario")
        if (!mes.isNullOrEmpty() && !anio.isNullOrEmpty()) {
            urlBuilder.append("&mes=$mes&anio=$anio")
        }

        val url = urlBuilder.toString()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", token)
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!isAdded) return
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (!isAdded) return
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                }

                if (response.isSuccessful && !body.isNullOrBlank()) {
                    try {
                        val json = JSONObject(body)
                        val status = json.optString("status", "")
                        val mensaje = json.optString("mensaje", "")

                        if (status == "ok") {
                            val urlPdf = json.optString("url_pdf", null)
                            activity?.runOnUiThread {
                                if (urlPdf != null) {
                                    Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                                    callback(urlPdf)
                                } else {
                                    Toast.makeText(requireContext(), "URL de factura no disponible", Toast.LENGTH_SHORT).show()
                                    callback(null)
                                }
                            }
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                                callback(null)
                            }
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show()
                            callback(null)
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Factura no disponible ", Toast.LENGTH_SHORT).show()
                        callback(null)
                    }
                }
            }
        })
    }

    private fun descargarArchivoDirecto(context: Context, url: String) {
        if (!isAdded) return
        activity?.runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
        }

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!isAdded) return
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Error de descarga", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful || !isAdded) {
                    activity?.runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(context, "Descarga fallida", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                nombreArchivoPDF = "factura_${System.currentTimeMillis()}.pdf"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), nombreArchivoPDF!!)

                val sink = file.sink().buffer()
                sink.writeAll(response.body!!.source())
                sink.close()

                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Factura guardada en: ${file.absolutePath}", Toast.LENGTH_LONG).show()

                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No hay una aplicación para abrir PDF", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun obtenerCodigoDesdePrefs(): String {
        val prefs = getSecurePrefs(requireContext())
        return prefs.getString("codigo_usuario", "") ?: ""
    }
}
