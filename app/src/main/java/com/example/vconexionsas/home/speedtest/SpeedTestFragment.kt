package com.example.vconexionsas.home.speedtest

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.R

class SpeedTestFragment : Fragment(R.layout.fragment_speedtest) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos el WebView
        val webView: WebView = view.findViewById(R.id.webView)

        // Configuramos el WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webView.webViewClient = WebViewClient()

        // Cargar URL de SpeedTest
        webView.loadUrl("https://vconexiontest.fireprobe.net/")

        // Botón visual de retroceso
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Botón físico / gesto de atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }
}
