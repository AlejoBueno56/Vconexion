package com.example.vconexionsas.home.contrasena

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.vconexionsas.R
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.example.vconexionsas.home.contrasena.onus.CredencialesDropdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentPruebaDetectarONU : Fragment() {

    private lateinit var webView: WebView
    private lateinit var modeloText: TextView
    private lateinit var composeView: ComposeView
    private lateinit var gatewayIp: String
    private var modeloDetectado: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detectar_modelo_onu, container, false)
        webView = view.findViewById(R.id.webViewONU)
        modeloText = view.findViewById(R.id.textModeloONU)
        composeView = view.findViewById(R.id.composeViewCredenciales)

        gatewayIp = obtenerGateway(requireContext())
        modeloText.text = "Puerta de enlace: $gatewayIp"

        cargarInterfazWeb(gatewayIp)
        // Configurar botón físico "atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        // Configurar el botón de regreso
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return view
    }

    private fun obtenerGateway(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcpInfo = wifiManager.dhcpInfo
        return intToIp(dhcpInfo.gateway)
    }

    private fun intToIp(ip: Int): String {
        return "${ip and 0xFF}.${ip shr 8 and 0xFF}.${ip shr 16 and 0xFF}.${ip shr 24 and 0xFF}"
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun cargarInterfazWeb(gatewayIp: String) {
        modeloText.text = "Intentando detectar modelo por WebView..."

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val titulo = view?.title ?: "Sin título"
                val modelo = identificarModeloDesdeTitulo(titulo, url)
                modeloDetectado = modelo

                // Mostramos toda la información en pantalla
                modeloText.text = """
        Título detectado: $titulo
        URL detectada: $url
        Modelo detectado: $modelo
    """.trimIndent()

                // Log para depuración
                Log.d("DETECTAR_ONU", "Título: $titulo, URL: $url, Modelo: $modelo")

                // Continúa normalmente
                mostrarCredencialesCompose()
            }
        }
        webView.loadUrl("http://$gatewayIp")
    }

    private fun identificarModeloDesdeTitulo(titulo: String, url: String?): String {
        return when {
            titulo.contains("XN020", ignoreCase = true) -> "TPLINK"
            titulo.contains("ZTE", ignoreCase = true) -> "ZTE"
            titulo.contains("Huawei", ignoreCase = true) -> "Huawei"
            titulo.contains("Fiberhome", ignoreCase = true) -> "Fiberhome"
            titulo.contains("ADC", ignoreCase = true) -> "ADC"
            titulo.contains("EASY4", ignoreCase = true) -> "EASY4Link"
            titulo.contains("TL-WR", ignoreCase = true) || url?.contains("LoginRpm", ignoreCase = true) == true -> "TPLINK"
            titulo.contains("ZKXX", ignoreCase = true) -> "ZC"
            titulo.contains("F663N", ignoreCase = true) -> "ZTE_China"
            url?.contains("login.asp", ignoreCase = true) == true -> "LATIC" // para detección por URL
            else -> "Desconocido ($url)"
        }
    }

    private fun mostrarCredencialesCompose() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                composeView.apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        CredencialesDropdown(modelo = modeloDetectado, webViewRef = webView)
                    }
                }
            }
        }
    }
}
